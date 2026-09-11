package br.com.banavita.app.core.security;

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
public final class AndroidDeviceFingerprintProvider_Factory implements Factory<AndroidDeviceFingerprintProvider> {
  private final Provider<Context> contextProvider;

  private final Provider<DeviceKeystoreManager> deviceKeystoreManagerProvider;

  public AndroidDeviceFingerprintProvider_Factory(Provider<Context> contextProvider,
      Provider<DeviceKeystoreManager> deviceKeystoreManagerProvider) {
    this.contextProvider = contextProvider;
    this.deviceKeystoreManagerProvider = deviceKeystoreManagerProvider;
  }

  @Override
  public AndroidDeviceFingerprintProvider get() {
    return newInstance(contextProvider.get(), deviceKeystoreManagerProvider.get());
  }

  public static AndroidDeviceFingerprintProvider_Factory create(Provider<Context> contextProvider,
      Provider<DeviceKeystoreManager> deviceKeystoreManagerProvider) {
    return new AndroidDeviceFingerprintProvider_Factory(contextProvider, deviceKeystoreManagerProvider);
  }

  public static AndroidDeviceFingerprintProvider newInstance(Context context,
      DeviceKeystoreManager deviceKeystoreManager) {
    return new AndroidDeviceFingerprintProvider(context, deviceKeystoreManager);
  }
}
