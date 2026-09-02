package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.com.banavita.app.data.local.entity.PagamentoEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PagamentoDao {
    @Query("SELECT * FROM pagamento ORDER BY PAG_DATA DESC")
    fun observeAll(): Flow<List<PagamentoEntity>>

    @Query("SELECT * FROM pagamento WHERE PAG_COMPRA = :compraId ORDER BY PAG_DATA ASC")
    fun observeByCompra(compraId: UUID): Flow<List<PagamentoEntity>>

    @Query("SELECT * FROM pagamento WHERE PAG_COMPRA = :compraId")
    suspend fun getByCompra(compraId: UUID): List<PagamentoEntity>

    @Query("SELECT COALESCE(SUM(PAG_VALOR), 0) FROM pagamento WHERE PAG_COMPRA = :compraId")
    suspend fun totalByCompra(compraId: UUID): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pagamento: PagamentoEntity)
}
