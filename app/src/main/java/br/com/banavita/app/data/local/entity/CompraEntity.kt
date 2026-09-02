package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "compra",
    foreignKeys = [
        ForeignKey(
            entity = FornecedorEntity::class,
            parentColumns = ["FOR_ID"],
            childColumns = ["COM_FORNECEDOR"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["COM_DATA"]),
        Index(value = ["COM_FORNECEDOR"]),
    ],
)
data class CompraEntity(
    @PrimaryKey
    @ColumnInfo(name = "COM_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "COM_DATA")
    val data: Long,
    @ColumnInfo(name = "COM_FORNECEDOR")
    val fornecedorId: UUID?,
    @ColumnInfo(name = "COM_VLRTOT")
    val valorTotalCentavos: Long,
    @ColumnInfo(name = "COM_PRAZO")
    val aPrazo: Boolean = false,
)
