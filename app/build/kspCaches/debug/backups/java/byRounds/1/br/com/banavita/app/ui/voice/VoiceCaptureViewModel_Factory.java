package br.com.banavita.app.ui.voice;

import android.content.Context;
import br.com.banavita.app.core.speech.AudioCapture;
import br.com.banavita.app.data.repository.ExtractionRepository;
import br.com.banavita.app.data.repository.SpeechToTextRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class VoiceCaptureViewModel_Factory implements Factory<VoiceCaptureViewModel> {
  private final Provider<Context> appContextProvider;

  private final Provider<SpeechToTextRepository> speechToTextRepositoryProvider;

  private final Provider<ExtractionRepository> extractionRepositoryProvider;

  private final Provider<AudioCapture> audioCaptureProvider;

  public VoiceCaptureViewModel_Factory(Provider<Context> appContextProvider,
      Provider<SpeechToTextRepository> speechToTextRepositoryProvider,
      Provider<ExtractionRepository> extractionRepositoryProvider,
      Provider<AudioCapture> audioCaptureProvider) {
    this.appContextProvider = appContextProvider;
    this.speechToTextRepositoryProvider = speechToTextRepositoryProvider;
    this.extractionRepositoryProvider = extractionRepositoryProvider;
    this.audioCaptureProvider = audioCaptureProvider;
  }

  @Override
  public VoiceCaptureViewModel get() {
    return newInstance(appContextProvider.get(), speechToTextRepositoryProvider.get(), extractionRepositoryProvider.get(), audioCaptureProvider.get());
  }

  public static VoiceCaptureViewModel_Factory create(Provider<Context> appContextProvider,
      Provider<SpeechToTextRepository> speechToTextRepositoryProvider,
      Provider<ExtractionRepository> extractionRepositoryProvider,
      Provider<AudioCapture> audioCaptureProvider) {
    return new VoiceCaptureViewModel_Factory(appContextProvider, speechToTextRepositoryProvider, extractionRepositoryProvider, audioCaptureProvider);
  }

  public static VoiceCaptureViewModel newInstance(Context appContext,
      SpeechToTextRepository speechToTextRepository, ExtractionRepository extractionRepository,
      AudioCapture audioCapture) {
    return new VoiceCaptureViewModel(appContext, speechToTextRepository, extractionRepository, audioCapture);
  }
}
