package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.dao.GrupoDao;
import br.com.banavita.app.data.local.dao.ProdutoDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ProdutoRepository_Factory implements Factory<ProdutoRepository> {
  private final Provider<ProdutoDao> produtoDaoProvider;

  private final Provider<GrupoDao> grupoDaoProvider;

  public ProdutoRepository_Factory(Provider<ProdutoDao> produtoDaoProvider,
      Provider<GrupoDao> grupoDaoProvider) {
    this.produtoDaoProvider = produtoDaoProvider;
    this.grupoDaoProvider = grupoDaoProvider;
  }

  @Override
  public ProdutoRepository get() {
    return newInstance(produtoDaoProvider, grupoDaoProvider);
  }

  public static ProdutoRepository_Factory create(Provider<ProdutoDao> produtoDaoProvider,
      Provider<GrupoDao> grupoDaoProvider) {
    return new ProdutoRepository_Factory(produtoDaoProvider, grupoDaoProvider);
  }

  public static ProdutoRepository newInstance(Provider<ProdutoDao> produtoDao,
      Provider<GrupoDao> grupoDao) {
    return new ProdutoRepository(produtoDao, grupoDao);
  }
}
