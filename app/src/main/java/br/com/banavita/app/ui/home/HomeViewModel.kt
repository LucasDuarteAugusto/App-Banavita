package br.com.banavita.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.repository.CompraRepository
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.EventoRepository
import br.com.banavita.app.data.repository.FinanceiroRepository
import br.com.banavita.app.data.repository.VendaRepository
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    estoqueRepository: EstoqueRepository,
    vendaRepository: VendaRepository,
    compraRepository: CompraRepository,
    financeiroRepository: FinanceiroRepository,
    eventoRepository: EventoRepository,
) : ViewModel() {

    private val _navEvents = MutableSharedFlow<HomeNavEvent>(extraBufferCapacity = 1)
    val navEvents = _navEvents.asSharedFlow()

    private val master = combine(
        estoqueRepository.observeAll(),
        vendaRepository.observeAll(),
        compraRepository.observeAll(),
        financeiroRepository.observeSaldosAReceber(),
        financeiroRepository.observeSaldosAPagar(),
    ) { estoques, vendas, compras, receber, pagar ->
        val hoje = PeriodRanges.of(br.com.banavita.app.domain.model.PeriodoPreset.HOJE)
        HomeUiState(
            zerados = estoques.count { it.saldo <= 1e-9 },
            vendasHojeCentavos = vendas.filter { it.data in hoje }.sumOf { it.valorTotalCentavos },
            comprasHojeCentavos = compras.filter { it.data in hoje }.sumOf { it.valorTotalCentavos },
            receberCentavos = receber.sumOf { it.saldoCentavos },
            pagarCentavos = pagar.sumOf { it.saldoCentavos },
            isLoading = false,
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        master,
        eventoRepository.observeFrom(nowEpochMillis()),
    ) { base, eventos ->
        base.copy(proximoEvento = eventos.firstOrNull())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun applyVoice(result: ConfirmResult) {
        when (result) {
            is ConfirmResult.VendaCriada -> Unit
            is ConfirmResult.EventoCriado -> _navEvents.tryEmit(HomeNavEvent.Agenda)
            is ConfirmResult.AgendaFiltroAplicado -> _navEvents.tryEmit(HomeNavEvent.Agenda)
            is ConfirmResult.ConsultaAplicada -> {
                when {
                    result.navigateNovaVenda -> _navEvents.tryEmit(HomeNavEvent.NovaVenda)
                    result.navigateAgenda -> _navEvents.tryEmit(HomeNavEvent.Agenda)
                    result.openProdutoId != null ->
                        _navEvents.tryEmit(HomeNavEvent.EstoqueDetalhe(result.openProdutoId))
                    result.openClienteId != null ->
                        _navEvents.tryEmit(HomeNavEvent.ReceberDetalhe(result.openClienteId))
                    result.openFornecedorId != null ->
                        _navEvents.tryEmit(HomeNavEvent.PagarDetalhe(result.openFornecedorId))
                    else -> _navEvents.tryEmit(
                        HomeNavEvent.Consultas(
                            modo = result.modo ?: ConsultaModo.ESTOQUE,
                            busca = result.busca,
                            periodo = result.periodo,
                        ),
                    )
                }
            }
            else -> Unit
        }
    }
}
