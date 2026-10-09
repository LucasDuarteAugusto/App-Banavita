package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import br.com.banavita.app.data.local.entity.EncomendaEntity
import br.com.banavita.app.data.local.entity.EncomendaItemEntity
import br.com.banavita.app.data.local.entity.PerdaEntity
import br.com.banavita.app.data.local.entity.PrecoQueimaEntity
import br.com.banavita.app.data.local.relation.EncomendaWithItems
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PerdaDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(perda: PerdaEntity)

    @Query("SELECT * FROM perda ORDER BY PER_DATA DESC")
    fun observeAll(): Flow<List<PerdaEntity>>

    @Query("SELECT * FROM perda WHERE PER_DATA >= :start AND PER_DATA < :endExclusive ORDER BY PER_DATA DESC")
    suspend fun listNoPeriodo(start: Long, endExclusive: Long): List<PerdaEntity>

    @Query("SELECT * FROM perda WHERE PER_PRODUTO = :produtoId ORDER BY PER_DATA DESC")
    suspend fun listDoProduto(produtoId: UUID): List<PerdaEntity>

    @Query("SELECT COALESCE(SUM(PER_QTDE), 0) FROM perda WHERE PER_PRODUTO = :produtoId AND PER_DATA >= :start AND PER_DATA < :endExclusive")
    suspend fun kgNoPeriodo(produtoId: UUID, start: Long, endExclusive: Long): Double

    @Query("SELECT COALESCE(SUM(PER_VALOR), 0) FROM perda WHERE PER_PRODUTO = :produtoId AND PER_DATA >= :start AND PER_DATA < :endExclusive")
    suspend fun valorNoPeriodo(produtoId: UUID, start: Long, endExclusive: Long): Long
}

@Dao
interface PrecoQueimaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(queima: PrecoQueimaEntity)

    @Query(
        """
        SELECT * FROM preco_queima
        WHERE QUE_PRODUTO = :produtoId AND QUE_ESTADO = :estado
          AND QUE_INICIO <= :nowMillis AND QUE_FIM > :nowMillis
        LIMIT 1
        """,
    )
    suspend fun vigente(produtoId: UUID, estado: String, nowMillis: Long): PrecoQueimaEntity?

    @Query("DELETE FROM preco_queima WHERE QUE_FIM <= :nowMillis")
    suspend fun apagarExpiradas(nowMillis: Long)
}

@Dao
interface EncomendaDao {
    @Transaction
    @Query("SELECT * FROM encomenda ORDER BY ENC_DATA_ENTREGA ASC")
    fun observeAllWithItems(): Flow<List<EncomendaWithItems>>

    @Transaction
    @Query("SELECT * FROM encomenda WHERE ENC_ID = :id")
    suspend fun getWithItems(id: UUID): EncomendaWithItems?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(encomenda: EncomendaEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItens(itens: List<EncomendaItemEntity>)

    @Update
    suspend fun update(encomenda: EncomendaEntity)

    @Query("DELETE FROM encomenda WHERE ENC_ID = :id")
    suspend fun delete(id: UUID)

    @Transaction
    suspend fun insertComItens(encomenda: EncomendaEntity, itens: List<EncomendaItemEntity>) {
        insert(encomenda)
        insertItens(itens)
    }

    @Update
    suspend fun updateItem(item: EncomendaItemEntity)

    @Query("DELETE FROM encomenda_item WHERE ENI_ENCOMENDA = :encomendaId")
    suspend fun deleteItens(encomendaId: UUID)

    @Query(
        """
        SELECT COALESCE(SUM(ENI_QTDE - ENI_ENTREGUE), 0) FROM encomenda_item
        INNER JOIN encomenda ON ENC_ID = ENI_ENCOMENDA
        WHERE ENI_PRODUTO = :produtoId AND ENI_ESTADO = :estado AND ENC_STATUS != 'ENTREGUE'
        """
    )
    suspend fun totalAbertoPorProdutoEstado(produtoId: UUID, estado: String): Double
}
