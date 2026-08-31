package br.com.banavita.app.data.repository

import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.dao.GrupoDao
import br.com.banavita.app.data.local.dao.ProdutoDao
import br.com.banavita.app.data.local.entity.ProdutoEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.Produto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProdutoRepository @Inject constructor(
    private val produtoDao: ProdutoDao,
    private val grupoDao: GrupoDao,
) {
    fun observeAll(): Flow<List<Produto>> =
        produtoDao.observeAll().map { produtos -> produtos.map { it.toDomain() } }

    suspend fun getById(id: UUID): Produto? =
        produtoDao.getById(id)?.toDomain()

    suspend fun create(
        descricao: String,
        precoCentavos: Long,
        grupoId: UUID?,
        unidade: String = "UN",
    ): UUID {
        grupoId?.let {
            if (grupoDao.getById(it) == null) {
                throw ReferencedEntityException("Grupo $it não encontrado.")
            }
        }
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        produtoDao.insert(
            ProdutoEntity(
                id = id,
                createdAt = timestamp,
                updatedAt = timestamp,
                descricao = descricao,
                precoCentavos = precoCentavos,
                grupoId = grupoId,
                unidade = unidade,
            ),
        )
        return id
    }

    suspend fun update(
        id: UUID,
        descricao: String,
        precoCentavos: Long,
        grupoId: UUID?,
        unidade: String,
    ) {
        grupoId?.let {
            if (grupoDao.getById(it) == null) {
                throw ReferencedEntityException("Grupo $it não encontrado.")
            }
        }
        val existing = produtoDao.getById(id)
            ?: throw ReferencedEntityException("Produto $id não encontrado.")
        produtoDao.update(
            existing.copy(
                descricao = descricao,
                precoCentavos = precoCentavos,
                grupoId = grupoId,
                unidade = unidade,
                updatedAt = nowEpochMillis(),
            ),
        )
    }

    suspend fun delete(id: UUID) {
        val vendas = produtoDao.countVendasByProduto(id)
        val compras = produtoDao.countComprasByProduto(id)
        if (vendas > 0 || compras > 0) {
            throw ReferencedEntityException(
                "Produto possui movimentações (vendas: $vendas, compras: $compras).",
            )
        }
        val existing = produtoDao.getById(id) ?: return
        produtoDao.delete(existing)
    }
}
