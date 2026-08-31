package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.banavita.app.data.local.entity.ClienteEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ClienteDao {
    @Query("SELECT * FROM cliente ORDER BY CLI_NOME ASC")
    fun observeAll(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM cliente WHERE CLI_ID = :id")
    suspend fun getById(id: UUID): ClienteEntity?

    @Query("SELECT COUNT(*) FROM venda WHERE VEN_CLIENTE = :clienteId")
    suspend fun countVendasByCliente(clienteId: UUID): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(cliente: ClienteEntity)

    @Update
    suspend fun update(cliente: ClienteEntity)

    @Delete
    suspend fun delete(cliente: ClienteEntity)
}
