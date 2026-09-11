package br.com.banavita.app.core.extraction;

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
public final class ExtractionPipeline_Factory implements Factory<ExtractionPipeline> {
  private final Provider<EntityResolver> entityResolverProvider;

  public ExtractionPipeline_Factory(Provider<EntityResolver> entityResolverProvider) {
    this.entityResolverProvider = entityResolverProvider;
  }

  @Override
  public ExtractionPipeline get() {
    return newInstance(entityResolverProvider.get());
  }

  public static ExtractionPipeline_Factory create(Provider<EntityResolver> entityResolverProvider) {
    return new ExtractionPipeline_Factory(entityResolverProvider);
  }

  public static ExtractionPipeline newInstance(EntityResolver entityResolver) {
    return new ExtractionPipeline(entityResolver);
  }
}
