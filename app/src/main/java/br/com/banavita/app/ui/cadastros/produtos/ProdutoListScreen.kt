package br.com.banavita.app.ui.cadastros.produtos

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
import java.util.UUID

@Composable
fun ProdutoListRoute(
    onAddClick: () -> Unit = {},
    onItemClick: (UUID) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProdutoListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    ProdutoListScreen(
        uiState = uiState,
        modifier = modifier,
        onAddClick = onAddClick,
        onItemClick = onItemClick,
    )
}

@Composable
fun ProdutoListScreen(
    uiState: ProdutoListUiState,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onItemClick: (UUID) -> Unit = {},
) {
    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = VoiceScreenContext.PRODUTO,
        addContentDescription = stringResource(R.string.produtos_add),
        onAddClick = onAddClick,
    ) { contentModifier ->
        EntityListContent(
            modifier = contentModifier,
            items = uiState.items,
            isLoading = uiState.isLoading,
            emptyMessage = stringResource(R.string.produtos_empty),
            key = { it.id },
            contentPadding = VoiceListContentPadding,
        ) { produto ->
            ProdutoListItem(
                produto = produto,
                onClick = { onItemClick(produto.id) },
            )
        }
    }
}

@Composable
private fun ProdutoListItem(produto: ProdutoListItem, onClick: () -> Unit) {
    val subtitle = buildString {
        append(produto.unidade)
        produto.grupoNome?.let { append(" • $it") }
    }
    ListItemCard(
        title = produto.descricao,
        subtitle = subtitle,
        trailing = produto.precoFormatado,
        onClick = onClick,
    )
}
