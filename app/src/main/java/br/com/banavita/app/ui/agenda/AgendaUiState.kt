package br.com.banavita.app.ui.agenda

import br.com.banavita.app.domain.model.AgendaRecorte
import br.com.banavita.app.domain.model.Evento

data class AgendaUiState(
    val recorte: AgendaRecorte = AgendaRecorte.HOJE,
    val eventos: List<Evento> = emptyList(),
    val isLoading: Boolean = true,
)
