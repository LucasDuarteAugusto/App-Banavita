package br.com.banavita.app.ui.cadastros.clientes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.ui.common.DeleteConfirmDialog
import br.com.banavita.app.ui.common.FormLoadingState
import br.com.banavita.app.ui.common.FormScaffold
import androidx.compose.foundation.layout.PaddingValues
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.voice.VoiceEnabledFormHost
import br.com.banavita.app.ui.voice.VoiceFormContentPadding

@Composable
fun ClienteFormRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClienteFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteDialog) {
        DeleteConfirmDialog(
            title = stringResource(R.string.cliente_delete_title),
            message = stringResource(R.string.cliente_delete_message),
            isDeleting = uiState.isDeleting,
            onConfirm = { viewModel.delete(onDone) },
            onDismiss = viewModel::onDismissDeleteDialog,
        )
    }

    if (uiState.isLoading) {
        FormLoadingState(modifier = modifier)
        return
    }

    VoiceEnabledFormHost(
        voiceContext = VoiceScreenContext.CLIENTE,
        enabled = !uiState.isEditMode,
        onVoiceSuccess = { onDone() },
        modifier = modifier,
    ) { formModifier ->
        ClienteFormScreen(
            uiState = uiState,
            modifier = formModifier,
            voiceEnabled = !uiState.isEditMode,
            onNomeChange = viewModel::onNomeChange,
            onTelefoneChange = viewModel::onTelefoneChange,
            onSave = { viewModel.save(onDone) },
            onDelete = viewModel::onDeleteClick,
        )
    }
}

@Composable
fun ClienteFormScreen(
    uiState: ClienteFormUiState,
    onNomeChange: (String) -> Unit,
    onTelefoneChange: (String) -> Unit,
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
            value = uiState.nome,
            onValueChange = onNomeChange,
            label = { Text(stringResource(R.string.form_nome)) },
            singleLine = true,
            enabled = !uiState.isSaving,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.telefone,
            onValueChange = onTelefoneChange,
            label = { Text(stringResource(R.string.form_telefone)) },
            placeholder = { Text(stringResource(R.string.form_telefone_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            enabled = !uiState.isSaving,
        )
    }
}
