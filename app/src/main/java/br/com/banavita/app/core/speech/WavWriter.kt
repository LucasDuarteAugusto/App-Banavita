package br.com.banavita.app.core.speech

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object WavWriter {
    fun floatMonoToWavBytes(
        samples: FloatArray,
        sampleRateHz: Int = SpeechConstants.SAMPLE_RATE_HZ,
    ): ByteArray {
        val pcm = ByteArray(samples.size * 2)
        var o = 0
        for (sample in samples) {
            val clamped = sample.coerceIn(-1f, 1f)
            val intSample = (clamped * 32767f).toInt().toShort()
            pcm[o++] = (intSample.toInt() and 0xff).toByte()
            pcm[o++] = ((intSample.toInt() shr 8) and 0xff).toByte()
        }

        val dataSize = pcm.size
        val out = ByteArrayOutputStream(44 + dataSize)
        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        header.put("RIFF".toByteArray(Charsets.US_ASCII))
        header.putInt(36 + dataSize)
        header.put("WAVE".toByteArray(Charsets.US_ASCII))
        header.put("fmt ".toByteArray(Charsets.US_ASCII))
        header.putInt(16)
        header.putShort(1) // PCM
        header.putShort(1) // mono
        header.putInt(sampleRateHz)
        header.putInt(sampleRateHz * 2) // byte rate
        header.putShort(2) // block align
        header.putShort(16) // bits
        header.put("data".toByteArray(Charsets.US_ASCII))
        header.putInt(dataSize)
        out.write(header.array())
        out.write(pcm)
        return out.toByteArray()
    }
}
