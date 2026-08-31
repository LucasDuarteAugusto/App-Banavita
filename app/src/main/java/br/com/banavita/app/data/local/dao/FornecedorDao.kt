package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.banavita.app.data.local.entity.FornecedorEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface FornecedorDao {
    @Query("SELECT * FROM fornecedor ORDER BY FOR_NOME ASC")
    fun observeAll(): Flow<List<FornecedorEntity>>

    @Query("SELECT * FROM fornecedor ORDER BY FOR_NOME ASC")
    suspend fun getAll(): List<FornecedorEntity>

    @Query("SELECT * FROM fornecedor WHERE FOR_ID = :id")
    suspend fun getById(id: UUID): FornecedorEntity?

    @Query("SELECT COUNT(*) FROM compra WHERE COM_FORNECEDOR = :fornecedorId")
    suspend fun countComprasByFornecedor(fornecedorId: UUID): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(fornecedor: FornecedorEntity)

    @Update
    suspend fun update(fornecedor: FornecedorEntity)

    @Delete
    suspend fun delete(fornecedor: FornecedorEntity)
}
