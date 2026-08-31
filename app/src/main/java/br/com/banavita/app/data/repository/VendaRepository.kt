package br.com.banavita.app.data.repository

import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.VendaDao
import br.com.banavita.app.data.local.entity.VendaEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.domain.model.Venda
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VendaRepository @Inject constructor(
    private val vendaDao: VendaDao,
    private val clienteDao: ClienteDao,
    private val documentoItemFactory: DocumentoItemFactory,
    private val estoqueValidator: EstoqueValidator,
) {
    fun observeAll(): Flow<List<Venda>> =
        vendaDao.observeAllWithItems().map { vendas -> vendas.map { it.toDomain() } }

    suspend fun getById(id: UUID): Venda? =
        vendaDao.getWithItems(id)?.toDomain()

    suspend fun create(
        data: Long,
        clienteId: UUID?,
        itens: List<DocumentoItemInput>,
    ): UUID {
        clienteId?.let {
            if (clienteDao.getById(it) == null) {
                throw ReferencedEntityException("Cliente $it não encontrado.")
            }
        }
        val timestamp = now()
        val vendaId = UUID.randomUUID()
        val itemEntities = documentoItemFactory.buildVendaItens(vendaId, itens, timestamp)
        estoqueValidator.validateVenda(itemEntities)
        val venda = VendaEntity(
            id = vendaId,
            createdAt = timestamp,
            updatedAt = timestamp,
            data = data,
            clienteId = clienteId,
            valorTotalCentavos = totalVendaCentavos(itemEntities),
        )
        vendaDao.insertVendaComItens(venda, itemEntities)
        return vendaId
    }

    suspend fun update(
        id: UUID,
        data: Long,
        clienteId: UUID?,
        itens: List<DocumentoItemInput>,
    ) {
        val existing = vendaDao.getWithItems(id)
            ?: throw ReferencedEntityException("Venda $id não encontrada.")
        clienteId?.let {
            if (clienteDao.getById(it) == null) {
                throw ReferencedEntityException("Cliente $it não encontrado.")
            }
        }
        val timestamp = now()
        val itemEntities = documentoItemFactory.buildVendaItens(id, itens, timestamp)
        estoqueValidator.validateVenda(itemEntities, excludeVendaId = id)
        val venda = existing.venda.copy(
            data = data,
            clienteId = clienteId,
            valorTotalCentavos = totalVendaCentavos(itemEntities),
            updatedAt = timestamp,
        )
        vendaDao.updateVendaComItens(venda, itemEntities)
    }

    suspend fun delete(id: UUID) {
        vendaDao.deleteVendaComItens(id)
    }
}
