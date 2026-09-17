package br.com.banavita.app

import android.app.Application
import br.com.banavita.app.core.reminder.AgendaAlarmReceiver
import br.com.banavita.app.core.reminder.PereciveisAlarmReceiver
import br.com.banavita.app.data.repository.AppSettingsRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BanavitaApplication : Application() {

    @Inject lateinit var appSettingsRepository: AppSettingsRepository

    override fun onCreate() {
        super.onCreate()
        AgendaAlarmReceiver.ensureChannel(this, appSettingsRepository)
        PereciveisAlarmReceiver.ensureChannel(this, appSettingsRepository)
    }
}
