package br.com.banavita.app.core.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.banavita.app.MainActivity
import br.com.banavita.app.R
import br.com.banavita.app.data.repository.AppSettingsRepository
import br.com.banavita.app.data.repository.LoteRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

@AndroidEntryPoint
class PereciveisAlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var dashboardRepository: br.com.banavita.app.data.repository.DashboardRepository
    @Inject lateinit var loteRepository: LoteRepository
    @Inject lateinit var morningScheduler: PereciveisMorningScheduler
    @Inject lateinit var appSettingsRepository: AppSettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dashboardRepository.refreshStockSnapshot()
                val resumo = loteRepository.resumoCriticos()
                val hasAlert = resumo.kgPintada > 1e-9 || resumo.kgPodreOuAmanha > 1e-9 || resumo.moverParaArmazenagem.isNotEmpty()
                if (hasAlert) {
                    val parts = buildList {
                        if (resumo.kgPintada > 1e-9) {
                            add(
                                context.getString(
                                    R.string.home_card_pereciveis_pintada,
                                    formatQty(resumo.kgPintada),
                                    resumo.unidadePintada ?: "Kg",
                                    resumo.descricaoPintada.orEmpty(),
                                ),
                            )
                        }
                        if (resumo.kgPodreOuAmanha > 1e-9) {
                            add(
                                context.getString(
                                    R.string.home_card_pereciveis_podre,
                                    formatQty(resumo.kgPodreOuAmanha),
                                    resumo.unidadePodre ?: "Kg",
                                ),
                            )
                        }
                        resumo.moverParaArmazenagem.forEach { advice ->
                            add(
                                context.getString(
                                    R.string.home_card_pereciveis_mover,
                                    formatQty(advice.quantidade),
                                    advice.unidade,
                                    advice.descricao,
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

    private fun formatQty(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else String.format(java.util.Locale.getDefault(), "%.1f", value)

    private fun showNotification(context: Context, body: String) {
        ensureChannel(context, appSettingsRepository)
        val deepLink = Intent(
            Intent.ACTION_VIEW,
            PereciveisNotifications.DEEP_LINK.toUri(),
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
        val channelId = getActiveChannelId(appSettingsRepository)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_banavita)
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

    private fun getActiveChannelId(repo: AppSettingsRepository): String {
        return if (repo.getRingtoneUri() != null) {
            "${PereciveisNotifications.CHANNEL_ID}_custom"
        } else {
            PereciveisNotifications.CHANNEL_ID
        }
    }

    companion object {
        fun ensureChannel(context: Context, appSettingsRepository: AppSettingsRepository? = null) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            val soundUri = appSettingsRepository?.getRingtoneUri()?.toUri()
                ?: ("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/${R.raw.olha_a_banana}").toUri()

            val channelId = if (appSettingsRepository?.getRingtoneUri() != null) {
                "${PereciveisNotifications.CHANNEL_ID}_custom"
            } else {
                PereciveisNotifications.CHANNEL_ID
            }

            val channel = NotificationChannel(
                channelId,
                PereciveisNotifications.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                setSound(
                    soundUri,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }
            manager.createNotificationChannel(channel)
        }
    }
}
