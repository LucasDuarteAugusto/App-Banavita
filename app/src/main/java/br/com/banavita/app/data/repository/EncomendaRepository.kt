package br.com.banavita.app.data.repository

import androidx.room.withTransaction
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.EncomendaDao
import br.com.banavita.app.data.local.entity.EncomendaEntity
import br.com.banavita.app.data.local.entity.EncomendaItemEntity
import br.com.banavita.app.data.local.relation.EncomendaWithItems
import br.com.banavita.app.domain.exception.InsufficientStockException
import br.com.banavita.app.domain.exception.InvalidDocumentException
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.domain.model.Encomenda
import br.com.banavita.app.domain.model.EncomendaStatus
import br.com.banavita.app.domain.model.MaturacaoEstado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class EncomendaRepository @Inject constructor(
    private val database: Provider<BanavitaDatabase>,
    private val encomendaDao: Provider<EncomendaDao>,
    private val clienteDao: Provider<ClienteDao>,
    private val loteRepository: LoteRepository,
    private val vendaRepository: VendaRepository,
) {
    fun observeAll(): Flow<List<Encomenda>> =
        encomendaDao.get().observeAllWithItems().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: UUID): Encomenda? =
        encomendaDao.get().getWithItems(id)?.toDomain()

    suspend fun create(
        clienteId: UUID,
        dataEntrega: Long,
        itens: List<DocumentoItemInput>,
    ): UUID {
        if (clienteDao.get().getById(clienteId) == null) {
            throw ReferencedEntityException("Cliente $clienteId não encontrado.")
        }
        if (itens.isEmpty()) {
            throw InvalidDocumentException("Informe ao menos um item na encomenda.")
        }
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        val itemEntities = itens.map { input ->
            val estado = input.estado
                ?: throw InvalidDocumentException("Informe o estado de cada item da encomenda.")
            if (input.quantidade <= 0) {
                throw InvalidDocumentException("Quantidade deve ser maior que zero.")
            }
            EncomendaItemEntity(
                id = UUID.randomUUID(),
                createdAt = timestamp,
                updatedAt = timestamp,
                encomendaId = id,
                produtoId = input.produtoId,
                estado = estado.name,
                quantidade = input.quantidade,
            )
        }
        encomendaDao.get().insertComItens(
            EncomendaEntity(
                id = id,
                createdAt = timestamp,
                updatedAt = timestamp,
                clienteId = clienteId,
                dataEntrega = dataEntrega,
                status = EncomendaStatus.ABERTO.name,
                vendaId = null,
            ),
            itemEntities,
        )
        return id
    }

    suspend fun update(
        id: UUID,
        clienteId: UUID,
        dataEntrega: Long,
        itens: List<DocumentoItemInput>,
    ) {
        val existing = encomendaDao.get().getWithItems(id)
            ?: throw ReferencedEntityException("Encomenda $id não encontrada.")
        if (existing.encomenda.status != EncomendaStatus.ABERTO.name) {
            throw InvalidDocumentException("Encomenda já atendida não pode ser alterada.")
        }
        database.get().withTransaction {
            val timestamp = nowEpochMillis()
            val itemEntities = itens.map { input ->
                val estado = input.estado
                    ?: throw InvalidDocumentException("Informe o estado de cada item da encomenda.")
                if (input.quantidade <= 0) {
                    throw InvalidDocumentException("Quantidade deve ser maior que zero.")
                }
                EncomendaItemEntity(
                    id = UUID.randomUUID(),
                    createdAt = timestamp,
                    updatedAt = timestamp,
                    encomendaId = id,
                    produtoId = input.produtoId,
                    estado = estado.name,
                    quantidade = input.quantidade,
                )
            }
            encomendaDao.get().deleteItens(id)
            encomendaDao.get().update(
                existing.encomenda.copy(
                    clienteId = clienteId,
                    dataEntrega = dataEntrega,
                    updatedAt = timestamp,
                ),
            )
            encomendaDao.get().insertItens(itemEntities)
        }
    }

    suspend fun delete(id: UUID) {
        val existing = encomendaDao.get().getWithItems(id) ?: return
        if (existing.encomenda.status != EncomendaStatus.ABERTO.name) {
            throw InvalidDocumentException("Encomenda já atendida não pode ser excluída.")
        }
        encomendaDao.get().delete(id)
    }

    /**
     * Gera uma venda com os itens da encomenda.
     * [permitirParcial]: se faltar saldo, reduz a quantidade ao disponível (com confirmação na UI).
     */
    suspend fun atender(id: UUID, aPrazo: Boolean, permitirParcial: Boolean): UUID {
        val existing = encomendaDao.get().getWithItems(id)
            ?: throw ReferencedEntityException("Encomenda $id não encontrada.")
        if (existing.encomenda.status != EncomendaStatus.ABERTO.name) {
            throw InvalidDocumentException("Esta encomenda já foi atendida.")
        }
        val inputs = mutableListOf<DocumentoItemInput>()
        existing.itens.forEach { item ->
            val estado = MaturacaoEstado.fromStored(item.estado)
                ?: throw InvalidDocumentException("Item da encomenda sem estado.")
            val disponivel = loteRepository.saldoPorEstado(item.produtoId, estado)
            val quantidade = when {
                disponivel + 1e-9 >= item.quantidade -> item.quantidade
                permitirParcial -> disponivel
                else -> throw InsufficientStockException(
                    produtoId = item.produtoId.toString(),
                    disponivel = disponivel,
                    solicitado = item.quantidade,
                )
            }
            if (quantidade > 1e-9) {
                inputs += DocumentoItemInput(
                    produtoId = item.produtoId,
                    quantidade = quantidade,
                    estado = estado,
                )
            }
        }
        if (inputs.isEmpty()) {
            throw InvalidDocumentException("Não há saldo do estado pedido para atender esta encomenda.")
        }
        val vendaId = vendaRepository.create(
            data = nowEpochMillis(),
            clienteId = existing.encomenda.clienteId,
            itens = inputs,
            aPrazo = aPrazo,
        )
        encomendaDao.get().update(
            existing.encomenda.copy(
                status = EncomendaStatus.ATENDIDO.name,
                vendaId = vendaId,
                updatedAt = nowEpochMillis(),
            ),
        )
        return vendaId
    }
}

fun EncomendaWithItems.toDomain(): Encomenda =
    Encomenda(
        id = encomenda.id,
        createdAt = encomenda.createdAt,
        updatedAt = encomenda.updatedAt,
        clienteId = encomenda.clienteId,
        dataEntrega = encomenda.dataEntrega,
        status = EncomendaStatus.valueOf(encomenda.status),
        vendaId = encomenda.vendaId,
        itens = itens.map { item ->
            br.com.banavita.app.domain.model.EncomendaItem(
                id = item.id,
                encomendaId = item.encomendaId,
                produtoId = item.produtoId,
                estado = MaturacaoEstado.valueOf(item.estado),
                quantidade = item.quantidade,
            )
        },
    )
