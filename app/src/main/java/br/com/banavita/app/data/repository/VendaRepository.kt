package br.com.banavita.app.data.repository

import androidx.room.withTransaction
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.RecebimentoDao
import br.com.banavita.app.data.local.dao.VendaDao
import br.com.banavita.app.data.local.entity.RecebimentoEntity
import br.com.banavita.app.data.local.entity.VendaEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.domain.model.Venda
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class VendaRepository @Inject constructor(
    private val database: Provider<BanavitaDatabase>,
    private val vendaDao: Provider<VendaDao>,
    private val clienteDao: Provider<ClienteDao>,
    private val documentoItemFactory: DocumentoItemFactory,
    private val estoqueValidator: EstoqueValidator,
    private val recebimentoDao: Provider<RecebimentoDao>,
    private val loteRepository: LoteRepository,
    private val produtoDao: Provider<br.com.banavita.app.data.local.dao.ProdutoDao>,
) {
    fun observeAll(): Flow<List<Venda>> =
        vendaDao.get().observeAllWithItems().map { vendas -> vendas.map { it.toDomain() } }

    suspend fun getById(id: UUID): Venda? =
        vendaDao.get().getWithItems(id)?.toDomain()

    suspend fun create(
        data: Long,
        clienteId: UUID?,
        itens: List<DocumentoItemInput>,
        aPrazo: Boolean = false,
    ): UUID {
        clienteId?.let {
            if (clienteDao.get().getById(it) == null) {
                throw ReferencedEntityException("Cliente $it não encontrado.")
            }
        }
        val timestamp = now()
        val vendaId = UUID.randomUUID()
        val itemEntities = documentoItemFactory.buildVendaItens(vendaId, itens, timestamp)
        estoqueValidator.validateVenda(itemEntities)
        val total = totalVendaCentavos(itemEntities)
        val venda = VendaEntity(
            id = vendaId,
            createdAt = timestamp,
            updatedAt = timestamp,
            data = data,
            clienteId = clienteId,
            valorTotalCentavos = total,
            aPrazo = aPrazo,
        )
        database.get().withTransaction {
            vendaDao.get().insertVendaComItens(venda, itemEntities)
            baixarPereciveis(vendaId, itemEntities, timestamp)
            if (!aPrazo) {
                recebimentoDao.get().insert(
                    RecebimentoEntity(
                        id = UUID.randomUUID(),
                        createdAt = timestamp,
                        updatedAt = timestamp,
                        vendaId = vendaId,
                        data = data,
                        valorCentavos = total,
                    ),
                )
            }
        }
        return vendaId
    }

    suspend fun update(
        id: UUID,
        data: Long,
        clienteId: UUID?,
        itens: List<DocumentoItemInput>,
    ) {
        val existing = vendaDao.get().getWithItems(id)
            ?: throw ReferencedEntityException("Venda $id não encontrada.")
        clienteId?.let {
            if (clienteDao.get().getById(it) == null) {
                throw ReferencedEntityException("Cliente $it não encontrado.")
            }
        }
        val timestamp = now()
        val itemEntities = documentoItemFactory.buildVendaItens(id, itens, timestamp)
        val venda = existing.venda.copy(
            data = data,
            clienteId = clienteId,
            valorTotalCentavos = totalVendaCentavos(itemEntities),
            updatedAt = timestamp,
        )
        database.get().withTransaction {
            loteRepository.restaurarVenda(id)
            estoqueValidator.validateVenda(itemEntities, excludeVendaId = id)
            vendaDao.get().updateVendaComItens(venda, itemEntities)
            baixarPereciveis(id, itemEntities, timestamp)
        }
    }

    suspend fun delete(id: UUID) {
        database.get().withTransaction {
            loteRepository.restaurarVenda(id)
            vendaDao.get().deleteVendaComItens(id)
        }
    }

    private suspend fun baixarPereciveis(
        vendaId: UUID,
        itens: List<br.com.banavita.app.data.local.entity.VendaItemEntity>,
        timestamp: Long,
    ) {
        itens.forEach { item ->
            val produto = produtoDao.get().getById(item.produtoId) ?: return@forEach
            if (!produto.perecivel) return@forEach
            val estado = br.com.banavita.app.domain.model.MaturacaoEstado.fromStored(item.estado)
                ?: throw br.com.banavita.app.domain.exception.InvalidDocumentException(
                    "Informe o estado da banana para vender.",
                )
            loteRepository.consumirFifo(item.produtoId, estado, item.quantidade, vendaId, timestamp)
        }
    }
}
