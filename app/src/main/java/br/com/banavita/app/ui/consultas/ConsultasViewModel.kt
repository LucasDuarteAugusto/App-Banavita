package br.com.banavita.app.ui.consultas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.data.repository.ClienteRepository
import br.com.banavita.app.data.repository.CompraRepository
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.FinanceiroRepository
import br.com.banavita.app.data.repository.FornecedorRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.data.repository.VendaRepository
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.ConsultaVisao
import br.com.banavita.app.domain.model.EstoqueOrdenacao
import br.com.banavita.app.domain.model.PeriodoPreset
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.ui.common.DateInputParser
import br.com.banavita.app.ui.common.formatCurrency
import br.com.banavita.app.ui.common.formatQuantity
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConsultasViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    estoqueRepository: EstoqueRepository,
    produtoRepository: ProdutoRepository,
    vendaRepository: VendaRepository,
    compraRepository: CompraRepository,
    clienteRepository: ClienteRepository,
    fornecedorRepository: FornecedorRepository,
    financeiroRepository: FinanceiroRepository,
) : ViewModel() {

    private val initialModo = ConsultaModo.fromRoute(
        savedStateHandle.get<String>(BanavitaRoutes.ARG_MODO),
    )
    private val initialBusca = savedStateHandle.get<String>(BanavitaRoutes.ARG_BUSCA).orEmpty()
    private val initialPeriodo = savedStateHandle.get<String>(BanavitaRoutes.ARG_PERIODO)
        ?.takeIf { it.isNotBlank() }
        ?.let { runCatching { PeriodoPreset.valueOf(it) }.getOrNull() }
        ?: PeriodoPreset.HOJE

    private val filters = MutableStateFlow(
        ConsultasFilters(modo = initialModo, busca = initialBusca, periodo = initialPeriodo),
    )

    private val _navEvents = MutableSharedFlow<ConsultasNavEvent>(extraBufferCapacity = 1)
    val navEvents = _navEvents.asSharedFlow()

    private val master = combine(
        estoqueRepository.observeAll(),
        produtoRepository.observeAll(),
        vendaRepository.observeAll(),
        compraRepository.observeAll(),
        clienteRepository.observeAll(),
    ) { estoques, produtos, vendas, compras, clientes ->
        MasterCatalog(estoques, produtos, vendas, compras, clientes)
    }

    private val money = combine(
        fornecedorRepository.observeAll(),
        financeiroRepository.observeSaldosAReceber(),
        financeiroRepository.observeSaldosAPagar(),
    ) { fornecedores, receber, pagar ->
        MoneyCatalog(fornecedores, receber, pagar)
    }

    val uiState: StateFlow<ConsultasUiState> = combine(filters, master, money) { current, masters, moneyData ->
        buildState(
            current,
            Catalogs(
                estoques = masters.estoques,
                produtos = masters.produtos,
                vendas = masters.vendas,
                compras = masters.compras,
                clientes = masters.clientes,
                fornecedores = moneyData.fornecedores,
                receber = moneyData.receber,
                pagar = moneyData.pagar,
            ),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ConsultasUiState(filters = filters.value),
    )

    fun onModo(modo: ConsultaModo) {
        filters.update { it.copy(modo = modo, visao = ConsultaVisao.DOCUMENTOS) }
    }

    fun onBusca(value: String) {
        filters.update { it.copy(busca = value) }
    }

    fun onPeriodo(periodo: PeriodoPreset) {
        filters.update { it.copy(periodo = periodo) }
    }

    fun onVisao(visao: ConsultaVisao) {
        filters.update { it.copy(visao = visao) }
    }

    fun onSomenteZerados(value: Boolean) {
        filters.update { it.copy(somenteZerados = value) }
    }

    fun onOrdenacao(ordenacao: EstoqueOrdenacao) {
        filters.update { it.copy(ordenacaoEstoque = ordenacao) }
    }

    fun onIntervaloInicio(value: String) {
        filters.update { it.copy(intervaloInicioInput = value) }
    }

    fun onIntervaloFim(value: String) {
        filters.update { it.copy(intervaloFimInput = value) }
    }

    fun applyVoice(result: ConfirmResult) {
        when (result) {
            is ConfirmResult.ConsultaAplicada -> {
                filters.update { current ->
                    current.copy(
                        modo = result.modo ?: current.modo,
                        busca = result.busca ?: current.busca,
                        periodo = result.periodo ?: current.periodo,
                        visao = result.visao ?: current.visao,
                        somenteZerados = result.somenteZerados || current.somenteZerados,
                    )
                }
                result.openProdutoId?.let { _navEvents.tryEmit(ConsultasNavEvent.EstoqueDetalhe(it)) }
                result.openClienteId?.let { _navEvents.tryEmit(ConsultasNavEvent.ReceberDetalhe(it)) }
                result.openFornecedorId?.let { _navEvents.tryEmit(ConsultasNavEvent.PagarDetalhe(it)) }
            }
            else -> Unit
        }
    }

    fun onEstoqueClick(produtoId: java.util.UUID) {
        _navEvents.tryEmit(ConsultasNavEvent.EstoqueDetalhe(produtoId))
    }

    fun onDocumentoClick(id: java.util.UUID) {
        when (filters.value.modo) {
            ConsultaModo.VENDAS -> _navEvents.tryEmit(ConsultasNavEvent.Venda(id))
            ConsultaModo.COMPRAS -> _navEvents.tryEmit(ConsultasNavEvent.Compra(id))
            else -> Unit
        }
    }

    fun onFinanceiroClick(parceiroId: java.util.UUID?) {
        val id = parceiroId ?: return
        when (filters.value.modo) {
            ConsultaModo.RECEBER -> _navEvents.tryEmit(ConsultasNavEvent.ReceberDetalhe(id))
            ConsultaModo.PAGAR -> _navEvents.tryEmit(ConsultasNavEvent.PagarDetalhe(id))
            else -> Unit
        }
    }

    private fun buildState(current: ConsultasFilters, data: Catalogs): ConsultasUiState {
        val busca = current.busca.trim()
        val range = PeriodRanges.of(
            preset = current.periodo,
            intervaloInicio = DateInputParser.parseToEpochMillis(current.intervaloInicioInput),
            intervaloFimInclusive = DateInputParser.parseToEpochMillis(current.intervaloFimInput),
        )
        val produtoMap = data.produtos.associateBy { it.id }
        val clienteMap = data.clientes.associateBy { it.id }
        val fornecedorMap = data.fornecedores.associateBy { it.id }

        val estoqueItems = data.estoques.mapNotNull { estoque ->
            val produto = produtoMap[estoque.produtoId] ?: return@mapNotNull null
            if (busca.isNotBlank() && !produto.descricao.contains(busca, ignoreCase = true)) {
                return@mapNotNull null
            }
            if (current.somenteZerados && estoque.saldo > 1e-9) return@mapNotNull null
            EstoqueConsultaItem(
                produtoId = estoque.produtoId,
                descricao = produto.descricao,
                unidade = estoque.unidade,
                saldo = estoque.saldo,
                saldoFormatado = formatQuantity(estoque.saldo, estoque.unidade),
                zerado = estoque.saldo <= 1e-9,
            )
        }.let { list ->
            when (current.ordenacaoEstoque) {
                EstoqueOrdenacao.SALDO_ASC -> list.sortedBy { it.saldo }
                EstoqueOrdenacao.NOME_ASC -> list.sortedBy { it.descricao.lowercase() }
            }
        }

        val vendasPeriodo = data.vendas.filter { it.data in range }
            .filter { venda ->
                if (busca.isBlank()) return@filter true
                val clienteNome = venda.clienteId?.let { clienteMap[it]?.nome }.orEmpty()
                clienteNome.contains(busca, ignoreCase = true) ||
                    venda.itens.any { item ->
                        produtoMap[item.produtoId]?.descricao.orEmpty().contains(busca, ignoreCase = true)
                    }
            }
        val comprasPeriodo = data.compras.filter { it.data in range }
            .filter { compra ->
                if (busca.isBlank()) return@filter true
                val nome = compra.fornecedorId?.let { fornecedorMap[it]?.nome }.orEmpty()
                nome.contains(busca, ignoreCase = true) ||
                    compra.itens.any { item ->
                        produtoMap[item.produtoId]?.descricao.orEmpty().contains(busca, ignoreCase = true)
                    }
            }

        val receber = data.receber.filter {
            busca.isBlank() || it.nome.contains(busca, ignoreCase = true)
        }
        val pagar = data.pagar.filter {
            busca.isBlank() || it.nome.contains(busca, ignoreCase = true)
        }

        return when (current.modo) {
            ConsultaModo.ESTOQUE -> {
                val comSaldo = estoqueItems.count { it.saldo > 1e-9 }
                ConsultasUiState(
                    filters = current,
                    isLoading = false,
                    totalLabel = "Produtos com saldo",
                    totalValue = comSaldo.toString(),
                    estoqueItems = estoqueItems,
                )
            }
            ConsultaModo.VENDAS -> documentoState(
                current = current,
                documentos = vendasPeriodo.map { venda ->
                    DocumentoConsultaItem(
                        id = venda.id,
                        data = venda.data,
                        parceiro = venda.clienteId?.let { clienteMap[it]?.nome } ?: "Cliente avulso",
                        valorCentavos = venda.valorTotalCentavos,
                    )
                },
                agregadosParceiro = vendasPeriodo.groupBy { it.clienteId }.map { (id, rows) ->
                    AgregadoConsultaItem(
                        nome = id?.let { clienteMap[it]?.nome } ?: "Cliente avulso",
                        quantidadeLabel = null,
                        valorCentavos = rows.sumOf { it.valorTotalCentavos },
                    )
                },
                agregadosProduto = vendasPeriodo.flatMap { it.itens }.groupBy { it.produtoId }.map { (id, rows) ->
                    val produto = produtoMap[id]
                    AgregadoConsultaItem(
                        nome = produto?.descricao ?: "Produto",
                        quantidadeLabel = produto?.let { formatQuantity(rows.sumOf { row -> row.quantidade }, it.unidade) },
                        valorCentavos = rows.sumOf { it.valorTotalCentavos },
                    )
                },
                totalLabel = "Total vendido",
            )
            ConsultaModo.COMPRAS -> documentoState(
                current = current,
                documentos = comprasPeriodo.map { compra ->
                    DocumentoConsultaItem(
                        id = compra.id,
                        data = compra.data,
                        parceiro = compra.fornecedorId?.let { fornecedorMap[it]?.nome } ?: "Sem fornecedor",
                        valorCentavos = compra.valorTotalCentavos,
                    )
                },
                agregadosParceiro = comprasPeriodo.groupBy { it.fornecedorId }.map { (id, rows) ->
                    AgregadoConsultaItem(
                        nome = id?.let { fornecedorMap[it]?.nome } ?: "Sem fornecedor",
                        quantidadeLabel = null,
                        valorCentavos = rows.sumOf { it.valorTotalCentavos },
                    )
                },
                agregadosProduto = comprasPeriodo.flatMap { it.itens }.groupBy { it.produtoId }.map { (id, rows) ->
                    val produto = produtoMap[id]
                    AgregadoConsultaItem(
                        nome = produto?.descricao ?: "Produto",
                        quantidadeLabel = produto?.let { formatQuantity(rows.sumOf { row -> row.quantidade }, it.unidade) },
                        valorCentavos = rows.sumOf { it.valorTotalCentavos },
                    )
                },
                totalLabel = "Total comprado",
            )
            ConsultaModo.RECEBER -> ConsultasUiState(
                filters = current,
                isLoading = false,
                totalLabel = "A receber",
                totalValue = formatCurrency(receber.sumOf { it.saldoCentavos }),
                financeiroItems = receber,
            )
            ConsultaModo.PAGAR -> ConsultasUiState(
                filters = current,
                isLoading = false,
                totalLabel = "A pagar",
                totalValue = formatCurrency(pagar.sumOf { it.saldoCentavos }),
                financeiroItems = pagar,
            )
        }
    }

    private fun documentoState(
        current: ConsultasFilters,
        documentos: List<DocumentoConsultaItem>,
        agregadosParceiro: List<AgregadoConsultaItem>,
        agregadosProduto: List<AgregadoConsultaItem>,
        totalLabel: String,
    ): ConsultasUiState {
        val total = documentos.sumOf { it.valorCentavos }
        val agregados = when (current.visao) {
            ConsultaVisao.DOCUMENTOS -> emptyList()
            ConsultaVisao.POR_PARCEIRO -> agregadosParceiro.sortedByDescending { it.valorCentavos }
            ConsultaVisao.POR_PRODUTO -> agregadosProduto.sortedByDescending { it.valorCentavos }
        }
        return ConsultasUiState(
            filters = current,
            isLoading = false,
            totalLabel = totalLabel,
            totalValue = formatCurrency(total),
            documentos = if (current.visao == ConsultaVisao.DOCUMENTOS) documentos.sortedByDescending { it.data } else emptyList(),
            agregados = agregados,
        )
    }

    private data class MasterCatalog(
        val estoques: List<br.com.banavita.app.domain.model.ProdutoEstoque>,
        val produtos: List<br.com.banavita.app.domain.model.Produto>,
        val vendas: List<br.com.banavita.app.domain.model.Venda>,
        val compras: List<br.com.banavita.app.domain.model.Compra>,
        val clientes: List<br.com.banavita.app.domain.model.Cliente>,
    )

    private data class MoneyCatalog(
        val fornecedores: List<br.com.banavita.app.domain.model.Fornecedor>,
        val receber: List<br.com.banavita.app.domain.model.ParceiroSaldo>,
        val pagar: List<br.com.banavita.app.domain.model.ParceiroSaldo>,
    )

    private data class Catalogs(
        val estoques: List<br.com.banavita.app.domain.model.ProdutoEstoque>,
        val produtos: List<br.com.banavita.app.domain.model.Produto>,
        val vendas: List<br.com.banavita.app.domain.model.Venda>,
        val compras: List<br.com.banavita.app.domain.model.Compra>,
        val clientes: List<br.com.banavita.app.domain.model.Cliente>,
        val fornecedores: List<br.com.banavita.app.domain.model.Fornecedor>,
        val receber: List<br.com.banavita.app.domain.model.ParceiroSaldo>,
        val pagar: List<br.com.banavita.app.domain.model.ParceiroSaldo>,
    )
}
