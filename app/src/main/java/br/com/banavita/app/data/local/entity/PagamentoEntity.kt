package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "pagamento",
    foreignKeys = [
        ForeignKey(
            entity = CompraEntity::class,
            parentColumns = ["COM_ID"],
            childColumns = ["PAG_COMPRA"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["PAG_COMPRA"]),
        Index(value = ["PAG_DATA"]),
    ],
)
data class PagamentoEntity(
    @PrimaryKey
    @ColumnInfo(name = "PAG_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "PAG_COMPRA")
    val compraId: UUID,
    @ColumnInfo(name = "PAG_DATA")
    val data: Long,
    @ColumnInfo(name = "PAG_VALOR")
    val valorCentavos: Long,
)
