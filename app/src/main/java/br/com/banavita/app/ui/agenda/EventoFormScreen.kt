package br.com.banavita.app.ui.agenda

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.DeleteConfirmDialog
import br.com.banavita.app.ui.common.DocumentEntityDropdown
import br.com.banavita.app.ui.common.FormLoadingState
import br.com.banavita.app.ui.common.FormScaffold
import br.com.banavita.app.ui.voice.VoiceEnabledFormHost
import br.com.banavita.app.ui.voice.VoiceFormContentPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun EventoFormRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EventoFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) {
        // Save even if notifications are denied; the reminder stays silent until allowed.
        viewModel.save(onDone)
    }

    if (uiState.showDeleteDialog) {
        DeleteConfirmDialog(
            title = stringResource(R.string.agenda_delete_title),
            message = stringResource(R.string.agenda_delete_message),
            isDeleting = uiState.isDeleting,
            onConfirm = { viewModel.delete(onDone) },
            onDismiss = viewModel::onDismissDelete,
        )
    }

    if (uiState.isLoading) {
        FormLoadingState(modifier = modifier)
        return
    }

    val save: () -> Unit = {
        val needsPermission = uiState.notificar &&
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.save(onDone)
        }
    }

    VoiceEnabledFormHost(
        voiceContext = VoiceScreenContext.AGENDA,
        enabled = !uiState.isEditMode,
        onVoiceSuccess = { onDone() },
        modifier = modifier,
    ) { formModifier ->
        EventoFormScreen(
            uiState = uiState,
            voiceEnabled = !uiState.isEditMode,
            onTitulo = viewModel::onTitulo,
            onData = viewModel::onData,
            onHora = viewModel::onHora,
            onObservacao = viewModel::onObservacao,
            onCliente = viewModel::onCliente,
            onFornecedor = viewModel::onFornecedor,
            onNotificar = viewModel::onNotificar,
            onAntecipacao = viewModel::onAntecipacao,
            onNotifTitulo = viewModel::onNotifTitulo,
            onNotifCorpo = viewModel::onNotifCorpo,
            onSave = save,
            onDelete = viewModel::onDeleteClick,
            modifier = formModifier,
        )
    }
}

@Composable
fun EventoFormScreen(
    uiState: EventoFormUiState,
    onTitulo: (String) -> Unit,
    onData: (String) -> Unit,
    onHora: (String) -> Unit,
    onObservacao: (String) -> Unit,
    onCliente: (UUID?) -> Unit,
    onFornecedor: (UUID?) -> Unit,
    onNotificar: (Boolean) -> Unit,
    onAntecipacao: (Int?) -> Unit,
    onNotifTitulo: (String) -> Unit,
    onNotifCorpo: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    voiceEnabled: Boolean = false,
) {
    FormScaffold(
        modifier = modifier,
        errorMessage = uiState.errorMessage,
        isSaving = uiState.isSaving,
        showDelete = uiState.isEditMode,
        contentPadding = if (voiceEnabled) VoiceFormContentPadding else PaddingValues(),
        onSave = onSave,
        onDelete = onDelete,
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.titulo,
            onValueChange = onTitulo,
            label = { Text(stringResource(R.string.agenda_form_titulo)) },
            singleLine = true,
            enabled = !uiState.isSaving,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.dataInput,
            onValueChange = onData,
            label = { Text(stringResource(R.string.form_data)) },
            singleLine = true,
            enabled = !uiState.isSaving,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.horaInput,
            onValueChange = onHora,
            label = { Text(stringResource(R.string.agenda_form_hora)) },
            singleLine = true,
            enabled = !uiState.isSaving,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.observacao,
            onValueChange = onObservacao,
            label = { Text(stringResource(R.string.form_observacao)) },
            enabled = !uiState.isSaving,
        )
        DocumentEntityDropdown(
            label = stringResource(R.string.form_cliente),
            noneLabel = stringResource(R.string.agenda_sem_vinculo),
            options = uiState.clienteOptions,
            selectedId = uiState.clienteId,
            onSelected = onCliente,
            enabled = !uiState.isSaving,
        )
        DocumentEntityDropdown(
            label = stringResource(R.string.form_fornecedor),
            noneLabel = stringResource(R.string.agenda_sem_vinculo),
            options = uiState.fornecedorOptions,
            selectedId = uiState.fornecedorId,
            onSelected = onFornecedor,
            enabled = !uiState.isSaving,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = uiState.notificar,
                onClick = { onNotificar(true) },
                label = { Text(stringResource(R.string.agenda_notif_on)) },
            )
            FilterChip(
                selected = !uiState.notificar,
                onClick = { onNotificar(false) },
                label = { Text(stringResource(R.string.agenda_notif_off)) },
            )
        }
        if (uiState.notificar) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.antecipacaoMinutos == null,
                    onClick = { onAntecipacao(null) },
                    label = { Text(stringResource(R.string.agenda_antecipacao_hora)) },
                )
                FilterChip(
                    selected = uiState.antecipacaoMinutos == 15,
                    onClick = { onAntecipacao(15) },
                    label = { Text(stringResource(R.string.agenda_antecipacao_15)) },
                )
                FilterChip(
                    selected = uiState.antecipacaoMinutos == 60,
                    onClick = { onAntecipacao(60) },
                    label = { Text(stringResource(R.string.agenda_antecipacao_1h)) },
                )
                FilterChip(
                    selected = uiState.antecipacaoMinutos == 1440,
                    onClick = { onAntecipacao(1440) },
                    label = { Text(stringResource(R.string.agenda_antecipacao_1d)) },
                )
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.notifTitulo,
                onValueChange = onNotifTitulo,
                label = { Text(stringResource(R.string.agenda_notif_titulo)) },
                singleLine = true,
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.notifCorpo,
                onValueChange = onNotifCorpo,
                label = { Text(stringResource(R.string.agenda_notif_corpo)) },
                enabled = !uiState.isSaving,
            )
        }
    }
}
