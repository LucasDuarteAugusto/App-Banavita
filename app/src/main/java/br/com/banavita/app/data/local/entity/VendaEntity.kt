package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "venda",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["CLI_ID"],
            childColumns = ["VEN_CLIENTE"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["VEN_DATA"]),
        Index(value = ["VEN_CLIENTE"]),
    ],
)
data class VendaEntity(
    @PrimaryKey
    @ColumnInfo(name = "VEN_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "VEN_DATA")
    val data: Long,
    @ColumnInfo(name = "VEN_CLIENTE")
    val clienteId: UUID?,
    @ColumnInfo(name = "VEN_VLRTOT")
    val valorTotalCentavos: Long,
    @ColumnInfo(name = "VEN_PRAZO")
    val aPrazo: Boolean = false,
    @ColumnInfo(name = "VEN_VENCIMENTO")
    val vencimento: Long? = null,
)
