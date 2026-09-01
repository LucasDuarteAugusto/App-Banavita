package br.com.banavita.app.core.speech

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import br.com.banavita.app.domain.exception.SpeechAudioException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

/**
 * Captures mono PCM 16-bit at 16 kHz. Caller must hold RECORD_AUDIO permission.
 */
@Singleton
class AudioCapture @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {

    fun recordSeconds(seconds: Int): FloatArray {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            throw SpeechAudioException("Permissão RECORD_AUDIO não concedida")
        }

        val duration = seconds.coerceIn(1, SpeechConstants.MAX_DURATION_SECONDS)
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

        val totalBytes = duration * sampleRate * 2
        val pcm = ByteArray(totalBytes)
        var offset = 0
        val chunk = ByteArray(bufferSize)

        try {
            recorder.startRecording()
            while (offset < totalBytes) {
                val toRead = min(chunk.size, totalBytes - offset)
                val read = recorder.read(chunk, 0, toRead)
                if (read < 0) {
                    throw SpeechAudioException("Erro na leitura do microfone ($read)")
                }
                if (read == 0) continue
                System.arraycopy(chunk, 0, pcm, offset, read)
                offset += read
            }
        } finally {
            runCatching { recorder.stop() }
            recorder.release()
        }

        return PcmConverter.pcm16LittleEndianToFloat(pcm.copyOf(offset))
    }
}
