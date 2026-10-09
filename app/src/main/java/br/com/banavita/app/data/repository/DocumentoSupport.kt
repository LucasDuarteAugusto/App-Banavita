package br.com.banavita.app.data.repository

import br.com.banavita.app.core.money.Money
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.dao.EstoqueDao
import br.com.banavita.app.data.local.dao.ProdutoDao
import br.com.banavita.app.data.local.entity.CompraItemEntity
import br.com.banavita.app.data.local.entity.VendaItemEntity
import br.com.banavita.app.domain.exception.InsufficientStockException
import br.com.banavita.app.domain.exception.InvalidDocumentException
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.domain.model.MaturacaoEstado
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class DocumentoItemFactory @Inject constructor(
    private val produtoDao: Provider<ProdutoDao>,
    private val loteRepository: LoteRepository,
) {
    suspend fun buildVendaItens(
        vendaId: UUID,
        inputs: List<DocumentoItemInput>,
        timestamp: Long,
    ): List<VendaItemEntity> {
        validateInputs(inputs)
        return inputs.map { input ->
            val produto = produtoDao.get().getById(input.produtoId)
                ?: throw InvalidDocumentException("Produto ${input.produtoId} não encontrado.")
            if (produto.perecivel) {
                val estado = input.estado
                    ?: throw InvalidDocumentException("Informe o estado da banana para vender.")
                if (estado == MaturacaoEstado.PODRE) {
                    throw InvalidDocumentException("Podre não se vende. Lance como perda.")
                }
            }
            val unitCents = input.valorUnitarioCentavos
                ?: loteRepository.precoVenda(input.produtoId, input.estado)
            VendaItemEntity(
                id = UUID.randomUUID(),
                createdAt = timestamp,
                updatedAt = timestamp,
                vendaId = vendaId,
                produtoId = input.produtoId,
                valorUnitarioCentavos = unitCents,
                quantidade = input.quantidade,
                valorTotalCentavos = Money.lineTotal(unitCents, input.quantidade),
                estado = input.estado?.name,
                unidade = input.unidade ?: produto.unidade,
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
            val produto = produtoDao.get().getById(input.produtoId)
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
                unidade = input.unidade ?: produto.unidade,
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
    private val estoqueDao: Provider<EstoqueDao>,
    private val produtoDao: Provider<ProdutoDao>,
    private val loteDao: Provider<br.com.banavita.app.data.local.dao.LoteDao>,
) {
    suspend fun validateVenda(
        itens: List<VendaItemEntity>,
        excludeVendaId: UUID? = null,
    ) {
        val grupos = itens.groupBy { Triple(it.produtoId, it.estado, it.unidade) }
        grupos.forEach { (chave, linhas) ->
            val (produtoId, estadoRaw, unidade) = chave
            val quantidadeSolicitada = linhas.sumOf { it.quantidade }
            val produto = produtoDao.get().getById(produtoId)
                ?: throw InvalidDocumentException("Produto $produtoId não encontrado.")
            if (produto.perecivel) {
                val estado = MaturacaoEstado.fromStored(estadoRaw)
                    ?: throw InvalidDocumentException("Informe o estado da banana para vender.")
                if (estado == MaturacaoEstado.PODRE) {
                    throw InvalidDocumentException("Podre não se vende. Lance como perda.")
                }
                val disponivel = loteDao.get().saldoPorEstado(produtoId, estado.name, unidade)
                if (disponivel + 1e-9 < quantidadeSolicitada) {
                    throw InsufficientStockException(
                        produtoId = produtoId.toString(),
                        disponivel = disponivel,
                        solicitado = quantidadeSolicitada,
                    )
                }
            } else {
                val comprado = estoqueDao.get().totalComprado(produtoId, unidade)
                val vendido = estoqueDao.get().totalVendido(produtoId, excludeVendaId, unidade)
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
}

internal fun totalVendaCentavos(itens: List<VendaItemEntity>): Long =
    Money.sumCents(itens.map { it.valorTotalCentavos })

internal fun totalCompraCentavos(itens: List<CompraItemEntity>): Long =
    Money.sumCents(itens.map { it.valorTotalCentavos })

internal fun now(): Long = nowEpochMillis()
