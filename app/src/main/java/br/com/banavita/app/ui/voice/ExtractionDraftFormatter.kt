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
                    add(DraftReviewLine("Pagamento", if (fields.aPrazo.value == true) "A prazo" else "À vista", fields.aPrazo.confidence))
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
                    add(DraftReviewLine("Pagamento", if (fields.aPrazo.value == true) "A prazo" else "À vista", fields.aPrazo.confidence))
                }
            }
            is ExtractionFields.ConsultasDraft -> listOf(
                DraftReviewLine("Modo", fields.modo.value?.name?.lowercase() ?: "—", fields.modo.confidence),
                fieldLine("Busca", fields.busca),
                DraftReviewLine("Período", fields.periodo.value?.name?.lowercase() ?: "—", fields.periodo.confidence),
                DraftReviewLine("Visão", fields.visao.value?.name?.lowercase() ?: "—", fields.visao.confidence),
                DraftReviewLine("Só zerados", if (fields.somenteZerados.value == true) "sim" else "não", fields.somenteZerados.confidence),
            )
            is ExtractionFields.BaixaDraft -> listOf(
                DraftReviewLine("Tipo", fields.tipo.name.lowercase(), 1f),
                entityLine("Parceiro", fields.parceiro?.label, fields.parceiroRaw),
                fieldLine("Valor", fields.valorCentavos),
            )
            is ExtractionFields.EventoDraft -> listOf(
                fieldLine("Título", fields.titulo),
                fieldLine("Quando", fields.dataHoraMillis),
                fieldLine("Observação", fields.observacao),
                entityLine("Cliente", fields.cliente?.label, fields.clienteRaw),
                DraftReviewLine("Lembrete", if (fields.notificar.value != false) "ligado" else "desligado", fields.notificar.confidence),
            )
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
            is Long -> {
                if (value > 1_000_000_000_000L) value.toString() else formatCurrency(value)
            }
            is Double -> if (value % 1.0 == 0.0) value.toLong().toString() else value.toString()
            else -> value.toString()
        }
    }
}
