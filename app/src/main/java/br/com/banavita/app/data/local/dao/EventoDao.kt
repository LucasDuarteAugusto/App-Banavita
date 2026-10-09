package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.banavita.app.data.local.entity.EventoEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface EventoDao {
    @Query("SELECT * FROM evento ORDER BY EVE_DATAHORA ASC")
    fun observeAll(): Flow<List<EventoEntity>>

    @Query("SELECT * FROM evento WHERE EVE_DATAHORA >= :fromMillis ORDER BY EVE_DATAHORA ASC")
    fun observeFrom(fromMillis: Long): Flow<List<EventoEntity>>

    @Query("SELECT * FROM evento WHERE EVE_ID = :id")
    suspend fun getById(id: UUID): EventoEntity?

    @Query("SELECT * FROM evento WHERE EVE_STATUS != 'CONCLUIDO' AND EVE_NOTIFICAR = 1 AND EVE_DATAHORA >= :fromMillis")
    suspend fun getPendingReminders(fromMillis: Long): List<EventoEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(evento: EventoEntity)

    @Update
    suspend fun update(evento: EventoEntity)

    @Query("SELECT COUNT(*) FROM encomenda WHERE ENC_EVENTO = :id")
    suspend fun isLinked(id: UUID): Int

    @Delete
    suspend fun delete(evento: EventoEntity)
}
