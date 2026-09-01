package br.com.banavita.app.core.security

import android.util.Base64
import br.com.banavita.app.domain.exception.InvalidLicenseException
import br.com.banavita.app.domain.model.ActivationRequest
import br.com.banavita.app.domain.model.LicensePayload
import br.com.banavita.app.domain.model.LicenseType
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LicenseCodec @Inject constructor() {

    fun buildRequestCode(fingerprint: DeviceFingerprint): ActivationRequest {
        val payloadJson = JSONObject()
            .put("v", LicenseConstants.LICENSE_VERSION)
            .put("bfp", fingerprint.baseFingerprintHex)
            .put("kfp", fingerprint.keystoreFingerprintHex)
            .put("hw", fingerprint.hardwareBacked)
            .toString()

        val encoded = Base64.encodeToString(
            payloadJson.toByteArray(StandardCharsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING,
        )

        return ActivationRequest(
            requestCode = LicenseConstants.REQUEST_PREFIX + encoded,
            baseFingerprintHex = fingerprint.baseFingerprintHex,
            keystoreFingerprintHex = fingerprint.keystoreFingerprintHex,
            hardwareBacked = fingerprint.hardwareBacked,
        )
    }

    fun parseRequestCode(requestCode: String): DeviceFingerprint {
        if (!requestCode.startsWith(LicenseConstants.REQUEST_PREFIX)) {
            throw InvalidLicenseException("Código de solicitação inválido.")
        }
        val encoded = requestCode.removePrefix(LicenseConstants.REQUEST_PREFIX)
        val jsonBytes = Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val json = JSONObject(String(jsonBytes, StandardCharsets.UTF_8))

        return DeviceFingerprint(
            baseFingerprintHex = json.getString("bfp"),
            keystoreFingerprintHex = json.getString("kfp"),
            hardwareBacked = json.optBoolean("hw", false),
        )
    }

    fun parseLicenseToken(licenseToken: String): ParsedLicense {
        if (!licenseToken.startsWith(LicenseConstants.LICENSE_PREFIX)) {
            throw InvalidLicenseException("Formato de licença inválido.")
        }

        val body = licenseToken.removePrefix(LicenseConstants.LICENSE_PREFIX)
        val parts = body.split(".")
        if (parts.size != 2) {
            throw InvalidLicenseException("Licença corrompida.")
        }

        val payloadBytes = Base64.decode(parts[0], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val signatureBytes = Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val canonicalMessage = String(payloadBytes, StandardCharsets.UTF_8)
        val payload = parseCanonicalMessage(canonicalMessage)

        return ParsedLicense(
            payload = payload,
            canonicalMessage = canonicalMessage,
            signatureBytes = signatureBytes,
        )
    }

    private fun parseCanonicalMessage(message: String): LicensePayload {
        val chunks = message.split("|")
        if (chunks.size != 4 || chunks[0] != "v${LicenseConstants.LICENSE_VERSION}") {
            throw InvalidLicenseException("Payload de licença inválido.")
        }

        val values = chunks.drop(1).associate { chunk ->
            val keyValue = chunk.split("=", limit = 2)
            if (keyValue.size != 2) {
                throw InvalidLicenseException("Payload de licença inválido.")
            }
            keyValue[0] to keyValue[1]
        }

        val licenseType = runCatching {
            LicenseType.valueOf(values.getValue("type"))
        }.getOrElse {
            throw InvalidLicenseException("Tipo de licença inválido.")
        }

        return LicensePayload(
            version = LicenseConstants.LICENSE_VERSION,
            baseFingerprintHex = values.getValue("bfp"),
            keystoreFingerprintHex = values.getValue("kfp"),
            licenseType = licenseType,
            issuedAtEpochMillis = System.currentTimeMillis(),
        )
    }
}

data class ParsedLicense(
    val payload: LicensePayload,
    val canonicalMessage: String,
    val signatureBytes: ByteArray, // WARNING - Property with 'Array' type in a 'data' class: it is recommended to override 'equals()' and 'hashCode()'
)
