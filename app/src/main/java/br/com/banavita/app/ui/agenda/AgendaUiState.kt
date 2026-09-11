package br.com.banavita.app.ui.agenda

import br.com.banavita.app.domain.model.Evento
import java.time.YearMonth

data class AgendaDayCell(
    val epochDay: Long,
    val dayOfMonth: Int,
    val inMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val eventCount: Int,
)

data class AgendaUiState(
    val visibleMonth: YearMonth = YearMonth.now(),
    val selectedEpochDay: Long = java.time.LocalDate.now().toEpochDay(),
    val monthTitle: String = "",
    val cells: List<AgendaDayCell> = emptyList(),
    val eventosDoDia: List<Evento> = emptyList(),
    val showMonthHint: Boolean = false,
    val showMonthPicker: Boolean = false,
    val pickerYear: Int = YearMonth.now().year,
    val isLoading: Boolean = true,
)
