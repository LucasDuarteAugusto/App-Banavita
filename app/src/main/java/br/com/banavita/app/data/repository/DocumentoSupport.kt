package br.com.banavita.app.data.repository

import br.com.banavita.app.core.money.Money
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.dao.CompraDao
import br.com.banavita.app.data.local.dao.EstoqueDao
import br.com.banavita.app.data.local.dao.ProdutoDao
import br.com.banavita.app.data.local.entity.CompraEntity
import br.com.banavita.app.data.local.entity.CompraItemEntity
import br.com.banavita.app.data.local.entity.VendaEntity
import br.com.banavita.app.data.local.entity.VendaItemEntity
import br.com.banavita.app.domain.exception.InsufficientStockException
import br.com.banavita.app.domain.exception.InvalidDocumentException
import br.com.banavita.app.domain.model.DocumentoItemInput
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentoItemFactory @Inject constructor(
    private val produtoDao: ProdutoDao,
) {
    suspend fun buildVendaItens(
        vendaId: UUID,
        inputs: List<DocumentoItemInput>,
        timestamp: Long,
    ): List<VendaItemEntity> {
        validateInputs(inputs)
        return inputs.map { input ->
            val produto = produtoDao.getById(input.produtoId)
                ?: throw InvalidDocumentException("Produto ${input.produtoId} não encontrado.")
            val unitCents = input.valorUnitarioCentavos ?: produto.precoCentavos
            VendaItemEntity(
                id = UUID.randomUUID(),
                createdAt = timestamp,
                updatedAt = timestamp,
                vendaId = vendaId,
                produtoId = input.produtoId,
                valorUnitarioCentavos = unitCents,
                quantidade = input.quantidade,
                valorTotalCentavos = Money.lineTotal(unitCents, input.quantidade),
            )
        }
    }

    suspend fun buildCompraItens(
        compraId: UUID,
        inputs: List<DocumentoItemInput>,
        timestamp: Long,
    ): List<CompraItemEntity> {
        validateInputs(inputs)
        return inputs.map { input ->
            val produto = produtoDao.getById(input.produtoId)
                ?: throw InvalidDocumentException("Produto ${input.produtoId} não encontrado.")
            val unitCents = input.valorUnitarioCentavos
                ?: throw InvalidDocumentException("Valor unitário obrigatório para compra.")
            CompraItemEntity(
                id = UUID.randomUUID(),
                createdAt = timestamp,
                updatedAt = timestamp,
                compraId = compraId,
                produtoId = input.produtoId,
                valorUnitarioCentavos = unitCents,
                quantidade = input.quantidade,
                valorTotalCentavos = Money.lineTotal(unitCents, input.quantidade),
            )
        }
    }

    private fun validateInputs(inputs: List<DocumentoItemInput>) {
        if (inputs.isEmpty()) {
            throw InvalidDocumentException("Documento deve conter ao menos um item.")
        }
        inputs.forEach { input ->
            if (input.quantidade <= 0.0) {
                throw InvalidDocumentException("Quantidade deve ser maior que zero.")
            }
        }
    }
}

@Singleton
class EstoqueValidator @Inject constructor(
    private val estoqueDao: EstoqueDao,
) {
    suspend fun validateVenda(
        itens: List<VendaItemEntity>,
        excludeVendaId: UUID? = null,
    ) {
        val demandaPorProduto = itens.groupBy { it.produtoId }
            .mapValues { (_, linhas) -> linhas.sumOf { linha -> linha.quantidade } }

        demandaPorProduto.forEach { (produtoId, quantidadeSolicitada) ->
            val comprado = estoqueDao.totalComprado(produtoId)
            val vendido = estoqueDao.totalVendido(produtoId, excludeVendaId)
            val disponivel = comprado - vendido
            if (disponivel + 1e-9 < quantidadeSolicitada) {
                throw InsufficientStockException(
                    produtoId = produtoId.toString(),
                    disponivel = disponivel,
                    solicitado = quantidadeSolicitada,
                )
            }
        }
    }
}

internal fun totalVendaCentavos(itens: List<VendaItemEntity>): Long =
    Money.sumCents(itens.map { it.valorTotalCentavos })

internal fun totalCompraCentavos(itens: List<CompraItemEntity>): Long =
    Money.sumCents(itens.map { it.valorTotalCentavos })

internal fun now(): Long = nowEpochMillis()
