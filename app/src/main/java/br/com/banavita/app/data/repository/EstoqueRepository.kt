package br.com.banavita.app.data.repository

import br.com.banavita.app.data.local.dao.EstoqueDao
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.model.ProdutoEstoque
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EstoqueRepository @Inject constructor(
    private val estoqueDao: EstoqueDao,
) {
    fun observeAll(): Flow<List<ProdutoEstoque>> =
        estoqueDao.observeAll().map { estoques -> estoques.map { it.toDomain() } }

    suspend fun getSaldo(produtoId: UUID): Double =
        estoqueDao.getByProdutoId(produtoId)?.saldo ?: 0.0
}
