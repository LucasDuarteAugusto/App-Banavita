package br.com.banavita.app.ui.vendas

import br.com.banavita.app.domain.model.Venda
import java.util.UUID

data class VendaListItem(
    val id: UUID,
    val dataFormatada: String,
    val valorTotalFormatado: String,
    val clienteNome: String,
    val itensResumo: String,
)

data class VendaListUiState(
    val items: List<VendaListItem> = emptyList(),
    val isLoading: Boolean = true,
)

fun Venda.toListItem(
    dataFormatada: String,
    valorTotalFormatado: String,
    clienteNome: String,
    itensResumo: String,
): VendaListItem = VendaListItem(
    id = id,
    dataFormatada = dataFormatada,
    valorTotalFormatado = valorTotalFormatado,
    clienteNome = clienteNome,
    itensResumo = itensResumo,
)
