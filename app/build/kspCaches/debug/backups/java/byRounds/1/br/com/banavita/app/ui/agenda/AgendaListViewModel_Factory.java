package br.com.banavita.app.ui.agenda;

import androidx.lifecycle.SavedStateHandle;
import br.com.banavita.app.data.repository.EventoRepository;
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
public final class AgendaListViewModel_Factory implements Factory<AgendaListViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<EventoRepository> eventoRepositoryProvider;

  public AgendaListViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EventoRepository> eventoRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.eventoRepositoryProvider = eventoRepositoryProvider;
  }

  @Override
  public AgendaListViewModel get() {
    return newInstance(savedStateHandleProvider.get(), eventoRepositoryProvider.get());
  }

  public static AgendaListViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EventoRepository> eventoRepositoryProvider) {
    return new AgendaListViewModel_Factory(savedStateHandleProvider, eventoRepositoryProvider);
  }

  public static AgendaListViewModel newInstance(SavedStateHandle savedStateHandle,
      EventoRepository eventoRepository) {
    return new AgendaListViewModel(savedStateHandle, eventoRepository);
  }
}
