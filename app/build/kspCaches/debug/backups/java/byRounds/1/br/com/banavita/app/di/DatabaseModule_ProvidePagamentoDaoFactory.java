package br.com.banavita.app.di;

import br.com.banavita.app.data.local.BanavitaDatabase;
import br.com.banavita.app.data.local.dao.PagamentoDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvidePagamentoDaoFactory implements Factory<PagamentoDao> {
  private final Provider<BanavitaDatabase> databaseProvider;

  public DatabaseModule_ProvidePagamentoDaoFactory(Provider<BanavitaDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public PagamentoDao get() {
    return providePagamentoDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvidePagamentoDaoFactory create(
      Provider<BanavitaDatabase> databaseProvider) {
    return new DatabaseModule_ProvidePagamentoDaoFactory(databaseProvider);
  }

  public static PagamentoDao providePagamentoDao(BanavitaDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePagamentoDao(database));
  }
}
