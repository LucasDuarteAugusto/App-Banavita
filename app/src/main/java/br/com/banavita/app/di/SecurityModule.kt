package br.com.banavita.app.di

import br.com.banavita.app.core.security.AndroidDeviceFingerprintProvider
import br.com.banavita.app.core.security.AndroidDeviceKeystoreManager
import br.com.banavita.app.core.security.DefaultLicensePublicKeyProvider
import br.com.banavita.app.core.security.DeviceFingerprintProvider
import br.com.banavita.app.core.security.DeviceKeystoreManager
import br.com.banavita.app.core.security.LicensePublicKeyProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {
    @Binds
    @Singleton
    abstract fun bindDeviceKeystoreManager(
        impl: AndroidDeviceKeystoreManager,
    ): DeviceKeystoreManager

    @Binds
    @Singleton
    abstract fun bindLicensePublicKeyProvider(
        impl: DefaultLicensePublicKeyProvider,
    ): LicensePublicKeyProvider

    @Binds
    @Singleton
    abstract fun bindDeviceFingerprintProvider(
        impl: AndroidDeviceFingerprintProvider,
    ): DeviceFingerprintProvider
}
