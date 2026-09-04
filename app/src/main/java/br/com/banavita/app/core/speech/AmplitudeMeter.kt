package br.com.banavita.app.core.speech

import kotlin.math.sqrt

object AmplitudeMeter {
    const val BAR_COUNT = 28
    const val MIN_BAR = 0.08f
    private const val VISUAL_GAIN = 4f

    fun rmsPcm16Le(pcm: ByteArray, length: Int): Float {
        val evenLength = length - (length % 2)
        if (evenLength < 2) return MIN_BAR
        var sumSquares = 0.0
        var count = 0
        var i = 0
        while (i < evenLength) {
            val lo = pcm[i].toInt() and 0xff
            val hi = pcm[i + 1].toInt()
            val sample = ((hi shl 8) or lo).toShort() / 32768.0
            sumSquares += sample * sample
            count++
            i += 2
        }
        val rms = sqrt(sumSquares / count).toFloat()
        return (rms * VISUAL_GAIN).coerceIn(MIN_BAR, 1f)
    }

    fun pushBar(current: List<Float>, next: Float, max: Int = BAR_COUNT): List<Float> {
        val padded = when {
            current.size >= max -> current.takeLast(max)
            else -> List(max - current.size) { MIN_BAR } + current
        }
        return padded.drop(1) + next.coerceIn(MIN_BAR, 1f)
    }

    fun idleBars(count: Int = BAR_COUNT): List<Float> = List(count) { MIN_BAR }

    fun formatElapsed(elapsedMs: Long): String {
        val totalSec = (elapsedMs.coerceAtLeast(0L) / 1_000L).toInt()
        val minutes = totalSec / 60
        val seconds = totalSec % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
