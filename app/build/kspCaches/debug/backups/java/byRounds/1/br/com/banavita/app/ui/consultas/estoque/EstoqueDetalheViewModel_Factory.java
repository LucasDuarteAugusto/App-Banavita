package br.com.banavita.app.ui.consultas.estoque;

import androidx.lifecycle.SavedStateHandle;
import br.com.banavita.app.data.repository.EstoqueRepository;
import br.com.banavita.app.data.repository.LoteRepository;
import br.com.banavita.app.data.repository.ProdutoRepository;
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
public final class EstoqueDetalheViewModel_Factory implements Factory<EstoqueDetalheViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<ProdutoRepository> produtoRepositoryProvider;

  private final Provider<EstoqueRepository> estoqueRepositoryProvider;

  private final Provider<LoteRepository> loteRepositoryProvider;

  public EstoqueDetalheViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<EstoqueRepository> estoqueRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.produtoRepositoryProvider = produtoRepositoryProvider;
    this.estoqueRepositoryProvider = estoqueRepositoryProvider;
    this.loteRepositoryProvider = loteRepositoryProvider;
  }

  @Override
  public EstoqueDetalheViewModel get() {
    return newInstance(savedStateHandleProvider.get(), produtoRepositoryProvider.get(), estoqueRepositoryProvider.get(), loteRepositoryProvider.get());
  }

  public static EstoqueDetalheViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<EstoqueRepository> estoqueRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider) {
    return new EstoqueDetalheViewModel_Factory(savedStateHandleProvider, produtoRepositoryProvider, estoqueRepositoryProvider, loteRepositoryProvider);
  }

  public static EstoqueDetalheViewModel newInstance(SavedStateHandle savedStateHandle,
      ProdutoRepository produtoRepository, EstoqueRepository estoqueRepository,
      LoteRepository loteRepository) {
    return new EstoqueDetalheViewModel(savedStateHandle, produtoRepository, estoqueRepository, loteRepository);
  }
}
