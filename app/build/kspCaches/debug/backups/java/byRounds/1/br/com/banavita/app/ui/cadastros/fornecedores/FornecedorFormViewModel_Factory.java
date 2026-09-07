package br.com.banavita.app.ui.cadastros.fornecedores;

import androidx.lifecycle.SavedStateHandle;
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
public final class FornecedorFormViewModel_Factory implements Factory<FornecedorFormViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<FornecedorRepository> fornecedorRepositoryProvider;

  public FornecedorFormViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
  }

  @Override
  public FornecedorFormViewModel get() {
    return newInstance(savedStateHandleProvider.get(), fornecedorRepositoryProvider.get());
  }

  public static FornecedorFormViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    return new FornecedorFormViewModel_Factory(savedStateHandleProvider, fornecedorRepositoryProvider);
  }

  public static FornecedorFormViewModel newInstance(SavedStateHandle savedStateHandle,
      FornecedorRepository fornecedorRepository) {
    return new FornecedorFormViewModel(savedStateHandle, fornecedorRepository);
  }
}
