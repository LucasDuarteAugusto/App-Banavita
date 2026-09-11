package br.com.banavita.app.domain.model.extraction

import br.com.banavita.app.domain.model.AgendaRecorte
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.ConsultaVisao
import br.com.banavita.app.domain.model.PeriodoPreset
import java.util.UUID

enum class VoiceScreenContext {
    VENDA,
    COMPRA,
    CLIENTE,
    FORNECEDOR,
    PRODUTO,
    GRUPO,
    CONSULTAS,
    INICIO,
    RECEBER,
    PAGAR,
    AGENDA,
}

enum class FieldSource {
    HEURISTIC,
    DICTIONARY,
    DATABASE_MATCH,
}

enum class BaixaTipo {
    RECEBER,
    PAGAR,
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
        val aPrazo: ExtractionField<Boolean> = ExtractionField(false, "à vista", 0.4f),
    ) : ExtractionFields()

    data class CompraDraft(
        val itens: List<DraftLineItem>,
        val fornecedor: ResolvedEntity?,
        val fornecedorRaw: ExtractionField<String>,
        val aPrazo: ExtractionField<Boolean> = ExtractionField(false, "à vista", 0.4f),
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

    data class ConsultasDraft(
        val modo: ExtractionField<ConsultaModo>,
        val busca: ExtractionField<String>,
        val periodo: ExtractionField<PeriodoPreset>,
        val visao: ExtractionField<ConsultaVisao>,
        val somenteZerados: ExtractionField<Boolean>,
        val navigateNovaVenda: Boolean = false,
        val navigateAgenda: Boolean = false,
        val openDetailIfResolved: Boolean = false,
        val produtoResolvido: ResolvedEntity? = null,
        val clienteResolvido: ResolvedEntity? = null,
        val fornecedorResolvido: ResolvedEntity? = null,
    ) : ExtractionFields()

    data class BaixaDraft(
        val tipo: BaixaTipo,
        val parceiro: ResolvedEntity?,
        val parceiroRaw: ExtractionField<String>,
        val valorCentavos: ExtractionField<Long>,
    ) : ExtractionFields()

    data class EventoDraft(
        val titulo: ExtractionField<String>,
        val dataHoraMillis: ExtractionField<Long>,
        val observacao: ExtractionField<String>,
        val cliente: ResolvedEntity?,
        val clienteRaw: ExtractionField<String>,
        val fornecedor: ResolvedEntity?,
        val fornecedorRaw: ExtractionField<String>,
        val notificar: ExtractionField<Boolean>,
        val antecipacaoMinutos: ExtractionField<Int>,
        val notifTitulo: ExtractionField<String>,
        val notifCorpo: ExtractionField<String>,
        val recorteLista: ExtractionField<AgendaRecorte>,
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
    data class ConsultaAplicada(
        val modo: ConsultaModo? = null,
        val busca: String? = null,
        val periodo: PeriodoPreset? = null,
        val visao: ConsultaVisao? = null,
        val somenteZerados: Boolean = false,
        val openProdutoId: UUID? = null,
        val openClienteId: UUID? = null,
        val openFornecedorId: UUID? = null,
        val navigateNovaVenda: Boolean = false,
        val navigateAgenda: Boolean = false,
        val agendaRecorte: AgendaRecorte? = null,
    ) : ConfirmResult()
    data class RecebimentoCriado(val recebimentoIds: List<UUID>) : ConfirmResult()
    data class PagamentoCriado(val pagamentoIds: List<UUID>) : ConfirmResult()
    data class EventoCriado(val eventoId: UUID, val dataHoraMillis: Long) : ConfirmResult()
    data class AgendaFiltroAplicado(
        val epochDay: Long,
        val monthOverviewHint: Boolean = false,
    ) : ConfirmResult()
}
