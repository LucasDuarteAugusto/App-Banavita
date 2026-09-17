package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.banavita.app.data.local.entity.LoteBaixaEntity
import br.com.banavita.app.data.local.entity.LoteEntity
import br.com.banavita.app.data.local.view.EstoqueLinhaView
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface LoteDao {
    @Query("SELECT * FROM lote WHERE LOT_ID = :id")
    suspend fun getById(id: UUID): LoteEntity?

    @Query("SELECT * FROM lote WHERE LOT_PRODUTO = :produtoId ORDER BY LOT_DATA ASC, created_at ASC")
    fun observeByProduto(produtoId: UUID): Flow<List<LoteEntity>>

    @Query("SELECT * FROM lote WHERE LOT_PRODUTO = :produtoId ORDER BY LOT_DATA ASC, created_at ASC")
    suspend fun listByProduto(produtoId: UUID): List<LoteEntity>

    @Query(
        """
        SELECT * FROM lote
        WHERE LOT_PRODUTO = :produtoId
          AND LOT_ESTADO = :estado
          AND LOT_QTDE_RESTANTE > 0.0000001
        ORDER BY CASE LOT_CAMARA WHEN 'ETILENO' THEN 0 ELSE 1 END, LOT_DATA ASC, created_at ASC
        """,
    )
    suspend fun fifoDisponiveis(produtoId: UUID, estado: String): List<LoteEntity>

    @Query(
        """
        SELECT COALESCE(SUM(LOT_QTDE_RESTANTE), 0) FROM lote
        WHERE LOT_PRODUTO = :produtoId AND LOT_ESTADO = :estado AND LOT_QTDE_RESTANTE > 0.0000001
        """,
    )
    suspend fun saldoPorEstado(produtoId: UUID, estado: String): Double

    @Query(
        """
        SELECT COALESCE(SUM(LOT_QTDE_RESTANTE), 0) FROM lote
        WHERE LOT_CAMARA = 'ETILENO' AND LOT_QTDE_RESTANTE > 0.0000001
        """,
    )
    suspend fun qtdeNaEtileno(): Double

    @Query(
        """
        SELECT COALESCE(SUM(LOT_QTDE_RESTANTE), 0) FROM lote
        WHERE LOT_CAMARA = 'FRIA' AND LOT_QTDE_RESTANTE > 0.0000001
        """,
    )
    suspend fun qtdeNaFria(): Double

    @Query(
        """
        SELECT COALESCE(SUM(LOT_QTDE_INICIAL), 0) FROM lote
        WHERE LOT_CAMARA = 'ETILENO'
          AND LOT_ORIGEM IS NOT NULL
          AND created_at >= :startMillis AND created_at < :endMillis
        """,
    )
    suspend fun qtdeSoltaNoPeriodo(startMillis: Long, endMillis: Long): Double

    @Query(
        """
        SELECT * FROM lote
        WHERE LOT_CAMARA = 'FRIA' AND LOT_QTDE_RESTANTE > 0.0000001
        ORDER BY LOT_DATA ASC, created_at ASC
        """,
    )
    fun observeFilaFria(): Flow<List<LoteEntity>>

    @Query(
        """
        SELECT * FROM lote
        WHERE LOT_CAMARA = 'ETILENO' AND LOT_QTDE_RESTANTE > 0.0000001
        """,
    )
    suspend fun listEtilenoAbertos(): List<LoteEntity>

    @Query("SELECT * FROM lote WHERE LOT_COMPRA = :compraId")
    suspend fun listByCompra(compraId: UUID): List<LoteEntity>

    @Query("SELECT * FROM vw_estoque_linha")
    fun observeLinhas(): Flow<List<EstoqueLinhaView>>

    @Query("SELECT * FROM vw_estoque_linha")
    suspend fun listLinhas(): List<EstoqueLinhaView>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(lote: LoteEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(lotes: List<LoteEntity>)

    @Update
    suspend fun update(lote: LoteEntity)

    @Query("DELETE FROM lote WHERE LOT_COMPRA = :compraId")
    suspend fun deleteByCompra(compraId: UUID)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBaixa(baixa: LoteBaixaEntity)

    @Query("SELECT * FROM lote_baixa WHERE LBA_VENDA = :vendaId")
    suspend fun baixasDaVenda(vendaId: UUID): List<LoteBaixaEntity>

    @Query("DELETE FROM lote_baixa WHERE LBA_VENDA = :vendaId")
    suspend fun deleteBaixasDaVenda(vendaId: UUID)
}
