package br.com.banavita.app.core.security

import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

interface LicensePublicKeyProvider {
    fun publicKey(): PublicKey
}

fun LicensePublicKeyProvider(block: () -> PublicKey): LicensePublicKeyProvider =
    object : LicensePublicKeyProvider {
        override fun publicKey(): PublicKey = block()
    }

@Singleton
class DefaultLicensePublicKeyProvider @Inject constructor() : LicensePublicKeyProvider {
    override fun publicKey(): PublicKey = LicensePublicKeyLoader.fromPem(LicenseConstants.PUBLIC_KEY_PEM)
}

object LicensePublicKeyLoader {
    fun fromPem(pem: String): PublicKey {
        val normalized = pem
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s".toRegex(), "")
        val decoded = Base64.getDecoder().decode(normalized)
        val spec = X509EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }
}
