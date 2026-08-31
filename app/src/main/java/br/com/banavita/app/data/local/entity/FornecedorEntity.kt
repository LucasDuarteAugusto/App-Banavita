package br.com.banavita.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "fornecedor")
data class FornecedorEntity(
    @PrimaryKey
    @ColumnInfo(name = "FOR_ID")
    val id: UUID,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
    @ColumnInfo(name = "FOR_NOME")
    val nome: String,
    @ColumnInfo(name = "FOR_TELEFONE")
    val telefone: String?,
)
