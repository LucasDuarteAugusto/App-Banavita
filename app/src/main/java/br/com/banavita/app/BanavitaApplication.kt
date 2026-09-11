package br.com.banavita.app

import android.app.Application
import br.com.banavita.app.core.reminder.AgendaAlarmReceiver
import br.com.banavita.app.core.reminder.PereciveisAlarmReceiver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BanavitaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AgendaAlarmReceiver.ensureChannel(this)
        PereciveisAlarmReceiver.ensureChannel(this)
    }
}
