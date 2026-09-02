package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "recebimento",
    foreignKeys = [
        ForeignKey(
            entity = VendaEntity::class,
            parentColumns = ["VEN_ID"],
            childColumns = ["REC_VENDA"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["REC_VENDA"]),
        Index(value = ["REC_DATA"]),
    ],
)
data class RecebimentoEntity(
    @PrimaryKey
    @ColumnInfo(name = "REC_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "REC_VENDA")
    val vendaId: UUID,
    @ColumnInfo(name = "REC_DATA")
    val data: Long,
    @ColumnInfo(name = "REC_VALOR")
    val valorCentavos: Long,
)
