package br.com.banavita.app.ui.compras

import br.com.banavita.app.domain.model.Compra
import java.util.UUID

data class CompraListItem(
    val id: UUID,
    val dataFormatada: String,
    val valorTotalFormatado: String,
    val fornecedorNome: String,
    val itensResumo: String,
)

data class CompraListUiState(
    val items: List<CompraListItem> = emptyList(),
    val isLoading: Boolean = true,
)

fun Compra.toListItem(
    dataFormatada: String,
    valorTotalFormatado: String,
    fornecedorNome: String,
    itensResumo: String,
): CompraListItem = CompraListItem(
    id = id,
    dataFormatada = dataFormatada,
    valorTotalFormatado = valorTotalFormatado,
    fornecedorNome = fornecedorNome,
    itensResumo = itensResumo,
)
