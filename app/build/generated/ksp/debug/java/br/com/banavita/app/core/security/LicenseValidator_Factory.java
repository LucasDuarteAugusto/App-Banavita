package br.com.banavita.app.core.security;

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
public final class LicenseValidator_Factory implements Factory<LicenseValidator> {
  private final Provider<LicenseCodec> licenseCodecProvider;

  private final Provider<DeviceFingerprintProvider> deviceFingerprintProvider;

  private final Provider<LicensePublicKeyProvider> publicKeyProvider;

  public LicenseValidator_Factory(Provider<LicenseCodec> licenseCodecProvider,
      Provider<DeviceFingerprintProvider> deviceFingerprintProvider,
      Provider<LicensePublicKeyProvider> publicKeyProvider) {
    this.licenseCodecProvider = licenseCodecProvider;
    this.deviceFingerprintProvider = deviceFingerprintProvider;
    this.publicKeyProvider = publicKeyProvider;
  }

  @Override
  public LicenseValidator get() {
    return newInstance(licenseCodecProvider.get(), deviceFingerprintProvider.get(), publicKeyProvider.get());
  }

  public static LicenseValidator_Factory create(Provider<LicenseCodec> licenseCodecProvider,
      Provider<DeviceFingerprintProvider> deviceFingerprintProvider,
      Provider<LicensePublicKeyProvider> publicKeyProvider) {
    return new LicenseValidator_Factory(licenseCodecProvider, deviceFingerprintProvider, publicKeyProvider);
  }

  public static LicenseValidator newInstance(LicenseCodec licenseCodec,
      DeviceFingerprintProvider deviceFingerprintProvider,
      LicensePublicKeyProvider publicKeyProvider) {
    return new LicenseValidator(licenseCodec, deviceFingerprintProvider, publicKeyProvider);
  }
}
