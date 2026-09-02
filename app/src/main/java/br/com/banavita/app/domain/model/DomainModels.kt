package br.com.banavita.app.domain.model

import java.util.UUID

data class Grupo(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val nome: String,
    val observacao: String?,
)

data class Produto(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val descricao: String,
    val precoCentavos: Long,
    val grupoId: UUID?,
    val unidade: String,
)

data class Cliente(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val nome: String,
    val telefone: String?,
)

data class Fornecedor(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val nome: String,
    val telefone: String?,
)

data class DocumentoItemInput(
    val produtoId: UUID,
    val quantidade: Double,
    val valorUnitarioCentavos: Long? = null,
)

data class VendaItem(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val vendaId: UUID,
    val produtoId: UUID,
    val valorUnitarioCentavos: Long,
    val quantidade: Double,
    val valorTotalCentavos: Long,
)

data class Venda(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val data: Long,
    val clienteId: UUID?,
    val valorTotalCentavos: Long,
    val aPrazo: Boolean = false,
    val itens: List<VendaItem>,
)

data class CompraItem(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val compraId: UUID,
    val produtoId: UUID,
    val valorUnitarioCentavos: Long,
    val quantidade: Double,
    val valorTotalCentavos: Long,
)

data class Compra(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val data: Long,
    val fornecedorId: UUID?,
    val valorTotalCentavos: Long,
    val aPrazo: Boolean = false,
    val itens: List<CompraItem>,
)

data class ProdutoEstoque(
    val produtoId: UUID,
    val unidade: String,
    val saldo: Double,
)

data class Recebimento(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val vendaId: UUID,
    val data: Long,
    val valorCentavos: Long,
)

data class Pagamento(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val compraId: UUID,
    val data: Long,
    val valorCentavos: Long,
)

data class ParceiroSaldo(
    val parceiroId: UUID?,
    val nome: String,
    val telefone: String?,
    val saldoCentavos: Long,
    val dataMaisAntiga: Long?,
)

data class DocumentoAberto(
    val documentoId: UUID,
    val data: Long,
    val valorTotalCentavos: Long,
    val saldoCentavos: Long,
    val parceiroNome: String,
)

data class EstoqueMovimento(
    val data: Long,
    val tipo: EstoqueMovimentoTipo,
    val quantidade: Double,
    val documentoId: UUID,
)

data class Evento(
    val id: UUID,
    val createdAt: Long,
    val updatedAt: Long,
    val titulo: String,
    val dataHora: Long,
    val observacao: String?,
    val clienteId: UUID?,
    val fornecedorId: UUID?,
    val notificar: Boolean,
    val antecipacaoMinutos: Int?,
    val notifTitulo: String?,
    val notifCorpo: String?,
)
