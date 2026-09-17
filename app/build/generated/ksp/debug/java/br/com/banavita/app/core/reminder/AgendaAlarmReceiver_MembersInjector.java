package br.com.banavita.app.core.reminder;

import br.com.banavita.app.data.repository.AppSettingsRepository;
import br.com.banavita.app.data.repository.EventoRepository;
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
public final class AgendaAlarmReceiver_MembersInjector implements MembersInjector<AgendaAlarmReceiver> {
  private final Provider<EventoRepository> eventoRepositoryProvider;

  private final Provider<AppSettingsRepository> appSettingsRepositoryProvider;

  public AgendaAlarmReceiver_MembersInjector(Provider<EventoRepository> eventoRepositoryProvider,
      Provider<AppSettingsRepository> appSettingsRepositoryProvider) {
    this.eventoRepositoryProvider = eventoRepositoryProvider;
    this.appSettingsRepositoryProvider = appSettingsRepositoryProvider;
  }

  public static MembersInjector<AgendaAlarmReceiver> create(
      Provider<EventoRepository> eventoRepositoryProvider,
      Provider<AppSettingsRepository> appSettingsRepositoryProvider) {
    return new AgendaAlarmReceiver_MembersInjector(eventoRepositoryProvider, appSettingsRepositoryProvider);
  }

  @Override
  public void injectMembers(AgendaAlarmReceiver instance) {
    injectEventoRepository(instance, eventoRepositoryProvider.get());
    injectAppSettingsRepository(instance, appSettingsRepositoryProvider.get());
  }

  @InjectedFieldSignature("br.com.banavita.app.core.reminder.AgendaAlarmReceiver.eventoRepository")
  public static void injectEventoRepository(AgendaAlarmReceiver instance,
      EventoRepository eventoRepository) {
    instance.eventoRepository = eventoRepository;
  }

  @InjectedFieldSignature("br.com.banavita.app.core.reminder.AgendaAlarmReceiver.appSettingsRepository")
  public static void injectAppSettingsRepository(AgendaAlarmReceiver instance,
      AppSettingsRepository appSettingsRepository) {
    instance.appSettingsRepository = appSettingsRepository;
  }
}
