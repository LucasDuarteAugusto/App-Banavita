package br.com.banavita.app.ui.app

import br.com.banavita.app.domain.model.ThemeMode

data class AppUiState(
    val isChecking: Boolean = true,
    val isActivated: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.LIGHT,
)
