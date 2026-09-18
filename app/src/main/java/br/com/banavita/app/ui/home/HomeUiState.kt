package br.com.banavita.app.ui.home

import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.Evento
import br.com.banavita.app.domain.model.PeriodoPreset
import br.com.banavita.app.domain.model.MoverAdvice
import java.util.UUID

data class HomeUiState(
    val zerados: Int = 0,
    val vendasHojeCentavos: Long = 0,
    val comprasHojeCentavos: Long = 0,
    val receberCentavos: Long = 0,
    val pagarCentavos: Long = 0,
    val proximoEvento: Evento? = null,
    val kgPintada: Double = 0.0,
    val unidadePintada: String? = null,
    val descricaoPintada: String? = null,
    val produtoIdPintada: UUID? = null,
    val precoQueimaSugeridoCentavos: Long = 0,
    val kgPodreOuAmanha: Double = 0.0,
    val unidadePodre: String? = null,
    val descricaoPodre: String? = null,
    val moverParaArmazenagem: List<MoverAdvice> = emptyList(),
    val encomendasAbertasSemana: Int = 0,
    val showQueimaDialog: Boolean = false,
    val queimaPrecoInput: String = "",
    val queimaError: String? = null,
    val isLoading: Boolean = true,
)

sealed class HomeNavEvent {
    data object NovaVenda : HomeNavEvent()
    data class Consultas(
        val modo: ConsultaModo,
        val busca: String? = null,
        val periodo: PeriodoPreset? = null,
        val criticos: Boolean = false,
    ) : HomeNavEvent()
    data class Agenda(
        val epochDay: Long? = null,
        val monthOverviewHint: Boolean = false,
    ) : HomeNavEvent()
    data class Evento(val eventoId: UUID) : HomeNavEvent()
    data class EstoqueDetalhe(val produtoId: UUID, val somenteCriticos: Boolean = false) : HomeNavEvent()
    data class ReceberDetalhe(val clienteId: UUID) : HomeNavEvent()
    data class PagarDetalhe(val fornecedorId: UUID) : HomeNavEvent()
    data object FilaEtileno : HomeNavEvent()
    data object NovaPerda : HomeNavEvent()
    data object NovaEncomenda : HomeNavEvent()
    data class Encomenda(val encomendaId: UUID) : HomeNavEvent()
}
