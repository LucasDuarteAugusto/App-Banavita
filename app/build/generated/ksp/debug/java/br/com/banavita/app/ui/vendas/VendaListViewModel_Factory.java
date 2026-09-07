package br.com.banavita.app.ui.vendas;

import br.com.banavita.app.data.repository.ClienteRepository;
import br.com.banavita.app.data.repository.VendaRepository;
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
public final class VendaListViewModel_Factory implements Factory<VendaListViewModel> {
  private final Provider<VendaRepository> vendaRepositoryProvider;

  private final Provider<ClienteRepository> clienteRepositoryProvider;

  public VendaListViewModel_Factory(Provider<VendaRepository> vendaRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider) {
    this.vendaRepositoryProvider = vendaRepositoryProvider;
    this.clienteRepositoryProvider = clienteRepositoryProvider;
  }

  @Override
  public VendaListViewModel get() {
    return newInstance(vendaRepositoryProvider.get(), clienteRepositoryProvider.get());
  }

  public static VendaListViewModel_Factory create(Provider<VendaRepository> vendaRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider) {
    return new VendaListViewModel_Factory(vendaRepositoryProvider, clienteRepositoryProvider);
  }

  public static VendaListViewModel newInstance(VendaRepository vendaRepository,
      ClienteRepository clienteRepository) {
    return new VendaListViewModel(vendaRepository, clienteRepository);
  }
}
