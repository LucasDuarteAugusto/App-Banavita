package br.com.banavita.app.ui.common

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.banavita.app.domain.model.*

@Composable
fun AgendaClassification(tipo: EventoTipo, status: EventoStatus, prioridade: Prioridade,
    onTipo: (EventoTipo) -> Unit, onStatus: (EventoStatus) -> Unit, onPrioridade: (Prioridade) -> Unit,
    locked: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (locked) Text("Tipo e status acompanham a encomenda vinculada.")
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EventoTipo.entries.forEach { FilterChip(it == tipo, { if (!locked) onTipo(it) }, { Text(it.label) }, enabled = !locked) }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            EventoStatus.entries.forEach { FilterChip(it == status, { if (!locked) onStatus(it) }, { Text(it.label) }, enabled = !locked) }
        }
        if (tipo == EventoTipo.TAREFA) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Prioridade.entries.forEach { FilterChip(it == prioridade, { onPrioridade(it) }, { Text(it.label) }) }
        }
    }
}
