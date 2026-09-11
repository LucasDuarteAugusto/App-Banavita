package br.com.banavita.app.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.ThemeMode
import br.com.banavita.app.domain.model.TranscriptionProvider
import br.com.banavita.app.ui.common.DeleteConfirmDialog

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val createDocument = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
    ) { uri ->
        if (uri != null) viewModel.exportTo(uri)
    }

    val openDocument = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) viewModel.onRestoreFileChosen(uri)
    }

    if (uiState.showRestoreConfirm) {
        DeleteConfirmDialog(
            title = stringResource(R.string.settings_restore_confirm_title),
            message = stringResource(R.string.settings_restore_confirm_message),
            isDeleting = uiState.isBackupBusy,
            confirmLabel = stringResource(R.string.settings_restore_button),
            onConfirm = viewModel::confirmRestore,
            onDismiss = viewModel::onDismissRestoreConfirm,
        )
    }

    SettingsScreen(
        uiState = uiState,
        onProviderSelected = viewModel::onProviderSelected,
        onThemeSelected = viewModel::onThemeSelected,
        onExportClick = { createDocument.launch(viewModel.suggestedBackupFileName()) },
        onRestoreClick = {
            openDocument.launch(
                arrayOf(
                    "application/octet-stream",
                    "application/zip",
                    "*/*",
                ),
            )
        },
        onDismissMessage = viewModel::dismissMessage,
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onProviderSelected: (TranscriptionProvider) -> Unit,
    onThemeSelected: (ThemeMode) -> Unit,
    onExportClick: () -> Unit,
    onRestoreClick: () -> Unit,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = stringResource(R.string.settings_appearance_section),
            style = MaterialTheme.typography.titleMedium,
        )

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsOptionCard(
                title = stringResource(R.string.settings_theme_light_title),
                description = stringResource(R.string.settings_theme_light_desc),
                selected = uiState.selectedTheme == ThemeMode.LIGHT,
                enabled = !uiState.isBackupBusy,
                onClick = { onThemeSelected(ThemeMode.LIGHT) },
            )
            SettingsOptionCard(
                title = stringResource(R.string.settings_theme_dark_title),
                description = stringResource(R.string.settings_theme_dark_desc),
                selected = uiState.selectedTheme == ThemeMode.DARK,
                enabled = !uiState.isBackupBusy,
                onClick = { onThemeSelected(ThemeMode.DARK) },
            )
        }

        Text(
            text = stringResource(R.string.settings_transcription_section),
            style = MaterialTheme.typography.titleMedium,
        )

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsOptionCard(
                title = stringResource(R.string.settings_provider_groq_title),
                description = stringResource(R.string.settings_provider_groq_desc),
                selected = uiState.selectedProvider == TranscriptionProvider.GROQ,
                enabled = uiState.groqApiKeyConfigured && !uiState.isBackupBusy,
                footnote = if (uiState.groqApiKeyConfigured) {
                    stringResource(R.string.settings_provider_groq_ready)
                } else {
                    stringResource(R.string.settings_provider_groq_missing_key)
                },
                onClick = { onProviderSelected(TranscriptionProvider.GROQ) },
            )
            SettingsOptionCard(
                title = stringResource(R.string.settings_provider_local_title),
                description = stringResource(R.string.settings_provider_local_desc),
                selected = uiState.selectedProvider == TranscriptionProvider.LOCAL,
                enabled = !uiState.isBackupBusy,
                footnote = stringResource(R.string.settings_provider_local_note),
                onClick = { onProviderSelected(TranscriptionProvider.LOCAL) },
            )
        }

        Text(
            text = stringResource(R.string.settings_database_section),
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = stringResource(R.string.settings_database_section_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = stringResource(R.string.settings_export_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isBackupBusy,
            onClick = onExportClick,
        ) {
            if (uiState.isBackupBusy) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(stringResource(R.string.settings_export_button))
        }

        Text(
            text = stringResource(R.string.settings_restore_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isBackupBusy,
            onClick = onRestoreClick,
        ) {
            Text(stringResource(R.string.settings_restore_button))
        }

        uiState.statusMessage?.let { code ->
            val text = when (code) {
                SettingsViewModel.SUCCESS_EXPORT ->
                    stringResource(R.string.settings_export_success)
                SettingsViewModel.SUCCESS_RESTORE ->
                    stringResource(R.string.settings_restore_success)
                else -> code
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                onClick = onDismissMessage,
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SettingsOptionCard(
    title: String,
    description: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    footnote: String? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick,
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                enabled = enabled,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    },
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (footnote != null) {
                    Text(
                        text = footnote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
