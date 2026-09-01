package br.com.banavita.app.ui.cadastros.clientes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.Cliente
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.EntityListContent
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.voice.VoiceEnabledScreen
import br.com.banavita.app.ui.voice.VoiceListContentPadding
import java.util.UUID

@Composable
fun ClienteListRoute(
    onAddClick: () -> Unit = {},
    onItemClick: (UUID) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ClienteListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    ClienteListScreen(
        uiState = uiState,
        modifier = modifier,
        onAddClick = onAddClick,
        onItemClick = onItemClick,
    )
}

@Composable
fun ClienteListScreen(
    uiState: ClienteListUiState,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onItemClick: (UUID) -> Unit = {},
) {
    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = VoiceScreenContext.CLIENTE,
        addContentDescription = stringResource(R.string.clientes_add),
        onAddClick = onAddClick,
    ) { contentModifier ->
        EntityListContent(
            modifier = contentModifier,
            items = uiState.items,
            isLoading = uiState.isLoading,
            emptyMessage = stringResource(R.string.clientes_empty),
            key = { it.id },
            contentPadding = VoiceListContentPadding,
        ) { cliente ->
            ClienteListItem(
                cliente = cliente,
                onClick = { onItemClick(cliente.id) },
            )
        }
    }
}

@Composable
private fun ClienteListItem(cliente: Cliente, onClick: () -> Unit) {
    ListItemCard(
        title = cliente.nome,
        subtitle = cliente.telefone ?: stringResource(R.string.list_no_phone),
        onClick = onClick,
    )
}
