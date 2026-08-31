package br.com.banavita.app.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.core.security.DeviceFingerprint
import br.com.banavita.app.core.security.DeviceFingerprintProvider
import br.com.banavita.app.core.security.LicenseCodec
import br.com.banavita.app.core.security.LicenseConstants
import br.com.banavita.app.core.security.LicensePublicKeyLoader
import br.com.banavita.app.core.security.LicensePublicKeyProvider
import br.com.banavita.app.core.security.LicenseValidator
import br.com.banavita.app.data.local.activation.ActivationLocalDataSource
import br.com.banavita.app.domain.model.LicenseType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ActivationRepositoryTest {

    private lateinit var repository: ActivationRepository
    private lateinit var localDataSource: ActivationLocalDataSource

    private val fingerprint = DeviceFingerprint(
        baseFingerprintHex = "test-base-fingerprint",
        keystoreFingerprintHex = "test-keystore-fingerprint",
        hardwareBacked = false,
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        localDataSource = ActivationLocalDataSource(context)
        localDataSource.clearActivation()

        val fingerprintProvider = FixedDeviceFingerprintProvider(fingerprint)
        val licenseCodec = LicenseCodec()
        val publicKeyProvider = LicensePublicKeyProvider {
            LicensePublicKeyLoader.fromPem(readToolsFile("public.pem"))
        }
        val licenseValidator = LicenseValidator(
            licenseCodec = licenseCodec,
            deviceFingerprintProvider = fingerprintProvider,
            publicKeyProvider = publicKeyProvider,
        )

        repository = ActivationRepository(
            deviceFingerprintProvider = fingerprintProvider,
            licenseCodec = licenseCodec,
            licenseValidator = licenseValidator,
            activationLocalDataSource = localDataSource,
        )
    }

    @After
    fun tearDown() {
        localDataSource.clearActivation()
    }

    @Test
    fun `activate persiste licenca permanente`() {
        val license = signWithToolsKey(LicenseType.PERMANENT)

        val status = repository.activate(license)

        assertTrue(status.isActivated)
        assertEquals(LicenseType.PERMANENT, status.licenseType)
        assertTrue(repository.isActivated())
    }

    @Test
    fun `activate persiste licenca mensal com expiracao`() {
        val license = signWithToolsKey(LicenseType.MONTHLY)

        val status = repository.activate(license)

        assertTrue(status.isActivated)
        assertEquals(LicenseType.MONTHLY, status.licenseType)
        assertTrue(status.expiresAtEpochMillis!! > status.activatedAtEpochMillis!!)
    }

    @Test
    fun `status inativo quando nao ha licenca`() {
        assertFalse(repository.isActivated())
    }

    private fun signWithToolsKey(type: LicenseType): String {
        val canonical = "v1|bfp=${fingerprint.baseFingerprintHex}|kfp=${fingerprint.keystoreFingerprintHex}|type=$type"
        val privateKey = loadPrivateKey(readToolsFile("private.pem"))
        val signatureBytes = Signature.getInstance("SHA256withRSA").run {
            initSign(privateKey)
            update(canonical.toByteArray())
            sign()
        }
        val payloadPart = Base64.getUrlEncoder().withoutPadding().encodeToString(canonical.toByteArray())
        val signaturePart = Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes)
        return "${LicenseConstants.LICENSE_PREFIX}$payloadPart.$signaturePart"
    }

    private fun loadPrivateKey(pem: String): java.security.PrivateKey {
        val normalized = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")
        val decoded = Base64.getDecoder().decode(normalized)
        val spec = PKCS8EncodedKeySpec(decoded)
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    private fun readToolsFile(name: String): String {
        val candidates = listOf(
            File("tools/license-generator/keys/$name"),
            File("../tools/license-generator/keys/$name"),
            File("../../tools/license-generator/keys/$name"),
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: error("Arquivo $name não encontrado. Execute npm run generate-keys.")
        return file.readText()
    }

    private class FixedDeviceFingerprintProvider(
        private val fingerprint: DeviceFingerprint,
    ) : DeviceFingerprintProvider {
        override fun currentFingerprint(): DeviceFingerprint = fingerprint
    }
}
