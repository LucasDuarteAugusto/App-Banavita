package br.com.banavita.app.data.repository;

import br.com.banavita.app.core.speech.SpeechModelManager;
import br.com.banavita.app.core.speech.WhisperEngine;
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

  public SpeechToTextRepository_Factory(Provider<SpeechModelManager> speechModelManagerProvider,
      Provider<WhisperEngine> whisperEngineProvider) {
    this.speechModelManagerProvider = speechModelManagerProvider;
    this.whisperEngineProvider = whisperEngineProvider;
  }

  @Override
  public SpeechToTextRepository get() {
    return newInstance(speechModelManagerProvider.get(), whisperEngineProvider.get());
  }

  public static SpeechToTextRepository_Factory create(
      Provider<SpeechModelManager> speechModelManagerProvider,
      Provider<WhisperEngine> whisperEngineProvider) {
    return new SpeechToTextRepository_Factory(speechModelManagerProvider, whisperEngineProvider);
  }

  public static SpeechToTextRepository newInstance(SpeechModelManager speechModelManager,
      WhisperEngine whisperEngine) {
    return new SpeechToTextRepository(speechModelManager, whisperEngine);
  }
}
