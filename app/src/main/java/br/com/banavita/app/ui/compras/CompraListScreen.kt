package br.com.banavita.app.ui.compras

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
fun CompraListRoute(
    onAddClick: () -> Unit = {},
    onItemClick: (java.util.UUID) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CompraListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    CompraListScreen(
        uiState = uiState,
        modifier = modifier,
        onAddClick = onAddClick,
        onItemClick = onItemClick,
    )
}

@Composable
fun CompraListScreen(
    uiState: CompraListUiState,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onItemClick: (java.util.UUID) -> Unit = {},
) {
    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = VoiceScreenContext.COMPRA,
        addContentDescription = stringResource(R.string.compras_new_button),
        onAddClick = onAddClick,
    ) { contentModifier ->
        EntityListContent(
            modifier = contentModifier,
            items = uiState.items,
            isLoading = uiState.isLoading,
            emptyMessage = stringResource(R.string.compras_empty_message),
            key = { it.id },
            contentPadding = VoiceListContentPadding,
        ) { compra ->
            CompraListItem(
                compra = compra,
                onClick = { onItemClick(compra.id) },
            )
        }
    }
}

@Composable
private fun CompraListItem(compra: CompraListItem, onClick: () -> Unit) {
    ListItemCard(
        title = compra.valorTotalFormatado,
        subtitle = "${compra.dataFormatada} • ${compra.fornecedorNome} • ${compra.itensResumo}",
        onClick = onClick,
    )
}
