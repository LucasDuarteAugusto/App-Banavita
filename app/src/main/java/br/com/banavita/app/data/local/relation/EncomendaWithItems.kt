package br.com.banavita.app.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import br.com.banavita.app.data.local.entity.EncomendaEntity
import br.com.banavita.app.data.local.entity.EncomendaItemEntity

data class EncomendaWithItems(
    @Embedded val encomenda: EncomendaEntity,
    @Relation(
        parentColumn = "ENC_ID",
        entityColumn = "ENI_ENCOMENDA",
    )
    val itens: List<EncomendaItemEntity>,
)
