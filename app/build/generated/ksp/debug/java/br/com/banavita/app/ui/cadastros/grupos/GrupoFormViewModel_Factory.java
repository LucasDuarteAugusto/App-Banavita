package br.com.banavita.app.ui.cadastros.grupos;

import androidx.lifecycle.SavedStateHandle;
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
public final class GrupoFormViewModel_Factory implements Factory<GrupoFormViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<GrupoRepository> grupoRepositoryProvider;

  public GrupoFormViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GrupoRepository> grupoRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.grupoRepositoryProvider = grupoRepositoryProvider;
  }

  @Override
  public GrupoFormViewModel get() {
    return newInstance(savedStateHandleProvider.get(), grupoRepositoryProvider.get());
  }

  public static GrupoFormViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<GrupoRepository> grupoRepositoryProvider) {
    return new GrupoFormViewModel_Factory(savedStateHandleProvider, grupoRepositoryProvider);
  }

  public static GrupoFormViewModel newInstance(SavedStateHandle savedStateHandle,
      GrupoRepository grupoRepository) {
    return new GrupoFormViewModel(savedStateHandle, grupoRepository);
  }
}
