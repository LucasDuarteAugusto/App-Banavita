package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "lote_baixa",
    foreignKeys = [
        ForeignKey(
            entity = LoteEntity::class,
            parentColumns = ["LOT_ID"],
            childColumns = ["LBA_LOTE"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["LBA_LOTE"]),
        Index(value = ["LBA_VENDA"]),
        Index(value = ["LBA_PERDA"]),
    ],
)
data class LoteBaixaEntity(
    @PrimaryKey
    @ColumnInfo(name = "LBA_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "LBA_LOTE")
    val loteId: UUID,
    @ColumnInfo(name = "LBA_QTDE")
    val quantidade: Double,
    @ColumnInfo(name = "LBA_VENDA")
    val vendaId: UUID?,
    @ColumnInfo(name = "LBA_PERDA")
    val perdaId: UUID?,
)

@Entity(
    tableName = "perda",
    foreignKeys = [
        ForeignKey(
            entity = ProdutoEntity::class,
            parentColumns = ["PRO_ID"],
            childColumns = ["PER_PRODUTO"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = LoteEntity::class,
            parentColumns = ["LOT_ID"],
            childColumns = ["PER_LOTE"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["PER_PRODUTO"]),
        Index(value = ["PER_LOTE"]),
        Index(value = ["PER_DATA"]),
    ],
)
data class PerdaEntity(
    @PrimaryKey
    @ColumnInfo(name = "PER_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "PER_DATA")
    val data: Long,
    @ColumnInfo(name = "PER_PRODUTO")
    val produtoId: UUID,
    @ColumnInfo(name = "PER_LOTE")
    val loteId: UUID?,
    @ColumnInfo(name = "PER_ESTADO")
    val estado: String,
    @ColumnInfo(name = "PER_QTDE")
    val quantidade: Double,
    @ColumnInfo(name = "PER_MOTIVO")
    val motivo: String,
    @ColumnInfo(name = "PER_VALOR")
    val valorCentavos: Long,
)

@Entity(
    tableName = "preco_queima",
    foreignKeys = [
        ForeignKey(
            entity = ProdutoEntity::class,
            parentColumns = ["PRO_ID"],
            childColumns = ["QUE_PRODUTO"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["QUE_PRODUTO", "QUE_ESTADO", "QUE_INICIO"], unique = true),
    ],
)
data class PrecoQueimaEntity(
    @PrimaryKey
    @ColumnInfo(name = "QUE_ID")
    val id: UUID,
    @ColumnInfo(name = "QUE_PRODUTO")
    val produtoId: UUID,
    @ColumnInfo(name = "QUE_ESTADO")
    val estado: String,
    @ColumnInfo(name = "QUE_PRECO")
    val precoCentavos: Long,
    @ColumnInfo(name = "QUE_INICIO")
    val inicioDiaMillis: Long,
    @ColumnInfo(name = "QUE_FIM")
    val fimExclusivoMillis: Long,
)
