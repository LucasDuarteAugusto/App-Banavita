package br.com.banavita.app.core.speech

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AmplitudeMeterTest {

    @Test
    fun `silencio pcm fica no piso visual`() {
        val pcm = ByteArray(64)
        val rms = AmplitudeMeter.rmsPcm16Le(pcm, pcm.size)
        assertEquals(AmplitudeMeter.MIN_BAR, rms, 0.0001f)
    }

    @Test
    fun `pcm forte sobe a barra`() {
        val pcm = ByteArray(8)
        pcm[0] = 0xFF.toByte()
        pcm[1] = 0x7F
        pcm[2] = 0xFF.toByte()
        pcm[3] = 0x7F
        pcm[4] = 0xFF.toByte()
        pcm[5] = 0x7F
        pcm[6] = 0xFF.toByte()
        pcm[7] = 0x7F
        val rms = AmplitudeMeter.rmsPcm16Le(pcm, pcm.size)
        assertEquals(1f, rms, 0.0001f)
    }

    @Test
    fun `pushBar mantem janela rolante`() {
        val start = AmplitudeMeter.idleBars()
        val next = AmplitudeMeter.pushBar(start, 0.5f)
        assertEquals(AmplitudeMeter.BAR_COUNT, next.size)
        assertEquals(0.5f, next.last(), 0.0001f)
        assertTrue(next.first() <= AmplitudeMeter.MIN_BAR + 0.0001f)
    }

    @Test
    fun `formatElapsed usa m ss`() {
        assertEquals("0:00", AmplitudeMeter.formatElapsed(0))
        assertEquals("0:09", AmplitudeMeter.formatElapsed(9_000))
        assertEquals("1:05", AmplitudeMeter.formatElapsed(65_000))
    }
}
