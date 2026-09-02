package br.com.banavita.app.domain.reminder

import br.com.banavita.app.domain.model.Evento
import java.util.UUID

interface ReminderScheduler {
    fun schedule(evento: Evento)
    fun cancel(eventoId: UUID)
    fun rescheduleAll(eventos: List<Evento>)
}
