package br.com.banavita.app.data.repository

import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.dao.FornecedorDao
import br.com.banavita.app.data.local.entity.FornecedorEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.Fornecedor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FornecedorRepository @Inject constructor(
    private val fornecedorDao: FornecedorDao,
) {
    fun observeAll(): Flow<List<Fornecedor>> =
        fornecedorDao.observeAll().map { fornecedores -> fornecedores.map { it.toDomain() } }

    suspend fun getById(id: UUID): Fornecedor? =
        fornecedorDao.getById(id)?.toDomain()

    suspend fun create(nome: String, telefone: String?): UUID {
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        fornecedorDao.insert(
            FornecedorEntity(
                id = id,
                createdAt = timestamp,
                updatedAt = timestamp,
                nome = nome,
                telefone = telefone,
            ),
        )
        return id
    }

    suspend fun update(id: UUID, nome: String, telefone: String?) {
        val existing = fornecedorDao.getById(id)
            ?: throw ReferencedEntityException("Fornecedor $id não encontrado.")
        fornecedorDao.update(
            existing.copy(
                nome = nome,
                telefone = telefone,
                updatedAt = nowEpochMillis(),
            ),
        )
    }

    suspend fun delete(id: UUID) {
        val compras = fornecedorDao.countComprasByFornecedor(id)
        if (compras > 0) {
            throw ReferencedEntityException("Fornecedor possui $compras compra(s) vinculada(s).")
        }
        val existing = fornecedorDao.getById(id) ?: return
        fornecedorDao.delete(existing)
    }
}
