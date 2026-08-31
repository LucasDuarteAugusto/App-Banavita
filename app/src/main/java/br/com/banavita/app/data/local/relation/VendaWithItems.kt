package br.com.banavita.app.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import br.com.banavita.app.data.local.entity.VendaEntity
import br.com.banavita.app.data.local.entity.VendaItemEntity

data class VendaWithItems(
    @Embedded val venda: VendaEntity,
    @Relation(
        parentColumn = "VEN_ID",
        entityColumn = "VEI_VENDA",
    )
    val itens: List<VendaItemEntity>,
)
