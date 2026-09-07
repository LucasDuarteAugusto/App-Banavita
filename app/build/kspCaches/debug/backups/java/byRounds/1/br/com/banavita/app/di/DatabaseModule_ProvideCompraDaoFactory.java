package br.com.banavita.app.di;

import br.com.banavita.app.data.local.BanavitaDatabase;
import br.com.banavita.app.data.local.dao.CompraDao;
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
public final class DatabaseModule_ProvideCompraDaoFactory implements Factory<CompraDao> {
  private final Provider<BanavitaDatabase> databaseProvider;

  public DatabaseModule_ProvideCompraDaoFactory(Provider<BanavitaDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CompraDao get() {
    return provideCompraDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideCompraDaoFactory create(
      Provider<BanavitaDatabase> databaseProvider) {
    return new DatabaseModule_ProvideCompraDaoFactory(databaseProvider);
  }

  public static CompraDao provideCompraDao(BanavitaDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideCompraDao(database));
  }
}
