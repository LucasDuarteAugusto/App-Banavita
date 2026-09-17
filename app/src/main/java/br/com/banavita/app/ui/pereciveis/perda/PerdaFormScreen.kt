package br.com.banavita.app.ui.pereciveis.perda

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.PerdaMotivo
import br.com.banavita.app.ui.common.DocumentDateField
import br.com.banavita.app.ui.common.FormScaffold
import br.com.banavita.app.ui.common.SearchableEntityDropdown
import br.com.banavita.app.ui.common.formatQuantity
import br.com.banavita.app.ui.common.labelRes
import java.util.UUID

@Composable
fun PerdaFormRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PerdaFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    PerdaFormScreen(
        uiState = uiState,
        modifier = modifier,
        onProdutoSelected = viewModel::onProdutoSelected,
        onLoteSelected = viewModel::onLoteSelected,
        onQuantidadeChange = viewModel::onQuantidadeChange,
        onDataChange = viewModel::onDataChange,
        onMotivo = viewModel::onMotivo,
        onSave = { viewModel.save(onDone) },
    )
}

@Composable
fun PerdaFormScreen(
    uiState: PerdaFormUiState,
    onProdutoSelected: (UUID?) -> Unit,
    onLoteSelected: (UUID?) -> Unit,
    onQuantidadeChange: (String) -> Unit,
    onDataChange: (String) -> Unit,
    onMotivo: (PerdaMotivo) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormScaffold(
        modifier = modifier,
        errorMessage = uiState.errorMessage,
        isSaving = uiState.isSaving,
        contentPadding = PaddingValues(),
        onSave = onSave,
    ) {
        Text(
            text = stringResource(R.string.perda_confirm),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        SearchableEntityDropdown(
            label = stringResource(R.string.form_produto),
            options = uiState.produtos.map { it.id to it.descricao },
            selectedId = uiState.produtoId,
            onSelected = onProdutoSelected,
            enabled = !uiState.isSaving,
            placeholder = stringResource(R.string.form_produto_select),
        )
        SearchableEntityDropdown(
            label = stringResource(R.string.perda_lote),
            options = uiState.lotes.map { lote ->
                val produto = uiState.produtos.find { it.id == lote.produtoId }
                val unidade = produto?.unidade ?: "UN"
                lote.id to "${lote.rotulo} · ${formatQuantity(lote.quantidadeRestante, unidade)}"
            },
            selectedId = uiState.loteId,
            onSelected = onLoteSelected,
            enabled = !uiState.isSaving && uiState.lotes.isNotEmpty(),
            placeholder = stringResource(R.string.perda_lote),
        )
        val loteSelecionado = uiState.lotes.find { it.id == uiState.loteId }
        val produtoSelecionado = uiState.produtos.find { it.id == (loteSelecionado?.produtoId ?: uiState.produtoId) }
        val unidade = produtoSelecionado?.unidade

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.quantidadeInput,
            onValueChange = onQuantidadeChange,
            label = {
                if (!unidade.isNullOrBlank()) {
                    Text("${stringResource(R.string.form_quantidade)} ($unidade)")
                } else {
                    Text(stringResource(R.string.form_quantidade))
                }
            },
            suffix = {
                if (!unidade.isNullOrBlank()) {
                    Text(unidade)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            enabled = !uiState.isSaving,
        )
        DocumentDateField(
            value = uiState.dataInput,
            onValueChange = onDataChange,
            enabled = !uiState.isSaving,
        )
        Text(text = stringResource(R.string.perda_motivo))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PerdaMotivo.entries.forEach { motivo ->
                FilterChip(
                    selected = uiState.motivo == motivo,
                    onClick = { onMotivo(motivo) },
                    enabled = !uiState.isSaving,
                    label = { Text(stringResource(motivo.labelRes())) },
                )
            }
        }
    }
}
