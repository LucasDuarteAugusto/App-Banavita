package br.com.banavita.app.core.security

import br.com.banavita.app.domain.model.LicenseType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.Signature
import java.util.Base64

class LicenseValidatorTest {

    private lateinit var privateKey: PrivateKey
    private lateinit var licenseCodec: LicenseCodec
    private lateinit var licenseValidator: LicenseValidator

    private val deviceFingerprint = DeviceFingerprint(
        baseFingerprintHex = "abc123base",
        keystoreFingerprintHex = "def456keystore",
        hardwareBacked = true,
    )

    @Before
    fun setUp() {
        val keyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.genKeyPair()
        privateKey = keyPair.private

        licenseCodec = LicenseCodec()
        licenseValidator = LicenseValidator(
            licenseCodec = licenseCodec,
            deviceFingerprintProvider = FixedDeviceFingerprintProvider(deviceFingerprint),
            publicKeyProvider = LicensePublicKeyProvider { keyPair.public },
        )
    }

    @Test
    fun `licenca permanente valida quando assinatura e fingerprints conferem`() {
        val token = signLicense(
            bfp = deviceFingerprint.baseFingerprintHex,
            kfp = deviceFingerprint.keystoreFingerprintHex,
            type = LicenseType.PERMANENT,
        )

        val validated = licenseValidator.validateLicenseToken(token, activatedAtEpochMillis = null)

        assertEquals(LicenseType.PERMANENT, validated.payload.licenseType)
        assertEquals(null, validated.expiresAtEpochMillis)
        assertTrue(validated.hardwareBacked)
    }

    @Test
    fun `licenca mensal expira apos 30 dias da ativacao`() {
        val token = signLicense(
            bfp = deviceFingerprint.baseFingerprintHex,
            kfp = deviceFingerprint.keystoreFingerprintHex,
            type = LicenseType.MONTHLY,
        )
        val activatedAt = 1_700_000_000_000L
        val expiresAt = activatedAt + LicenseConstants.MONTHLY_DURATION_MILLIS

        val stillValid = licenseValidator.validateLicenseToken(
            licenseToken = token,
            activatedAtEpochMillis = activatedAt,
            nowEpochMillis = expiresAt,
        )
        assertEquals(expiresAt, stillValid.expiresAtEpochMillis)
    }

    @Test(expected = br.com.banavita.app.domain.exception.LicenseExpiredException::class)
    fun `licenca mensal rejeita apos expiracao`() {
        val token = signLicense(
            bfp = deviceFingerprint.baseFingerprintHex,
            kfp = deviceFingerprint.keystoreFingerprintHex,
            type = LicenseType.MONTHLY,
        )
        val activatedAt = 1_700_000_000_000L

        licenseValidator.validateLicenseToken(
            licenseToken = token,
            activatedAtEpochMillis = activatedAt,
            nowEpochMillis = activatedAt + LicenseConstants.MONTHLY_DURATION_MILLIS + 1,
        )
    }

    @Test(expected = br.com.banavita.app.domain.exception.DeviceMismatchException::class)
    fun `licenca rejeita fingerprint diferente`() {
        val token = signLicense(
            bfp = "outro-dispositivo",
            kfp = deviceFingerprint.keystoreFingerprintHex,
            type = LicenseType.PERMANENT,
        )

        licenseValidator.validateLicenseToken(token, activatedAtEpochMillis = null)
    }

    private fun signLicense(bfp: String, kfp: String, type: LicenseType): String {
        val canonical = "v1|bfp=$bfp|kfp=$kfp|type=$type"
        val signature = Signature.getInstance("SHA256withRSA").run {
            initSign(privateKey)
            update(canonical.toByteArray())
            sign()
        }
        val payloadPart = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(canonical.toByteArray())
        val signaturePart = Base64.getUrlEncoder().withoutPadding().encodeToString(signature)
        return "${LicenseConstants.LICENSE_PREFIX}$payloadPart.$signaturePart"
    }

    private class FixedDeviceFingerprintProvider(
        private val fingerprint: DeviceFingerprint,
    ) : DeviceFingerprintProvider {
        override fun currentFingerprint(): DeviceFingerprint = fingerprint
    }
}
