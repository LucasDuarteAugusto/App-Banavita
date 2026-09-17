package br.com.banavita.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.repository.CompraRepository
import br.com.banavita.app.data.repository.EncomendaRepository
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.EventoRepository
import br.com.banavita.app.data.repository.FinanceiroRepository
import br.com.banavita.app.data.repository.LoteRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.data.repository.VendaRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.EncomendaStatus
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.PeriodoPreset
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.ui.common.MoneyInputParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    estoqueRepository: EstoqueRepository,
    vendaRepository: VendaRepository,
    compraRepository: CompraRepository,
    financeiroRepository: FinanceiroRepository,
    eventoRepository: EventoRepository,
    private val loteRepository: LoteRepository,
    encomendaRepository: EncomendaRepository,
    produtoRepository: ProdutoRepository,
) : ViewModel() {

    private val _navEvents = MutableSharedFlow<HomeNavEvent>(extraBufferCapacity = 1)
    val navEvents = _navEvents.asSharedFlow()

    private val queima = MutableStateFlow(QueimaUi())

    private val master = combine(
        estoqueRepository.observeAll(),
        vendaRepository.observeAll(),
        compraRepository.observeAll(),
        financeiroRepository.observeSaldosAReceber(),
        financeiroRepository.observeSaldosAPagar(),
    ) { estoques, vendas, compras, receber, pagar ->
        val hoje = PeriodRanges.of(PeriodoPreset.HOJE)
        HomeUiState(
            zerados = estoques.count { it.saldo <= 1e-9 },
            vendasHojeCentavos = vendas.filter { it.data in hoje }.sumOf { it.valorTotalCentavos },
            comprasHojeCentavos = compras.filter { it.data in hoje }.sumOf { it.valorTotalCentavos },
            receberCentavos = receber.sumOf { it.saldoCentavos },
            pagarCentavos = pagar.sumOf { it.saldoCentavos },
            isLoading = false,
        )
    }

    private val criticos = loteRepository.observeLinhas().mapLatest {
        val resumo = loteRepository.resumoCriticos()
        val preco = resumo.produtoIdPintada
            ?.let { produtoRepository.getById(it) }
            ?.precoSugerido(MaturacaoEstado.PINTADA)
            ?: 0L
        Triple(resumo, preco, 0)
    }

    private val encomendasSemana = encomendaRepository.observeAll().mapLatest { list ->
        val semana = PeriodRanges.of(PeriodoPreset.SEMANA)
        list.count { it.status == EncomendaStatus.ABERTO && it.dataEntrega in semana }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        master,
        eventoRepository.observeFrom(nowEpochMillis()),
        criticos,
        encomendasSemana,
        queima,
    ) { base, eventos, perc, encCount, queimaUi ->
        val (resumo, preco) = perc.first to perc.second
        base.copy(
            proximoEvento = eventos.firstOrNull(),
            kgPintada = resumo.kgPintada,
            descricaoPintada = resumo.descricaoPintada,
            produtoIdPintada = resumo.produtoIdPintada,
            precoQueimaSugeridoCentavos = preco,
            kgPodreOuAmanha = resumo.kgPodreOuAmanha,
            descricaoPodre = resumo.descricaoPodre,
            moverParaArmazenagem = resumo.moverParaArmazenagem,
            encomendasAbertasSemana = encCount,
            showQueimaDialog = queimaUi.visible,
            queimaPrecoInput = queimaUi.precoInput,
            queimaError = queimaUi.error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onPereciveisClick() {
        _navEvents.tryEmit(
            HomeNavEvent.Consultas(modo = ConsultaModo.ESTOQUE, criticos = true),
        )
    }

    fun onEncomendasClick() {
        _navEvents.tryEmit(
            HomeNavEvent.Consultas(modo = ConsultaModo.ENCOMENDAS, periodo = PeriodoPreset.SEMANA),
        )
    }

    fun onQueimaClick() {
        val state = uiState.value
        queima.value = QueimaUi(
            visible = true,
            precoInput = MoneyInputParser.formatForInput(state.precoQueimaSugeridoCentavos),
        )
    }

    fun onQueimaPreco(value: String) {
        queima.update { it.copy(precoInput = value, error = null) }
    }

    fun dismissQueima() {
        queima.value = QueimaUi()
    }

    fun confirmQueima() {
        val produtoId = uiState.value.produtoIdPintada ?: return
        val cents = MoneyInputParser.parseToCentavos(queima.value.precoInput)
        if (cents == null || cents < 0) {
            queima.update { it.copy(error = "Informe o preço de queima de hoje.") }
            return
        }
        viewModelScope.launch {
            runCatching { loteRepository.aplicarQueima(produtoId, MaturacaoEstado.PINTADA, cents) }
                .onSuccess { queima.value = QueimaUi() }
                .onFailure { error ->
                    queima.update {
                        it.copy(
                            error = (error as? BanavitaDomainException)?.message
                                ?: error.message
                                ?: "Não foi possível aplicar a queima.",
                        )
                    }
                }
        }
    }

    fun applyVoice(result: ConfirmResult) {
        when (result) {
            is ConfirmResult.VendaCriada -> Unit
            is ConfirmResult.EventoCriado -> {
                val epochDay = java.time.Instant.ofEpochMilli(result.dataHoraMillis)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                    .toEpochDay()
                _navEvents.tryEmit(HomeNavEvent.Agenda(epochDay))
            }
            is ConfirmResult.AgendaFiltroAplicado -> _navEvents.tryEmit(
                HomeNavEvent.Agenda(result.epochDay, result.monthOverviewHint),
            )
            is ConfirmResult.ConsultaAplicada -> {
                when {
                    result.navigateNovaVenda -> _navEvents.tryEmit(HomeNavEvent.NovaVenda)
                    result.navigateAgenda -> _navEvents.tryEmit(HomeNavEvent.Agenda())
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
                            criticos = result.somenteCriticos,
                        ),
                    )
                }
            }
            else -> Unit
        }
    }

    private data class QueimaUi(
        val visible: Boolean = false,
        val precoInput: String = "",
        val error: String? = null,
    )
}
