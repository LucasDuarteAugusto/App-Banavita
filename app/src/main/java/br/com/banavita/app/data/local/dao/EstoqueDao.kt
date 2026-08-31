package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.banavita.app.data.local.view.ProdutoEstoqueView
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface EstoqueDao {
    @Query("SELECT * FROM vw_produto_estoque ORDER BY produto_id")
    fun observeAll(): Flow<List<ProdutoEstoqueView>>

    @Query("SELECT * FROM vw_produto_estoque WHERE produto_id = :produtoId")
    suspend fun getByProdutoId(produtoId: UUID): ProdutoEstoqueView?

    @Query(
        """
        SELECT COALESCE(SUM(COI_QTDE), 0)
        FROM compra_item
        WHERE COI_PRODUTO = :produtoId
        """,
    )
    suspend fun totalComprado(produtoId: UUID): Double

    @Query(
        """
        SELECT COALESCE(SUM(VEI_QTDE), 0)
        FROM venda_item
        WHERE VEI_PRODUTO = :produtoId
        AND (:excludeVendaId IS NULL OR VEI_VENDA != :excludeVendaId)
        """,
    )
    suspend fun totalVendido(produtoId: UUID, excludeVendaId: UUID? = null): Double
}
