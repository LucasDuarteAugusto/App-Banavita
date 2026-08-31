package br.com.banavita.app.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.ECGenParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidDeviceKeystoreManager @Inject constructor() : DeviceKeystoreManager {

    override fun getOrCreateKeyFingerprint(): KeystoreFingerprint {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            createKeyPair()
        }

        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        val publicKeyBytes = entry.certificate.publicKey.encoded
        val fingerprint = Hashing.sha256Hex(publicKeyBytes)
        val hardwareBacked = isHardwareBacked(entry)

        return KeystoreFingerprint(
            fingerprintHex = fingerprint,
            hardwareBacked = hardwareBacked,
        )
    }

    private fun createKeyPair() {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            ANDROID_KEYSTORE,
        )
        val parameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY,
        )
            .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setUserAuthenticationRequired(false)
            .build()

        keyPairGenerator.initialize(parameterSpec)
        keyPairGenerator.generateKeyPair()
    }

    private fun isHardwareBacked(entry: KeyStore.PrivateKeyEntry): Boolean {
        return try {
            val factory = KeyFactory.getInstance(entry.privateKey.algorithm, ANDROID_KEYSTORE)
            val keyInfo = factory.getKeySpec(entry.privateKey, KeyInfo::class.java)
            keyInfo.isInsideSecureHardware
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "banavita_activation_key_v1"
    }
}
