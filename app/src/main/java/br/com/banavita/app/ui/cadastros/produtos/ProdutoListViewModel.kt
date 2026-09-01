package br.com.banavita.app.ui.cadastros.produtos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.GrupoRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.ui.common.formatCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProdutoListViewModel @Inject constructor(
    produtoRepository: ProdutoRepository,
    grupoRepository: GrupoRepository,
) : ViewModel() {

    val uiState: StateFlow<ProdutoListUiState> = combine(
        produtoRepository.observeAll(),
        grupoRepository.observeAll(),
    ) { produtos, grupos ->
        val grupoMap = grupos.associateBy { it.id }
        produtos
            .map { produto ->
                produto.toListItem(
                    precoFormatado = formatCurrency(produto.precoCentavos),
                    grupoNome = produto.grupoId?.let { grupoMap[it]?.nome },
                )
            }
            .sortedBy { it.descricao.lowercase() }
    }
        .map { items -> ProdutoListUiState(items = items, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProdutoListUiState(),
        )
}
