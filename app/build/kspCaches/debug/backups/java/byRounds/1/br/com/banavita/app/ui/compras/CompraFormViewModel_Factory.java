package br.com.banavita.app.ui.compras;

import androidx.lifecycle.SavedStateHandle;
import br.com.banavita.app.data.repository.CompraRepository;
import br.com.banavita.app.data.repository.FornecedorRepository;
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
public final class CompraFormViewModel_Factory implements Factory<CompraFormViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<CompraRepository> compraRepositoryProvider;

  private final Provider<FornecedorRepository> fornecedorRepositoryProvider;

  private final Provider<ProdutoRepository> produtoRepositoryProvider;

  private final Provider<LoteRepository> loteRepositoryProvider;

  public CompraFormViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CompraRepository> compraRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.compraRepositoryProvider = compraRepositoryProvider;
    this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
    this.produtoRepositoryProvider = produtoRepositoryProvider;
    this.loteRepositoryProvider = loteRepositoryProvider;
  }

  @Override
  public CompraFormViewModel get() {
    return newInstance(savedStateHandleProvider.get(), compraRepositoryProvider.get(), fornecedorRepositoryProvider.get(), produtoRepositoryProvider.get(), loteRepositoryProvider.get());
  }

  public static CompraFormViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<CompraRepository> compraRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider) {
    return new CompraFormViewModel_Factory(savedStateHandleProvider, compraRepositoryProvider, fornecedorRepositoryProvider, produtoRepositoryProvider, loteRepositoryProvider);
  }

  public static CompraFormViewModel newInstance(SavedStateHandle savedStateHandle,
      CompraRepository compraRepository, FornecedorRepository fornecedorRepository,
      ProdutoRepository produtoRepository, LoteRepository loteRepository) {
    return new CompraFormViewModel(savedStateHandle, compraRepository, fornecedorRepository, produtoRepository, loteRepository);
  }
}
