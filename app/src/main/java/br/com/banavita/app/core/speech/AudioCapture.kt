package br.com.banavita.app.core.speech

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import br.com.banavita.app.domain.exception.SpeechAudioException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

/**
 * Captures mono PCM 16-bit at 16 kHz. Caller must hold RECORD_AUDIO permission.
 */
@Singleton
class AudioCapture @Inject constructor() {

    fun recordSeconds(seconds: Int): FloatArray {
        val duration = seconds.coerceIn(1, SpeechConstants.MAX_DURATION_SECONDS)
        val sampleRate = SpeechConstants.SAMPLE_RATE_HZ
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val minBuffer = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        if (minBuffer <= 0) {
            throw SpeechAudioException("AudioRecord indisponível neste dispositivo")
        }

        val bufferSize = minBuffer * 2
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize,
        ) // ERROR - Call requires permission which may be rejected by user: code should explicitly check to see if permission is available (with checkPermission) or explicitly handle a potential SecurityException
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
