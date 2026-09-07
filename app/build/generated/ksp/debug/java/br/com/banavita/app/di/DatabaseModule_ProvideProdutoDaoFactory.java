package br.com.banavita.app.di;

import br.com.banavita.app.data.local.BanavitaDatabase;
import br.com.banavita.app.data.local.dao.ProdutoDao;
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
public final class DatabaseModule_ProvideProdutoDaoFactory implements Factory<ProdutoDao> {
  private final Provider<BanavitaDatabase> databaseProvider;

  public DatabaseModule_ProvideProdutoDaoFactory(Provider<BanavitaDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ProdutoDao get() {
    return provideProdutoDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideProdutoDaoFactory create(
      Provider<BanavitaDatabase> databaseProvider) {
    return new DatabaseModule_ProvideProdutoDaoFactory(databaseProvider);
  }

  public static ProdutoDao provideProdutoDao(BanavitaDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideProdutoDao(database));
  }
}
