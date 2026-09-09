package br.com.banavita.app.di;

import br.com.banavita.app.data.local.BanavitaDatabase;
import br.com.banavita.app.data.local.BanavitaDatabaseHolder;
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
public final class DatabaseModule_ProvideBanavitaDatabaseFactory implements Factory<BanavitaDatabase> {
  private final Provider<BanavitaDatabaseHolder> holderProvider;

  public DatabaseModule_ProvideBanavitaDatabaseFactory(
      Provider<BanavitaDatabaseHolder> holderProvider) {
    this.holderProvider = holderProvider;
  }

  @Override
  public BanavitaDatabase get() {
    return provideBanavitaDatabase(holderProvider.get());
  }

  public static DatabaseModule_ProvideBanavitaDatabaseFactory create(
      Provider<BanavitaDatabaseHolder> holderProvider) {
    return new DatabaseModule_ProvideBanavitaDatabaseFactory(holderProvider);
  }

  public static BanavitaDatabase provideBanavitaDatabase(BanavitaDatabaseHolder holder) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBanavitaDatabase(holder));
  }
}
