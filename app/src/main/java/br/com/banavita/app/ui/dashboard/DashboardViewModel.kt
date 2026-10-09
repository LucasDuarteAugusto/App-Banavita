package br.com.banavita.app.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.*
import br.com.banavita.app.domain.model.*
import br.com.banavita.app.ui.common.DateInputParser
import br.com.banavita.app.ui.common.MoneyInputParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class DashboardUiState(
    val kind: DashboardKind = DashboardKind.GERAL,
    val filter: DashboardFilter = DashboardFilter(),
    val data: DashboardData = DashboardData(),
    val products: List<Produto> = emptyList(),
    val clients: List<Cliente> = emptyList(),
    val suppliers: List<Fornecedor> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: DashboardRepository,
    products: ProdutoRepository,
    clients: ClienteRepository,
    suppliers: FornecedorRepository,
) : ViewModel() {
    val kind = savedStateHandle.get<String>("kind")?.let(DashboardKind::valueOf) ?: DashboardKind.GERAL
    private val initial = DashboardFilter(period = when (kind) {
        DashboardKind.GERAL, DashboardKind.AGENDA -> DashboardPeriod.HOJE
        DashboardKind.ENCOMENDAS, DashboardKind.FINANCEIRO -> DashboardPeriod.DIAS_7
        else -> DashboardPeriod.DIAS_30
    }, unit = if (kind == DashboardKind.ESTOQUE) "KG" else null)
    private val filter = MutableStateFlow(initial)
    private val refresh = MutableStateFlow(0)
    private val actionError = MutableStateFlow<String?>(null)
    private val clock = flow {
        while (true) {
            emit(LocalDate.now())
            delay(60_000)
        }
    }.distinctUntilChanged().onEach {
        runCatching { repository.refreshStockSnapshot(it) }.onFailure { error -> actionError.value = error.message }
    }
    private val result = combine(filter, refresh, clock) { f, _, day -> f to day }.flatMapLatest { (f, day) ->
        flow {
            emit(DashboardUiState(kind = kind, filter = f))
            emitAll(repository.observe(kind, f, day).map { DashboardUiState(kind = kind, filter = f, data = it, isLoading = false) })
        }.catch { emit(DashboardUiState(kind = kind, filter = f, isLoading = false, error = it.message ?: "Não foi possível carregar os indicadores.")) }
    }
    val uiState: StateFlow<DashboardUiState> = combine(result, products.observeAll(), clients.observeAll(), suppliers.observeAll(), actionError) { data, p, c, s, error ->
        data.copy(products = p, clients = c, suppliers = s, error = error ?: data.error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState(kind = kind, filter = initial))

    fun changeFilter(value: DashboardFilter) { actionError.value = null; filter.value = value }
    fun retry() { actionError.value = null; refresh.value++ }
    fun saveCash(date: String, amount: String, onSuccess: () -> Unit) {
        val day = DateInputParser.parseToEpochMillis(date)
        val cents = MoneyInputParser.parseToCentavos(amount)
        if (day == null || cents == null) { actionError.value = "Informe data e saldo válidos."; return }
        val todayEnd = br.com.banavita.app.core.time.PeriodRanges.startOfDay(LocalDate.now().plusDays(1), java.time.ZoneId.systemDefault())
        if (day >= todayEnd) { actionError.value = "A data base não pode estar no futuro."; return }
        viewModelScope.launch { runCatching { repository.setCashBase(day, cents) }.onSuccess { onSuccess() }.onFailure { actionError.value = it.message } }
    }
    fun completeTask(id: String, complete: Boolean) {
        viewModelScope.launch { runCatching { repository.completeTask(UUID.fromString(id), complete) }.onFailure { actionError.value = it.message } }
    }
}
