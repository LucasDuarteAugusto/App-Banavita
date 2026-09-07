package br.com.banavita.app.data.remote.groq

import br.com.banavita.app.GroqBuildFields
import javax.inject.Inject
import javax.inject.Singleton

interface GroqConfig {
    val apiKey: String
    val modelId: String

    fun isConfigured(): Boolean = apiKey.isNotBlank()
}

@Singleton
class EnvGroqConfig @Inject constructor() : GroqConfig {
    override val apiKey: String = GroqBuildFields.API_KEY

    override val modelId: String
        get() = GroqBuildFields.WHISPER_MODEL.ifBlank { "whisper-large-v3-turbo" }
}
