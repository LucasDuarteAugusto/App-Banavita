package br.com.banavita.app.data.cache;

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
public final class ExtractionSessionCache_Factory implements Factory<ExtractionSessionCache> {
  @Override
  public ExtractionSessionCache get() {
    return newInstance();
  }

  public static ExtractionSessionCache_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ExtractionSessionCache newInstance() {
    return new ExtractionSessionCache();
  }

  private static final class InstanceHolder {
    private static final ExtractionSessionCache_Factory INSTANCE = new ExtractionSessionCache_Factory();
  }
}
