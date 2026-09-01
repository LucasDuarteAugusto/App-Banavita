package br.com.banavita.app.ui.vendas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.EntityListContent
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.voice.VoiceEnabledScreen
import br.com.banavita.app.ui.voice.VoiceListContentPadding

@Composable
fun VendaListRoute(
    onAddClick: () -> Unit = {},
    onItemClick: (java.util.UUID) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: VendaListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    VendaListScreen(
        uiState = uiState,
        modifier = modifier,
        onAddClick = onAddClick,
        onItemClick = onItemClick,
    )
}

@Composable
fun VendaListScreen(
    uiState: VendaListUiState,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onItemClick: (java.util.UUID) -> Unit = {},
) {
    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = VoiceScreenContext.VENDA,
        addContentDescription = stringResource(R.string.vendas_new_button),
        onAddClick = onAddClick,
    ) { contentModifier ->
        EntityListContent(
            modifier = contentModifier,
            items = uiState.items,
            isLoading = uiState.isLoading,
            emptyMessage = stringResource(R.string.vendas_empty_message),
            key = { it.id },
            contentPadding = VoiceListContentPadding,
        ) { venda ->
            VendaListItem(
                venda = venda,
                onClick = { onItemClick(venda.id) },
            )
        }
    }
}

@Composable
private fun VendaListItem(venda: VendaListItem, onClick: () -> Unit) {
    ListItemCard(
        title = venda.valorTotalFormatado,
        subtitle = "${venda.dataFormatada} • ${venda.clienteNome} • ${venda.itensResumo}",
        onClick = onClick,
    )
}
