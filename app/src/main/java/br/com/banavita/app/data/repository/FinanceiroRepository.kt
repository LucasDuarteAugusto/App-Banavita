package br.com.banavita.app.data.repository

import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.CompraDao
import br.com.banavita.app.data.local.dao.FornecedorDao
import br.com.banavita.app.data.local.dao.PagamentoDao
import br.com.banavita.app.data.local.dao.RecebimentoDao
import br.com.banavita.app.data.local.dao.VendaDao
import br.com.banavita.app.data.local.entity.PagamentoEntity
import br.com.banavita.app.data.local.entity.RecebimentoEntity
import br.com.banavita.app.domain.exception.OverpaymentException
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.DocumentoAberto
import br.com.banavita.app.domain.model.ParceiroSaldo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceiroRepository @Inject constructor(
    private val vendaDao: VendaDao,
    private val compraDao: CompraDao,
    private val recebimentoDao: RecebimentoDao,
    private val pagamentoDao: PagamentoDao,
    private val clienteDao: ClienteDao,
    private val fornecedorDao: FornecedorDao,
) {
    fun observeSaldosAReceber(): Flow<List<ParceiroSaldo>> =
        combine(
            vendaDao.observeAllWithItems(),
            recebimentoDao.observeAll(),
            clienteDao.observeAll(),
        ) { vendas, recebimentos, clientes ->
            val pagoPorVenda = recebimentos.groupBy { it.vendaId }
                .mapValues { (_, rows) -> rows.sumOf { it.valorCentavos } }
            val clienteMap = clientes.associateBy { it.id }
            vendas
                .filter { it.venda.aPrazo } // WARNING - Call chain on a collection could be converted into 'Sequence' to improve performance
                .map { relation ->
                    val saldo = relation.venda.valorTotalCentavos - (pagoPorVenda[relation.venda.id] ?: 0L)
                    Triple(relation.venda, saldo, clienteMap[relation.venda.clienteId])
                }
                .filter { it.second > 0 }
                .groupBy { it.first.clienteId }
                .map { (clienteId, rows) ->
                    val cliente = rows.first().third
                    ParceiroSaldo(
                        parceiroId = clienteId,
                        nome = cliente?.nome ?: "Cliente avulso",
                        telefone = cliente?.telefone,
                        saldoCentavos = rows.sumOf { it.second },
                        dataMaisAntiga = rows.minOf { it.first.data },
                    )
                }
                .sortedByDescending { it.saldoCentavos }
        }

    fun observeSaldosAPagar(): Flow<List<ParceiroSaldo>> =
        combine(
            compraDao.observeAllWithItems(),
            pagamentoDao.observeAll(),
            fornecedorDao.observeAll(),
        ) { compras, pagamentos, fornecedores ->
            val pagoPorCompra = pagamentos.groupBy { it.compraId }
                .mapValues { (_, rows) -> rows.sumOf { it.valorCentavos } }
            val fornecedorMap = fornecedores.associateBy { it.id }
            compras
                .filter { it.compra.aPrazo } // WARNING - Call chain on a collection could be converted into 'Sequence' to improve performance
                .map { relation ->
                    val saldo = relation.compra.valorTotalCentavos - (pagoPorCompra[relation.compra.id] ?: 0L)
                    Triple(relation.compra, saldo, fornecedorMap[relation.compra.fornecedorId])
                }
                .filter { it.second > 0 }
                .groupBy { it.first.fornecedorId }
                .map { (fornecedorId, rows) ->
                    val fornecedor = rows.first().third
                    ParceiroSaldo(
                        parceiroId = fornecedorId,
                        nome = fornecedor?.nome ?: "Sem fornecedor",
                        telefone = fornecedor?.telefone,
                        saldoCentavos = rows.sumOf { it.second },
                        dataMaisAntiga = rows.minOf { it.first.data },
                    )
                }
                .sortedByDescending { it.saldoCentavos }
        }

    fun observeDocumentosAbertosCliente(clienteId: UUID): Flow<List<DocumentoAberto>> =
        combine(vendaDao.observeAllWithItems(), recebimentoDao.observeAll()) { vendas, recebimentos ->
            val pagoPorVenda = recebimentos.groupBy { it.vendaId }
                .mapValues { (_, rows) -> rows.sumOf { it.valorCentavos } }
            vendas
                .filter { it.venda.aPrazo && it.venda.clienteId == clienteId }
                .mapNotNull { relation ->
                    val saldo = relation.venda.valorTotalCentavos - (pagoPorVenda[relation.venda.id] ?: 0L)
                    if (saldo <= 0) return@mapNotNull null
                    DocumentoAberto(
                        documentoId = relation.venda.id,
                        data = relation.venda.data,
                        valorTotalCentavos = relation.venda.valorTotalCentavos,
                        saldoCentavos = saldo,
                        parceiroNome = "",
                    )
                }
                .sortedBy { it.data }
        }

    fun observeDocumentosAbertosFornecedor(fornecedorId: UUID): Flow<List<DocumentoAberto>> =
        combine(compraDao.observeAllWithItems(), pagamentoDao.observeAll()) { compras, pagamentos ->
            val pagoPorCompra = pagamentos.groupBy { it.compraId }
                .mapValues { (_, rows) -> rows.sumOf { it.valorCentavos } }
            compras
                .filter { it.compra.aPrazo && it.compra.fornecedorId == fornecedorId }
                .mapNotNull { relation ->
                    val saldo = relation.compra.valorTotalCentavos - (pagoPorCompra[relation.compra.id] ?: 0L)
                    if (saldo <= 0) return@mapNotNull null
                    DocumentoAberto(
                        documentoId = relation.compra.id,
                        data = relation.compra.data,
                        valorTotalCentavos = relation.compra.valorTotalCentavos,
                        saldoCentavos = saldo,
                        parceiroNome = "",
                    )
                }
                .sortedBy { it.data }
        }

    suspend fun saldoAbertoVenda(vendaId: UUID): Long {
        val venda = vendaDao.getWithItems(vendaId)?.venda
            ?: throw ReferencedEntityException("Venda $vendaId não encontrada.")
        if (!venda.aPrazo) return 0L
        return venda.valorTotalCentavos - recebimentoDao.totalByVenda(vendaId)
    }

    suspend fun saldoAbertoCompra(compraId: UUID): Long {
        val compra = compraDao.getWithItems(compraId)?.compra
            ?: throw ReferencedEntityException("Compra $compraId não encontrada.")
        if (!compra.aPrazo) return 0L
        return compra.valorTotalCentavos - pagamentoDao.totalByCompra(compraId)
    }

    suspend fun registrarRecebimento(vendaId: UUID, valorCentavos: Long, data: Long = nowEpochMillis()): UUID {
        if (valorCentavos <= 0) {
            throw OverpaymentException("Valor do recebimento deve ser maior que zero.")
        }
        val saldo = saldoAbertoVenda(vendaId)
        if (valorCentavos > saldo) {
            throw OverpaymentException("Valor maior que o saldo em aberto.")
        }
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        recebimentoDao.insert(
            RecebimentoEntity(
                id = id,
                createdAt = timestamp,
                updatedAt = timestamp,
                vendaId = vendaId,
                data = data,
                valorCentavos = valorCentavos,
            ),
        )
        return id
    }

    suspend fun registrarPagamento(compraId: UUID, valorCentavos: Long, data: Long = nowEpochMillis()): UUID {
        if (valorCentavos <= 0) {
            throw OverpaymentException("Valor do pagamento deve ser maior que zero.")
        }
        val saldo = saldoAbertoCompra(compraId)
        if (valorCentavos > saldo) {
            throw OverpaymentException("Valor maior que o saldo em aberto.")
        }
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        pagamentoDao.insert(
            PagamentoEntity(
                id = id,
                createdAt = timestamp,
                updatedAt = timestamp,
                compraId = compraId,
                data = data,
                valorCentavos = valorCentavos,
            ),
        )
        return id
    }

    suspend fun registrarRecebimentoCliente(clienteId: UUID, valorCentavos: Long): List<UUID> {
        val abertos = observeDocumentosAbertosCliente(clienteId).first()
            .map { it.documentoId to it.saldoCentavos }
        return applyFifo(abertos, valorCentavos) { docId, valor ->
            registrarRecebimento(docId, valor)
        }
    }

    suspend fun registrarPagamentoFornecedor(fornecedorId: UUID, valorCentavos: Long): List<UUID> {
        val abertos = observeDocumentosAbertosFornecedor(fornecedorId).first()
            .map { it.documentoId to it.saldoCentavos }
        return applyFifo(abertos, valorCentavos) { docId, valor ->
            registrarPagamento(docId, valor)
        }
    }

    private suspend fun applyFifo(
        abertos: List<Pair<UUID, Long>>,
        valorCentavos: Long,
        registrar: suspend (UUID, Long) -> UUID,
    ): List<UUID> {
        if (valorCentavos <= 0) {
            throw OverpaymentException("Valor deve ser maior que zero.")
        }
        val totalAberto = abertos.sumOf { it.second }
        if (valorCentavos > totalAberto) {
            throw OverpaymentException("Valor maior que o saldo em aberto.")
        }
        var restante = valorCentavos
        val ids = mutableListOf<UUID>()
        for ((docId, saldo) in abertos) {
            if (restante <= 0) break
            val aplicar = minOf(restante, saldo)
            ids += registrar(docId, aplicar)
            restante -= aplicar
        }
        return ids
    }
}
