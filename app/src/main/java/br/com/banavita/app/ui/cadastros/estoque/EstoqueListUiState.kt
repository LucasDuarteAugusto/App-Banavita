package br.com.banavita.app.ui.cadastros.estoque

import java.util.UUID

data class EstoqueListItem(
    val produtoId: UUID,
    val produtoDescricao: String,
    val saldoFormatado: String,
    val unidade: String,
)

data class EstoqueListUiState(
    val items: List<EstoqueListItem> = emptyList(),
    val isLoading: Boolean = true,
)
