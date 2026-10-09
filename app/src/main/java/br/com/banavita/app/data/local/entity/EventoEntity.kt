package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "evento",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["CLI_ID"],
            childColumns = ["EVE_CLIENTE"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = FornecedorEntity::class,
            parentColumns = ["FOR_ID"],
            childColumns = ["EVE_FORNECEDOR"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.NO_ACTION,
        ),
    ],
    indices = [
        Index(value = ["EVE_DATAHORA"]),
        Index(value = ["EVE_CLIENTE"]),
        Index(value = ["EVE_FORNECEDOR"]),
    ],
)
data class EventoEntity(
    @PrimaryKey
    @ColumnInfo(name = "EVE_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "EVE_TITULO")
    val titulo: String,
    @ColumnInfo(name = "EVE_DATAHORA")
    val dataHora: Long,
    @ColumnInfo(name = "EVE_OBS")
    val observacao: String?,
    @ColumnInfo(name = "EVE_CLIENTE")
    val clienteId: UUID?,
    @ColumnInfo(name = "EVE_FORNECEDOR")
    val fornecedorId: UUID?,
    @ColumnInfo(name = "EVE_NOTIFICAR")
    val notificar: Boolean = true,
    @ColumnInfo(name = "EVE_ANTECIPACAO_MIN")
    val antecipacaoMinutos: Int? = null,
    @ColumnInfo(name = "EVE_NOTIF_TITULO")
    val notifTitulo: String? = null,
    @ColumnInfo(name = "EVE_NOTIF_CORPO")
    val notifCorpo: String? = null,
    @ColumnInfo(name = "EVE_TIPO", defaultValue = "'LEMBRETE'")
    val tipo: String = "LEMBRETE",
    @ColumnInfo(name = "EVE_STATUS", defaultValue = "'PENDENTE'")
    val status: String = "PENDENTE",
    @ColumnInfo(name = "EVE_PRIORIDADE", defaultValue = "'MEDIA'")
    val prioridade: String = "MEDIA",
)
