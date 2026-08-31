package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.banavita.app.data.local.entity.ProdutoEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ProdutoDao {
    @Query("SELECT * FROM produto ORDER BY PRO_DESCRICAO ASC")
    fun observeAll(): Flow<List<ProdutoEntity>>

    @Query("SELECT * FROM produto WHERE PRO_ID = :id")
    suspend fun getById(id: UUID): ProdutoEntity?

    @Query("SELECT COUNT(*) FROM venda_item WHERE VEI_PRODUTO = :produtoId")
    suspend fun countVendasByProduto(produtoId: UUID): Int

    @Query("SELECT COUNT(*) FROM compra_item WHERE COI_PRODUTO = :produtoId")
    suspend fun countComprasByProduto(produtoId: UUID): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(produto: ProdutoEntity)

    @Update
    suspend fun update(produto: ProdutoEntity)

    @Delete
    suspend fun delete(produto: ProdutoEntity)
}
