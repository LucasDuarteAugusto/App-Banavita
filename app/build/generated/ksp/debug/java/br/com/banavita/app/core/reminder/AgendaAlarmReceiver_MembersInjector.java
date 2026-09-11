package br.com.banavita.app.core.reminder;

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

  public AgendaAlarmReceiver_MembersInjector(Provider<EventoRepository> eventoRepositoryProvider) {
    this.eventoRepositoryProvider = eventoRepositoryProvider;
  }

  public static MembersInjector<AgendaAlarmReceiver> create(
      Provider<EventoRepository> eventoRepositoryProvider) {
    return new AgendaAlarmReceiver_MembersInjector(eventoRepositoryProvider);
  }

  @Override
  public void injectMembers(AgendaAlarmReceiver instance) {
    injectEventoRepository(instance, eventoRepositoryProvider.get());
  }

  @InjectedFieldSignature("br.com.banavita.app.core.reminder.AgendaAlarmReceiver.eventoRepository")
  public static void injectEventoRepository(AgendaAlarmReceiver instance,
      EventoRepository eventoRepository) {
    instance.eventoRepository = eventoRepository;
  }
}
