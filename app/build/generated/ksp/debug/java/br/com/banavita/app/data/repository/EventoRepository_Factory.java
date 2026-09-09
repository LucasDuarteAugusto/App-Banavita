package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.dao.ClienteDao;
import br.com.banavita.app.data.local.dao.EventoDao;
import br.com.banavita.app.data.local.dao.FornecedorDao;
import br.com.banavita.app.domain.reminder.ReminderScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class EventoRepository_Factory implements Factory<EventoRepository> {
  private final Provider<EventoDao> eventoDaoProvider;

  private final Provider<ClienteDao> clienteDaoProvider;

  private final Provider<FornecedorDao> fornecedorDaoProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  public EventoRepository_Factory(Provider<EventoDao> eventoDaoProvider,
      Provider<ClienteDao> clienteDaoProvider, Provider<FornecedorDao> fornecedorDaoProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    this.eventoDaoProvider = eventoDaoProvider;
    this.clienteDaoProvider = clienteDaoProvider;
    this.fornecedorDaoProvider = fornecedorDaoProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
  }

  @Override
  public EventoRepository get() {
    return newInstance(eventoDaoProvider, clienteDaoProvider, fornecedorDaoProvider, reminderSchedulerProvider.get());
  }

  public static EventoRepository_Factory create(Provider<EventoDao> eventoDaoProvider,
      Provider<ClienteDao> clienteDaoProvider, Provider<FornecedorDao> fornecedorDaoProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    return new EventoRepository_Factory(eventoDaoProvider, clienteDaoProvider, fornecedorDaoProvider, reminderSchedulerProvider);
  }

  public static EventoRepository newInstance(Provider<EventoDao> eventoDao,
      Provider<ClienteDao> clienteDao, Provider<FornecedorDao> fornecedorDao,
      ReminderScheduler reminderScheduler) {
    return new EventoRepository(eventoDao, clienteDao, fornecedorDao, reminderScheduler);
  }
}
