package br.com.banavita.app.ui.cadastros.produtos;

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
public final class ProdutoListViewModel_Factory implements Factory<ProdutoListViewModel> {
  private final Provider<ProdutoRepository> produtoRepositoryProvider;

  private final Provider<GrupoRepository> grupoRepositoryProvider;

  public ProdutoListViewModel_Factory(Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<GrupoRepository> grupoRepositoryProvider) {
    this.produtoRepositoryProvider = produtoRepositoryProvider;
    this.grupoRepositoryProvider = grupoRepositoryProvider;
  }

  @Override
  public ProdutoListViewModel get() {
    return newInstance(produtoRepositoryProvider.get(), grupoRepositoryProvider.get());
  }

  public static ProdutoListViewModel_Factory create(
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<GrupoRepository> grupoRepositoryProvider) {
    return new ProdutoListViewModel_Factory(produtoRepositoryProvider, grupoRepositoryProvider);
  }

  public static ProdutoListViewModel newInstance(ProdutoRepository produtoRepository,
      GrupoRepository grupoRepository) {
    return new ProdutoListViewModel(produtoRepository, grupoRepository);
  }
}
