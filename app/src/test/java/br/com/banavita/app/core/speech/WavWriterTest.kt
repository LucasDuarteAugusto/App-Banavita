package br.com.banavita.app.core.speech

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WavWriterTest {
    @Test
    fun `floatMonoToWavBytes gera header RIFF WAVE PCM`() {
        val samples = FloatArray(4) { 0.5f }
        val bytes = WavWriter.floatMonoToWavBytes(samples, sampleRateHz = 16_000)
        assertTrue(bytes.size >= 44 + samples.size * 2)

        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val riff = ByteArray(4).also { buffer.get(it) }.toString(Charsets.US_ASCII)
        buffer.int
        val wave = ByteArray(4).also { buffer.get(it) }.toString(Charsets.US_ASCII)
        assertEquals("RIFF", riff)
        assertEquals("WAVE", wave)
    }
}
