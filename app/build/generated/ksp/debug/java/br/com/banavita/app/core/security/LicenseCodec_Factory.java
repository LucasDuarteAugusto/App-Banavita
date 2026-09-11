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
public final class LicenseCodec_Factory implements Factory<LicenseCodec> {
  @Override
  public LicenseCodec get() {
    return newInstance();
  }

  public static LicenseCodec_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LicenseCodec newInstance() {
    return new LicenseCodec();
  }

  private static final class InstanceHolder {
    private static final LicenseCodec_Factory INSTANCE = new LicenseCodec_Factory();
  }
}
