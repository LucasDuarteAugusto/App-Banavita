package br.com.banavita.app.core.security

data class DeviceFingerprint(
    val baseFingerprintHex: String,
    val keystoreFingerprintHex: String,
    val hardwareBacked: Boolean,
)

interface DeviceFingerprintProvider {
    fun currentFingerprint(): DeviceFingerprint
}
