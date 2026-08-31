package br.com.banavita.app.domain.model.extraction

import java.util.UUID

enum class VoiceScreenContext {
    VENDA,
    COMPRA,
    CLIENTE,
    FORNECEDOR,
    PRODUTO,
    GRUPO,
}

enum class FieldSource {
    HEURISTIC,
    DICTIONARY,
    DATABASE_MATCH,
}

data class ExtractionField<T>(
    val value: T?,
    val rawSpan: String? = null,
    val confidence: Float = 0f,
    val source: FieldSource = FieldSource.HEURISTIC,
) {
    val isPresent: Boolean get() = value != null
    val isStrong: Boolean get() = value != null && confidence >= 0.7f
}

data class EntityCandidate(
    val id: UUID,
    val label: String,
    val score: Float,
)

data class ResolvedEntity(
    val id: UUID?,
    val label: String?,
    val rawSpan: String?,
    val confidence: Float,
    val candidates: List<EntityCandidate> = emptyList(),
)

data class DraftLineItem(
    val produtoRaw: ExtractionField<String>,
    val produtoResolvido: ResolvedEntity?,
    val quantidade: ExtractionField<Double>,
    val unidade: ExtractionField<String>,
    val valorUnitarioCentavos: ExtractionField<Long>,
)

sealed class ExtractionFields {
    data class VendaDraft(
        val itens: List<DraftLineItem>,
        val cliente: ResolvedEntity?,
        val clienteRaw: ExtractionField<String>,
    ) : ExtractionFields()

    data class CompraDraft(
        val itens: List<DraftLineItem>,
        val fornecedor: ResolvedEntity?,
        val fornecedorRaw: ExtractionField<String>,
    ) : ExtractionFields()

    data class ClienteDraft(
        val nome: ExtractionField<String>,
        val telefone: ExtractionField<String>,
    ) : ExtractionFields()

    data class FornecedorDraft(
        val nome: ExtractionField<String>,
        val telefone: ExtractionField<String>,
    ) : ExtractionFields()

    data class ProdutoDraft(
        val descricao: ExtractionField<String>,
        val precoCentavos: ExtractionField<Long>,
        val unidade: ExtractionField<String>,
        val grupo: ResolvedEntity?,
        val grupoRaw: ExtractionField<String>,
    ) : ExtractionFields()

    data class GrupoDraft(
        val nome: ExtractionField<String>,
        val observacao: ExtractionField<String>,
    ) : ExtractionFields()
}

data class ExtractionDraft(
    val id: UUID,
    val context: VoiceScreenContext,
    val transcript: String,
    val createdAtEpochMillis: Long,
    val fields: ExtractionFields,
    val unresolvedTokens: List<String>,
    val overallConfidence: Float,
    val needsReview: Boolean,
)

sealed class ConfirmResult {
    data class VendaCriada(val vendaId: UUID) : ConfirmResult()
    data class CompraCriada(val compraId: UUID) : ConfirmResult()
    data class ClienteCriado(val clienteId: UUID) : ConfirmResult()
    data class FornecedorCriado(val fornecedorId: UUID) : ConfirmResult()
    data class ProdutoCriado(val produtoId: UUID) : ConfirmResult()
    data class GrupoCriado(val grupoId: UUID) : ConfirmResult()
}
