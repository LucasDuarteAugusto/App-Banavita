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
import javax.inject.Singleton

@Singleton
class VendaRepository @Inject constructor(
    private val database: BanavitaDatabase,
    private val vendaDao: VendaDao,
    private val clienteDao: ClienteDao,
    private val documentoItemFactory: DocumentoItemFactory,
    private val estoqueValidator: EstoqueValidator,
    private val recebimentoDao: RecebimentoDao,
) {
    fun observeAll(): Flow<List<Venda>> =
        vendaDao.observeAllWithItems().map { vendas -> vendas.map { it.toDomain() } }

    suspend fun getById(id: UUID): Venda? =
        vendaDao.getWithItems(id)?.toDomain()

    suspend fun create(
        data: Long,
        clienteId: UUID?,
        itens: List<DocumentoItemInput>,
        aPrazo: Boolean = false,
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
        database.withTransaction {
            vendaDao.insertVendaComItens(venda, itemEntities)
            if (!aPrazo) {
                recebimentoDao.insert(
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
