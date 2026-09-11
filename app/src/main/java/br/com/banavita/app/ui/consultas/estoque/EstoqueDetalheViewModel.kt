package br.com.banavita.app.ui.consultas.estoque

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.core.time.RipeningClock
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.LoteRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.EstoqueMovimento
import br.com.banavita.app.domain.model.Lote
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.Produto
import br.com.banavita.app.ui.common.MoneyInputParser
import br.com.banavita.app.ui.common.QuantityInputParser
import br.com.banavita.app.ui.common.formatQuantity
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class EstoqueLoteUi(
    val lote: Lote,
    val quantidadeFormatada: String,
    val daysUntilNext: Int?,
    val nextEstado: MaturacaoEstado?,
)

data class EstoqueDetalheUiState(
    val produtoId: UUID? = null,
    val descricao: String = "",
    val unidade: String = "UN",
    val perecivel: Boolean = false,
    val saldoFormatado: String = "",
    val lotes: List<EstoqueLoteUi> = emptyList(),
    val movimentos: List<EstoqueMovimento> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val transferLoteId: UUID? = null,
    val transferQtyInput: String = "",
    val corrigirLoteId: UUID? = null,
    val showQueima: Boolean = false,
    val queimaPrecoInput: String = "",
)

@HiltViewModel
class EstoqueDetalheViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val produtoRepository: ProdutoRepository,
    private val estoqueRepository: EstoqueRepository,
    private val loteRepository: LoteRepository,
) : ViewModel() {

    private val produtoId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_PRODUTO_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(EstoqueDetalheUiState(produtoId = produtoId))
    val uiState: StateFlow<EstoqueDetalheUiState> = _uiState.asStateFlow()

    private var produto: Produto? = null

    init {
        viewModelScope.launch {
            val id = produtoId ?: return@launch
            produto = produtoRepository.getById(id)
            loteRepository.observeLotesDoProduto(id).collect { lotes ->
                refresh(id, lotes)
            }
        }
    }

    private suspend fun refresh(id: UUID, lotes: List<Lote>) {
        val atual = produto ?: produtoRepository.getById(id)
        produto = atual
        val unidade = atual?.unidade ?: "UN"
        val today = RipeningClock.todayEpochDay()
        val loteUi = lotes
            .filter { it.quantidadeRestante > 1e-9 }
            .map { lote ->
                val snap = if (lote.camara == Camara.ETILENO && atual != null) {
                    RipeningClock.snapshot(
                        camara = lote.camara,
                        estadoRegistrado = lote.estado,
                        etapaInicioEpochDay = lote.etapaInicioEpochDay,
                        curve = atual.curva,
                        todayEpochDay = today,
                    )
                } else {
                    null
                }
                EstoqueLoteUi(
                    lote = lote,
                    quantidadeFormatada = formatQuantity(lote.quantidadeRestante, unidade),
                    daysUntilNext = snap?.daysUntilNext,
                    nextEstado = snap?.daysUntilNext?.let { lote.estado.next() },
                )
            }
        _uiState.update {
            it.copy(
                produtoId = id,
                descricao = atual?.descricao.orEmpty(),
                unidade = unidade,
                perecivel = atual?.perecivel == true,
                saldoFormatado = formatQuantity(estoqueRepository.getSaldo(id), unidade),
                lotes = loteUi,
                movimentos = estoqueRepository.movimentosDoProduto(id),
                isLoading = false,
            )
        }
    }

    fun onSoltarClick(loteId: UUID) {
        val lote = _uiState.value.lotes.firstOrNull { it.lote.id == loteId }?.lote ?: return
        _uiState.update {
            it.copy(
                transferLoteId = loteId,
                transferQtyInput = lote.quantidadeRestante.toString().replace('.', ','),
                errorMessage = null,
            )
        }
    }

    fun onTransferQty(value: String) {
        _uiState.update { it.copy(transferQtyInput = value, errorMessage = null) }
    }

    fun dismissTransfer() {
        _uiState.update { it.copy(transferLoteId = null) }
    }

    fun confirmTransfer() {
        val loteId = _uiState.value.transferLoteId ?: return
        val qtde = QuantityInputParser.parse(_uiState.value.transferQtyInput)
        if (qtde == null) {
            _uiState.update { it.copy(errorMessage = "Informe a quantidade a soltar.") }
            return
        }
        viewModelScope.launch {
            runCatching { loteRepository.transferirParaEtileno(loteId, qtde) }
                .onSuccess { _uiState.update { it.copy(transferLoteId = null, errorMessage = null) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = (error as? BanavitaDomainException)?.message
                                ?: error.message
                                ?: "Não foi possível soltar o lote.",
                        )
                    }
                }
        }
    }

    fun onCorrigirClick(loteId: UUID) {
        _uiState.update { it.copy(corrigirLoteId = loteId, errorMessage = null) }
    }

    fun dismissCorrigir() {
        _uiState.update { it.copy(corrigirLoteId = null) }
    }

    fun corrigirEstado(estado: MaturacaoEstado) {
        val loteId = _uiState.value.corrigirLoteId ?: return
        viewModelScope.launch {
            runCatching { loteRepository.corrigirEstado(loteId, estado) }
                .onSuccess { _uiState.update { it.copy(corrigirLoteId = null) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = (error as? BanavitaDomainException)?.message
                                ?: error.message
                                ?: "Não foi possível corrigir o estado.",
                        )
                    }
                }
        }
    }

    fun onQueimaClick() {
        val sugerido = produto?.precoSugerido(MaturacaoEstado.PINTADA) ?: produto?.precoCentavos ?: 0L
        _uiState.update {
            it.copy(
                showQueima = true,
                queimaPrecoInput = MoneyInputParser.formatForInput(sugerido),
                errorMessage = null,
            )
        }
    }

    fun onQueimaPreco(value: String) {
        _uiState.update { it.copy(queimaPrecoInput = value) }
    }

    fun dismissQueima() {
        _uiState.update { it.copy(showQueima = false) }
    }

    fun confirmQueima() {
        val id = produtoId ?: return
        val cents = MoneyInputParser.parseToCentavos(_uiState.value.queimaPrecoInput)
        if (cents == null || cents < 0) {
            _uiState.update { it.copy(errorMessage = "Informe o preço de queima de hoje.") }
            return
        }
        viewModelScope.launch {
            runCatching { loteRepository.aplicarQueima(id, MaturacaoEstado.PINTADA, cents) }
                .onSuccess { _uiState.update { it.copy(showQueima = false, errorMessage = null) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = (error as? BanavitaDomainException)?.message
                                ?: error.message
                                ?: "Não foi possível aplicar a queima.",
                        )
                    }
                }
        }
    }
}
