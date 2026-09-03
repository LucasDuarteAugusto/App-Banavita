package br.com.banavita.app.core.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.domain.model.Evento
import br.com.banavita.app.domain.reminder.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context, // WARNING - The annotation is only applied to the parameter. An explicit annotation use-site target is recommended.
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(evento: Evento) {
        cancel(evento.id)
        if (!evento.notificar) return
        val now = nowEpochMillis()
        if (evento.dataHora > now) {
            setAlarm(evento, evento.dataHora, AgendaNotifications.KIND_ON_TIME)
        }
        val advance = evento.antecipacaoMinutos
        if (advance != null) {
            val whenAdvance = evento.dataHora - advance * 60_000L
            if (whenAdvance > now) {
                setAlarm(evento, whenAdvance, AgendaNotifications.KIND_ADVANCE)
            }
        }
    }

    override fun cancel(eventoId: UUID) {
        cancelKind(eventoId, AgendaNotifications.KIND_ON_TIME)
        cancelKind(eventoId, AgendaNotifications.KIND_ADVANCE)
    }

    override fun rescheduleAll(eventos: List<Evento>) {
        eventos.forEach { schedule(it) }
    }

    private fun setAlarm(evento: Evento, triggerAt: Long, kind: String) {
        val pending = pendingIntent(evento.id, kind, create = true) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            return
        }
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerAt, pending),
            pending,
        )
    }

    private fun cancelKind(eventoId: UUID, kind: String) {
        val pending = pendingIntent(eventoId, kind, create = false) ?: return
        alarmManager.cancel(pending)
        pending.cancel()
    }

    private fun pendingIntent(eventoId: UUID, kind: String, create: Boolean): PendingIntent? {
        val intent = Intent(context, AgendaAlarmReceiver::class.java).apply {
            putExtra(AgendaNotifications.EXTRA_EVENTO_ID, eventoId.toString())
            putExtra(AgendaNotifications.EXTRA_KIND, kind)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val requestCode = requestCode(eventoId, kind)
        return if (create) {
            PendingIntent.getBroadcast(context, requestCode, intent, flags)
        } else {
            PendingIntent.getBroadcast(context, requestCode, intent, flags or PendingIntent.FLAG_NO_CREATE)
        }
    }

    private fun requestCode(eventoId: UUID, kind: String): Int =
        (eventoId.hashCode() xor kind.hashCode()) and 0x7fffffff
}
