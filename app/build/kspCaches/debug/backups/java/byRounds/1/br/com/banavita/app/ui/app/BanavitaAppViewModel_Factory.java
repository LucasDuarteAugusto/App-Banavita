package br.com.banavita.app.ui.app;

import br.com.banavita.app.core.reminder.PereciveisMorningScheduler;
import br.com.banavita.app.data.repository.ActivationRepository;
import br.com.banavita.app.data.repository.AppSettingsRepository;
import br.com.banavita.app.data.repository.LoteRepository;
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

  private final Provider<AppSettingsRepository> appSettingsRepositoryProvider;

  private final Provider<LoteRepository> loteRepositoryProvider;

  private final Provider<PereciveisMorningScheduler> morningSchedulerProvider;

  public BanavitaAppViewModel_Factory(Provider<ActivationRepository> activationRepositoryProvider,
      Provider<AppSettingsRepository> appSettingsRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider,
      Provider<PereciveisMorningScheduler> morningSchedulerProvider) {
    this.activationRepositoryProvider = activationRepositoryProvider;
    this.appSettingsRepositoryProvider = appSettingsRepositoryProvider;
    this.loteRepositoryProvider = loteRepositoryProvider;
    this.morningSchedulerProvider = morningSchedulerProvider;
  }

  @Override
  public BanavitaAppViewModel get() {
    return newInstance(activationRepositoryProvider.get(), appSettingsRepositoryProvider.get(), loteRepositoryProvider.get(), morningSchedulerProvider.get());
  }

  public static BanavitaAppViewModel_Factory create(
      Provider<ActivationRepository> activationRepositoryProvider,
      Provider<AppSettingsRepository> appSettingsRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider,
      Provider<PereciveisMorningScheduler> morningSchedulerProvider) {
    return new BanavitaAppViewModel_Factory(activationRepositoryProvider, appSettingsRepositoryProvider, loteRepositoryProvider, morningSchedulerProvider);
  }

  public static BanavitaAppViewModel newInstance(ActivationRepository activationRepository,
      AppSettingsRepository appSettingsRepository, LoteRepository loteRepository,
      PereciveisMorningScheduler morningScheduler) {
    return new BanavitaAppViewModel(activationRepository, appSettingsRepository, loteRepository, morningScheduler);
  }
}
