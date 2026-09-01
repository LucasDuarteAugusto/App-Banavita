package br.com.banavita.app.ui.vendas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ClienteRepository
import br.com.banavita.app.data.repository.VendaRepository
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
class VendaListViewModel @Inject constructor(
    vendaRepository: VendaRepository,
    clienteRepository: ClienteRepository,
) : ViewModel() {

    val uiState: StateFlow<VendaListUiState> = combine(
        vendaRepository.observeAll(),
        clienteRepository.observeAll(),
    ) { vendas, clientes ->
        val clienteMap = clientes.associateBy { it.id }
        vendas
            .sortedByDescending { it.data }
            .map { venda ->
                val clienteNome = venda.clienteId?.let { clienteMap[it]?.nome }
                    ?: CLIENTE_AVULSO
                val itensCount = venda.itens.size
                venda.toListItem(
                    dataFormatada = formatDate(venda.data),
                    valorTotalFormatado = formatCurrency(venda.valorTotalCentavos),
                    clienteNome = clienteNome,
                    itensResumo = if (itensCount == 1) {
                        "1 item"
                    } else {
                        "$itensCount itens"
                    },
                )
            }
    }
        .map { items -> VendaListUiState(items = items, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VendaListUiState(),
        )

    companion object {
        const val CLIENTE_AVULSO = "Cliente avulso"
    }
}
