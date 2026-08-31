package br.com.banavita.app.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import br.com.banavita.app.data.local.entity.CompraEntity
import br.com.banavita.app.data.local.entity.CompraItemEntity

data class CompraWithItems(
    @Embedded val compra: CompraEntity,
    @Relation(
        parentColumn = "COM_ID",
        entityColumn = "COI_COMPRA",
    )
    val itens: List<CompraItemEntity>,
)
