package br.com.banavita.app.ui.cadastros.fornecedores

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.Fornecedor
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.EntityListContent
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.voice.VoiceEnabledScreen
import br.com.banavita.app.ui.voice.VoiceListContentPadding
import java.util.UUID

@Composable
fun FornecedorListRoute(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onItemClick: (UUID) -> Unit = {},
    viewModel: FornecedorListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    FornecedorListScreen(
        uiState = uiState,
        modifier = modifier,
        onAddClick = onAddClick,
        onItemClick = onItemClick,
    )
}

@Composable
fun FornecedorListScreen(
    uiState: FornecedorListUiState,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onItemClick: (UUID) -> Unit = {},
) {
    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = VoiceScreenContext.FORNECEDOR,
        addContentDescription = stringResource(R.string.fornecedores_add),
        onAddClick = onAddClick,
    ) { contentModifier ->
        EntityListContent(
            modifier = contentModifier,
            items = uiState.items,
            isLoading = uiState.isLoading,
            emptyMessage = stringResource(R.string.fornecedores_empty),
            key = { it.id },
            contentPadding = VoiceListContentPadding,
        ) { fornecedor ->
            FornecedorListItem(
                fornecedor = fornecedor,
                onClick = { onItemClick(fornecedor.id) },
            )
        }
    }
}

@Composable
private fun FornecedorListItem(fornecedor: Fornecedor, onClick: () -> Unit) {
    ListItemCard(
        title = fornecedor.nome,
        subtitle = fornecedor.telefone ?: stringResource(R.string.list_no_phone),
        onClick = onClick,
    )
}
