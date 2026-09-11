package br.com.banavita.app.ui.compras;

import br.com.banavita.app.data.repository.CompraRepository;
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
public final class CompraListViewModel_Factory implements Factory<CompraListViewModel> {
  private final Provider<CompraRepository> compraRepositoryProvider;

  private final Provider<FornecedorRepository> fornecedorRepositoryProvider;

  public CompraListViewModel_Factory(Provider<CompraRepository> compraRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    this.compraRepositoryProvider = compraRepositoryProvider;
    this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
  }

  @Override
  public CompraListViewModel get() {
    return newInstance(compraRepositoryProvider.get(), fornecedorRepositoryProvider.get());
  }

  public static CompraListViewModel_Factory create(
      Provider<CompraRepository> compraRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    return new CompraListViewModel_Factory(compraRepositoryProvider, fornecedorRepositoryProvider);
  }

  public static CompraListViewModel newInstance(CompraRepository compraRepository,
      FornecedorRepository fornecedorRepository) {
    return new CompraListViewModel(compraRepository, fornecedorRepository);
  }
}
