package br.com.banavita.app.ui.voice

import br.com.banavita.app.domain.model.extraction.ExtractionDraft
import br.com.banavita.app.domain.model.extraction.ExtractionField
import br.com.banavita.app.domain.model.extraction.ExtractionFields
import br.com.banavita.app.ui.common.formatCurrency

data class DraftReviewLine(
    val label: String,
    val value: String,
    val confidence: Float,
)

object ExtractionDraftFormatter {

    fun toReviewLines(draft: ExtractionDraft): List<DraftReviewLine> =
        when (val fields = draft.fields) {
            is ExtractionFields.GrupoDraft -> listOf(
                fieldLine("Nome", fields.nome),
                fieldLine("Observação", fields.observacao),
            )
            is ExtractionFields.ClienteDraft -> listOf(
                fieldLine("Nome", fields.nome),
                fieldLine("Telefone", fields.telefone),
            )
            is ExtractionFields.FornecedorDraft -> listOf(
                fieldLine("Nome", fields.nome),
                fieldLine("Telefone", fields.telefone),
            )
            is ExtractionFields.ProdutoDraft -> listOf(
                fieldLine("Descrição", fields.descricao),
                fieldLine("Preço", fields.precoCentavos),
                fieldLine("Unidade", fields.unidade),
                entityLine("Grupo", fields.grupo?.label, fields.grupoRaw),
            )
            is ExtractionFields.VendaDraft -> {
                val item = fields.itens.firstOrNull()
                buildList {
                    if (item != null) {
                        add(entityLine("Produto", item.produtoResolvido?.label, item.produtoRaw))
                        add(fieldLine("Quantidade", item.quantidade))
                        add(fieldLine("Unidade", item.unidade))
                        add(fieldLine("Preço unit.", item.valorUnitarioCentavos))
                    }
                    add(entityLine("Cliente", fields.cliente?.label, fields.clienteRaw))
                }
            }
            is ExtractionFields.CompraDraft -> {
                val item = fields.itens.firstOrNull()
                buildList {
                    if (item != null) {
                        add(entityLine("Produto", item.produtoResolvido?.label, item.produtoRaw))
                        add(fieldLine("Quantidade", item.quantidade))
                        add(fieldLine("Unidade", item.unidade))
                        add(fieldLine("Preço unit.", item.valorUnitarioCentavos))
                    }
                    add(entityLine("Fornecedor", fields.fornecedor?.label, fields.fornecedorRaw))
                }
            }
        }.filter { it.value.isNotBlank() && it.value != "—" }

    private fun fieldLine(label: String, field: ExtractionField<*>): DraftReviewLine =
        DraftReviewLine(label = label, value = formatValue(field), confidence = field.confidence)

    private fun entityLine(label: String, resolved: String?, rawField: ExtractionField<String>): DraftReviewLine {
        val value = resolved ?: rawField.value ?: rawField.rawSpan ?: "—"
        val confidence = if (resolved != null) rawField.confidence.coerceAtLeast(0.8f) else rawField.confidence
        return DraftReviewLine(label = label, value = value, confidence = confidence)
    }

    private fun formatValue(field: ExtractionField<*>): String {
        val value = field.value
        if (value == null) return field.rawSpan ?: "—"
        return when (value) {
            is Long -> formatCurrency(value)
            is Double -> if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
            else -> value.toString()
        }
    }
}
