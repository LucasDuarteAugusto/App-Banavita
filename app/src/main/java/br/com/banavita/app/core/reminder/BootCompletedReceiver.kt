package br.com.banavita.app.core.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.repository.EventoRepository
import br.com.banavita.app.data.repository.LoteRepository
import br.com.banavita.app.domain.reminder.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject lateinit var eventoRepository: EventoRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler
    @Inject lateinit var loteRepository: LoteRepository
    @Inject lateinit var morningScheduler: PereciveisMorningScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val eventos = eventoRepository.getPendingReminders(nowEpochMillis())
                reminderScheduler.rescheduleAll(eventos)
                loteRepository.avancarMaturacao()
                morningScheduler.scheduleNext()
            } finally {
                pending.finish()
            }
        }
    }
}
