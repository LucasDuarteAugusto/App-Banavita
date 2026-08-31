package br.com.banavita.app.domain.model

enum class LicenseType {
    PERMANENT,
    MONTHLY,
}

data class ActivationRequest(
    val requestCode: String,
    val baseFingerprintHex: String,
    val keystoreFingerprintHex: String,
    val hardwareBacked: Boolean,
)

data class ActivationStatus(
    val isActivated: Boolean,
    val licenseType: LicenseType?,
    val activatedAtEpochMillis: Long?,
    val expiresAtEpochMillis: Long?,
    val hardwareBacked: Boolean,
)

data class LicensePayload(
    val version: Int,
    val baseFingerprintHex: String,
    val keystoreFingerprintHex: String,
    val licenseType: LicenseType,
    val issuedAtEpochMillis: Long,
) {
    fun toCanonicalMessage(): String =
        buildString {
            append("v")
            append(version)
            append("|bfp=")
            append(baseFingerprintHex)
            append("|kfp=")
            append(keystoreFingerprintHex)
            append("|type=")
            append(licenseType.name)
        }
}
