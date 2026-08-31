package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import br.com.banavita.app.data.local.entity.CompraEntity
import br.com.banavita.app.data.local.entity.CompraItemEntity
import br.com.banavita.app.data.local.relation.CompraWithItems
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface CompraDao {
    @Query("SELECT * FROM compra ORDER BY COM_DATA DESC")
    fun observeAll(): Flow<List<CompraEntity>>

    @Transaction
    @Query("SELECT * FROM compra WHERE COM_ID = :id")
    suspend fun getWithItems(id: UUID): CompraWithItems?

    @Transaction
    @Query("SELECT * FROM compra ORDER BY COM_DATA DESC")
    fun observeAllWithItems(): Flow<List<CompraWithItems>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCompra(compra: CompraEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItens(itens: List<CompraItemEntity>)

    @Update
    suspend fun updateCompra(compra: CompraEntity)

    @Query("DELETE FROM compra_item WHERE COI_COMPRA = :compraId")
    suspend fun deleteItensByCompra(compraId: UUID)

    @Query("DELETE FROM compra WHERE COM_ID = :id")
    suspend fun deleteCompra(id: UUID)

    @Transaction
    suspend fun replaceItens(compraId: UUID, itens: List<CompraItemEntity>) {
        deleteItensByCompra(compraId)
        insertItens(itens)
    }

    @Transaction
    suspend fun insertCompraComItens(compra: CompraEntity, itens: List<CompraItemEntity>) {
        insertCompra(compra)
        insertItens(itens)
    }

    @Transaction
    suspend fun updateCompraComItens(compra: CompraEntity, itens: List<CompraItemEntity>) {
        updateCompra(compra)
        replaceItens(compra.id, itens)
    }

    @Transaction
    suspend fun deleteCompraComItens(id: UUID) {
        deleteItensByCompra(id)
        deleteCompra(id)
    }
}
