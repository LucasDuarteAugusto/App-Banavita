package br.com.banavita.app.core.speech

import kotlin.math.sqrt

data class TrimResult(
    val samples: FloatArray,
    val originalSampleCount: Int,
    val speechSampleCount: Int,
) {
    val originalSeconds: Double
        get() = originalSampleCount.toDouble() / SpeechConstants.SAMPLE_RATE_HZ

    val speechSeconds: Double
        get() = speechSampleCount.toDouble() / SpeechConstants.SAMPLE_RATE_HZ
}

/**
 * Energy VAD: drop leading/trailing silence so Whisper does not score hold-to-talk padding.
 * If no speech is found, the original buffer is kept.
 */
object SilenceTrimmer {
    private const val FRAME_MS = 20
    private const val PAD_MS = 200
    private const val ENERGY_THRESHOLD = 0.02f

    fun trim(
        samples: FloatArray,
        sampleRate: Int = SpeechConstants.SAMPLE_RATE_HZ,
        energyThreshold: Float = ENERGY_THRESHOLD,
    ): TrimResult {
        if (samples.isEmpty()) {
            return TrimResult(samples, 0, 0)
        }
        val frameSize = ((sampleRate * FRAME_MS) / 1000).coerceAtLeast(1)
        val padSamples = ((sampleRate * PAD_MS) / 1000)
        val frameCount = samples.size / frameSize
        if (frameCount == 0) {
            return TrimResult(samples, samples.size, samples.size)
        }

        var first = -1
        var last = -1
        for (frame in 0 until frameCount) {
            val start = frame * frameSize
            if (rms(samples, start, frameSize) >= energyThreshold) {
                if (first < 0) first = frame
                last = frame
            }
        }
        if (first < 0 || last < first) {
            return TrimResult(samples, samples.size, samples.size)
        }

        val from = (first * frameSize - padSamples).coerceAtLeast(0)
        val toExclusive = ((last + 1) * frameSize + padSamples).coerceAtMost(samples.size)
        if (toExclusive <= from) {
            return TrimResult(samples, samples.size, samples.size)
        }
        val trimmed = samples.copyOfRange(from, toExclusive)
        return TrimResult(
            samples = trimmed,
            originalSampleCount = samples.size,
            speechSampleCount = trimmed.size,
        )
    }

    private fun rms(samples: FloatArray, start: Int, length: Int): Float {
        val end = (start + length).coerceAtMost(samples.size)
        if (end <= start) return 0f
        var sum = 0.0
        var i = start
        while (i < end) {
            val v = samples[i].toDouble()
            sum += v * v
            i++
        }
        return sqrt(sum / (end - start)).toFloat()
    }
}
