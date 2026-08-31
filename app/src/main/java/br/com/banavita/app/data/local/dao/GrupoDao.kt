package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.banavita.app.data.local.entity.GrupoEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface GrupoDao {
    @Query("SELECT * FROM grupo ORDER BY GRU_NOME ASC")
    fun observeAll(): Flow<List<GrupoEntity>>

    @Query("SELECT * FROM grupo WHERE GRU_ID = :id")
    suspend fun getById(id: UUID): GrupoEntity?

    @Query("SELECT COUNT(*) FROM produto WHERE PRO_GRUPO = :grupoId")
    suspend fun countProdutosByGrupo(grupoId: UUID): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(grupo: GrupoEntity)

    @Update
    suspend fun update(grupo: GrupoEntity)

    @Delete
    suspend fun delete(grupo: GrupoEntity)
}
