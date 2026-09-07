package br.com.banavita.app.data.repository

import br.com.banavita.app.data.local.settings.AppSettingsLocalDataSource
import br.com.banavita.app.data.remote.groq.GroqConfig
import br.com.banavita.app.domain.model.TranscriptionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    private val localDataSource: AppSettingsLocalDataSource,
    private val groqConfig: GroqConfig,
) {
    fun getTranscriptionProvider(): TranscriptionProvider {
        localDataSource.getTranscriptionProviderOrNull()?.let { return it }
        return if (groqConfig.isConfigured()) {
            TranscriptionProvider.GROQ
        } else {
            TranscriptionProvider.LOCAL
        }
    }

    fun setTranscriptionProvider(provider: TranscriptionProvider) {
        localDataSource.setTranscriptionProvider(provider)
    }

    fun isGroqApiKeyConfigured(): Boolean = groqConfig.isConfigured()
}
