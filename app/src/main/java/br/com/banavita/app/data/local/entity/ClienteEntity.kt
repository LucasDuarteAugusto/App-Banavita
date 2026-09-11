package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "cliente")
data class ClienteEntity(
    @PrimaryKey
    @ColumnInfo(name = "CLI_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "CLI_NOME")
    val nome: String,
    @ColumnInfo(name = "CLI_TELEFONE")
    val telefone: String?,
    @ColumnInfo(name = "CLI_PONTO_PREF")
    val pontoPreferido: String? = null,
)
