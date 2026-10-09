package br.com.banavita.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import java.util.UUID

@Entity(tableName = "entrega_item", primaryKeys = ["requestId", "itemId"], foreignKeys = [
    ForeignKey(entity = EncomendaItemEntity::class, parentColumns = ["ENI_ID"], childColumns = ["itemId"], onDelete = ForeignKey.RESTRICT),
    ForeignKey(entity = VendaEntity::class, parentColumns = ["VEN_ID"], childColumns = ["vendaId"], onDelete = ForeignKey.RESTRICT),
], indices = [androidx.room.Index(value = ["itemId"]), androidx.room.Index(value = ["vendaId"])])
data class EntregaItemEntity(
    val requestId: UUID,
    val itemId: UUID,
    val vendaId: UUID,
    val quantidade: Double,
    val data: Long,
)

@Entity(tableName = "estoque_snapshot", primaryKeys = ["dia", "produtoId", "unidade", "estado", "camara"])
data class EstoqueSnapshotEntity(
    val dia: Long,
    val produtoId: UUID,
    val unidade: String,
    val estado: String,
    val camara: String,
    val quantidade: Double,
)

@Entity(tableName = "caixa_base")
data class CaixaBaseEntity(
    @androidx.room.PrimaryKey val id: Int = 1,
    val data: Long,
    val saldoCentavos: Long,
)
