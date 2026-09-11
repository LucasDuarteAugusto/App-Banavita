package br.com.banavita.app.core.security;

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
public final class DefaultLicensePublicKeyProvider_Factory implements Factory<DefaultLicensePublicKeyProvider> {
  @Override
  public DefaultLicensePublicKeyProvider get() {
    return newInstance();
  }

  public static DefaultLicensePublicKeyProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DefaultLicensePublicKeyProvider newInstance() {
    return new DefaultLicensePublicKeyProvider();
  }

  private static final class InstanceHolder {
    private static final DefaultLicensePublicKeyProvider_Factory INSTANCE = new DefaultLicensePublicKeyProvider_Factory();
  }
}
