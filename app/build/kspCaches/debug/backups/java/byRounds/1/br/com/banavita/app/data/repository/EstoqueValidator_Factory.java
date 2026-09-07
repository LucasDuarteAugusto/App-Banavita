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
public final class EstoqueValidator_Factory implements Factory<EstoqueValidator> {
  private final Provider<EstoqueDao> estoqueDaoProvider;

  public EstoqueValidator_Factory(Provider<EstoqueDao> estoqueDaoProvider) {
    this.estoqueDaoProvider = estoqueDaoProvider;
  }

  @Override
  public EstoqueValidator get() {
    return newInstance(estoqueDaoProvider.get());
  }

  public static EstoqueValidator_Factory create(Provider<EstoqueDao> estoqueDaoProvider) {
    return new EstoqueValidator_Factory(estoqueDaoProvider);
  }

  public static EstoqueValidator newInstance(EstoqueDao estoqueDao) {
    return new EstoqueValidator(estoqueDao);
  }
}
