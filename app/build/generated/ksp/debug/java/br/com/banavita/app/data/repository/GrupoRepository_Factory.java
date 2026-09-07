package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.dao.GrupoDao;
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
public final class GrupoRepository_Factory implements Factory<GrupoRepository> {
  private final Provider<GrupoDao> grupoDaoProvider;

  public GrupoRepository_Factory(Provider<GrupoDao> grupoDaoProvider) {
    this.grupoDaoProvider = grupoDaoProvider;
  }

  @Override
  public GrupoRepository get() {
    return newInstance(grupoDaoProvider.get());
  }

  public static GrupoRepository_Factory create(Provider<GrupoDao> grupoDaoProvider) {
    return new GrupoRepository_Factory(grupoDaoProvider);
  }

  public static GrupoRepository newInstance(GrupoDao grupoDao) {
    return new GrupoRepository(grupoDao);
  }
}
