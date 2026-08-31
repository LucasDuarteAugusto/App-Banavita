package br.com.banavita.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import br.com.banavita.app.data.local.entity.VendaEntity
import br.com.banavita.app.data.local.entity.VendaItemEntity
import br.com.banavita.app.data.local.relation.VendaWithItems
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface VendaDao {
    @Query("SELECT * FROM venda ORDER BY VEN_DATA DESC")
    fun observeAll(): Flow<List<VendaEntity>>

    @Transaction
    @Query("SELECT * FROM venda WHERE VEN_ID = :id")
    suspend fun getWithItems(id: UUID): VendaWithItems?

    @Transaction
    @Query("SELECT * FROM venda ORDER BY VEN_DATA DESC")
    fun observeAllWithItems(): Flow<List<VendaWithItems>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertVenda(venda: VendaEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItens(itens: List<VendaItemEntity>)

    @Update
    suspend fun updateVenda(venda: VendaEntity)

    @Query("DELETE FROM venda_item WHERE VEI_VENDA = :vendaId")
    suspend fun deleteItensByVenda(vendaId: UUID)

    @Query("DELETE FROM venda WHERE VEN_ID = :id")
    suspend fun deleteVenda(id: UUID)

    @Transaction
    suspend fun replaceItens(vendaId: UUID, itens: List<VendaItemEntity>) {
        deleteItensByVenda(vendaId)
        insertItens(itens)
    }

    @Transaction
    suspend fun insertVendaComItens(venda: VendaEntity, itens: List<VendaItemEntity>) {
        insertVenda(venda)
        insertItens(itens)
    }

    @Transaction
    suspend fun updateVendaComItens(venda: VendaEntity, itens: List<VendaItemEntity>) {
        updateVenda(venda)
        replaceItens(venda.id, itens)
    }

    @Transaction
    suspend fun deleteVendaComItens(id: UUID) {
        deleteItensByVenda(id)
        deleteVenda(id)
    }
}
