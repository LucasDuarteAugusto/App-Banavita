package br.com.banavita.app.core.security

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidDeviceFingerprintProvider @Inject constructor(
    @ApplicationContext private val context: Context, // WARNING - The annotation is only applied to the parameter. An explicit annotation use-site target is recommended.
    private val deviceKeystoreManager: DeviceKeystoreManager,
) : DeviceFingerprintProvider {

    override fun currentFingerprint(): DeviceFingerprint {
        val keystoreFingerprint = deviceKeystoreManager.getOrCreateKeyFingerprint()
        return DeviceFingerprint(
            baseFingerprintHex = computeBaseFingerprint(),
            keystoreFingerprintHex = keystoreFingerprint.fingerprintHex,
            hardwareBacked = keystoreFingerprint.hardwareBacked,
        )
    }

    @SuppressLint("HardwareIds")
    private fun computeBaseFingerprint(): String {
        val androidId = android.provider.Settings.Secure.getString(
            context.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID,
        ).orEmpty()

        val material = listOf(
            androidId,
            context.packageName,
            LicenseConstants.APP_SALT,
        ).joinToString("|")

        return Hashing.sha256Hex(material)
    }
}
