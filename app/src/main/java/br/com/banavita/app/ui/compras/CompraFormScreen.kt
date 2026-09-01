package br.com.banavita.app.ui.compras

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.ui.common.DeleteConfirmDialog
import br.com.banavita.app.ui.common.DocumentDateField
import br.com.banavita.app.ui.common.DocumentEntityDropdown
import br.com.banavita.app.ui.common.DocumentLineItemCard
import br.com.banavita.app.ui.common.DocumentTotalRow
import br.com.banavita.app.ui.common.FormLoadingState
import br.com.banavita.app.ui.common.FormScaffold
import androidx.compose.foundation.layout.PaddingValues
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.voice.VoiceEnabledFormHost
import br.com.banavita.app.ui.voice.VoiceFormContentPadding

@Composable
fun CompraFormRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CompraFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteDialog) {
        DeleteConfirmDialog(
            title = stringResource(R.string.compra_delete_title),
            message = stringResource(R.string.compra_delete_message),
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
        voiceContext = VoiceScreenContext.COMPRA,
        enabled = !uiState.isEditMode,
        onVoiceSuccess = onDone,
        modifier = modifier,
    ) { formModifier ->
        CompraFormScreen(
            uiState = uiState,
            modifier = formModifier,
            voiceEnabled = !uiState.isEditMode,
            onDataChange = viewModel::onDataChange,
            onFornecedorSelected = viewModel::onFornecedorSelected,
            onProdutoSelected = viewModel::onProdutoSelected,
            onQuantidadeChange = viewModel::onQuantidadeChange,
            onPrecoChange = viewModel::onPrecoChange,
            onAddItem = viewModel::onAddItem,
            onRemoveItem = viewModel::onRemoveItem,
            onSave = { viewModel.save(onDone) },
            onDelete = viewModel::onDeleteClick,
        )
    }
}

@Composable
fun CompraFormScreen(
    uiState: CompraFormUiState,
    onDataChange: (String) -> Unit,
    onFornecedorSelected: (java.util.UUID?) -> Unit,
    onProdutoSelected: (Int, java.util.UUID?) -> Unit,
    onQuantidadeChange: (Int, String) -> Unit,
    onPrecoChange: (Int, String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (Int) -> Unit,
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
        DocumentDateField(
            value = uiState.dataInput,
            onValueChange = onDataChange,
            enabled = !uiState.isSaving,
        )

        DocumentEntityDropdown(
            label = stringResource(R.string.form_fornecedor),
            noneLabel = stringResource(R.string.compra_sem_fornecedor),
            options = uiState.fornecedorOptions,
            selectedId = uiState.fornecedorId,
            onSelected = onFornecedorSelected,
            enabled = !uiState.isSaving,
        )

        uiState.itens.forEachIndexed { index, item ->
            DocumentLineItemCard(
                index = index,
                item = item,
                produtos = uiState.produtos,
                precoObrigatorio = true,
                showEstoque = false,
                enabled = !uiState.isSaving,
                canRemove = uiState.itens.size > 1,
                onProdutoSelected = { produtoId -> onProdutoSelected(index, produtoId) },
                onQuantidadeChange = { value -> onQuantidadeChange(index, value) },
                onPrecoChange = { value -> onPrecoChange(index, value) },
                onRemove = { onRemoveItem(index) },
            )
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving,
            onClick = onAddItem,
        ) {
            Text(stringResource(R.string.form_add_item))
        }

        DocumentTotalRow(totalCentavos = uiState.valorTotalCentavos)
    }
}
