package br.com.banavita.app.core.speech

/**
 * Decode-side knobs for short operational commands (not interview transcription).
 *
 * Encoder frames ≈ 50/s (hop 160 × conv stride 2 → 320 samples/frame at 16 kHz).
 * Default whisper.cpp audio_ctx is 1500 (~30 s); smaller ctx matches the clip length.
 */
object WhisperDecodeParams {
    const val ENCODER_FRAME_SAMPLES = 320
    const val MAX_AUDIO_CTX = 1500
    const val MIN_AUDIO_CTX = 64
    const val PAD_FRAMES = 50

    fun threadCount(availableProcessors: Int = Runtime.getRuntime().availableProcessors()): Int {
        if (availableProcessors <= 1) return 1
        return SpeechConstants.WHISPER_THREADS
            .coerceAtMost(SpeechConstants.WHISPER_THREADS_MAX)
            .coerceAtMost(availableProcessors)
    }

    fun audioCtx(sampleCount: Int): Int {
        val frames = (sampleCount / ENCODER_FRAME_SAMPLES) + PAD_FRAMES
        return frames.coerceIn(MIN_AUDIO_CTX, MAX_AUDIO_CTX)
    }
}
