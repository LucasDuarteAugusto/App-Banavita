package br.com.banavita.app.ui.activation;

import br.com.banavita.app.data.repository.ActivationRepository;
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
public final class ActivationViewModel_Factory implements Factory<ActivationViewModel> {
  private final Provider<ActivationRepository> activationRepositoryProvider;

  public ActivationViewModel_Factory(Provider<ActivationRepository> activationRepositoryProvider) {
    this.activationRepositoryProvider = activationRepositoryProvider;
  }

  @Override
  public ActivationViewModel get() {
    return newInstance(activationRepositoryProvider.get());
  }

  public static ActivationViewModel_Factory create(
      Provider<ActivationRepository> activationRepositoryProvider) {
    return new ActivationViewModel_Factory(activationRepositoryProvider);
  }

  public static ActivationViewModel newInstance(ActivationRepository activationRepository) {
    return new ActivationViewModel(activationRepository);
  }
}
