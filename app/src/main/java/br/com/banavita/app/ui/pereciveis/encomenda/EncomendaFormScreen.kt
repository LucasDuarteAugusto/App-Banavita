package br.com.banavita.app.ui.pereciveis.encomenda

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.EncomendaStatus
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.ui.common.DeleteConfirmDialog
import br.com.banavita.app.ui.common.DocumentDateField
import br.com.banavita.app.ui.common.DocumentLineItemCard
import br.com.banavita.app.ui.common.FormLoadingState
import br.com.banavita.app.ui.common.FormScaffold
import br.com.banavita.app.ui.common.SearchableEntityDropdown
import java.util.UUID

@Composable
fun EncomendaFormRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EncomendaFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.showDeleteDialog) {
        DeleteConfirmDialog(
            title = stringResource(R.string.encomenda_delete_title),
            message = stringResource(R.string.encomenda_delete_message),
            isDeleting = uiState.isDeleting,
            onConfirm = { viewModel.delete(onDone) },
            onDismiss = viewModel::onDismissDeleteDialog,
        )
    }
    if (uiState.showParcialDialog) {
        DeleteConfirmDialog(
            title = stringResource(R.string.encomenda_atender_parcial),
            message = stringResource(R.string.encomenda_atender_parcial_msg),
            isDeleting = uiState.isAtendendo,
            confirmLabel = stringResource(R.string.encomenda_atender_parcial),
            onConfirm = { viewModel.atender(onDone, parcial = true) },
            onDismiss = viewModel::onDismissParcial,
        )
    }
    if (uiState.isLoading) {
        FormLoadingState(modifier = modifier)
        return
    }
    EncomendaFormScreen(
        uiState = uiState,
        modifier = modifier,
        onDataChange = viewModel::onDataChange,
        onClienteSelected = viewModel::onClienteSelected,
        onAPrazoChange = viewModel::onAPrazoChange,
        onProdutoSelected = viewModel::onProdutoSelected,
        onEstadoSelected = viewModel::onEstadoSelected,
        onQuantidadeChange = viewModel::onQuantidadeChange,
        onAddItem = viewModel::onAddItem,
        onRemoveItem = viewModel::onRemoveItem,
        onSave = { viewModel.save(onDone) },
        onDelete = viewModel::onDeleteClick,
        onAtender = { viewModel.atender(onDone) },
    )
}

@Composable
fun EncomendaFormScreen(
    uiState: EncomendaFormUiState,
    onDataChange: (String) -> Unit,
    onClienteSelected: (UUID?) -> Unit,
    onAPrazoChange: (Boolean) -> Unit,
    onProdutoSelected: (Int, UUID?) -> Unit,
    onEstadoSelected: (Int, MaturacaoEstado?) -> Unit,
    onQuantidadeChange: (Int, String) -> Unit,
    onAddItem: () -> Unit,
    onRemoveItem: (Int) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onAtender: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val aberto = uiState.status == EncomendaStatus.ABERTO
    FormScaffold(
        modifier = modifier,
        errorMessage = uiState.errorMessage,
        isSaving = uiState.isSaving || uiState.isAtendendo,
        showDelete = uiState.isEditMode && aberto,
        contentPadding = PaddingValues(),
        onSave = onSave,
        onDelete = onDelete,
    ) {
        Text(
            text = if (uiState.status == EncomendaStatus.ATENDIDO) {
                stringResource(R.string.encomenda_status_atendido)
            } else {
                stringResource(R.string.encomenda_status_aberto)
            },
            style = MaterialTheme.typography.titleMedium,
        )
        DocumentDateField(
            value = uiState.dataInput,
            onValueChange = onDataChange,
            enabled = aberto && !uiState.isSaving,
            label = stringResource(R.string.encomenda_data_entrega),
        )
        SearchableEntityDropdown(
            label = stringResource(R.string.form_cliente),
            options = uiState.clientes.map { it.id to it.nome },
            selectedId = uiState.clienteId,
            onSelected = onClienteSelected,
            enabled = aberto && !uiState.isSaving,
            placeholder = stringResource(R.string.form_cliente_select),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = aberto) { onAPrazoChange(!uiState.aPrazo) }
                .padding(vertical = 4.dp),
        ) {
            Checkbox(
                checked = !uiState.aPrazo,
                onCheckedChange = { isPago -> onAPrazoChange(!isPago) },
                enabled = aberto && !uiState.isSaving,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.form_pago_checkbox))
        }
        uiState.itens.forEachIndexed { index, item ->
            DocumentLineItemCard(
                index = index,
                item = item,
                produtos = uiState.produtos,
                precoObrigatorio = false,
                showEstoque = false,
                enabled = aberto && !uiState.isSaving,
                canRemove = aberto && uiState.itens.size > 1,
                onProdutoSelected = { onProdutoSelected(index, it) },
                onQuantidadeChange = { onQuantidadeChange(index, it) },
                onPrecoChange = {},
                onRemove = { onRemoveItem(index) },
                onEstadoSelected = { onEstadoSelected(index, it) },
            )
        }
        if (aberto) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
                onClick = onAddItem,
            ) {
                Text(stringResource(R.string.form_add_item))
            }
        }
        if (uiState.isEditMode && aberto) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving && !uiState.isAtendendo,
                onClick = onAtender,
            ) {
                Text(stringResource(R.string.encomenda_atender))
            }
        }
    }
}
