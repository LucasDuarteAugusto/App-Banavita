package br.com.banavita.app.ui.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.repository.EventoRepository
import br.com.banavita.app.domain.model.AgendaRecorte
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AgendaListViewModel @Inject constructor(
    eventoRepository: EventoRepository,
) : ViewModel() {

    private val recorte = MutableStateFlow(AgendaRecorte.HOJE)

    val uiState: StateFlow<AgendaUiState> = combine(
        recorte,
        eventoRepository.observeAll(),
    ) { current, eventos ->
        val now = nowEpochMillis()
        val filtered = when (current) {
            AgendaRecorte.HOJE -> {
                val range = PeriodRanges.of(br.com.banavita.app.domain.model.PeriodoPreset.HOJE, now)
                eventos.filter { it.dataHora in range }
            }
            AgendaRecorte.PROXIMOS_7_DIAS -> {
                val range = PeriodRanges.next7Days(now)
                eventos.filter { it.dataHora in range }
            }
            AgendaRecorte.TODOS -> eventos
        }
        AgendaUiState(recorte = current, eventos = filtered, isLoading = false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AgendaUiState())

    fun onRecorte(value: AgendaRecorte) {
        recorte.value = value
    }

    fun applyVoice(result: ConfirmResult) {
        if (result is ConfirmResult.AgendaFiltroAplicado) {
            recorte.value = result.recorte
        }
    }
}
