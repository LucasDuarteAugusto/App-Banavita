package br.com.banavita.app.ui.pereciveis.etileno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import br.com.banavita.app.ui.common.ListEmptyState
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.common.formatQuantity
import br.com.banavita.app.ui.common.labelRes
import java.util.UUID

@Composable
fun EtilenoFilaRoute(
    modifier: Modifier = Modifier,
    viewModel: EtilenoFilaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    EtilenoFilaScreen(
        uiState = uiState,
        onQuantidadeChange = viewModel::onQuantidadeChange,
        onSoltar = viewModel::soltar,
        modifier = modifier,
    )
}

@Composable
fun EtilenoFilaScreen(
    uiState: EtilenoFilaUiState,
    onQuantidadeChange: (UUID, String) -> Unit,
    onSoltar: (UUID) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cap = uiState.capacidade
    val folgaLabel = cap?.folgaKg?.let { formatQuantity(it, "KG") }
        ?: stringResource(R.string.etileno_sem_teto)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (cap != null) {
            Text(
                text = stringResource(
                    R.string.etileno_resumo,
                    formatQuantity(cap.kgNaFria, "KG"),
                    formatQuantity(cap.kgNaEtileno, "KG"),
                    folgaLabel,
                ),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Text(
            text = stringResource(R.string.etileno_regra),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        uiState.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
        uiState.statusMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.primary) }
        if (uiState.lotes.isEmpty()) {
            ListEmptyState(message = stringResource(R.string.etileno_empty))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.lotes, key = { it.id }) { lote ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ListItemCard(
                            title = lote.rotulo,
                            subtitle = stringResource(lote.estado.labelRes()) +
                                " · " + formatQuantity(lote.quantidadeRestante, "KG"),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = uiState.quantidadePorLote[lote.id].orEmpty(),
                                onValueChange = { onQuantidadeChange(lote.id, it) },
                                label = { Text(stringResource(R.string.estoque_soltar_qtde)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            )
                            Button(onClick = { onSoltar(lote.id) }) {
                                Text(stringResource(R.string.etileno_soltar))
                            }
                        }
                    }
                }
            }
        }
    }
}
