package br.com.banavita.app.data.local.settings

import android.content.Context
import androidx.core.content.edit
import br.com.banavita.app.domain.model.ThemeMode
import br.com.banavita.app.domain.model.TranscriptionProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsLocalDataSource @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getTranscriptionProviderOrNull(): TranscriptionProvider? {
        val raw = preferences.getString(KEY_TRANSCRIPTION_PROVIDER, null) ?: return null
        return runCatching { TranscriptionProvider.valueOf(raw) }.getOrNull()
    }

    fun setTranscriptionProvider(provider: TranscriptionProvider) {
        preferences.edit {
            putString(KEY_TRANSCRIPTION_PROVIDER, provider.name)
        }
    }

    fun getThemeMode(): ThemeMode {
        val raw = preferences.getString(KEY_THEME_MODE, null) ?: return ThemeMode.LIGHT
        return runCatching { ThemeMode.valueOf(raw) }.getOrNull() ?: ThemeMode.LIGHT
    }

    fun setThemeMode(mode: ThemeMode) {
        preferences.edit {
            putString(KEY_THEME_MODE, mode.name)
        }
    }

    companion object {
        private const val PREFS_NAME = "banavita_app_settings"
        private const val KEY_TRANSCRIPTION_PROVIDER = "transcription_provider"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
