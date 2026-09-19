package br.com.banavita.app.domain.model

import java.util.UUID

enum class Camara {
    FRIA,
    ETILENO,
}

enum class MaturacaoEstado {
    CRUA,
    EM_MATURACAO,
    VERDE,
    VERDOLENGA,
    AMARELA,
    PINTADA,
    PODRE,
    ;

    fun next(): MaturacaoEstado? = when (this) {
        CRUA -> EM_MATURACAO
        EM_MATURACAO -> VERDE
        VERDE -> VERDOLENGA
        VERDOLENGA -> AMARELA
        AMARELA -> PINTADA
        PINTADA -> PODRE
        PODRE -> null
    }

    companion object {
        fun fromStored(raw: String?): MaturacaoEstado? =
            raw?.let { runCatching { valueOf(it) }.getOrNull() }
    }
}

enum class PerdaMotivo {
    APODRECEU,
    MANUSEIO,
    DOENCA,
    OUTRO,
}

enum class EncomendaStatus {
    ABERTO,
    ATENDIDO,
}

data class RipeningCurve(
    val diasEmMaturacao: Int,
    val diasVerde: Int,
    val diasVerdolenga: Int,
    val diasAmarela: Int,
    val diasPintada: Int,
) {
    fun daysOf(estado: MaturacaoEstado): Int? = when (estado) {
        MaturacaoEstado.CRUA -> null
        MaturacaoEstado.EM_MATURACAO -> diasEmMaturacao
        MaturacaoEstado.VERDE -> diasVerde
        MaturacaoEstado.VERDOLENGA -> diasVerdolenga
        MaturacaoEstado.AMARELA -> diasAmarela
        MaturacaoEstado.PINTADA -> diasPintada
        MaturacaoEstado.PODRE -> null
    }

    fun isValid(): Boolean =
        diasEmMaturacao > 0 && diasVerde > 0 && diasVerdolenga > 0 && diasAmarela > 0 && diasPintada > 0

    companion object {
        val DEFAULT = RipeningCurve(
            diasEmMaturacao = 1,
            diasVerde = 3,
            diasVerdolenga = 2,
            diasAmarela = 2,
            diasPintada = 2,
        )
    }
}

data class PrecosPorEstado(
    val cruaCentavos: Long,
    val emMaturacaoCentavos: Long,
    val verdeCentavos: Long,
    val verdolengaCentavos: Long,
    val amarelaCentavos: Long,
    val pintadaCentavos: Long,
    val podreCentavos: Long = 0L,
) {
    fun of(estado: MaturacaoEstado): Long = when (estado) {
        MaturacaoEstado.CRUA -> cruaCentavos
        MaturacaoEstado.EM_MATURACAO -> emMaturacaoCentavos
        MaturacaoEstado.VERDE -> verdeCentavos
        MaturacaoEstado.VERDOLENGA -> verdolengaCentavos
        MaturacaoEstado.AMARELA -> amarelaCentavos
        MaturacaoEstado.PINTADA -> pintadaCentavos
        MaturacaoEstado.PODRE -> podreCentavos
    }

    companion object {
        fun fromTabela(precoCentavos: Long): PrecosPorEstado =
            PrecosPorEstado(
                cruaCentavos = precoCentavos,
                emMaturacaoCentavos = precoCentavos,
                verdeCentavos = precoCentavos,
                verdolengaCentavos = precoCentavos,
                amarelaCentavos = precoCentavos,
                pintadaCentavos = precoCentavos,
                podreCentavos = 0L,
            )
    }
}

data class RipeningSnapshot(
    val estado: MaturacaoEstado,
    val etapaInicioEpochDay: Long,
    val daysUntilNext: Int?,
)

data class Lote(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val produtoId: UUID,
    val compraId: UUID?,
    val compraItemId: UUID?,
    val origemId: UUID?,
    val dataEntrada: Long,
    val quantidadeInicial: Double,
    val quantidadeRestante: Double,
    val camara: Camara,
    val estado: MaturacaoEstado,
    val etapaInicioEpochDay: Long?,
    val custoUnitarioCentavos: Long,
    val rotulo: String,
    val unidade: String? = null,
)

data class EstoqueLinha(
    val produtoId: UUID,
    val descricao: String,
    val unidade: String,
    val estado: MaturacaoEstado?,
    val camara: Camara?,
    val quantidade: Double,
    val perecivel: Boolean,
)

data class Perda(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val data: Long,
    val produtoId: UUID,
    val loteId: UUID?,
    val estado: MaturacaoEstado,
    val quantidade: Double,
    val motivo: PerdaMotivo,
    val valorCentavos: Long,
    val unidade: String? = null,
)

data class PrecoQueima(
    val id: UUID,
    val produtoId: UUID,
    val estado: MaturacaoEstado,
    val precoCentavos: Long,
    val inicioDiaMillis: Long,
    val fimExclusivoMillis: Long,
)

data class EncomendaItem(
    val id: UUID,
    val encomendaId: UUID,
    val produtoId: UUID,
    val estado: MaturacaoEstado,
    val quantidade: Double,
    val valorUnitarioCentavos: Long? = null,
)

data class Encomenda(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val clienteId: UUID,
    val dataEntrega: Long,
    val status: EncomendaStatus,
    val vendaId: UUID?,
    val eventoId: UUID? = null,
    val itens: List<EncomendaItem>,
)

data class OfertaPrevista(
    val produtoId: UUID,
    val descricao: String,
    val unidade: String,
    val estado: MaturacaoEstado,
    val kgEm3Dias: Double,
    val kgEm7Dias: Double,
)

data class RelatorioPerdaLinha(
    val produtoId: UUID,
    val descricao: String,
    val kgComprados: Double,
    val kgVendidos: Double,
    val kgPerdidos: Double,
    val percentualPerda: Double,
    val valorPerdidoCentavos: Long,
)

data class PerecivelCriticoResumo(
    val kgPintada: Double,
    val descricaoPintada: String?,
    val unidadePintada: String? = null,
    val produtoIdPintada: UUID? = null,
    val kgPodreOuAmanha: Double,
    val descricaoPodre: String?,
    val unidadePodre: String? = null,
    val produtoIdPodre: UUID? = null,
    val moverParaArmazenagem: List<MoverAdvice> = emptyList(),
)

data class MoverAdvice(
    val produtoId: UUID,
    val descricao: String,
    val quantidade: Double,
    val unidade: String,
)

data class EtilenoCapacidadeSnapshot(
    val capacidade: Double,
    val qtdeNaEtileno: Double,
    val qtdeNaFria: Double,
    val folga: Double?,
)
