package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "encomenda",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["CLI_ID"],
            childColumns = ["ENC_CLIENTE"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["ENC_CLIENTE"]),
        Index(value = ["ENC_DATA_ENTREGA"]),
        Index(value = ["ENC_STATUS"]),
    ],
)
data class EncomendaEntity(
    @PrimaryKey
    @ColumnInfo(name = "ENC_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "ENC_CLIENTE")
    val clienteId: UUID,
    @ColumnInfo(name = "ENC_DATA_ENTREGA")
    val dataEntrega: Long,
    @ColumnInfo(name = "ENC_STATUS")
    val status: String,
    @ColumnInfo(name = "ENC_VENDA")
    val vendaId: UUID?,
)

@Entity(
    tableName = "encomenda_item",
    foreignKeys = [
        ForeignKey(
            entity = EncomendaEntity::class,
            parentColumns = ["ENC_ID"],
            childColumns = ["ENI_ENCOMENDA"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = ProdutoEntity::class,
            parentColumns = ["PRO_ID"],
            childColumns = ["ENI_PRODUTO"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["ENI_ENCOMENDA"]),
        Index(value = ["ENI_PRODUTO"]),
    ],
)
data class EncomendaItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "ENI_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "ENI_ENCOMENDA")
    val encomendaId: UUID,
    @ColumnInfo(name = "ENI_PRODUTO")
    val produtoId: UUID,
    @ColumnInfo(name = "ENI_ESTADO")
    val estado: String,
    @ColumnInfo(name = "ENI_QTDE")
    val quantidade: Double,
)
