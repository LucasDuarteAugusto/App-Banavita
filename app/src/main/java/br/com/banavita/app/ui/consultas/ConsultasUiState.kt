package br.com.banavita.app.ui.consultas

import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.ConsultaVisao
import br.com.banavita.app.domain.model.EstoqueOrdenacao
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.ParceiroSaldo
import br.com.banavita.app.domain.model.PeriodoPreset
import java.util.UUID

data class ConsultasFilters(
    val modo: ConsultaModo = ConsultaModo.ESTOQUE,
    val busca: String = "",
    val periodo: PeriodoPreset = PeriodoPreset.HOJE,
    val visao: ConsultaVisao = ConsultaVisao.DOCUMENTOS,
    val somenteZerados: Boolean = false,
    val somenteCriticos: Boolean = false,
    val ordenacaoEstoque: EstoqueOrdenacao = EstoqueOrdenacao.SALDO_ASC,
    val intervaloInicioInput: String = "",
    val intervaloFimInput: String = "",
)

data class EstoqueConsultaItem(
    val key: String,
    val produtoId: UUID,
    val descricao: String,
    val unidade: String,
    val saldo: Double,
    val saldoFormatado: String,
    val zerado: Boolean,
    val estado: MaturacaoEstado? = null,
    val camara: Camara? = null,
    val critico: Boolean = false,
)

data class DocumentoConsultaItem(
    val id: UUID,
    val data: Long,
    val parceiro: String,
    val valorCentavos: Long,
)

data class AgregadoConsultaItem(
    val nome: String,
    val quantidadeLabel: String?,
    val valorCentavos: Long,
)

data class ConsultasUiState(
    val filters: ConsultasFilters = ConsultasFilters(),
    val isLoading: Boolean = true,
    val totalLabel: String = "",
    val totalValue: String = "",
    val hint: String? = null,
    val estoqueItems: List<EstoqueConsultaItem> = emptyList(),
    val documentos: List<DocumentoConsultaItem> = emptyList(),
    val agregados: List<AgregadoConsultaItem> = emptyList(),
    val financeiroItems: List<ParceiroSaldo> = emptyList(),
    val previsaoItems: List<PrevisaoConsultaItem> = emptyList(),
    val perdaItems: List<PerdaConsultaItem> = emptyList(),
    val encomendaItems: List<EncomendaConsultaItem> = emptyList(),
)

data class PrevisaoConsultaItem(
    val key: String,
    val descricao: String,
    val estado: MaturacaoEstado,
    val kg3: String,
    val kg7: String,
)

data class PerdaConsultaItem(
    val produtoId: UUID,
    val descricao: String,
    val detalhe: String,
    val valorFormatado: String,
)

data class EncomendaConsultaItem(
    val id: UUID,
    val titulo: String,
    val subtitle: String,
    val trailing: String,
)

sealed class ConsultasNavEvent {
    data class EstoqueDetalhe(val produtoId: UUID) : ConsultasNavEvent()
    data class Venda(val vendaId: UUID) : ConsultasNavEvent()
    data class Compra(val compraId: UUID) : ConsultasNavEvent()
    data class ProdutoEdit(val produtoId: UUID) : ConsultasNavEvent()
    data class ReceberDetalhe(val clienteId: UUID) : ConsultasNavEvent()
    data class PagarDetalhe(val fornecedorId: UUID) : ConsultasNavEvent()
    data class Encomenda(val encomendaId: UUID) : ConsultasNavEvent()
    data object NovaEncomenda : ConsultasNavEvent()
    data object FilaEtileno : ConsultasNavEvent()
    data object NovaPerda : ConsultasNavEvent()
}
