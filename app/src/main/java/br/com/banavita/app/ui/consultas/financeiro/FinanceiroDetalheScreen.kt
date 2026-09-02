package br.com.banavita.app.ui.consultas.financeiro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.ui.common.ListEmptyState
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.common.ListLoadingState
import br.com.banavita.app.ui.common.formatCurrency
import br.com.banavita.app.ui.common.formatDate

@Composable
fun FinanceiroDetalheRoute(
    receber: Boolean,
    modifier: Modifier = Modifier,
    viewModel: FinanceiroDetalheViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    FinanceiroDetalheScreen(
        uiState = uiState,
        receber = receber,
        onBaixaDocumento = viewModel::onBaixaDocumento,
        onBaixaTotal = viewModel::onBaixaTotalClick,
        onValorChange = viewModel::onValorChange,
        onConfirmBaixa = viewModel::confirmBaixa,
        onDismissBaixa = viewModel::dismissBaixa,
        modifier = modifier,
    )
}

@Composable
fun FinanceiroDetalheScreen(
    uiState: FinanceiroDetalheUiState,
    receber: Boolean,
    onBaixaDocumento: (java.util.UUID) -> Unit,
    onBaixaTotal: () -> Unit,
    onValorChange: (String) -> Unit,
    onConfirmBaixa: () -> Unit,
    onDismissBaixa: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.showBaixaDialog) {
        AlertDialog(
            onDismissRequest = onDismissBaixa,
            title = {
                Text(
                    if (receber) stringResource(R.string.consultas_receber_acao)
                    else stringResource(R.string.consultas_pagar_acao),
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.errorMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    OutlinedTextField(
                        value = uiState.valorInput,
                        onValueChange = onValorChange,
                        label = { Text(stringResource(R.string.form_preco)) },
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmBaixa, enabled = !uiState.isSaving) {
                    Text(stringResource(R.string.voice_review_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissBaixa) {
                    Text(stringResource(R.string.form_cancel))
                }
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (uiState.isLoading) {
            ListLoadingState()
            return
        }
        Text(uiState.nome, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            text = formatCurrency(uiState.saldoTotal),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBaixaTotal,
            enabled = uiState.documentos.isNotEmpty(),
        ) {
            Text(
                if (receber) stringResource(R.string.consultas_receber_acao)
                else stringResource(R.string.consultas_pagar_acao),
            )
        }
        if (uiState.documentos.isEmpty()) {
            ListEmptyState(
                message = if (receber) {
                    stringResource(R.string.consultas_receber_empty)
                } else {
                    stringResource(R.string.consultas_pagar_empty)
                },
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.documentos, key = { it.documentoId }) { doc ->
                    ListItemCard(
                        title = formatDate(doc.data),
                        subtitle = formatCurrency(doc.valorTotalCentavos),
                        trailing = formatCurrency(doc.saldoCentavos),
                        onClick = { onBaixaDocumento(doc.documentoId) },
                    )
                }
            }
        }
    }
}
