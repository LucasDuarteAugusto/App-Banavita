package br.com.banavita.app.ui.cadastros.fornecedores;

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
public final class FornecedorListViewModel_Factory implements Factory<FornecedorListViewModel> {
  private final Provider<FornecedorRepository> fornecedorRepositoryProvider;

  public FornecedorListViewModel_Factory(
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
  }

  @Override
  public FornecedorListViewModel get() {
    return newInstance(fornecedorRepositoryProvider.get());
  }

  public static FornecedorListViewModel_Factory create(
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    return new FornecedorListViewModel_Factory(fornecedorRepositoryProvider);
  }

  public static FornecedorListViewModel newInstance(FornecedorRepository fornecedorRepository) {
    return new FornecedorListViewModel(fornecedorRepository);
  }
}
