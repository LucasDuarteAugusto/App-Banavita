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
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class CompraRepository @Inject constructor(
    private val database: Provider<BanavitaDatabase>,
    private val compraDao: Provider<CompraDao>,
    private val fornecedorDao: Provider<FornecedorDao>,
    private val documentoItemFactory: DocumentoItemFactory,
    private val pagamentoDao: Provider<PagamentoDao>,
    private val loteRepository: LoteRepository,
) {
    fun observeAll(): Flow<List<Compra>> =
        compraDao.get().observeAllWithItems().map { compras -> compras.map { it.toDomain() } }

    suspend fun getById(id: UUID): Compra? =
        compraDao.get().getWithItems(id)?.toDomain()

    suspend fun create(
        data: Long,
        fornecedorId: UUID?,
        itens: List<DocumentoItemInput>,
        aPrazo: Boolean = false,
        vencimento: Long? = null,
    ): UUID {
        fornecedorId?.let {
            if (fornecedorDao.get().getById(it) == null) {
                throw ReferencedEntityException("Fornecedor $it não encontrado.")
            }
        }
        if (aPrazo && vencimento == null) throw br.com.banavita.app.domain.exception.InvalidDocumentException("Informe o vencimento.")
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
            vencimento = if (aPrazo) vencimento else null,
        )
        database.get().withTransaction {
            compraDao.get().insertCompraComItens(compra, itemEntities)
            loteRepository.criarLotesDaCompra(compraId, data, itemEntities, timestamp)
            if (!aPrazo) {
                pagamentoDao.get().insert(
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
        vencimento: Long? = null,
    ) {
        val existing = compraDao.get().getWithItems(id)
            ?: throw ReferencedEntityException("Compra $id não encontrada.")
        fornecedorId?.let {
            if (fornecedorDao.get().getById(it) == null) {
                throw ReferencedEntityException("Fornecedor $it não encontrado.")
            }
        }
        val timestamp = now()
        val itemEntities = documentoItemFactory.buildCompraItens(id, itens, timestamp)
        val compra = existing.compra.copy(
            data = data,
            fornecedorId = fornecedorId,
            valorTotalCentavos = totalCompraCentavos(itemEntities),
            vencimento = if (existing.compra.aPrazo) vencimento else null,
            updatedAt = timestamp,
        )
        database.get().withTransaction {
            loteRepository.removerLotesDaCompraSeIntocados(id)
            compraDao.get().updateCompraComItens(compra, itemEntities)
            loteRepository.criarLotesDaCompra(id, data, itemEntities, timestamp)
        }
    }

    suspend fun delete(id: UUID) {
        database.get().withTransaction {
            loteRepository.removerLotesDaCompraSeIntocados(id)
            compraDao.get().deleteCompraComItens(id)
        }
    }
}
