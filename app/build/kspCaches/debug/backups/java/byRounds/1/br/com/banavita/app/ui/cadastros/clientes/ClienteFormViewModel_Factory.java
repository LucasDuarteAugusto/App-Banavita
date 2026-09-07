package br.com.banavita.app.ui.cadastros.clientes;

import androidx.lifecycle.SavedStateHandle;
import br.com.banavita.app.data.repository.ClienteRepository;
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
public final class ClienteFormViewModel_Factory implements Factory<ClienteFormViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<ClienteRepository> clienteRepositoryProvider;

  public ClienteFormViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ClienteRepository> clienteRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.clienteRepositoryProvider = clienteRepositoryProvider;
  }

  @Override
  public ClienteFormViewModel get() {
    return newInstance(savedStateHandleProvider.get(), clienteRepositoryProvider.get());
  }

  public static ClienteFormViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ClienteRepository> clienteRepositoryProvider) {
    return new ClienteFormViewModel_Factory(savedStateHandleProvider, clienteRepositoryProvider);
  }

  public static ClienteFormViewModel newInstance(SavedStateHandle savedStateHandle,
      ClienteRepository clienteRepository) {
    return new ClienteFormViewModel(savedStateHandle, clienteRepository);
  }
}
