package br.com.banavita.app.data.local.activation

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import br.com.banavita.app.domain.model.LicenseType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class StoredActivation(
    val licenseToken: String,
    val licenseType: LicenseType,
    val activatedAtEpochMillis: Long,
    val hardwareBacked: Boolean,
)

@Singleton
class ActivationLocalDataSource @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun readActivation(): StoredActivation? {
        val licenseToken = preferences.getString(KEY_LICENSE_TOKEN, null) ?: return null
        val licenseTypeRaw = preferences.getString(KEY_LICENSE_TYPE, null) ?: return null
        if (!preferences.contains(KEY_ACTIVATED_AT)) return null

        return StoredActivation(
            licenseToken = licenseToken,
            licenseType = LicenseType.valueOf(licenseTypeRaw),
            activatedAtEpochMillis = preferences.getLong(KEY_ACTIVATED_AT, 0L),
            hardwareBacked = preferences.getBoolean(KEY_HARDWARE_BACKED, false),
        )
    }

    fun saveActivation(storedActivation: StoredActivation) {
        preferences.edit {
            putString(KEY_LICENSE_TOKEN, storedActivation.licenseToken)
            putString(KEY_LICENSE_TYPE, storedActivation.licenseType.name)
            putLong(KEY_ACTIVATED_AT, storedActivation.activatedAtEpochMillis)
            putBoolean(KEY_HARDWARE_BACKED, storedActivation.hardwareBacked)
        }
    }

    fun clearActivation() {
        preferences.edit { clear() }
    }

    companion object {
        private const val PREFS_NAME = "banavita_activation_secure"
        private const val KEY_LICENSE_TOKEN = "license_token"
        private const val KEY_LICENSE_TYPE = "license_type"
        private const val KEY_ACTIVATED_AT = "activated_at"
        private const val KEY_HARDWARE_BACKED = "hardware_backed"
    }
}
