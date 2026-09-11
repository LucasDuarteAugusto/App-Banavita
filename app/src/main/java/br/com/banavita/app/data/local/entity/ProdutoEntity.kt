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
    @ColumnInfo(name = "PRO_PERECIVEL", defaultValue = "0")
    val perecivel: Boolean = false,
    @ColumnInfo(name = "PRO_DIAS_VERDE", defaultValue = "3")
    val diasVerde: Int = 3,
    @ColumnInfo(name = "PRO_DIAS_VERDOLENGA", defaultValue = "2")
    val diasVerdolenga: Int = 2,
    @ColumnInfo(name = "PRO_DIAS_AMARELA", defaultValue = "2")
    val diasAmarela: Int = 2,
    @ColumnInfo(name = "PRO_DIAS_PINTADA", defaultValue = "2")
    val diasPintada: Int = 2,
    @ColumnInfo(name = "PRO_PRECO_VERDE", defaultValue = "0")
    val precoVerdeCentavos: Long = 0,
    @ColumnInfo(name = "PRO_PRECO_VERDOLENGA", defaultValue = "0")
    val precoVerdolengaCentavos: Long = 0,
    @ColumnInfo(name = "PRO_PRECO_AMARELA", defaultValue = "0")
    val precoAmarelaCentavos: Long = 0,
    @ColumnInfo(name = "PRO_PRECO_PINTADA", defaultValue = "0")
    val precoPintadaCentavos: Long = 0,
    @ColumnInfo(name = "PRO_PRECO_PODRE", defaultValue = "0")
    val precoPodreCentavos: Long = 0,
)
