package br.com.banavita.app.ui.consultas.estoque

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.EstoqueMovimentoTipo
import br.com.banavita.app.ui.common.ListEmptyState
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.common.ListLoadingState
import br.com.banavita.app.ui.common.formatDate
import br.com.banavita.app.ui.common.formatQuantity
import java.util.UUID

@Composable
fun EstoqueDetalheRoute(
    onEditProduto: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EstoqueDetalheViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    EstoqueDetalheScreen(
        uiState = uiState,
        onEditProduto = { uiState.produtoId?.let(onEditProduto) },
        modifier = modifier,
    )
}

@Composable
fun EstoqueDetalheScreen(
    uiState: EstoqueDetalheUiState,
    onEditProduto: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onEditProduto,
        ) {
            Text(stringResource(R.string.estoque_detalhe_cadastro))
        }
        Text(
            text = stringResource(R.string.estoque_detalhe_movimentos),
            style = MaterialTheme.typography.titleMedium,
        )
        if (uiState.movimentos.isEmpty()) {
            ListEmptyState(message = stringResource(R.string.estoque_detalhe_sem_movimento))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.movimentos, key = { it.documentoId.toString() + it.tipo.name + it.data }) { movimento ->
                    val tipo = if (movimento.tipo == EstoqueMovimentoTipo.ENTRADA) {
                        stringResource(R.string.estoque_movimento_entrada)
                    } else {
                        stringResource(R.string.estoque_movimento_saida)
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
