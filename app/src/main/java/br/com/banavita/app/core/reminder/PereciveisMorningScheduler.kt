package br.com.banavita.app.core.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.repository.AppSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PereciveisMorningScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val settings: AppSettingsRepository,
) {
    fun scheduleNext() {
        PereciveisAlarmReceiver.ensureChannel(context)
        val minutes = settings.getAvisoMatinalMinutos()
        val zone = ZoneId.systemDefault()
        val now = Instant.ofEpochMilli(nowEpochMillis()).atZone(zone)
        val time = LocalTime.of(minutes / 60, minutes % 60)
        var date = now.toLocalDate()
        if (!now.toLocalTime().isBefore(time)) {
            date = date.plusDays(1)
        }
        val triggerAt = PeriodRanges.atTime(date, time, zone)
        val pending = pendingIntent(create = true) ?: return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
            return
        }
        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerAt, pending), pending)
    }

    private fun pendingIntent(create: Boolean): PendingIntent? {
        val intent = Intent(context, PereciveisAlarmReceiver::class.java)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return if (create) {
            PendingIntent.getBroadcast(
                context,
                PereciveisNotifications.ALARM_REQUEST_CODE,
                intent,
                flags,
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                PereciveisNotifications.ALARM_REQUEST_CODE,
                intent,
                flags or PendingIntent.FLAG_NO_CREATE,
            )
        }
    }
}
