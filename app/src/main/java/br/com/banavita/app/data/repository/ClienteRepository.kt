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
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ClienteRepository @Inject constructor(
    private val clienteDao: Provider<ClienteDao>,
) {
    fun observeAll(): Flow<List<Cliente>> =
        clienteDao.get().observeAll().map { clientes -> clientes.map { it.toDomain() } }

    suspend fun getById(id: UUID): Cliente? =
        clienteDao.get().getById(id)?.toDomain()

    suspend fun create(
        nome: String,
        telefone: String?,
        pontoPreferido: br.com.banavita.app.domain.model.MaturacaoEstado? = null,
    ): UUID {
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        clienteDao.get().insert(
            ClienteEntity(
                id = id,
                createdAt = timestamp,
                updatedAt = timestamp,
                nome = nome,
                telefone = telefone,
                pontoPreferido = pontoPreferido?.name,
            ),
        )
        return id
    }

    suspend fun update(
        id: UUID,
        nome: String,
        telefone: String?,
        pontoPreferido: br.com.banavita.app.domain.model.MaturacaoEstado? = null,
    ) {
        val existing = clienteDao.get().getById(id)
            ?: throw ReferencedEntityException("Cliente $id não encontrado.")
        clienteDao.get().update(
            existing.copy(
                nome = nome,
                telefone = telefone,
                pontoPreferido = pontoPreferido?.name,
                updatedAt = nowEpochMillis(),
            ),
        )
    }

    suspend fun delete(id: UUID) {
        val vendas = clienteDao.get().countVendasByCliente(id)
        if (vendas > 0) {
            throw ReferencedEntityException("Cliente possui $vendas venda(s) vinculada(s).")
        }
        val existing = clienteDao.get().getById(id) ?: return
        clienteDao.get().delete(existing)
    }
}
