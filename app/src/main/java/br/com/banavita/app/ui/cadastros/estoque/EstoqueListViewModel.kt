package br.com.banavita.app.ui.cadastros.estoque

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.ui.common.formatQuantity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EstoqueListViewModel @Inject constructor(
    estoqueRepository: EstoqueRepository,
    produtoRepository: ProdutoRepository,
) : ViewModel() {

    val uiState: StateFlow<EstoqueListUiState> = combine(
        estoqueRepository.observeAll(),
        produtoRepository.observeAll(),
    ) { estoques, produtos ->
        val produtoMap = produtos.associateBy { it.id }
        estoques
            .mapNotNull { estoque ->
                val produto = produtoMap[estoque.produtoId] ?: return@mapNotNull null
                EstoqueListItem(
                    produtoId = estoque.produtoId,
                    produtoDescricao = produto.descricao,
                    saldoFormatado = formatQuantity(estoque.saldo, estoque.unidade),
                    unidade = estoque.unidade,
                )
            }
            .sortedBy { it.produtoDescricao.lowercase() }
    }
        .map { items -> EstoqueListUiState(items = items, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EstoqueListUiState(),
        )
}
