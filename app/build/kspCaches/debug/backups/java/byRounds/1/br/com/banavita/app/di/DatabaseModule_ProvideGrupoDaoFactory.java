package br.com.banavita.app.di;

import br.com.banavita.app.data.local.BanavitaDatabase;
import br.com.banavita.app.data.local.dao.GrupoDao;
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
public final class DatabaseModule_ProvideGrupoDaoFactory implements Factory<GrupoDao> {
  private final Provider<BanavitaDatabase> databaseProvider;

  public DatabaseModule_ProvideGrupoDaoFactory(Provider<BanavitaDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public GrupoDao get() {
    return provideGrupoDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideGrupoDaoFactory create(
      Provider<BanavitaDatabase> databaseProvider) {
    return new DatabaseModule_ProvideGrupoDaoFactory(databaseProvider);
  }

  public static GrupoDao provideGrupoDao(BanavitaDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideGrupoDao(database));
  }
}
