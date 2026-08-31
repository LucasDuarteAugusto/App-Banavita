package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "grupo")
data class GrupoEntity(
    @PrimaryKey
    @ColumnInfo(name = "GRU_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "GRU_NOME")
    val nome: String,
    @ColumnInfo(name = "GRU_OBS")
    val observacao: String?,
)
