package br.com.banavita.app.domain.reminder

import br.com.banavita.app.domain.model.Evento
import java.util.UUID

class FakeReminderScheduler : ReminderScheduler {
    val scheduled = mutableListOf<UUID>()
    val cancelled = mutableListOf<UUID>()

    override fun schedule(evento: Evento) {
        scheduled += evento.id
    }

    override fun cancel(eventoId: UUID) {
        cancelled += eventoId
    }

    override fun rescheduleAll(eventos: List<Evento>) {
        eventos.forEach { schedule(it) }
    }
}
