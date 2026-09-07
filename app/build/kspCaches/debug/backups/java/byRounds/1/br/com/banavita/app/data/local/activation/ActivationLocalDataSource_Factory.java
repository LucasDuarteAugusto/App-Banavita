package br.com.banavita.app.data.local.activation;

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
public final class ActivationLocalDataSource_Factory implements Factory<ActivationLocalDataSource> {
  private final Provider<Context> contextProvider;

  public ActivationLocalDataSource_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ActivationLocalDataSource get() {
    return newInstance(contextProvider.get());
  }

  public static ActivationLocalDataSource_Factory create(Provider<Context> contextProvider) {
    return new ActivationLocalDataSource_Factory(contextProvider);
  }

  public static ActivationLocalDataSource newInstance(Context context) {
    return new ActivationLocalDataSource(context);
  }
}
