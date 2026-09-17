package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "lote",
    foreignKeys = [
        ForeignKey(
            entity = ProdutoEntity::class,
            parentColumns = ["PRO_ID"],
            childColumns = ["LOT_PRODUTO"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = CompraEntity::class,
            parentColumns = ["COM_ID"],
            childColumns = ["LOT_COMPRA"],
            onDelete = ForeignKey.SET_NULL,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["LOT_PRODUTO"]),
        Index(value = ["LOT_COMPRA"]),
        Index(value = ["LOT_ESTADO"]),
        Index(value = ["LOT_CAMARA"]),
        Index(value = ["LOT_ORIGEM"]),
    ],
)
data class LoteEntity(
    @PrimaryKey
    @ColumnInfo(name = "LOT_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "LOT_PRODUTO")
    val produtoId: UUID,
    @ColumnInfo(name = "LOT_COMPRA")
    val compraId: UUID?,
    @ColumnInfo(name = "LOT_COMPRA_ITEM")
    val compraItemId: UUID?,
    @ColumnInfo(name = "LOT_ORIGEM")
    val origemId: UUID?,
    @ColumnInfo(name = "LOT_DATA")
    val dataEntrada: Long,
    @ColumnInfo(name = "LOT_QTDE_INICIAL")
    val quantidadeInicial: Double,
    @ColumnInfo(name = "LOT_QTDE_RESTANTE")
    val quantidadeRestante: Double,
    @ColumnInfo(name = "LOT_CAMARA")
    val camara: String,
    @ColumnInfo(name = "LOT_ESTADO")
    val estado: String,
    @ColumnInfo(name = "LOT_ETAPA_INICIO_DAY")
    val etapaInicioEpochDay: Long?,
    @ColumnInfo(name = "LOT_CUSTO_UN")
    val custoUnitarioCentavos: Long,
    @ColumnInfo(name = "LOT_ROTULO")
    val rotulo: String,
    @ColumnInfo(name = "LOT_UNIDADE")
    val unidade: String? = null,
)
