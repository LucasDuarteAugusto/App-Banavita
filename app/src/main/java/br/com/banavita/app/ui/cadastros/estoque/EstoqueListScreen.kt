package br.com.banavita.app.ui.cadastros.estoque

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.ui.common.ListEmptyState
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.common.ListLoadingState

@Composable
fun EstoqueListRoute(
    modifier: Modifier = Modifier,
    viewModel: EstoqueListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    EstoqueListScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
fun EstoqueListScreen(
    uiState: EstoqueListUiState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> ListLoadingState()
            uiState.items.isEmpty() -> ListEmptyState(message = stringResource(R.string.estoque_empty))
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.items, key = { it.produtoId }) { item ->
                        ListItemCard(
                            title = item.produtoDescricao,
                            subtitle = stringResource(R.string.estoque_unit_label, item.unidade),
                            trailing = item.saldoFormatado,
                        )
                    }
                }
            }
        }
    }
}
