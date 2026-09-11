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
    const val ACTIVE_MODEL_ID: String = BuildConfig.WHISPER_MODEL_ID
    const val ACTIVE_MODEL_FILE: String = BuildConfig.WHISPER_MODEL_FILE
    const val ACTIVE_MODEL_SHA256: String = BuildConfig.WHISPER_MODEL_SHA256
    const val ACTIVE_MODEL_PROFILE: String = BuildConfig.WHISPER_MODEL_PROFILE
    const val ACTIVE_MODEL_SIZE_MB: Int = BuildConfig.WHISPER_MODEL_SIZE_MB
}
