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

    @Query(
        """
        SELECT compra.COM_DATA AS data, compra_item.COI_QTDE AS quantidade, compra.COM_ID AS documentoId
        FROM compra_item
        INNER JOIN compra ON compra.COM_ID = compra_item.COI_COMPRA
        WHERE compra_item.COI_PRODUTO = :produtoId
        ORDER BY compra.COM_DATA DESC
        """,
    )
    suspend fun entradasDoProduto(produtoId: UUID): List<EstoqueMovimentoRow>

    @Query(
        """
        SELECT venda.VEN_DATA AS data, venda_item.VEI_QTDE AS quantidade, venda.VEN_ID AS documentoId
        FROM venda_item
        INNER JOIN venda ON venda.VEN_ID = venda_item.VEI_VENDA
        WHERE venda_item.VEI_PRODUTO = :produtoId
        ORDER BY venda.VEN_DATA DESC
        """,
    )
    suspend fun saidasDoProduto(produtoId: UUID): List<EstoqueMovimentoRow>
}
