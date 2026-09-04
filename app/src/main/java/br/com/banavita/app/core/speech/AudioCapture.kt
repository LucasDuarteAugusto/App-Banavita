package br.com.banavita.app.core.speech

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.SystemClock
import androidx.core.content.ContextCompat
import br.com.banavita.app.domain.exception.SpeechAudioException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

data class RecordingLevels(
    val elapsedMs: Long = 0L,
    val bars: List<Float> = AmplitudeMeter.idleBars(),
    val maxDurationReached: Boolean = false,
)

/**
 * Captures mono PCM 16-bit at 16 kHz. Caller must hold RECORD_AUDIO permission.
 * Recording runs on [Dispatchers.IO]; [start] returns as soon as the mic is live.
 */
@Singleton
class AudioCapture @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val mutex = Mutex()
    private val stopRequested = AtomicBoolean(false)
    private val pcmBuffer = ByteArrayOutputStream()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var recordingJob: Job? = null
    private var recorder: AudioRecord? = null

    private val _levels = MutableStateFlow(RecordingLevels())
    val levels: StateFlow<RecordingLevels> = _levels.asStateFlow()

    suspend fun start() {
        mutex.withLock {
            if (recordingJob?.isActive == true) {
                throw SpeechAudioException("Gravação já em andamento")
            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                throw SpeechAudioException("Permissão RECORD_AUDIO não concedida")
            }

            stopRequested.set(false)
            pcmBuffer.reset()
            _levels.value = RecordingLevels()

            val (rec, chunkSize) = createRecorder()
            recorder = rec
            try {
                rec.startRecording()
            } catch (_: IllegalStateException) {
                rec.release()
                recorder = null
                throw SpeechAudioException("Falha ao iniciar o microfone")
            }
            val startedAt = SystemClock.elapsedRealtime()
            recordingJob = scope.launch {
                try {
                    captureLoop(rec, startedAt, chunkSize)
                } catch (_: SpeechAudioException) {
                    if (!stopRequested.get()) {
                        throw SpeechAudioException("Erro na leitura do microfone")
                    }
                } finally {
                    runCatching { rec.stop() }
                    rec.release()
                    if (recorder === rec) {
                        recorder = null
                    }
                }
            }
        }
    }

    suspend fun stop(): FloatArray {
        mutex.withLock {
            stopRequested.set(true)
            recordingJob?.join()
            recordingJob = null
            val pcm = pcmBuffer.toByteArray()
            pcmBuffer.reset()
            _levels.value = RecordingLevels()
            if (pcm.size < 2) {
                throw SpeechAudioException("Áudio vazio")
            }
            val even = if (pcm.size % 2 == 0) pcm else pcm.copyOf(pcm.size - 1)
            return PcmConverter.pcm16LittleEndianToFloat(even)
        }
    }

    fun cancel() {
        stopRequested.set(true)
        recordingJob?.cancel()
        recordingJob = null
        val rec = recorder
        recorder = null
        if (rec != null) {
            runCatching { rec.stop() }
            rec.release()
        }
        pcmBuffer.reset()
        _levels.value = RecordingLevels()
    }

    private fun captureLoop(recorder: AudioRecord, startedAt: Long, chunkSize: Int) {
        val sampleRate = SpeechConstants.SAMPLE_RATE_HZ
        val maxBytes = SpeechConstants.MAX_DURATION_SECONDS * sampleRate * 2
        val chunk = ByteArray(chunkSize.coerceAtLeast(2))
        var offset = 0
        var bars = AmplitudeMeter.idleBars()

        while (!stopRequested.get() && offset < maxBytes) {
            val toRead = min(chunk.size, maxBytes - offset)
            val read = recorder.read(chunk, 0, toRead)
            if (read < 0) {
                if (stopRequested.get()) break
                throw SpeechAudioException("Erro na leitura do microfone ($read)")
            }
            if (read == 0) continue
            synchronized(pcmBuffer) {
                pcmBuffer.write(chunk, 0, read)
            }
            offset += read
            val rms = AmplitudeMeter.rmsPcm16Le(chunk, read)
            bars = AmplitudeMeter.pushBar(bars, rms)
            val elapsed = SystemClock.elapsedRealtime() - startedAt
            val maxReached = offset >= maxBytes
            _levels.value = RecordingLevels(
                elapsedMs = elapsed,
                bars = bars,
                maxDurationReached = maxReached,
            )
            if (maxReached) break
        }
        if (offset >= maxBytes) {
            _levels.value = _levels.value.copy(maxDurationReached = true)
        }
    }

    private fun createRecorder(): Pair<AudioRecord, Int> {
        val sampleRate = SpeechConstants.SAMPLE_RATE_HZ
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val minBuffer = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        if (minBuffer <= 0) {
            throw SpeechAudioException("AudioRecord indisponível neste dispositivo")
        }
        val bufferSize = minBuffer * 2
        val recorder = try {
            AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize,
            )
        } catch (_: SecurityException) {
            throw SpeechAudioException("Permissão RECORD_AUDIO negada")
        }
        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            recorder.release()
            throw SpeechAudioException("Falha ao inicializar AudioRecord")
        }
        return recorder to bufferSize
    }
}
