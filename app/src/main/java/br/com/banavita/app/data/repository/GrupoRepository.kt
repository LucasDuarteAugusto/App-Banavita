package br.com.banavita.app.data.repository

import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.dao.GrupoDao
import br.com.banavita.app.data.local.entity.GrupoEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.Grupo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class GrupoRepository @Inject constructor(
    private val grupoDao: Provider<GrupoDao>,
) {
    fun observeAll(): Flow<List<Grupo>> =
        grupoDao.get().observeAll().map { grupos -> grupos.map { it.toDomain() } }

    suspend fun getById(id: UUID): Grupo? =
        grupoDao.get().getById(id)?.toDomain()

    suspend fun create(nome: String, observacao: String?): UUID {
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        grupoDao.get().insert(
            GrupoEntity(
                id = id,
                createdAt = timestamp,
                updatedAt = timestamp,
                nome = nome,
                observacao = observacao,
            ),
        )
        return id
    }

    suspend fun update(id: UUID, nome: String, observacao: String?) {
        val existing = grupoDao.get().getById(id)
            ?: throw ReferencedEntityException("Grupo $id não encontrado.")
        grupoDao.get().update(
            existing.copy(
                nome = nome,
                observacao = observacao,
                updatedAt = nowEpochMillis(),
            ),
        )
    }

    suspend fun delete(id: UUID) {
        val produtos = grupoDao.get().countProdutosByGrupo(id)
        if (produtos > 0) {
            throw ReferencedEntityException("Grupo possui $produtos produto(s) vinculado(s).")
        }
        val existing = grupoDao.get().getById(id)
            ?: return
        grupoDao.get().delete(existing)
    }
}
