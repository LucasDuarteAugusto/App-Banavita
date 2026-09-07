package br.com.banavita.app.core.reminder;

import br.com.banavita.app.data.repository.EventoRepository;
import br.com.banavita.app.domain.reminder.ReminderScheduler;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class BootCompletedReceiver_MembersInjector implements MembersInjector<BootCompletedReceiver> {
  private final Provider<EventoRepository> eventoRepositoryProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  public BootCompletedReceiver_MembersInjector(Provider<EventoRepository> eventoRepositoryProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    this.eventoRepositoryProvider = eventoRepositoryProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
  }

  public static MembersInjector<BootCompletedReceiver> create(
      Provider<EventoRepository> eventoRepositoryProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    return new BootCompletedReceiver_MembersInjector(eventoRepositoryProvider, reminderSchedulerProvider);
  }

  @Override
  public void injectMembers(BootCompletedReceiver instance) {
    injectEventoRepository(instance, eventoRepositoryProvider.get());
    injectReminderScheduler(instance, reminderSchedulerProvider.get());
  }

  @InjectedFieldSignature("br.com.banavita.app.core.reminder.BootCompletedReceiver.eventoRepository")
  public static void injectEventoRepository(BootCompletedReceiver instance,
      EventoRepository eventoRepository) {
    instance.eventoRepository = eventoRepository;
  }

  @InjectedFieldSignature("br.com.banavita.app.core.reminder.BootCompletedReceiver.reminderScheduler")
  public static void injectReminderScheduler(BootCompletedReceiver instance,
      ReminderScheduler reminderScheduler) {
    instance.reminderScheduler = reminderScheduler;
  }
}
