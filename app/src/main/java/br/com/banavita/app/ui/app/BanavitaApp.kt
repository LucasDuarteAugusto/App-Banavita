package br.com.banavita.app.ui.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.domain.model.ThemeMode
import br.com.banavita.app.ui.activation.ActivationScreen
import br.com.banavita.app.ui.activation.ActivationViewModel
import br.com.banavita.app.ui.shell.MainAppShell
import br.com.banavita.app.ui.theme.BanavitaTheme

@Composable
fun BanavitaApp(
    modifier: Modifier = Modifier,
    appViewModel: BanavitaAppViewModel = hiltViewModel(),
) {
    val appState by appViewModel.uiState.collectAsState()

    BanavitaTheme(darkTheme = appState.themeMode == ThemeMode.DARK) {
        when {
            appState.isChecking -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            appState.isActivated -> {
                MainAppShell(modifier = modifier)
            }

            else -> {
                ActivationRoute(
                    modifier = modifier,
                    onActivated = appViewModel::onActivated,
                )
            }
        }
    }
}

@Composable
private fun ActivationRoute(
    onActivated: () -> Unit,
    modifier: Modifier = Modifier,
    activationViewModel: ActivationViewModel = hiltViewModel(),
) {
    val uiState by activationViewModel.uiState.collectAsState()

    ActivationScreen(
        modifier = modifier,
        uiState = uiState,
        onLicenseTokenChange = activationViewModel::onLicenseTokenChange,
        onToggleLicenseVisibility = activationViewModel::onToggleLicenseVisibility,
        onCopyRequestCode = activationViewModel::onRequestCodeCopied,
        onActivate = { activationViewModel.activate(onActivated) },
    )
}
