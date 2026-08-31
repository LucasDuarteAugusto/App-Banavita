package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "venda_item",
    foreignKeys = [
        ForeignKey(
            entity = VendaEntity::class,
            parentColumns = ["VEN_ID"],
            childColumns = ["VEI_VENDA"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = ProdutoEntity::class,
            parentColumns = ["PRO_ID"],
            childColumns = ["VEI_PRODUTO"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["VEI_VENDA"]),
        Index(value = ["VEI_PRODUTO"]),
    ],
)
data class VendaItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "VEI_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "VEI_VENDA")
    val vendaId: UUID,
    @ColumnInfo(name = "VEI_PRODUTO")
    val produtoId: UUID,
    @ColumnInfo(name = "VEI_VLRUN")
    val valorUnitarioCentavos: Long,
    @ColumnInfo(name = "VEI_QTDE")
    val quantidade: Double,
    @ColumnInfo(name = "VEI_VLRTOT")
    val valorTotalCentavos: Long,
)
