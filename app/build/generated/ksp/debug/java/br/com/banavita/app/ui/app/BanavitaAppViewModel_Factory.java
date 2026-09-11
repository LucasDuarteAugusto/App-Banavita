package br.com.banavita.app.ui.app;

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
public final class BanavitaAppViewModel_Factory implements Factory<BanavitaAppViewModel> {
  private final Provider<ActivationRepository> activationRepositoryProvider;

  public BanavitaAppViewModel_Factory(Provider<ActivationRepository> activationRepositoryProvider) {
    this.activationRepositoryProvider = activationRepositoryProvider;
  }

  @Override
  public BanavitaAppViewModel get() {
    return newInstance(activationRepositoryProvider.get());
  }

  public static BanavitaAppViewModel_Factory create(
      Provider<ActivationRepository> activationRepositoryProvider) {
    return new BanavitaAppViewModel_Factory(activationRepositoryProvider);
  }

  public static BanavitaAppViewModel newInstance(ActivationRepository activationRepository) {
    return new BanavitaAppViewModel(activationRepository);
  }
}
