package br.com.banavita.app.ui.common

import br.com.banavita.app.core.money.Money
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.Produto
import java.util.UUID

data class DocumentLineItemFormState(
    val localId: String = UUID.randomUUID().toString(),
    val produtoId: UUID? = null,
    val quantidadeInput: String = "",
    val precoInput: String = "",
    val estoqueDisponivel: Double? = null,
    val estado: MaturacaoEstado? = null,
) {
    fun lineTotalCentavos(produtos: List<Produto>, precoObrigatorio: Boolean): Long? {
        val id = produtoId ?: return null
        val quantidade = QuantityInputParser.parse(quantidadeInput) ?: return null
        val produto = produtos.find { it.id == id }
        val defaultPrice = if ((produto?.perecivel == true) && (estado != null)) {
            produto.precoSugerido(estado)
        } else {
            produto?.precoCentavos
        }
        val precoCentavos = MoneyInputParser.parseToCentavos(precoInput)
            ?: if (!precoObrigatorio) defaultPrice else return null
        return Money.lineTotal(precoCentavos, quantidade)
    }
}

fun computeDocumentTotalCentavos(
    itens: List<DocumentLineItemFormState>,
    produtos: List<Produto>,
    precoObrigatorio: Boolean,
): Long = itens.fold(0L) { acc, item ->
    acc + (item.lineTotalCentavos(produtos, precoObrigatorio) ?: 0L)
}
