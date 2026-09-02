package br.com.banavita.app.data.local.dao

import java.util.UUID

data class EstoqueMovimentoRow(
    val data: Long,
    val quantidade: Double,
    val documentoId: UUID,
)
