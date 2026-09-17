package br.com.banavita.app.core.speech

import br.com.banavita.app.BuildConfig

object SpeechConstants {
    const val LANGUAGE_PT = "pt"
    const val SAMPLE_RATE_HZ = 16_000
    const val MAX_DURATION_SECONDS = 15
    const val MAX_SAMPLES = SAMPLE_RATE_HZ * MAX_DURATION_SECONDS

    /** Default inference threads (budget SoCs: extra little-cores hurt). */
    const val WHISPER_THREADS = 2
    const val WHISPER_THREADS_MAX = 4

    const val MODELS_DIR_NAME = "models"
    
    // Modelos embutidos (Build Config)
    const val TINY_MODEL_ID = BuildConfig.WHISPER_TINY_ID
    const val TINY_MODEL_FILE = BuildConfig.WHISPER_TINY_FILE
    const val TINY_MODEL_SHA256 = BuildConfig.WHISPER_TINY_SHA256
    const val TINY_MODEL_SIZE = BuildConfig.WHISPER_TINY_SIZE_MB

    const val BASE_MODEL_ID = BuildConfig.WHISPER_BASE_ID
    const val BASE_MODEL_FILE = BuildConfig.WHISPER_BASE_FILE
    const val BASE_MODEL_SHA256 = BuildConfig.WHISPER_BASE_SHA256
    const val BASE_MODEL_SIZE = BuildConfig.WHISPER_BASE_SIZE_MB
}
