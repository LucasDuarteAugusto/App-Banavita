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
public final class AndroidDeviceKeystoreManager_Factory implements Factory<AndroidDeviceKeystoreManager> {
  @Override
  public AndroidDeviceKeystoreManager get() {
    return newInstance();
  }

  public static AndroidDeviceKeystoreManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AndroidDeviceKeystoreManager newInstance() {
    return new AndroidDeviceKeystoreManager();
  }

  private static final class InstanceHolder {
    private static final AndroidDeviceKeystoreManager_Factory INSTANCE = new AndroidDeviceKeystoreManager_Factory();
  }
}
