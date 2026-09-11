package br.com.banavita.app.data.repository;

import br.com.banavita.app.core.security.DeviceFingerprintProvider;
import br.com.banavita.app.core.security.LicenseCodec;
import br.com.banavita.app.core.security.LicenseValidator;
import br.com.banavita.app.data.local.activation.ActivationLocalDataSource;
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
public final class ActivationRepository_Factory implements Factory<ActivationRepository> {
  private final Provider<DeviceFingerprintProvider> deviceFingerprintProvider;

  private final Provider<LicenseCodec> licenseCodecProvider;

  private final Provider<LicenseValidator> licenseValidatorProvider;

  private final Provider<ActivationLocalDataSource> activationLocalDataSourceProvider;

  public ActivationRepository_Factory(Provider<DeviceFingerprintProvider> deviceFingerprintProvider,
      Provider<LicenseCodec> licenseCodecProvider,
      Provider<LicenseValidator> licenseValidatorProvider,
      Provider<ActivationLocalDataSource> activationLocalDataSourceProvider) {
    this.deviceFingerprintProvider = deviceFingerprintProvider;
    this.licenseCodecProvider = licenseCodecProvider;
    this.licenseValidatorProvider = licenseValidatorProvider;
    this.activationLocalDataSourceProvider = activationLocalDataSourceProvider;
  }

  @Override
  public ActivationRepository get() {
    return newInstance(deviceFingerprintProvider.get(), licenseCodecProvider.get(), licenseValidatorProvider.get(), activationLocalDataSourceProvider.get());
  }

  public static ActivationRepository_Factory create(
      Provider<DeviceFingerprintProvider> deviceFingerprintProvider,
      Provider<LicenseCodec> licenseCodecProvider,
      Provider<LicenseValidator> licenseValidatorProvider,
      Provider<ActivationLocalDataSource> activationLocalDataSourceProvider) {
    return new ActivationRepository_Factory(deviceFingerprintProvider, licenseCodecProvider, licenseValidatorProvider, activationLocalDataSourceProvider);
  }

  public static ActivationRepository newInstance(
      DeviceFingerprintProvider deviceFingerprintProvider, LicenseCodec licenseCodec,
      LicenseValidator licenseValidator, ActivationLocalDataSource activationLocalDataSource) {
    return new ActivationRepository(deviceFingerprintProvider, licenseCodec, licenseValidator, activationLocalDataSource);
  }
}
