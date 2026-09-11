package br.com.banavita.app.ui.agenda;

import androidx.lifecycle.SavedStateHandle;
import br.com.banavita.app.data.repository.ClienteRepository;
import br.com.banavita.app.data.repository.EventoRepository;
import br.com.banavita.app.data.repository.FornecedorRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class EventoFormViewModel_Factory implements Factory<EventoFormViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<EventoRepository> eventoRepositoryProvider;

  private final Provider<ClienteRepository> clienteRepositoryProvider;

  private final Provider<FornecedorRepository> fornecedorRepositoryProvider;

  public EventoFormViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EventoRepository> eventoRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.eventoRepositoryProvider = eventoRepositoryProvider;
    this.clienteRepositoryProvider = clienteRepositoryProvider;
    this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
  }

  @Override
  public EventoFormViewModel get() {
    return newInstance(savedStateHandleProvider.get(), eventoRepositoryProvider.get(), clienteRepositoryProvider.get(), fornecedorRepositoryProvider.get());
  }

  public static EventoFormViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EventoRepository> eventoRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    return new EventoFormViewModel_Factory(savedStateHandleProvider, eventoRepositoryProvider, clienteRepositoryProvider, fornecedorRepositoryProvider);
  }

  public static EventoFormViewModel newInstance(SavedStateHandle savedStateHandle,
      EventoRepository eventoRepository, ClienteRepository clienteRepository,
      FornecedorRepository fornecedorRepository) {
    return new EventoFormViewModel(savedStateHandle, eventoRepository, clienteRepository, fornecedorRepository);
  }
}
