package br.com.banavita.app.di

import br.com.banavita.app.core.reminder.AndroidReminderScheduler
import br.com.banavita.app.domain.reminder.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReminderModule {
    @Binds
    @Singleton
    abstract fun bindReminderScheduler(impl: AndroidReminderScheduler): ReminderScheduler
}
