package br.com.banavita.app.core.speech

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PcmConverterTest {

    @Test
    fun `pcm16 silence vira floats proximos de zero`() {
        val pcm = ByteArray(8) // 4 samples
        val floats = PcmConverter.pcm16LittleEndianToFloat(pcm)
        assertEquals(4, floats.size)
        floats.forEach { sample ->
            assertEquals(0f, sample, 0.0001f)
        }
    }

    @Test
    fun `pcm16 max positivo`() {
        // little-endian 0x7FFF = 32767
        val pcm = byteArrayOf(0xFF.toByte(), 0x7F)
        val floats = PcmConverter.pcm16LittleEndianToFloat(pcm)
        assertEquals(1, floats.size)
        assertTrue(floats[0] > 0.99f && floats[0] <= 1f)
    }

    @Test
    fun `clamp corta amostras longas`() {
        val samples = FloatArray(SpeechConstants.MAX_SAMPLES + 100) { 0.1f }
        val clamped = PcmConverter.clampToMaxDuration(samples)
        assertEquals(SpeechConstants.MAX_SAMPLES, clamped.size)
    }
}
