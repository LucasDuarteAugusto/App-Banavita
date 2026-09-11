package br.com.banavita.app.domain.model

import java.util.UUID

enum class Camara {
    FRIA,
    ETILENO,
}

enum class MaturacaoEstado {
    VERDE,
    VERDOLENGA,
    AMARELA,
    PINTADA,
    PODRE,
    ;

    fun next(): MaturacaoEstado? = when (this) {
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
    OUTRO,
}

enum class EncomendaStatus {
    ABERTO,
    ATENDIDO,
}

data class RipeningCurve(
    val diasVerde: Int,
    val diasVerdolenga: Int,
    val diasAmarela: Int,
    val diasPintada: Int,
) {
    fun daysOf(estado: MaturacaoEstado): Int? = when (estado) {
        MaturacaoEstado.VERDE -> diasVerde
        MaturacaoEstado.VERDOLENGA -> diasVerdolenga
        MaturacaoEstado.AMARELA -> diasAmarela
        MaturacaoEstado.PINTADA -> diasPintada
        MaturacaoEstado.PODRE -> null
    }

    fun isValid(): Boolean =
        diasVerde > 0 && diasVerdolenga > 0 && diasAmarela > 0 && diasPintada > 0

    companion object {
        val DEFAULT = RipeningCurve(
            diasVerde = 3,
            diasVerdolenga = 2,
            diasAmarela = 2,
            diasPintada = 2,
        )
    }
}

data class PrecosPorEstado(
    val verdeCentavos: Long,
    val verdolengaCentavos: Long,
    val amarelaCentavos: Long,
    val pintadaCentavos: Long,
    val podreCentavos: Long = 0L,
) {
    fun of(estado: MaturacaoEstado): Long = when (estado) {
        MaturacaoEstado.VERDE -> verdeCentavos
        MaturacaoEstado.VERDOLENGA -> verdolengaCentavos
        MaturacaoEstado.AMARELA -> amarelaCentavos
        MaturacaoEstado.PINTADA -> pintadaCentavos
        MaturacaoEstado.PODRE -> podreCentavos
    }

    companion object {
        fun fromTabela(precoCentavos: Long): PrecosPorEstado =
            PrecosPorEstado(
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
)

data class Encomenda(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val clienteId: UUID,
    val dataEntrega: Long,
    val status: EncomendaStatus,
    val vendaId: UUID?,
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
    val produtoIdPintada: UUID? = null,
    val kgPodreOuAmanha: Double,
    val descricaoPodre: String?,
    val produtoIdPodre: UUID? = null,
)

data class EtilenoCapacidadeSnapshot(
    val capacidadeKg: Double,
    val soltarPorDiaKg: Double,
    val kgNaEtileno: Double,
    val kgNaFria: Double,
    val kgSoltosHoje: Double,
    val folgaKg: Double?,
)
