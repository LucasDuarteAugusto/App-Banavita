package br.com.banavita.app.data.repository

import br.com.banavita.app.data.local.dao.EstoqueDao
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.model.EstoqueMovimento
import br.com.banavita.app.domain.model.EstoqueMovimentoTipo
import br.com.banavita.app.domain.model.ProdutoEstoque
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class EstoqueRepository @Inject constructor(
    private val estoqueDao: Provider<EstoqueDao>,
) {
    fun observeAll(): Flow<List<ProdutoEstoque>> =
        estoqueDao.get().observeAll().map { estoques -> estoques.map { it.toDomain() } }

    suspend fun getSaldo(produtoId: UUID): Double =
        estoqueDao.get().getByProdutoId(produtoId)?.saldo ?: 0.0

    suspend fun movimentosDoProduto(produtoId: UUID): List<EstoqueMovimento> {
        val entradas = estoqueDao.get().entradasDoProduto(produtoId).map { row ->
            EstoqueMovimento(
                data = row.data,
                tipo = EstoqueMovimentoTipo.ENTRADA,
                quantidade = row.quantidade,
                documentoId = row.documentoId,
            )
        }
        val saidas = estoqueDao.get().saidasDoProduto(produtoId).map { row ->
            EstoqueMovimento(
                data = row.data,
                tipo = EstoqueMovimentoTipo.SAIDA,
                quantidade = row.quantidade,
                documentoId = row.documentoId,
            )
        }
        val perdas = estoqueDao.get().perdasDoProduto(produtoId).map { row ->
            EstoqueMovimento(
                data = row.data,
                tipo = EstoqueMovimentoTipo.PERDA,
                quantidade = row.quantidade,
                documentoId = row.documentoId,
            )
        }
        val transferencias = estoqueDao.get().transferenciasDoProduto(produtoId).map { row ->
            EstoqueMovimento(
                data = row.data,
                tipo = EstoqueMovimentoTipo.TRANSFERENCIA,
                quantidade = row.quantidade,
                documentoId = row.documentoId,
            )
        }
        return (entradas + saidas + perdas + transferencias).sortedByDescending { it.data }
    }
}
