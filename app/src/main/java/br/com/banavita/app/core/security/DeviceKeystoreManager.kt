package br.com.banavita.app.core.security

data class KeystoreFingerprint(
    val fingerprintHex: String,
    val hardwareBacked: Boolean,
)

interface DeviceKeystoreManager {
    fun getOrCreateKeyFingerprint(): KeystoreFingerprint
}
