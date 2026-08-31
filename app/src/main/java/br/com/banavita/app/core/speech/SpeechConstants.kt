package br.com.banavita.app.core.speech

object SpeechConstants {
    const val LANGUAGE_PT = "pt"
    const val SAMPLE_RATE_HZ = 16_000
    const val MAX_DURATION_SECONDS = 15
    const val MAX_SAMPLES = SAMPLE_RATE_HZ * MAX_DURATION_SECONDS

    const val MODELS_DIR_NAME = "models"
    const val ACTIVE_MODEL_ID = "ggml-base-q5_1"
    const val ACTIVE_MODEL_FILE = "ggml-base-q5_1.bin"

    const val ACTIVE_MODEL_SHA256 =
        "422f1ae452ade6f30a004d7e5c6a43195e4433bc370bf23fac9cc591f01a8898"

    const val MODEL_DOWNLOAD_URL =
        "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-base-q5_1.bin"
}
