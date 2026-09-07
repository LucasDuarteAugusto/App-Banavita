package br.com.banavita.app.ui.settings

import br.com.banavita.app.domain.model.TranscriptionProvider

data class SettingsUiState(
    val selectedProvider: TranscriptionProvider = TranscriptionProvider.GROQ,
    val groqApiKeyConfigured: Boolean = false,
)
