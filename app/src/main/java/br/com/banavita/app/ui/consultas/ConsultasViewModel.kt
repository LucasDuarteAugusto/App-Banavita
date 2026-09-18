package br.com.banavita.app.ui.consultas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.data.repository.ClienteRepository
import br.com.banavita.app.data.repository.CompraRepository
import br.com.banavita.app.data.repository.EncomendaRepository
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.FinanceiroRepository
import br.com.banavita.app.data.repository.FornecedorRepository
import br.com.banavita.app.data.repository.LoteRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.data.repository.VendaRepository
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.ConsultaVisao
import br.com.banavita.app.domain.model.EncomendaStatus
import br.com.banavita.app.domain.model.EstoqueLinha
import br.com.banavita.app.domain.model.EstoqueOrdenacao
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.PeriodoPreset
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.ui.common.DateInputParser
import br.com.banavita.app.ui.common.formatCurrency
import br.com.banavita.app.ui.common.formatDate
import br.com.banavita.app.ui.common.formatQuantity
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
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
    loteRepository: LoteRepository,
    encomendaRepository: EncomendaRepository,
    private val recebimentoDao: br.com.banavita.app.data.local.dao.RecebimentoDao,
    private val pagamentoDao: br.com.banavita.app.data.local.dao.PagamentoDao,
) : ViewModel() {

    private val initialModo = ConsultaModo.fromRoute(
        savedStateHandle.get<String>(BanavitaRoutes.ARG_MODO),
    )
    private val initialBusca = savedStateHandle.get<String>(BanavitaRoutes.ARG_BUSCA).orEmpty()
    private val initialPeriodo = savedStateHandle.get<String>(BanavitaRoutes.ARG_PERIODO)
        ?.takeIf { it.isNotBlank() }
        ?.let { runCatching { PeriodoPreset.valueOf(it) }.getOrNull() }
        ?: PeriodoPreset.HOJE
    private val initialCriticos = savedStateHandle.get<String>(BanavitaRoutes.ARG_CRITICOS) == "1"

    private val filters = MutableStateFlow(
        ConsultasFilters(
            modo = initialModo,
            busca = initialBusca,
            periodo = initialPeriodo,
            somenteCriticos = initialCriticos,
        ),
    )

    private val _navEvents = MutableSharedFlow<ConsultasNavEvent>(extraBufferCapacity = 1)
    val navEvents = _navEvents.asSharedFlow()

    private val master = combine(
        estoqueRepository.observeAll(),
        produtoRepository.observeAll(),
        vendaRepository.observeAll(),
        compraRepository.observeAll(),
        clienteRepository.observeAll(),
        recebimentoDao.observeAll(),
        pagamentoDao.observeAll(),
    ) { args ->
        MasterCatalog(
            estoques = args[0] as List<br.com.banavita.app.domain.model.ProdutoEstoque>,
            produtos = args[1] as List<br.com.banavita.app.domain.model.Produto>,
            vendas = args[2] as List<br.com.banavita.app.domain.model.Venda>,
            compras = args[3] as List<br.com.banavita.app.domain.model.Compra>,
            clientes = args[4] as List<br.com.banavita.app.domain.model.Cliente>,
            recebimentos = args[5] as List<br.com.banavita.app.data.local.entity.RecebimentoEntity>,
            pagamentos = args[6] as List<br.com.banavita.app.data.local.entity.PagamentoEntity>,
        )
    }

    private val money = combine(
        fornecedorRepository.observeAll(),
        financeiroRepository.observeSaldosAReceber(),
        financeiroRepository.observeSaldosAPagar(),
    ) { fornecedores, receber, pagar ->
        MoneyCatalog(fornecedores, receber, pagar)
    }

    private val pereciveis = combine(
        loteRepository.observeLinhas(),
        loteRepository.observePerdas(),
        encomendaRepository.observeAll(),
        loteRepository.observeLinhas().flatMapLatest {
            flow { emit(loteRepository.previsao()) }
        },
    ) { linhas, perdas, encomendas, previsao ->
        PereciveisCatalog(linhas, perdas, encomendas, previsao)
    }

    val uiState: StateFlow<ConsultasUiState> = combine(
        filters,
        master,
        money,
        pereciveis,
    ) { current, masters, moneyData, perc ->
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
                linhas = perc.linhas,
                perdas = perc.perdas,
                encomendas = perc.encomendas,
                previsao = perc.previsao,
                recebimentos = masters.recebimentos,
                pagamentos = masters.pagamentos,
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

    fun onSomenteCriticos(value: Boolean) {
        filters.update { it.copy(somenteCriticos = value) }
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
                        somenteCriticos = result.somenteCriticos || current.somenteCriticos,
                    )
                }
                result.openProdutoId?.let { _navEvents.tryEmit(ConsultasNavEvent.EstoqueDetalhe(it)) }
                result.openClienteId?.let { _navEvents.tryEmit(ConsultasNavEvent.ReceberDetalhe(it)) }
                result.openFornecedorId?.let { _navEvents.tryEmit(ConsultasNavEvent.PagarDetalhe(it)) }
            }
            else -> Unit
        }
    }

    fun onFilaEtileno() {
        _navEvents.tryEmit(ConsultasNavEvent.FilaEtileno)
    }

    fun onNovaPerda() {
        _navEvents.tryEmit(ConsultasNavEvent.NovaPerda)
    }

    fun onNovaEncomenda() {
        _navEvents.tryEmit(ConsultasNavEvent.NovaEncomenda)
    }

    fun onNovaVenda() {
        _navEvents.tryEmit(ConsultasNavEvent.NovaVenda)
    }

    fun onNovaCompra() {
        _navEvents.tryEmit(ConsultasNavEvent.NovaCompra)
    }

    fun onEstoqueClick(produtoId: java.util.UUID) {
        _navEvents.tryEmit(ConsultasNavEvent.EstoqueDetalhe(produtoId, filters.value.somenteCriticos))
    }

    fun onDocumentoClick(id: java.util.UUID) {
        when (filters.value.modo) {
            ConsultaModo.VENDAS -> _navEvents.tryEmit(ConsultasNavEvent.Venda(id))
            ConsultaModo.COMPRAS -> _navEvents.tryEmit(ConsultasNavEvent.Compra(id))
            ConsultaModo.ENCOMENDAS -> _navEvents.tryEmit(ConsultasNavEvent.Encomenda(id))
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

        val estoqueItems = data.linhas.groupBy { it.produtoId }.mapNotNull { (produtoId, linhasDoProduto) ->
            val primeira = linhasDoProduto.first()
            val descricao = primeira.descricao
            val unidade = primeira.unidade

            if (busca.isNotBlank() && !descricao.contains(busca, ignoreCase = true)) {
                return@mapNotNull null
            }

            val totalSaldo = if (current.somenteCriticos) {
                linhasDoProduto.filter { it.estado == MaturacaoEstado.PINTADA || it.estado == MaturacaoEstado.PODRE }
                    .sumOf { it.quantidade }
            } else {
                linhasDoProduto.sumOf { it.quantidade }
            }
            if (current.somenteZerados && totalSaldo > 1e-9) return@mapNotNull null

            val temCritico = linhasDoProduto.any { it.estado == MaturacaoEstado.PINTADA || it.estado == MaturacaoEstado.PODRE }
            if (current.somenteCriticos && !temCritico) return@mapNotNull null

            EstoqueConsultaItem(
                key = produtoId.toString(),
                produtoId = produtoId,
                descricao = descricao,
                unidade = unidade,
                saldo = totalSaldo,
                saldoFormatado = formatQuantity(totalSaldo, unidade),
                zerado = totalSaldo <= 1e-9,
                estado = null,
                camara = null,
                critico = temCritico,
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
            ConsultaModo.RECEBER -> {
                val pagosMap = data.recebimentos.groupBy { it.vendaId }.mapValues { (_, v) -> v.sumOf { it.valorCentavos } }
                val parceiroMap = data.vendas.associate { it.id to it.clienteId }
                val abertos = data.vendas.filter { it.aPrazo && it.data in range }
                    .mapNotNull { v ->
                        val saldo = v.valorTotalCentavos - (pagosMap[v.id] ?: 0L)
                        if (saldo <= 0) return@mapNotNull null
                        DocumentoConsultaItem(
                            id = v.id,
                            data = v.data,
                            parceiro = v.clienteId?.let { clienteMap[it]?.nome } ?: "Cliente avulso",
                            valorCentavos = saldo
                        )
                    }
                documentoState(
                    current = current,
                    documentos = abertos,
                    agregadosParceiro = abertos.groupBy { parceiroMap[it.id] }.map { (id, rows) ->
                        AgregadoConsultaItem(
                            nome = id?.let { clienteMap[it]?.nome } ?: "Cliente avulso",
                            quantidadeLabel = null,
                            valorCentavos = rows.sumOf { it.valorCentavos },
                        )
                    },
                    agregadosProduto = emptyList(),
                    totalLabel = "A receber",
                )
            }
            ConsultaModo.PAGAR -> {
                val pagosMap = data.pagamentos.groupBy { it.compraId }.mapValues { (_, v) -> v.sumOf { it.valorCentavos } }
                val parceiroMap = data.compras.associate { it.id to it.fornecedorId }
                val abertos = data.compras.filter { it.aPrazo && it.data in range }
                    .mapNotNull { c ->
                        val saldo = c.valorTotalCentavos - (pagosMap[c.id] ?: 0L)
                        if (saldo <= 0) return@mapNotNull null
                        DocumentoConsultaItem(
                            id = c.id,
                            data = c.data,
                            parceiro = c.fornecedorId?.let { fornecedorMap[it]?.nome } ?: "Sem fornecedor",
                            valorCentavos = saldo
                        )
                    }
                documentoState(
                    current = current,
                    documentos = abertos,
                    agregadosParceiro = abertos.groupBy { parceiroMap[it.id] }.map { (id, rows) ->
                        AgregadoConsultaItem(
                            nome = id?.let { fornecedorMap[it]?.nome } ?: "Sem fornecedor",
                            quantidadeLabel = null,
                            valorCentavos = rows.sumOf { it.valorCentavos },
                        )
                    },
                    agregadosProduto = emptyList(),
                    totalLabel = "A pagar",
                )
            }
            ConsultaModo.PREVISAO -> {
                val items = data.previsao.filter { item ->
                    busca.isBlank() || item.descricao.contains(busca, ignoreCase = true)
                }.map { item ->
                    PrevisaoConsultaItem(
                        key = "${item.produtoId}-${item.estado}",
                        descricao = item.descricao,
                        estado = item.estado,
                        kg3 = formatQuantity(item.kgEm3Dias, item.unidade),
                        kg7 = formatQuantity(item.kgEm7Dias, item.unidade),
                    )
                }
                ConsultasUiState(
                    filters = current,
                    isLoading = false,
                    totalLabel = "Oferta prevista",
                    totalValue = items.size.toString(),
                    hint = "Só entra o que já está na etileno. O que está na fria não amadurece até ser solto.",
                    previsaoItems = items,
                )
            }
            ConsultaModo.PERDAS -> {
                val perdasPeriodo = data.perdas.filter { it.data in range }
                val comprados = data.compras.filter { it.data in range }
                    .flatMap { it.itens }
                    .groupBy { it.produtoId }
                    .mapValues { (_, rows) -> rows.sumOf { it.quantidade } }
                val vendidos = data.vendas.filter { it.data in range }
                    .flatMap { it.itens }
                    .groupBy { it.produtoId }
                    .mapValues { (_, rows) -> rows.sumOf { it.quantidade } }
                val porProduto = perdasPeriodo.groupBy { it.produtoId }
                val items = (porProduto.keys + comprados.keys).distinct().mapNotNull { produtoId ->
                    val produto = produtoMap[produtoId] ?: return@mapNotNull null
                    if (busca.isNotBlank() && !produto.descricao.contains(busca, ignoreCase = true)) {
                        return@mapNotNull null
                    }
                    val kgComp = comprados[produtoId] ?: 0.0
                    val kgVend = vendidos[produtoId] ?: 0.0
                    val kgPerd = porProduto[produtoId]?.sumOf { it.quantidade } ?: 0.0
                    val valor = porProduto[produtoId]?.sumOf { it.valorCentavos } ?: 0L
                    if (kgComp <= 1e-9 && kgPerd <= 1e-9) return@mapNotNull null
                    val pct = if (kgComp > 1e-9) 100.0 * kgPerd / kgComp else 0.0
                    PerdaConsultaItem(
                        produtoId = produtoId,
                        descricao = produto.descricao,
                        detalhe = "Comprado ${formatQuantity(kgComp, produto.unidade)} · vendido ${formatQuantity(kgVend, produto.unidade)} · perdido ${formatQuantity(kgPerd, produto.unidade)} (${String.format("%.1f", pct)}%)",
                        valorFormatado = formatCurrency(valor),
                    )
                }.sortedByDescending { it.descricao }
                ConsultasUiState(
                    filters = current,
                    isLoading = false,
                    totalLabel = "R$ perdido",
                    totalValue = formatCurrency(perdasPeriodo.sumOf { it.valorCentavos }),
                    hint = "% perda = kg perdidos ÷ kg comprados no recorte. R$ = custo do lote/compra.",
                    perdaItems = items,
                )
            }
            ConsultaModo.ENCOMENDAS -> {
                val items = data.encomendas.filter { enc ->
                    enc.dataEntrega in range &&
                        (busca.isBlank() ||
                            clienteMap[enc.clienteId]?.nome.orEmpty().contains(busca, ignoreCase = true))
                }.map { enc ->
                    EncomendaConsultaItem(
                        id = enc.id,
                        titulo = clienteMap[enc.clienteId]?.nome ?: "Cliente",
                        subtitle = formatDate(enc.dataEntrega),
                        trailing = if (enc.status == EncomendaStatus.ATENDIDO) "Atendido" else "Aberto",
                    )
                }
                ConsultasUiState(
                    filters = current,
                    isLoading = false,
                    totalLabel = "Encomendas",
                    totalValue = items.size.toString(),
                    encomendaItems = items,
                )
            }
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
        val recebimentos: List<br.com.banavita.app.data.local.entity.RecebimentoEntity>,
        val pagamentos: List<br.com.banavita.app.data.local.entity.PagamentoEntity>,
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
        val linhas: List<EstoqueLinha>,
        val perdas: List<br.com.banavita.app.domain.model.Perda>,
        val encomendas: List<br.com.banavita.app.domain.model.Encomenda>,
        val previsao: List<br.com.banavita.app.domain.model.OfertaPrevista>,
        val recebimentos: List<br.com.banavita.app.data.local.entity.RecebimentoEntity>,
        val pagamentos: List<br.com.banavita.app.data.local.entity.PagamentoEntity>,
    )

    private data class PereciveisCatalog(
        val linhas: List<EstoqueLinha>,
        val perdas: List<br.com.banavita.app.domain.model.Perda>,
        val encomendas: List<br.com.banavita.app.domain.model.Encomenda>,
        val previsao: List<br.com.banavita.app.domain.model.OfertaPrevista>,
    )
}
