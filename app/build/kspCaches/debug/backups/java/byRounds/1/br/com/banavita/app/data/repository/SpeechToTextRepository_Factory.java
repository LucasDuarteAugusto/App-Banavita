package br.com.banavita.app.data.repository;

import br.com.banavita.app.core.speech.SpeechModelManager;
import br.com.banavita.app.core.speech.WhisperEngine;
import br.com.banavita.app.data.remote.groq.GroqWhisperClient;
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
public final class SpeechToTextRepository_Factory implements Factory<SpeechToTextRepository> {
  private final Provider<SpeechModelManager> speechModelManagerProvider;

  private final Provider<WhisperEngine> whisperEngineProvider;

  private final Provider<GroqWhisperClient> groqWhisperClientProvider;

  private final Provider<AppSettingsRepository> appSettingsRepositoryProvider;

  public SpeechToTextRepository_Factory(Provider<SpeechModelManager> speechModelManagerProvider,
      Provider<WhisperEngine> whisperEngineProvider,
      Provider<GroqWhisperClient> groqWhisperClientProvider,
      Provider<AppSettingsRepository> appSettingsRepositoryProvider) {
    this.speechModelManagerProvider = speechModelManagerProvider;
    this.whisperEngineProvider = whisperEngineProvider;
    this.groqWhisperClientProvider = groqWhisperClientProvider;
    this.appSettingsRepositoryProvider = appSettingsRepositoryProvider;
  }

  @Override
  public SpeechToTextRepository get() {
    return newInstance(speechModelManagerProvider.get(), whisperEngineProvider.get(), groqWhisperClientProvider.get(), appSettingsRepositoryProvider.get());
  }

  public static SpeechToTextRepository_Factory create(
      Provider<SpeechModelManager> speechModelManagerProvider,
      Provider<WhisperEngine> whisperEngineProvider,
      Provider<GroqWhisperClient> groqWhisperClientProvider,
      Provider<AppSettingsRepository> appSettingsRepositoryProvider) {
    return new SpeechToTextRepository_Factory(speechModelManagerProvider, whisperEngineProvider, groqWhisperClientProvider, appSettingsRepositoryProvider);
  }

  public static SpeechToTextRepository newInstance(SpeechModelManager speechModelManager,
      WhisperEngine whisperEngine, GroqWhisperClient groqWhisperClient,
      AppSettingsRepository appSettingsRepository) {
    return new SpeechToTextRepository(speechModelManager, whisperEngine, groqWhisperClient, appSettingsRepository);
  }
}
