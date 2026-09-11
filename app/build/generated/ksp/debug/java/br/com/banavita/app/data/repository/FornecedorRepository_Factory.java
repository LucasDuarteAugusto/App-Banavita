package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.dao.FornecedorDao;
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
public final class FornecedorRepository_Factory implements Factory<FornecedorRepository> {
  private final Provider<FornecedorDao> fornecedorDaoProvider;

  public FornecedorRepository_Factory(Provider<FornecedorDao> fornecedorDaoProvider) {
    this.fornecedorDaoProvider = fornecedorDaoProvider;
  }

  @Override
  public FornecedorRepository get() {
    return newInstance(fornecedorDaoProvider);
  }

  public static FornecedorRepository_Factory create(Provider<FornecedorDao> fornecedorDaoProvider) {
    return new FornecedorRepository_Factory(fornecedorDaoProvider);
  }

  public static FornecedorRepository newInstance(Provider<FornecedorDao> fornecedorDao) {
    return new FornecedorRepository(fornecedorDao);
  }
}
