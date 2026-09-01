package br.com.banavita.app.ui.compras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.CompraRepository
import br.com.banavita.app.data.repository.FornecedorRepository
import br.com.banavita.app.ui.common.formatCurrency
import br.com.banavita.app.ui.common.formatDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CompraListViewModel @Inject constructor(
    compraRepository: CompraRepository,
    fornecedorRepository: FornecedorRepository,
) : ViewModel() {

    val uiState: StateFlow<CompraListUiState> = combine(
        compraRepository.observeAll(),
        fornecedorRepository.observeAll(),
    ) { compras, fornecedores ->
        val fornecedorMap = fornecedores.associateBy { it.id }
        compras
            .sortedByDescending { it.data }
            .map { compra ->
                val fornecedorNome = compra.fornecedorId?.let { fornecedorMap[it]?.nome }
                    ?: FORNECEDOR_NAO_INFORMADO
                val itensCount = compra.itens.size
                compra.toListItem(
                    dataFormatada = formatDate(compra.data),
                    valorTotalFormatado = formatCurrency(compra.valorTotalCentavos),
                    fornecedorNome = fornecedorNome,
                    itensResumo = if (itensCount == 1) {
                        "1 item"
                    } else {
                        "$itensCount itens"
                    },
                )
            }
    }
        .map { items -> CompraListUiState(items = items, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CompraListUiState(),
        )

    companion object {
        const val FORNECEDOR_NAO_INFORMADO = "Sem fornecedor"
    }
}
