package br.com.banavita.app.core.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.banavita.app.MainActivity
import br.com.banavita.app.R
import br.com.banavita.app.data.repository.EventoRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class AgendaAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var eventoRepository: EventoRepository

    override fun onReceive(context: Context, intent: Intent) {
        val eventoId = intent.getStringExtra(AgendaNotifications.EXTRA_EVENTO_ID) ?: return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val evento = eventoRepository.getById(UUID.fromString(eventoId)) ?: return@launch
                val title = evento.notifTitulo?.takeIf { it.isNotBlank() } ?: evento.titulo
                val body = evento.notifCorpo?.takeIf { it.isNotBlank() }
                    ?: context.getString(R.string.agenda_notification_body)
                showNotification(context, evento.id, title, body)
            } finally {
                pending.finish()
            }
        }
    }

    private fun showNotification(context: Context, eventoId: UUID, title: String, body: String) {
        ensureChannel(context)
        val deepLink = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(AgendaNotifications.DEEP_LINK_PREFIX + eventoId),
            context,
            MainActivity::class.java,
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            eventoId.hashCode() and 0x7fffffff,
            deepLink,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, AgendaNotifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        if (Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(eventoId.hashCode(), notification)
        }
    }

    companion object {
        fun ensureChannel(context: Context) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                AgendaNotifications.CHANNEL_ID,
                AgendaNotifications.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            )
            manager.createNotificationChannel(channel)
        }
    }
}
