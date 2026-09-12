package br.com.banavita.app.core.reminder;

import br.com.banavita.app.data.repository.EventoRepository;
import br.com.banavita.app.data.repository.LoteRepository;
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

  private final Provider<LoteRepository> loteRepositoryProvider;

  private final Provider<PereciveisMorningScheduler> morningSchedulerProvider;

  public BootCompletedReceiver_MembersInjector(Provider<EventoRepository> eventoRepositoryProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider,
      Provider<LoteRepository> loteRepositoryProvider,
      Provider<PereciveisMorningScheduler> morningSchedulerProvider) {
    this.eventoRepositoryProvider = eventoRepositoryProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
    this.loteRepositoryProvider = loteRepositoryProvider;
    this.morningSchedulerProvider = morningSchedulerProvider;
  }

  public static MembersInjector<BootCompletedReceiver> create(
      Provider<EventoRepository> eventoRepositoryProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider,
      Provider<LoteRepository> loteRepositoryProvider,
      Provider<PereciveisMorningScheduler> morningSchedulerProvider) {
    return new BootCompletedReceiver_MembersInjector(eventoRepositoryProvider, reminderSchedulerProvider, loteRepositoryProvider, morningSchedulerProvider);
  }

  @Override
  public void injectMembers(BootCompletedReceiver instance) {
    injectEventoRepository(instance, eventoRepositoryProvider.get());
    injectReminderScheduler(instance, reminderSchedulerProvider.get());
    injectLoteRepository(instance, loteRepositoryProvider.get());
    injectMorningScheduler(instance, morningSchedulerProvider.get());
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

  @InjectedFieldSignature("br.com.banavita.app.core.reminder.BootCompletedReceiver.loteRepository")
  public static void injectLoteRepository(BootCompletedReceiver instance,
      LoteRepository loteRepository) {
    instance.loteRepository = loteRepository;
  }

  @InjectedFieldSignature("br.com.banavita.app.core.reminder.BootCompletedReceiver.morningScheduler")
  public static void injectMorningScheduler(BootCompletedReceiver instance,
      PereciveisMorningScheduler morningScheduler) {
    instance.morningScheduler = morningScheduler;
  }
}
