package br.com.banavita.app.ui.consultas.estoque

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.EstoqueMovimentoTipo
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.ui.common.EstadoChips
import br.com.banavita.app.ui.common.ListEmptyState
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.common.ListLoadingState
import br.com.banavita.app.ui.common.QueimaPrecoDialog
import br.com.banavita.app.ui.common.formatDate
import br.com.banavita.app.ui.common.formatQuantity
import br.com.banavita.app.ui.common.labelRes
import java.util.UUID

@Composable
fun EstoqueDetalheRoute(
    onEditProduto: (UUID) -> Unit,
    onLancarPerda: (loteId: UUID, produtoId: UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EstoqueDetalheViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    EstoqueDetalheScreen(
        uiState = uiState,
        onEditProduto = { uiState.produtoId?.let(onEditProduto) },
        onSoltar = viewModel::onSoltarClick,
        onCorrigir = viewModel::onCorrigirClick,
        onPerda = { loteId ->
            uiState.produtoId?.let { onLancarPerda(loteId, it) }
        },
        onQueima = viewModel::onQueimaClick,
        onTransferQty = viewModel::onTransferQty,
        onConfirmTransfer = viewModel::confirmTransfer,
        onDismissTransfer = viewModel::dismissTransfer,
        onCorrigirEstado = viewModel::corrigirEstado,
        onDismissCorrigir = viewModel::dismissCorrigir,
        onQueimaPreco = viewModel::onQueimaPreco,
        onConfirmQueima = viewModel::confirmQueima,
        onDismissQueima = viewModel::dismissQueima,
        modifier = modifier,
    )
}

@Composable
fun EstoqueDetalheScreen(
    uiState: EstoqueDetalheUiState,
    onEditProduto: () -> Unit,
    onSoltar: (UUID) -> Unit,
    onCorrigir: (UUID) -> Unit,
    onPerda: (UUID) -> Unit,
    onQueima: () -> Unit,
    onTransferQty: (String) -> Unit,
    onConfirmTransfer: () -> Unit,
    onDismissTransfer: () -> Unit,
    onCorrigirEstado: (MaturacaoEstado) -> Unit,
    onDismissCorrigir: () -> Unit,
    onQueimaPreco: (String) -> Unit,
    onConfirmQueima: () -> Unit,
    onDismissQueima: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.transferLoteId != null) {
        AlertDialog(
            onDismissRequest = onDismissTransfer,
            title = { Text(stringResource(R.string.estoque_soltar_etileno)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.transferQtyInput,
                        onValueChange = onTransferQty,
                        label = { Text(stringResource(R.string.estoque_soltar_qtde)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmTransfer) {
                    Text(stringResource(R.string.etileno_soltar))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissTransfer) {
                    Text(stringResource(R.string.form_cancel))
                }
            },
        )
    }
    if (uiState.corrigirLoteId != null) {
        AlertDialog(
            onDismissRequest = onDismissCorrigir,
            title = { Text(stringResource(R.string.estoque_corrigir_estado)) },
            text = {
                EstadoChips(
                    selected = uiState.lotes.firstOrNull { it.lote.id == uiState.corrigirLoteId }?.lote?.estado,
                    onSelected = onCorrigirEstado,
                    includePodre = true,
                )
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismissCorrigir) {
                    Text(stringResource(R.string.form_cancel))
                }
            },
        )
    }
    if (uiState.showQueima) {
        QueimaPrecoDialog(
            precoInput = uiState.queimaPrecoInput,
            onPrecoChange = onQueimaPreco,
            onConfirm = onConfirmQueima,
            onDismiss = onDismissQueima,
            errorMessage = uiState.errorMessage,
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
        Text(
            text = uiState.descricao,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.estoque_detalhe_saldo, uiState.saldoFormatado),
            style = MaterialTheme.typography.titleMedium,
        )
        uiState.errorMessage
            ?.takeIf { uiState.transferLoteId == null && !uiState.showQueima }
            ?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onEditProduto,
        ) {
            Text(stringResource(R.string.estoque_detalhe_cadastro))
        }
        if (uiState.perecivel && uiState.lotes.any { it.lote.estado == MaturacaoEstado.PINTADA }) {
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onQueima,
            ) {
                Text(stringResource(R.string.estoque_aplicar_queima))
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (uiState.perecivel) {
                item {
                    Text(
                        text = stringResource(R.string.estoque_lotes_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (uiState.lotes.isEmpty()) {
                    item { ListEmptyState(message = stringResource(R.string.estoque_lotes_empty)) }
                } else {
                    items(uiState.lotes, key = { it.lote.id }) { item ->
                        LoteCard(
                            item = item,
                            onSoltar = { onSoltar(item.lote.id) },
                            onCorrigir = { onCorrigir(item.lote.id) },
                            onPerda = { onPerda(item.lote.id) },
                        )
                    }
                }
            }
            item {
                Text(
                    text = stringResource(R.string.estoque_detalhe_movimentos),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (uiState.movimentos.isEmpty()) {
                item { ListEmptyState(message = stringResource(R.string.estoque_detalhe_sem_movimento)) }
            } else {
                items(
                    uiState.movimentos,
                    key = { it.documentoId.toString() + it.tipo.name + it.data },
                ) { movimento ->
                    val tipo = when (movimento.tipo) {
                        EstoqueMovimentoTipo.ENTRADA -> stringResource(R.string.estoque_movimento_entrada)
                        EstoqueMovimentoTipo.SAIDA -> stringResource(R.string.estoque_movimento_saida)
                        EstoqueMovimentoTipo.PERDA -> stringResource(R.string.estoque_movimento_perda)
                        EstoqueMovimentoTipo.TRANSFERENCIA ->
                            stringResource(R.string.estoque_movimento_transferencia)
                    }
                    ListItemCard(
                        title = tipo,
                        subtitle = formatDate(movimento.data),
                        trailing = formatQuantity(movimento.quantidade, uiState.unidade),
                    )
                }
            }
        }
    }
}

@Composable
private fun LoteCard(
    item: EstoqueLoteUi,
    onSoltar: () -> Unit,
    onCorrigir: () -> Unit,
    onPerda: () -> Unit,
) {
    val lote = item.lote
    val subtitle = buildString {
        append(stringResource(lote.estado.labelRes()))
        append(" · ")
        append(stringResource(lote.camara.labelRes()))
        append(" · ")
        append(item.quantidadeFormatada)
        append('\n')
        if (lote.camara == Camara.FRIA) {
            append(stringResource(R.string.estoque_lote_fria_pausa))
        } else {
            val days = item.daysUntilNext
            val next = item.nextEstado
            if (days != null && next != null) {
                append(
                    stringResource(
                        R.string.estoque_lote_vira,
                        stringResource(next.labelRes()),
                        days,
                    ),
                )
            }
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ListItemCard(
            title = lote.rotulo,
            subtitle = subtitle,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (lote.camara == Camara.FRIA) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onSoltar
                    ) {
                        Text(stringResource(R.string.estoque_soltar_etileno))
                    }
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onCorrigir
                ) {
                    Text(stringResource(R.string.estoque_corrigir_estado))
                }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onPerda
            ) {
                Text(stringResource(R.string.estoque_lancar_perda))
            }
        }
    }
}
