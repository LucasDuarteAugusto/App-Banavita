package br.com.banavita.app.data.remote.groq

import br.com.banavita.app.GroqBuildFields
import br.com.banavita.app.data.local.settings.AppSettingsLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

interface GroqConfig {
    val apiKey: String
    val modelId: String

    fun isConfigured(): Boolean = apiKey.isNotBlank()
}

@Singleton
class EnvGroqConfig @Inject constructor(
    private val localDataSource: AppSettingsLocalDataSource
) : GroqConfig {
    override val apiKey: String
        get() = localDataSource.getGroqApiKey()

    override val modelId: String
        get() = GroqBuildFields.WHISPER_MODEL.ifBlank { "whisper-large-v3-turbo" }
}
