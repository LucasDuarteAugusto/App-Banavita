package br.com.banavita.app.ui.cadastros.produtos

import br.com.banavita.app.domain.model.Produto
import java.util.UUID

data class ProdutoListItem(
    val id: UUID,
    val descricao: String,
    val precoFormatado: String,
    val unidade: String,
    val grupoNome: String?,
)

data class ProdutoListUiState(
    val items: List<ProdutoListItem> = emptyList(),
    val isLoading: Boolean = true,
)

fun Produto.toListItem(precoFormatado: String, grupoNome: String?): ProdutoListItem =
    ProdutoListItem(
        id = id,
        descricao = descricao,
        precoFormatado = precoFormatado,
        unidade = unidade,
        grupoNome = grupoNome,
    )
