package br.com.banavita.app.ui.settings

import br.com.banavita.app.domain.model.ThemeMode
import br.com.banavita.app.domain.model.TranscriptionProvider

data class SettingsUiState(
    val selectedProvider: TranscriptionProvider = TranscriptionProvider.GROQ,
    val selectedTheme: ThemeMode = ThemeMode.LIGHT,
    val groqApiKeyConfigured: Boolean = false,
    val etilenoCapacidadeInput: String = "",
    val avisoMatinalInput: String = "07:00",
    val isBackupBusy: Boolean = false,
    val showRestoreConfirm: Boolean = false,
    val selectedRingtoneName: String? = null,
    val selectedOfflineModelId: String = "ggml-tiny-q5_1",
    val groqApiKeyInput: String = "",
    val statusMessage: String? = null,
    val errorMessage: String? = null,
)
