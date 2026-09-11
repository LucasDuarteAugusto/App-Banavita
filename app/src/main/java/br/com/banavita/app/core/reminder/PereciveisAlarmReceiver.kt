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
import br.com.banavita.app.data.repository.LoteRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PereciveisAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var loteRepository: LoteRepository
    @Inject lateinit var morningScheduler: PereciveisMorningScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                loteRepository.avancarMaturacao()
                val resumo = loteRepository.resumoCriticos()
                val hasAlert = resumo.kgPintada > 1e-9 || resumo.kgPodreOuAmanha > 1e-9
                if (hasAlert) {
                    val parts = buildList {
                        if (resumo.kgPintada > 1e-9) {
                            add(
                                context.getString(
                                    R.string.home_card_pereciveis_pintada,
                                    formatKg(resumo.kgPintada),
                                    resumo.descricaoPintada.orEmpty(),
                                ),
                            )
                        }
                        if (resumo.kgPodreOuAmanha > 1e-9) {
                            add(
                                context.getString(
                                    R.string.home_card_pereciveis_podre,
                                    formatKg(resumo.kgPodreOuAmanha),
                                ),
                            )
                        }
                    }
                    showNotification(context, parts.joinToString(" · "))
                }
                morningScheduler.scheduleNext()
            } finally {
                pending.finish()
            }
        }
    }

    private fun formatKg(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else String.format("%.1f", value)

    private fun showNotification(context: Context, body: String) {
        ensureChannel(context)
        val deepLink = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(PereciveisNotifications.DEEP_LINK),
            context,
            MainActivity::class.java,
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            PereciveisNotifications.NOTIFICATION_ID,
            deepLink,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, PereciveisNotifications.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(context.getString(R.string.notif_pereciveis_title))
            .setContentText(context.getString(R.string.notif_pereciveis_body, body))
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        if (Build.VERSION.SDK_INT < 33 ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context)
                .notify(PereciveisNotifications.NOTIFICATION_ID, notification)
        }
    }

    companion object {
        fun ensureChannel(context: Context) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                PereciveisNotifications.CHANNEL_ID,
                PereciveisNotifications.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            )
            manager.createNotificationChannel(channel)
        }
    }
}
