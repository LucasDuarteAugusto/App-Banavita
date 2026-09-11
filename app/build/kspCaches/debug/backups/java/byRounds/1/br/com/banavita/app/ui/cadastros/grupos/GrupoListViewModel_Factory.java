package br.com.banavita.app.ui.cadastros.grupos;

import br.com.banavita.app.data.repository.GrupoRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class GrupoListViewModel_Factory implements Factory<GrupoListViewModel> {
  private final Provider<GrupoRepository> grupoRepositoryProvider;

  public GrupoListViewModel_Factory(Provider<GrupoRepository> grupoRepositoryProvider) {
    this.grupoRepositoryProvider = grupoRepositoryProvider;
  }

  @Override
  public GrupoListViewModel get() {
    return newInstance(grupoRepositoryProvider.get());
  }

  public static GrupoListViewModel_Factory create(
      Provider<GrupoRepository> grupoRepositoryProvider) {
    return new GrupoListViewModel_Factory(grupoRepositoryProvider);
  }

  public static GrupoListViewModel newInstance(GrupoRepository grupoRepository) {
    return new GrupoListViewModel(grupoRepository);
  }
}
