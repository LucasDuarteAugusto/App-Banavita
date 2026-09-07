package br.com.banavita.app.di;

import br.com.banavita.app.data.local.BanavitaDatabase;
import br.com.banavita.app.data.local.dao.RecebimentoDao;
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
public final class DatabaseModule_ProvideRecebimentoDaoFactory implements Factory<RecebimentoDao> {
  private final Provider<BanavitaDatabase> databaseProvider;

  public DatabaseModule_ProvideRecebimentoDaoFactory(Provider<BanavitaDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public RecebimentoDao get() {
    return provideRecebimentoDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideRecebimentoDaoFactory create(
      Provider<BanavitaDatabase> databaseProvider) {
    return new DatabaseModule_ProvideRecebimentoDaoFactory(databaseProvider);
  }

  public static RecebimentoDao provideRecebimentoDao(BanavitaDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideRecebimentoDao(database));
  }
}
