package br.com.banavita.app.data.repository

import br.com.banavita.app.data.local.settings.AppSettingsLocalDataSource
import br.com.banavita.app.data.remote.groq.GroqConfig
import br.com.banavita.app.domain.model.ThemeMode
import br.com.banavita.app.domain.model.TranscriptionProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    private val localDataSource: AppSettingsLocalDataSource,
    private val groqConfig: GroqConfig,
) {
    private val _themeMode = MutableStateFlow(localDataSource.getThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

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

    fun getThemeMode(): ThemeMode = _themeMode.value

    fun setThemeMode(mode: ThemeMode) {
        localDataSource.setThemeMode(mode)
        _themeMode.value = mode
    }

    fun isGroqApiKeyConfigured(): Boolean = 
        localDataSource.getGroqApiKey().isNotBlank()

    fun getEtilenoCapacidadeKg(): Double = localDataSource.getEtilenoCapacidadeKg().toDouble()

    fun setEtilenoCapacidadeKg(value: Double) {
        localDataSource.setEtilenoCapacidadeKg(value.toFloat())
    }

    fun getAvisoMatinalMinutos(): Int = localDataSource.getAvisoMatinalMinutos()

    fun setAvisoMatinalMinutos(value: Int) {
        localDataSource.setAvisoMatinalMinutos(value)
    }

    fun getRingtoneUri(): String? = localDataSource.getRingtoneUri()

    fun setRingtoneUri(uri: String?) {
        localDataSource.setRingtoneUri(uri)
    }

    fun getOfflineModelId(): String = localDataSource.getOfflineModelId()

    fun setOfflineModelId(id: String) {
        localDataSource.setOfflineModelId(id)
    }

    fun getGroqApiKey(): String = localDataSource.getGroqApiKey()

    fun setGroqApiKey(key: String) {
        localDataSource.setGroqApiKey(key)
    }
}
