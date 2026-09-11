package br.com.banavita.app.core.speech;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class WhisperCppEngine_Factory implements Factory<WhisperCppEngine> {
  @Override
  public WhisperCppEngine get() {
    return newInstance();
  }

  public static WhisperCppEngine_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WhisperCppEngine newInstance() {
    return new WhisperCppEngine();
  }

  private static final class InstanceHolder {
    private static final WhisperCppEngine_Factory INSTANCE = new WhisperCppEngine_Factory();
  }
}
