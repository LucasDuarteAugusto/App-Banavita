package br.com.banavita.app.core.security

import br.com.banavita.app.domain.exception.DeviceMismatchException
import br.com.banavita.app.domain.exception.InvalidLicenseException
import br.com.banavita.app.domain.exception.LicenseExpiredException
import br.com.banavita.app.domain.model.LicensePayload
import br.com.banavita.app.domain.model.LicenseType
import java.nio.charset.StandardCharsets
import java.security.Signature
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseValidator @Inject constructor(
    private val licenseCodec: LicenseCodec,
    private val deviceFingerprintProvider: DeviceFingerprintProvider,
    private val publicKeyProvider: LicensePublicKeyProvider,
) {
    fun validateLicenseToken(
        licenseToken: String,
        activatedAtEpochMillis: Long?,
        nowEpochMillis: Long = System.currentTimeMillis(),
    ): ValidatedLicense {
        val parsed = licenseCodec.parseLicenseToken(licenseToken)
        verifySignature(parsed.canonicalMessage, parsed.signatureBytes)

        val currentFingerprint = deviceFingerprintProvider.currentFingerprint()
        assertFingerprintMatch(parsed.payload, currentFingerprint)

        val activationMoment = activatedAtEpochMillis ?: nowEpochMillis
        val expiresAt = resolveExpiresAt(parsed.payload.licenseType, activationMoment)

        if (expiresAt != null && nowEpochMillis > expiresAt) {
            throw LicenseExpiredException(
                licenseType = parsed.payload.licenseType,
                expiredAtEpochMillis = expiresAt,
            )
        }

        return ValidatedLicense(
            payload = parsed.payload,
            activatedAtEpochMillis = activationMoment,
            expiresAtEpochMillis = expiresAt,
            hardwareBacked = currentFingerprint.hardwareBacked,
            licenseToken = licenseToken,
        )
    }

    private fun verifySignature(message: String, signatureBytes: ByteArray) {
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initVerify(publicKeyProvider.publicKey())
        signature.update(message.toByteArray(StandardCharsets.UTF_8))
        if (!signature.verify(signatureBytes)) {
            throw InvalidLicenseException("Assinatura da licença inválida.")
        }
    }

    private fun assertFingerprintMatch(payload: LicensePayload, current: DeviceFingerprint) {
        if (payload.baseFingerprintHex != current.baseFingerprintHex) {
            throw DeviceMismatchException("Licença não pertence a este dispositivo (base).")
        }
        if (payload.keystoreFingerprintHex != current.keystoreFingerprintHex) {
            throw DeviceMismatchException("Licença não pertence a este dispositivo (Keystore).")
        }
    }

    private fun resolveExpiresAt(licenseType: LicenseType, activatedAt: Long): Long? =
        when (licenseType) {
            LicenseType.PERMANENT -> null
            LicenseType.MONTHLY -> activatedAt + LicenseConstants.MONTHLY_DURATION_MILLIS
        }

    companion object {
        private const val SIGNATURE_ALGORITHM = "SHA256withRSA"
    }
}

data class ValidatedLicense(
    val payload: LicensePayload,
    val activatedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long?,
    val hardwareBacked: Boolean,
    val licenseToken: String,
)
