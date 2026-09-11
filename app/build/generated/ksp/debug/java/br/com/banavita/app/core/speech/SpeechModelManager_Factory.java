package br.com.banavita.app.core.speech;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SpeechModelManager_Factory implements Factory<SpeechModelManager> {
  private final Provider<Context> contextProvider;

  public SpeechModelManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SpeechModelManager get() {
    return newInstance(contextProvider.get());
  }

  public static SpeechModelManager_Factory create(Provider<Context> contextProvider) {
    return new SpeechModelManager_Factory(contextProvider);
  }

  public static SpeechModelManager newInstance(Context context) {
    return new SpeechModelManager(context);
  }
}
