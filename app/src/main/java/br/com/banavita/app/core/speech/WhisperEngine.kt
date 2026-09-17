package br.com.banavita.app.core.speech

interface WhisperEngine {
    suspend fun load(modelPath: String, modelId: String)

    suspend fun transcribe(
        samples: FloatArray,
        language: String = SpeechConstants.LANGUAGE_PT,
        initialPrompt: String? = null,
    ): String

    fun release()

    val isLoaded: Boolean
    val loadedModelId: String?
}
