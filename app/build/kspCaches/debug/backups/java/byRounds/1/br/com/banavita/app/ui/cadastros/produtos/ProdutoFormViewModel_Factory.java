package br.com.banavita.app.ui.cadastros.produtos;

import androidx.lifecycle.SavedStateHandle;
import br.com.banavita.app.data.repository.GrupoRepository;
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
public final class ProdutoFormViewModel_Factory implements Factory<ProdutoFormViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<ProdutoRepository> produtoRepositoryProvider;

  private final Provider<GrupoRepository> grupoRepositoryProvider;

  public ProdutoFormViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<GrupoRepository> grupoRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.produtoRepositoryProvider = produtoRepositoryProvider;
    this.grupoRepositoryProvider = grupoRepositoryProvider;
  }

  @Override
  public ProdutoFormViewModel get() {
    return newInstance(savedStateHandleProvider.get(), produtoRepositoryProvider.get(), grupoRepositoryProvider.get());
  }

  public static ProdutoFormViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<GrupoRepository> grupoRepositoryProvider) {
    return new ProdutoFormViewModel_Factory(savedStateHandleProvider, produtoRepositoryProvider, grupoRepositoryProvider);
  }

  public static ProdutoFormViewModel newInstance(SavedStateHandle savedStateHandle,
      ProdutoRepository produtoRepository, GrupoRepository grupoRepository) {
    return new ProdutoFormViewModel(savedStateHandle, produtoRepository, grupoRepository);
  }
}
