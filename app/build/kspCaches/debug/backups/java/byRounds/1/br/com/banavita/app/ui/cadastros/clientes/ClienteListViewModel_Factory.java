package br.com.banavita.app.ui.cadastros.clientes;

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
public final class ClienteListViewModel_Factory implements Factory<ClienteListViewModel> {
  private final Provider<ClienteRepository> clienteRepositoryProvider;

  public ClienteListViewModel_Factory(Provider<ClienteRepository> clienteRepositoryProvider) {
    this.clienteRepositoryProvider = clienteRepositoryProvider;
  }

  @Override
  public ClienteListViewModel get() {
    return newInstance(clienteRepositoryProvider.get());
  }

  public static ClienteListViewModel_Factory create(
      Provider<ClienteRepository> clienteRepositoryProvider) {
    return new ClienteListViewModel_Factory(clienteRepositoryProvider);
  }

  public static ClienteListViewModel newInstance(ClienteRepository clienteRepository) {
    return new ClienteListViewModel(clienteRepository);
  }
}
