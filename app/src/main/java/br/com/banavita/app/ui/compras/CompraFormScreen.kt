package br.com.banavita.app.ui.compras

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.ui.common.DeleteConfirmDialog
import br.com.banavita.app.ui.common.DocumentDateField
import br.com.banavita.app.ui.common.DocumentLineItemCard
import br.com.banavita.app.ui.common.DocumentTotalRow
import br.com.banavita.app.ui.common.FormLoadingState
import br.com.banavita.app.ui.common.FormScaffold
import br.com.banavita.app.ui.common.SearchableEntityDropdown
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

    if (uiState.showCapacidadeWarning) {
        DeleteConfirmDialog(
            title = "Aviso de Capacidade",
            message = uiState.capacidadeWarningMessage,
            isDeleting = uiState.isSaving,
            confirmLabel = "Comprar mesmo assim",
            onConfirm = { viewModel.save(onDone, forcar = true) },
            onDismiss = viewModel::onDismissCapacidadeWarning,
        )
    }

    if (uiState.isLoading) {
        FormLoadingState(modifier = modifier)
        return
    }

    VoiceEnabledFormHost(
        voiceContext = VoiceScreenContext.COMPRA,
        enabled = !uiState.isEditMode,
        onVoiceSuccess = { onDone() },
        modifier = modifier,
    ) { formModifier ->
        CompraFormScreen(
            uiState = uiState,
            modifier = formModifier,
            voiceEnabled = !uiState.isEditMode,
            onDataChange = viewModel::onDataChange,
            onFornecedorSelected = viewModel::onFornecedorSelected,
            onAPrazoChange = viewModel::onAPrazoChange,
            onVencimentoChange = viewModel::onVencimentoChange,
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
    onAPrazoChange: (Boolean) -> Unit,
    onProdutoSelected: (Int, java.util.UUID?) -> Unit,
    onQuantidadeChange: (Int, String) -> Unit,
    onPrecoChange: (Int, String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    voiceEnabled: Boolean = false,
    onVencimentoChange: (String) -> Unit = {},
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

        SearchableEntityDropdown(
            label = stringResource(R.string.form_fornecedor),
            options = uiState.fornecedorOptions,
            selectedId = uiState.fornecedorId,
            onSelected = onFornecedorSelected,
            enabled = !uiState.isSaving,
            placeholder = stringResource(R.string.form_fornecedor_select),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !uiState.isSaving && !uiState.isEditMode) {
                    onAPrazoChange(!uiState.aPrazo)
                }
                .padding(vertical = 4.dp),
        ) {
            Checkbox(
                checked = !uiState.aPrazo,
                onCheckedChange = { isPago -> onAPrazoChange(!isPago) },
                enabled = !uiState.isSaving && !uiState.isEditMode,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.form_pago_compra_checkbox),
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        if (uiState.aPrazo) {
            DocumentDateField(value = uiState.vencimentoInput, onValueChange = onVencimentoChange, enabled = !uiState.isSaving, label = "Vencimento")
            if (uiState.vencimentoInput.isBlank() && uiState.isEditMode) Text("Sem vencimento: preencha para incluir na projeção.")
        }
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
