package br.com.banavita.app.core.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LicenseCodecTest {

    private val codec = LicenseCodec()

    @Test
    fun `request code faz roundtrip de fingerprints`() {
        val fingerprint = DeviceFingerprint(
            baseFingerprintHex = "bfp123",
            keystoreFingerprintHex = "kfp456",
            hardwareBacked = true,
        )

        val request = codec.buildRequestCode(fingerprint)
        assertTrue(request.requestCode.startsWith(LicenseConstants.REQUEST_PREFIX))
        assertEquals(fingerprint.baseFingerprintHex, request.baseFingerprintHex)

        val parsed = codec.parseRequestCode(request.requestCode)
        assertEquals(fingerprint.baseFingerprintHex, parsed.baseFingerprintHex)
        assertEquals(fingerprint.keystoreFingerprintHex, parsed.keystoreFingerprintHex)
        assertEquals(fingerprint.hardwareBacked, parsed.hardwareBacked)
    }
}
