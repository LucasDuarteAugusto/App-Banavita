package br.com.banavita.app.data.repository

import androidx.room.withTransaction
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.dao.CompraDao
import br.com.banavita.app.data.local.dao.FornecedorDao
import br.com.banavita.app.data.local.dao.PagamentoDao
import br.com.banavita.app.data.local.entity.CompraEntity
import br.com.banavita.app.data.local.entity.PagamentoEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.Compra
import br.com.banavita.app.domain.model.DocumentoItemInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompraRepository @Inject constructor(
    private val database: BanavitaDatabase,
    private val compraDao: CompraDao,
    private val fornecedorDao: FornecedorDao,
    private val documentoItemFactory: DocumentoItemFactory,
    private val pagamentoDao: PagamentoDao,
) {
    fun observeAll(): Flow<List<Compra>> =
        compraDao.observeAllWithItems().map { compras -> compras.map { it.toDomain() } }

    suspend fun getById(id: UUID): Compra? =
        compraDao.getWithItems(id)?.toDomain()

    suspend fun create(
        data: Long,
        fornecedorId: UUID?,
        itens: List<DocumentoItemInput>,
        aPrazo: Boolean = false,
    ): UUID {
        fornecedorId?.let {
            if (fornecedorDao.getById(it) == null) {
                throw ReferencedEntityException("Fornecedor $it não encontrado.")
            }
        }
        val timestamp = now()
        val compraId = UUID.randomUUID()
        val itemEntities = documentoItemFactory.buildCompraItens(compraId, itens, timestamp)
        val total = totalCompraCentavos(itemEntities)
        val compra = CompraEntity(
            id = compraId,
            createdAt = timestamp,
            updatedAt = timestamp,
            data = data,
            fornecedorId = fornecedorId,
            valorTotalCentavos = total,
            aPrazo = aPrazo,
        )
        database.withTransaction {
            compraDao.insertCompraComItens(compra, itemEntities)
            if (!aPrazo) {
                pagamentoDao.insert(
                    PagamentoEntity(
                        id = UUID.randomUUID(),
                        createdAt = timestamp,
                        updatedAt = timestamp,
                        compraId = compraId,
                        data = data,
                        valorCentavos = total,
                    ),
                )
            }
        }
        return compraId
    }

    suspend fun update(
        id: UUID,
        data: Long,
        fornecedorId: UUID?,
        itens: List<DocumentoItemInput>,
    ) {
        val existing = compraDao.getWithItems(id)
            ?: throw ReferencedEntityException("Compra $id não encontrada.")
        fornecedorId?.let {
            if (fornecedorDao.getById(it) == null) {
                throw ReferencedEntityException("Fornecedor $it não encontrado.")
            }
        }
        val timestamp = now()
        val itemEntities = documentoItemFactory.buildCompraItens(id, itens, timestamp)
        val compra = existing.compra.copy(
            data = data,
            fornecedorId = fornecedorId,
            valorTotalCentavos = totalCompraCentavos(itemEntities),
            updatedAt = timestamp,
        )
        compraDao.updateCompraComItens(compra, itemEntities)
    }

    suspend fun delete(id: UUID) {
        compraDao.deleteCompraComItens(id)
    }
}
