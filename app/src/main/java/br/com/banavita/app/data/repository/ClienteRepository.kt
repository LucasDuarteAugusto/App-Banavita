package br.com.banavita.app.data.repository

import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.entity.ClienteEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.Cliente
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClienteRepository @Inject constructor(
    private val clienteDao: ClienteDao,
) {
    fun observeAll(): Flow<List<Cliente>> =
        clienteDao.observeAll().map { clientes -> clientes.map { it.toDomain() } }

    suspend fun getById(id: UUID): Cliente? =
        clienteDao.getById(id)?.toDomain()

    suspend fun create(nome: String, telefone: String?): UUID {
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        clienteDao.insert(
            ClienteEntity(
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
        val existing = clienteDao.getById(id)
            ?: throw ReferencedEntityException("Cliente $id não encontrado.")
        clienteDao.update(
            existing.copy(
                nome = nome,
                telefone = telefone,
                updatedAt = nowEpochMillis(),
            ),
        )
    }

    suspend fun delete(id: UUID) {
        val vendas = clienteDao.countVendasByCliente(id)
        if (vendas > 0) {
            throw ReferencedEntityException("Cliente possui $vendas venda(s) vinculada(s).")
        }
        val existing = clienteDao.getById(id) ?: return
        clienteDao.delete(existing)
    }
}
