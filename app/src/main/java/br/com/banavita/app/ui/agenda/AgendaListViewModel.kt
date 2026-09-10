package br.com.banavita.app.ui.agenda

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.core.time.MonthCalendar
import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.data.repository.EventoRepository
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AgendaListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    eventoRepository: EventoRepository,
) : ViewModel() {

    private val zone: ZoneId = ZoneId.systemDefault()
    private val today: LocalDate = LocalDate.now(zone)
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale("pt", "BR"))

    private val calendar = MutableStateFlow(initialCalendar(savedStateHandle))

    val uiState: StateFlow<AgendaUiState> = combine(
        calendar,
        eventoRepository.observeAll(),
    ) { cal, eventos ->
        val grid = MonthCalendar.gridDates(cal.visibleMonth)
        val counts = eventos.groupingBy { epochToLocalDate(it.dataHora) }.eachCount()
        val dayRange = PeriodRanges.day(cal.selectedDate, zone)
        val dayEvents = eventos
            .filter { it.dataHora in dayRange }
            .sortedWith(compareBy({ it.dataHora }, { it.titulo }))
        val cells = grid.map { date ->
            AgendaDayCell(
                epochDay = date.toEpochDay(),
                dayOfMonth = date.dayOfMonth,
                inMonth = YearMonth.from(date) == cal.visibleMonth,
                isToday = date == today,
                isSelected = date == cal.selectedDate,
                eventCount = counts[date] ?: 0,
            )
        }
        AgendaUiState(
            visibleMonth = cal.visibleMonth,
            selectedEpochDay = cal.selectedDate.toEpochDay(),
            monthTitle = monthTitleFormatter.format(cal.visibleMonth),
            cells = cells,
            eventosDoDia = dayEvents,
            showMonthHint = cal.showMonthHint,
            showMonthPicker = cal.showMonthPicker,
            pickerYear = cal.pickerYear,
            isLoading = false,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        AgendaUiState(
            visibleMonth = calendar.value.visibleMonth,
            selectedEpochDay = calendar.value.selectedDate.toEpochDay(),
            monthTitle = monthTitleFormatter.format(calendar.value.visibleMonth),
            showMonthHint = calendar.value.showMonthHint,
            pickerYear = calendar.value.pickerYear,
        ),
    )

    fun onSelectDay(epochDay: Long) {
        val date = LocalDate.ofEpochDay(epochDay)
        calendar.update {
            it.copy(
                selectedDate = date,
                visibleMonth = YearMonth.from(date),
                pickerYear = YearMonth.from(date).year,
            )
        }
    }

    fun onPreviousMonth() = shiftMonth(-1)

    fun onNextMonth() = shiftMonth(1)

    fun onOpenMonthPicker() {
        calendar.update { it.copy(showMonthPicker = true, pickerYear = it.visibleMonth.year) }
    }

    fun onDismissMonthPicker() {
        calendar.update { it.copy(showMonthPicker = false) }
    }

    fun onPickerYear(delta: Int) {
        calendar.update { it.copy(pickerYear = (it.pickerYear + delta).coerceIn(2000, 2100)) }
    }

    fun onPickMonth(month: Int) {
        val yearMonth = YearMonth.of(calendar.value.pickerYear, month)
        calendar.update {
            it.copy(
                visibleMonth = yearMonth,
                selectedDate = dateInMonth(it.selectedDate, yearMonth),
                showMonthPicker = false,
                pickerYear = yearMonth.year,
            )
        }
    }

    fun consumeMonthHint() {
        calendar.update { it.copy(showMonthHint = false) }
    }

    fun applyVoice(result: ConfirmResult) {
        when (result) {
            is ConfirmResult.AgendaFiltroAplicado -> focusDate(
                LocalDate.ofEpochDay(result.epochDay),
                showHint = result.monthOverviewHint,
            )
            is ConfirmResult.EventoCriado -> focusDate(epochToLocalDate(result.dataHoraMillis))
            else -> Unit
        }
    }

    private fun shiftMonth(deltaMonths: Long) {
        val yearMonth = calendar.value.visibleMonth.plusMonths(deltaMonths)
        calendar.update {
            it.copy(
                visibleMonth = yearMonth,
                selectedDate = dateInMonth(it.selectedDate, yearMonth),
                pickerYear = yearMonth.year,
            )
        }
    }

    private fun focusDate(date: LocalDate, showHint: Boolean = false) {
        calendar.update {
            it.copy(
                selectedDate = date,
                visibleMonth = YearMonth.from(date),
                showMonthHint = showHint,
                pickerYear = YearMonth.from(date).year,
            )
        }
    }

    private fun epochToLocalDate(epochMillis: Long): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(zone).toLocalDate()

    private fun initialCalendar(savedStateHandle: SavedStateHandle): AgendaCalendarState {
        val epoch = savedStateHandle.get<String>(BanavitaRoutes.ARG_AGENDA_DATE)?.toLongOrNull()
        val selected = epoch?.let(LocalDate::ofEpochDay) ?: today
        return AgendaCalendarState(
            visibleMonth = YearMonth.from(selected),
            selectedDate = selected,
            showMonthHint = savedStateHandle.get<String>(BanavitaRoutes.ARG_AGENDA_HINT) == "1",
            pickerYear = YearMonth.from(selected).year,
        )
    }

    private fun dateInMonth(preferred: LocalDate, month: YearMonth): LocalDate {
        val day = preferred.dayOfMonth.coerceAtMost(month.lengthOfMonth())
        return month.atDay(day)
    }

    private data class AgendaCalendarState(
        val visibleMonth: YearMonth,
        val selectedDate: LocalDate,
        val showMonthHint: Boolean,
        val showMonthPicker: Boolean = false,
        val pickerYear: Int,
    )
}
