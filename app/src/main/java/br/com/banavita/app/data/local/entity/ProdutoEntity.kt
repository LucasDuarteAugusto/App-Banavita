package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "produto",
    foreignKeys = [
        ForeignKey(
            entity = GrupoEntity::class,
            parentColumns = ["GRU_ID"],
            childColumns = ["PRO_GRUPO"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["PRO_GRUPO"]),
    ],
)
data class ProdutoEntity(
    @PrimaryKey
    @ColumnInfo(name = "PRO_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "PRO_DESCRICAO")
    val descricao: String,
    @ColumnInfo(name = "PRO_PRECO")
    val precoCentavos: Long,
    @ColumnInfo(name = "PRO_GRUPO")
    val grupoId: UUID?,
    @ColumnInfo(name = "PRO_UNIDADE", defaultValue = "UN")
    val unidade: String = "UN",
)
