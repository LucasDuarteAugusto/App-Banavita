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

    fun getEtilenoCapacidadeKg(): Float =
        preferences.getFloat(KEY_ETILENO_CAPACIDADE_KG, 0f)

    fun setEtilenoCapacidadeKg(value: Float) {
        preferences.edit { putFloat(KEY_ETILENO_CAPACIDADE_KG, value.coerceAtLeast(0f)) }
    }

    fun getEtilenoSoltarPorDiaKg(): Float =
        preferences.getFloat(KEY_ETILENO_SOLTAR_DIA_KG, 0f)

    fun setEtilenoSoltarPorDiaKg(value: Float) {
        preferences.edit { putFloat(KEY_ETILENO_SOLTAR_DIA_KG, value.coerceAtLeast(0f)) }
    }

    fun getAvisoMatinalMinutos(): Int =
        preferences.getInt(KEY_AVISO_MATINAL_MINUTOS, DEFAULT_AVISO_MATINAL_MINUTOS)

    fun setAvisoMatinalMinutos(value: Int) {
        preferences.edit { putInt(KEY_AVISO_MATINAL_MINUTOS, value.coerceIn(0, 23 * 60 + 59)) }
    }

    companion object {
        private const val PREFS_NAME = "banavita_app_settings"
        private const val KEY_TRANSCRIPTION_PROVIDER = "transcription_provider"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_ETILENO_CAPACIDADE_KG = "etileno_capacidade_kg"
        private const val KEY_ETILENO_SOLTAR_DIA_KG = "etileno_soltar_dia_kg"
        private const val KEY_AVISO_MATINAL_MINUTOS = "aviso_matinal_minutos"
        const val DEFAULT_AVISO_MATINAL_MINUTOS = 7 * 60
    }
}
