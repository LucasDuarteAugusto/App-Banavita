package br.com.banavita.app.ui.settings

import br.com.banavita.app.domain.model.ThemeMode
import br.com.banavita.app.domain.model.TranscriptionProvider

data class SettingsUiState(
    val selectedProvider: TranscriptionProvider = TranscriptionProvider.GROQ,
    val selectedTheme: ThemeMode = ThemeMode.LIGHT,
    val groqApiKeyConfigured: Boolean = false,
    val isBackupBusy: Boolean = false,
    val showRestoreConfirm: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
)
