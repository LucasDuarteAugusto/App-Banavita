package br.com.banavita.app.ui.cadastros.grupos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.Grupo
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.EntityListContent
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.voice.VoiceEnabledScreen
import br.com.banavita.app.ui.voice.VoiceListContentPadding
import java.util.UUID

@Composable
fun GrupoListRoute(
    onAddClick: () -> Unit = {},
    onItemClick: (UUID) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: GrupoListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    GrupoListScreen(
        uiState = uiState,
        modifier = modifier,
        onAddClick = onAddClick,
        onItemClick = onItemClick,
    )
}

@Composable
fun GrupoListScreen(
    uiState: GrupoListUiState,
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onItemClick: (UUID) -> Unit = {},
) {
    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = VoiceScreenContext.GRUPO,
        addContentDescription = stringResource(R.string.grupos_add),
        onAddClick = onAddClick,
    ) { contentModifier ->
        EntityListContent(
            modifier = contentModifier,
            items = uiState.items,
            isLoading = uiState.isLoading,
            emptyMessage = stringResource(R.string.grupos_empty),
            key = { it.id },
            contentPadding = VoiceListContentPadding,
        ) { grupo ->
            GrupoListItem(
                grupo = grupo,
                onClick = { onItemClick(grupo.id) },
            )
        }
    }
}

@Composable
private fun GrupoListItem(grupo: Grupo, onClick: () -> Unit) {
    ListItemCard(
        title = grupo.nome,
        subtitle = grupo.observacao ?: stringResource(R.string.list_no_observation),
        onClick = onClick,
    )
}
