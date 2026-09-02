package br.com.banavita.app.ui.cadastros.grupos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
fun GrupoFormRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GrupoFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteDialog) {
        DeleteConfirmDialog(
            title = stringResource(R.string.grupo_delete_title),
            message = stringResource(R.string.grupo_delete_message),
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
        voiceContext = VoiceScreenContext.GRUPO,
        enabled = !uiState.isEditMode,
        onVoiceSuccess = { onDone() },
        modifier = modifier,
    ) { formModifier ->
        GrupoFormScreen(
            uiState = uiState,
            modifier = formModifier,
            voiceEnabled = !uiState.isEditMode,
            onNomeChange = viewModel::onNomeChange,
            onObservacaoChange = viewModel::onObservacaoChange,
            onSave = { viewModel.save(onDone) },
            onDelete = viewModel::onDeleteClick,
        )
    }
}

@Composable
fun GrupoFormScreen(
    uiState: GrupoFormUiState,
    onNomeChange: (String) -> Unit,
    onObservacaoChange: (String) -> Unit,
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
            value = uiState.observacao,
            onValueChange = onObservacaoChange,
            label = { Text(stringResource(R.string.form_observacao)) },
            minLines = 2,
            enabled = !uiState.isSaving,
        )
    }
}
