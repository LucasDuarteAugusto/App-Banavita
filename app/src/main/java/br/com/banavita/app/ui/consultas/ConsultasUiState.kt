package br.com.banavita.app.ui.consultas

import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.ConsultaVisao
import br.com.banavita.app.domain.model.EstoqueOrdenacao
import br.com.banavita.app.domain.model.ParceiroSaldo
import br.com.banavita.app.domain.model.PeriodoPreset
import java.util.UUID

data class ConsultasFilters(
    val modo: ConsultaModo = ConsultaModo.ESTOQUE,
    val busca: String = "",
    val periodo: PeriodoPreset = PeriodoPreset.HOJE,
    val visao: ConsultaVisao = ConsultaVisao.DOCUMENTOS,
    val somenteZerados: Boolean = false,
    val ordenacaoEstoque: EstoqueOrdenacao = EstoqueOrdenacao.SALDO_ASC,
    val intervaloInicioInput: String = "",
    val intervaloFimInput: String = "",
)

data class EstoqueConsultaItem(
    val produtoId: UUID,
    val descricao: String,
    val unidade: String,
    val saldo: Double,
    val saldoFormatado: String,
    val zerado: Boolean,
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
    val estoqueItems: List<EstoqueConsultaItem> = emptyList(),
    val documentos: List<DocumentoConsultaItem> = emptyList(),
    val agregados: List<AgregadoConsultaItem> = emptyList(),
    val financeiroItems: List<ParceiroSaldo> = emptyList(),
)

sealed class ConsultasNavEvent {
    data class EstoqueDetalhe(val produtoId: UUID) : ConsultasNavEvent()
    data class Venda(val vendaId: UUID) : ConsultasNavEvent()
    data class Compra(val compraId: UUID) : ConsultasNavEvent()
    data class ProdutoEdit(val produtoId: UUID) : ConsultasNavEvent()
    data class ReceberDetalhe(val clienteId: UUID) : ConsultasNavEvent()
    data class PagarDetalhe(val fornecedorId: UUID) : ConsultasNavEvent()
}
