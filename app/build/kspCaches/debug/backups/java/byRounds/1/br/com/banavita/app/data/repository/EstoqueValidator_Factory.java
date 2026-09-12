package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.dao.EstoqueDao;
import br.com.banavita.app.data.local.dao.LoteDao;
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
public final class EstoqueValidator_Factory implements Factory<EstoqueValidator> {
  private final Provider<EstoqueDao> estoqueDaoProvider;

  private final Provider<ProdutoDao> produtoDaoProvider;

  private final Provider<LoteDao> loteDaoProvider;

  public EstoqueValidator_Factory(Provider<EstoqueDao> estoqueDaoProvider,
      Provider<ProdutoDao> produtoDaoProvider, Provider<LoteDao> loteDaoProvider) {
    this.estoqueDaoProvider = estoqueDaoProvider;
    this.produtoDaoProvider = produtoDaoProvider;
    this.loteDaoProvider = loteDaoProvider;
  }

  @Override
  public EstoqueValidator get() {
    return newInstance(estoqueDaoProvider, produtoDaoProvider, loteDaoProvider);
  }

  public static EstoqueValidator_Factory create(Provider<EstoqueDao> estoqueDaoProvider,
      Provider<ProdutoDao> produtoDaoProvider, Provider<LoteDao> loteDaoProvider) {
    return new EstoqueValidator_Factory(estoqueDaoProvider, produtoDaoProvider, loteDaoProvider);
  }

  public static EstoqueValidator newInstance(Provider<EstoqueDao> estoqueDao,
      Provider<ProdutoDao> produtoDao, Provider<LoteDao> loteDao) {
    return new EstoqueValidator(estoqueDao, produtoDao, loteDao);
  }
}
