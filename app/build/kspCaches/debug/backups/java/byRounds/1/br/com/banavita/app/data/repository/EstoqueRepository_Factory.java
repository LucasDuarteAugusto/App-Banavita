package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.dao.EstoqueDao;
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
public final class EstoqueRepository_Factory implements Factory<EstoqueRepository> {
  private final Provider<EstoqueDao> estoqueDaoProvider;

  public EstoqueRepository_Factory(Provider<EstoqueDao> estoqueDaoProvider) {
    this.estoqueDaoProvider = estoqueDaoProvider;
  }

  @Override
  public EstoqueRepository get() {
    return newInstance(estoqueDaoProvider.get());
  }

  public static EstoqueRepository_Factory create(Provider<EstoqueDao> estoqueDaoProvider) {
    return new EstoqueRepository_Factory(estoqueDaoProvider);
  }

  public static EstoqueRepository newInstance(EstoqueDao estoqueDao) {
    return new EstoqueRepository(estoqueDao);
  }
}
