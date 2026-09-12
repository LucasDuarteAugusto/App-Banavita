package br.com.banavita.app.ui.vendas;

import androidx.lifecycle.SavedStateHandle;
import br.com.banavita.app.data.repository.ClienteRepository;
import br.com.banavita.app.data.repository.EstoqueRepository;
import br.com.banavita.app.data.repository.LoteRepository;
import br.com.banavita.app.data.repository.ProdutoRepository;
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
public final class VendaFormViewModel_Factory implements Factory<VendaFormViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<VendaRepository> vendaRepositoryProvider;

  private final Provider<ClienteRepository> clienteRepositoryProvider;

  private final Provider<ProdutoRepository> produtoRepositoryProvider;

  private final Provider<EstoqueRepository> estoqueRepositoryProvider;

  private final Provider<LoteRepository> loteRepositoryProvider;

  public VendaFormViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VendaRepository> vendaRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<EstoqueRepository> estoqueRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.vendaRepositoryProvider = vendaRepositoryProvider;
    this.clienteRepositoryProvider = clienteRepositoryProvider;
    this.produtoRepositoryProvider = produtoRepositoryProvider;
    this.estoqueRepositoryProvider = estoqueRepositoryProvider;
    this.loteRepositoryProvider = loteRepositoryProvider;
  }

  @Override
  public VendaFormViewModel get() {
    return newInstance(savedStateHandleProvider.get(), vendaRepositoryProvider.get(), clienteRepositoryProvider.get(), produtoRepositoryProvider.get(), estoqueRepositoryProvider.get(), loteRepositoryProvider.get());
  }

  public static VendaFormViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<VendaRepository> vendaRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<EstoqueRepository> estoqueRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider) {
    return new VendaFormViewModel_Factory(savedStateHandleProvider, vendaRepositoryProvider, clienteRepositoryProvider, produtoRepositoryProvider, estoqueRepositoryProvider, loteRepositoryProvider);
  }

  public static VendaFormViewModel newInstance(SavedStateHandle savedStateHandle,
      VendaRepository vendaRepository, ClienteRepository clienteRepository,
      ProdutoRepository produtoRepository, EstoqueRepository estoqueRepository,
      LoteRepository loteRepository) {
    return new VendaFormViewModel(savedStateHandle, vendaRepository, clienteRepository, produtoRepository, estoqueRepository, loteRepository);
  }
}
