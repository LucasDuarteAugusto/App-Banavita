package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "compra_item",
    foreignKeys = [
        ForeignKey(
            entity = CompraEntity::class,
            parentColumns = ["COM_ID"],
            childColumns = ["COI_COMPRA"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = ProdutoEntity::class,
            parentColumns = ["PRO_ID"],
            childColumns = ["COI_PRODUTO"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["COI_COMPRA"]),
        Index(value = ["COI_PRODUTO"]),
    ],
)
data class CompraItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "COI_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "COI_COMPRA")
    val compraId: UUID,
    @ColumnInfo(name = "COI_PRODUTO")
    val produtoId: UUID,
    @ColumnInfo(name = "COI_VLRUN")
    val valorUnitarioCentavos: Long,
    @ColumnInfo(name = "COI_QTDE")
    val quantidade: Double,
    @ColumnInfo(name = "COI_VLRTOT")
    val valorTotalCentavos: Long,
)
