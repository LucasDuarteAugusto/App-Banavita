package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.banavita.app.data.local.entity.RecebimentoEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface RecebimentoDao {
    @Query("SELECT * FROM recebimento ORDER BY REC_DATA DESC")
    fun observeAll(): Flow<List<RecebimentoEntity>>

    @Query("SELECT * FROM recebimento WHERE REC_VENDA = :vendaId ORDER BY REC_DATA ASC")
    fun observeByVenda(vendaId: UUID): Flow<List<RecebimentoEntity>>

    @Query("SELECT * FROM recebimento WHERE REC_VENDA = :vendaId")
    suspend fun getByVenda(vendaId: UUID): List<RecebimentoEntity>

    @Query("SELECT COALESCE(SUM(REC_VALOR), 0) FROM recebimento WHERE REC_VENDA = :vendaId")
    suspend fun totalByVenda(vendaId: UUID): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(recebimento: RecebimentoEntity)
}
