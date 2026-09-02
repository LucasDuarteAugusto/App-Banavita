package br.com.banavita.app.ui.consultas.estoque

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.domain.model.EstoqueMovimento
import br.com.banavita.app.ui.common.formatQuantity
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class EstoqueDetalheUiState(
    val produtoId: UUID? = null,
    val descricao: String = "",
    val unidade: String = "UN",
    val saldoFormatado: String = "",
    val movimentos: List<EstoqueMovimento> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class EstoqueDetalheViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val produtoRepository: ProdutoRepository,
    private val estoqueRepository: EstoqueRepository,
) : ViewModel() {

    private val produtoId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_PRODUTO_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(EstoqueDetalheUiState(produtoId = produtoId))
    val uiState: StateFlow<EstoqueDetalheUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val id = produtoId ?: return@launch
            val produto = produtoRepository.getById(id)
            val saldo = estoqueRepository.getSaldo(id)
            val unidade = produto?.unidade ?: "UN"
            val movimentos = estoqueRepository.movimentosDoProduto(id)
            _uiState.value = EstoqueDetalheUiState(
                produtoId = id,
                descricao = produto?.descricao ?: "",
                unidade = unidade,
                saldoFormatado = formatQuantity(saldo, unidade),
                movimentos = movimentos,
                isLoading = false,
            )
        }
    }
}
