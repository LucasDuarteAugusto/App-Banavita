package br.com.banavita.app.core.speech

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SilenceTrimmerTest {

    @Test
    fun `silencio nas pontas e cortado e fala permanece`() {
        val rate = SpeechConstants.SAMPLE_RATE_HZ
        val silence = FloatArray(rate) { 0f }
        val speech = FloatArray(rate) { 0.3f }
        val samples = silence + speech + silence

        val result = SilenceTrimmer.trim(samples)
        assertEquals(samples.size, result.originalSampleCount)
        assertTrue(result.speechSampleCount < samples.size)
        assertTrue(result.speechSampleCount >= speech.size)
        assertTrue(result.samples.all { kotlin.math.abs(it) < 0.01f || kotlin.math.abs(it - 0.3f) < 0.0001f })
        assertTrue(result.samples.count { kotlin.math.abs(it - 0.3f) < 0.0001f } >= speech.size)
    }

    @Test
    fun `buffer so silencio permanece intacto`() {
        val samples = FloatArray(SpeechConstants.SAMPLE_RATE_HZ) { 0.001f }
        val result = SilenceTrimmer.trim(samples)
        assertEquals(samples.size, result.speechSampleCount)
        assertEquals(samples.size, result.samples.size)
    }
}

class WhisperDecodeParamsTest {

    @Test
    fun `threads comeca em 2 e nunca passa de 4`() {
        assertEquals(1, WhisperDecodeParams.threadCount(1))
        assertEquals(2, WhisperDecodeParams.threadCount(2))
        assertEquals(2, WhisperDecodeParams.threadCount(8))
        assertEquals(2, WhisperDecodeParams.threadCount(16))
        assertTrue(WhisperDecodeParams.threadCount(8) <= SpeechConstants.WHISPER_THREADS_MAX)
    }

    @Test
    fun `audio_ctx escala com a duracao e respeita teto de 30s`() {
        val oneSecond = SpeechConstants.SAMPLE_RATE_HZ
        val ctx1s = WhisperDecodeParams.audioCtx(oneSecond)
        val ctx10s = WhisperDecodeParams.audioCtx(oneSecond * 10)
        assertTrue(ctx10s > ctx1s)
        assertEquals(1500, WhisperDecodeParams.audioCtx(oneSecond * 30))
        assertTrue(WhisperDecodeParams.audioCtx(100) >= WhisperDecodeParams.MIN_AUDIO_CTX)
    }
}

class SttDiagnosticsTest {

    @Test
    fun `mascara telefone com 11 digitos`() {
        assertEquals(
            "Alfredo 119****4321",
            SttDiagnostics.maskSensitive("Alfredo 11987654321"),
        )
    }
}
