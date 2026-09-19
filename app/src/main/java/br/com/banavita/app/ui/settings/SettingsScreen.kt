package br.com.banavita.app.ui.settings

import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material3.IconButton
import br.com.banavita.app.ui.common.BanavitaIcons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

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

    val ringtonePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val uri = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
        viewModel.onRingtoneSelected(uri)
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
        onOfflineModelSelected = viewModel::onOfflineModelSelected,
        onGroqApiKeyChange = viewModel::onGroqApiKeyChange,
        onEtilenoCapacidadeChange = viewModel::onEtilenoCapacidadeChange,
        onFriaCapacidadeChange = viewModel::onFriaCapacidadeChange,
        onAvisoMatinalChange = viewModel::onAvisoMatinalChange,
        onRingtoneClick = {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, context.getString(R.string.settings_ringtone))
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            }
            ringtonePicker.launch(intent)
        },
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
    onOfflineModelSelected: (String) -> Unit,
    onGroqApiKeyChange: (String) -> Unit,
    onEtilenoCapacidadeChange: (String) -> Unit,
    onFriaCapacidadeChange: (String) -> Unit,
    onAvisoMatinalChange: (String) -> Unit,
    onRingtoneClick: () -> Unit,
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
            
            var apiKeyVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                value = uiState.groqApiKeyInput,
                onValueChange = onGroqApiKeyChange,
                label = { Text("Chave API do Groq") },
                placeholder = { Text("gsk_...") },
                singleLine = true,
                enabled = !uiState.isBackupBusy,
                visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val icon = if (apiKeyVisible) BanavitaIcons.Visibility else BanavitaIcons.VisibilityOff
                    IconButton(onClick = { apiKeyVisible = !apiKeyVisible }) {
                        Icon(imageVector = icon, contentDescription = if (apiKeyVisible) "Ocultar" else "Mostrar")
                    }
                }
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
            text = stringResource(R.string.settings_pereciveis_section),
            style = MaterialTheme.typography.titleMedium,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.etilenoCapacidadeInput,
            onValueChange = onEtilenoCapacidadeChange,
            label = { Text(stringResource(R.string.settings_etileno_capacidade)) },
            supportingText = { Text(stringResource(R.string.settings_etileno_capacidade_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            enabled = !uiState.isBackupBusy,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.friaCapacidadeInput,
            onValueChange = onFriaCapacidadeChange,
            label = { Text(stringResource(R.string.settings_fria_capacidade)) },
            supportingText = { Text(stringResource(R.string.settings_fria_capacidade_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            enabled = !uiState.isBackupBusy,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.avisoMatinalInput,
            onValueChange = onAvisoMatinalChange,
            label = { Text(stringResource(R.string.settings_aviso_matinal)) },
            supportingText = { Text(stringResource(R.string.settings_aviso_matinal_hint)) },
            singleLine = true,
            enabled = !uiState.isBackupBusy,
        )

        Text(
            text = stringResource(R.string.settings_notifications_section),
            style = MaterialTheme.typography.titleMedium,
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRingtoneClick,
            enabled = !uiState.isBackupBusy,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_ringtone)) },
                supportingContent = {
                    Text(uiState.selectedRingtoneName ?: stringResource(R.string.settings_ringtone_default))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent
                )
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
