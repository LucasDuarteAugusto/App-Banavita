package br.com.banavita.app.core.speech

object PcmConverter {
    private const val PCM16_MAX = 32768f

    fun pcm16LittleEndianToFloat(pcm: ByteArray): FloatArray {
        require(pcm.size % 2 == 0) { "PCM16 buffer deve ter tamanho par" }
        val sampleCount = pcm.size / 2
        val floats = FloatArray(sampleCount)
        var i = 0
        var o = 0
        while (i < pcm.size) {
            val lo = pcm[i].toInt() and 0xff
            val hi = pcm[i + 1].toInt()
            val sample = ((hi shl 8) or lo).toShort()
            floats[o++] = sample / PCM16_MAX
            i += 2
        }
        return floats
    }

    fun clampToMaxDuration(samples: FloatArray, maxSamples: Int = SpeechConstants.MAX_SAMPLES): FloatArray {
        if (samples.size <= maxSamples) return samples
        return samples.copyOf(maxSamples)
    }
}
