package br.com.banavita.app.ui.pereciveis.perda

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.LoteRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.model.Lote
import br.com.banavita.app.domain.model.PerdaMotivo
import br.com.banavita.app.domain.model.Produto
import br.com.banavita.app.ui.common.DateInputParser
import br.com.banavita.app.ui.common.QuantityInputParser
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class PerdaFormUiState(
    val produtos: List<Produto> = emptyList(),
    val lotes: List<Lote> = emptyList(),
    val produtoId: UUID? = null,
    val loteId: UUID? = null,
    val quantidadeInput: String = "",
    val dataInput: String = DateInputParser.todayInput(),
    val motivo: PerdaMotivo = PerdaMotivo.APODRECEU,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class PerdaFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loteRepository: LoteRepository,
    private val produtoRepository: ProdutoRepository,
) : ViewModel() {

    private val initialLoteId = savedStateHandle.get<String>(BanavitaRoutes.ARG_LOTE_ID)
        ?.takeIf { it.isNotBlank() }
        ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
    private val initialProdutoId = savedStateHandle.get<String>(BanavitaRoutes.ARG_PRODUTO_ID)
        ?.takeIf { it.isNotBlank() }
        ?.let { runCatching { UUID.fromString(it) }.getOrNull() }

    private val _uiState = MutableStateFlow(PerdaFormUiState())
    val uiState: StateFlow<PerdaFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val produtos = produtoRepository.observeAll()
            produtos.collect { lista ->
                val pereciveis = lista.filter { it.perecivel }.sortedBy { it.descricao.lowercase() }
                _uiState.update { it.copy(produtos = pereciveis) }
            }
        }
        viewModelScope.launch {
            val lote = initialLoteId?.let { loteRepository.getById(it) }
            val produtoId = lote?.produtoId ?: initialProdutoId
            if (produtoId != null) {
                loteRepository.observeLotesDoProduto(produtoId).collect { lotes ->
                    _uiState.update {
                        it.copy(
                            produtoId = produtoId,
                            loteId = lote?.id ?: it.loteId ?: lotes.firstOrNull { l -> l.quantidadeRestante > 1e-9 }?.id,
                            lotes = lotes.filter { l -> l.quantidadeRestante > 1e-9 },
                            isLoading = false,
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onProdutoSelected(produtoId: UUID?) {
        _uiState.update { it.copy(produtoId = produtoId, loteId = null, errorMessage = null) }
        if (produtoId == null) {
            _uiState.update { it.copy(lotes = emptyList()) }
            return
        }
        viewModelScope.launch {
            loteRepository.observeLotesDoProduto(produtoId).collect { lotes ->
                val abertos = lotes.filter { it.quantidadeRestante > 1e-9 }
                _uiState.update {
                    it.copy(
                        lotes = abertos,
                        loteId = it.loteId?.takeIf { id -> abertos.any { l -> l.id == id } }
                            ?: abertos.firstOrNull()?.id,
                    )
                }
            }
        }
    }

    fun onLoteSelected(loteId: UUID?) {
        _uiState.update { it.copy(loteId = loteId, errorMessage = null) }
    }

    fun onQuantidadeChange(value: String) {
        _uiState.update { it.copy(quantidadeInput = value, errorMessage = null) }
    }

    fun onDataChange(value: String) {
        _uiState.update { it.copy(dataInput = value, errorMessage = null) }
    }

    fun onMotivo(motivo: PerdaMotivo) {
        _uiState.update { it.copy(motivo = motivo) }
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        val loteId = state.loteId
        if (loteId == null) {
            _uiState.update { it.copy(errorMessage = "Selecione o lote da perda.") }
            return
        }
        val qtde = QuantityInputParser.parse(state.quantidadeInput)
        if (qtde == null || qtde <= 0) {
            _uiState.update { it.copy(errorMessage = "Informe a quantidade da perda.") }
            return
        }
        val data = DateInputParser.parseToEpochMillis(state.dataInput)
        if (data == null) {
            _uiState.update { it.copy(errorMessage = "Informe uma data válida (dd/MM/aaaa).") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching { loteRepository.registrarPerda(loteId, qtde, state.motivo, data) }
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = (error as? BanavitaDomainException)?.message
                                ?: error.message
                                ?: "Erro ao lançar perda.",
                        )
                    }
                }
        }
    }
}
