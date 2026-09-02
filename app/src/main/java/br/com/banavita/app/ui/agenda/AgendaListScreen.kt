package br.com.banavita.app.ui.agenda

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.AgendaRecorte
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.EntityListContent
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.common.RequestNotificationPermissionIfNeeded
import br.com.banavita.app.ui.common.formatDateTime
import br.com.banavita.app.ui.voice.VoiceEnabledScreen
import br.com.banavita.app.ui.voice.VoiceListContentPadding
import java.util.UUID

@Composable
fun AgendaListRoute(
    onAddClick: () -> Unit,
    onItemClick: (UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AgendaListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    RequestNotificationPermissionIfNeeded()
    AgendaListScreen(
        uiState = uiState,
        onAddClick = onAddClick,
        onItemClick = onItemClick,
        onRecorte = viewModel::onRecorte,
        onVoiceResult = viewModel::applyVoice,
        modifier = modifier,
    )
}

@Composable
fun AgendaListScreen(
    uiState: AgendaUiState,
    onAddClick: () -> Unit,
    onItemClick: (UUID) -> Unit,
    onRecorte: (AgendaRecorte) -> Unit,
    onVoiceResult: (ConfirmResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = VoiceScreenContext.AGENDA,
        addContentDescription = stringResource(R.string.agenda_add),
        onAddClick = onAddClick,
        onVoiceSuccess = onVoiceResult,
    ) { contentModifier ->
        Column(modifier = contentModifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AgendaRecorte.entries.forEach { recorte ->
                    val label = when (recorte) {
                        AgendaRecorte.HOJE -> stringResource(R.string.agenda_recorte_hoje)
                        AgendaRecorte.PROXIMOS_7_DIAS -> stringResource(R.string.agenda_recorte_7)
                        AgendaRecorte.TODOS -> stringResource(R.string.agenda_recorte_todos)
                    }
                    FilterChip(
                        selected = uiState.recorte == recorte,
                        onClick = { onRecorte(recorte) },
                        label = { Text(label) },
                    )
                }
            }
            EntityListContent(
                items = uiState.eventos,
                isLoading = uiState.isLoading,
                emptyMessage = stringResource(R.string.agenda_empty),
                key = { it.id },
                contentPadding = VoiceListContentPadding,
            ) { evento ->
                ListItemCard(
                    title = evento.titulo,
                    subtitle = formatDateTime(evento.dataHora),
                    onClick = { onItemClick(evento.id) },
                )
            }
        }
    }
}
