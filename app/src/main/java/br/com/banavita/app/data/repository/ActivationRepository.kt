package br.com.banavita.app.data.repository

import br.com.banavita.app.core.security.DeviceFingerprintProvider
import br.com.banavita.app.core.security.LicenseCodec
import br.com.banavita.app.core.security.LicenseValidator
import br.com.banavita.app.core.security.ValidatedLicense
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.activation.ActivationLocalDataSource
import br.com.banavita.app.data.local.activation.StoredActivation
import br.com.banavita.app.domain.exception.ActivationException
import br.com.banavita.app.domain.model.ActivationRequest
import br.com.banavita.app.domain.model.ActivationStatus
import br.com.banavita.app.domain.model.LicenseType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivationRepository @Inject constructor(
    private val deviceFingerprintProvider: DeviceFingerprintProvider,
    private val licenseCodec: LicenseCodec,
    private val licenseValidator: LicenseValidator,
    private val activationLocalDataSource: ActivationLocalDataSource,
) {
    fun buildActivationRequest(): ActivationRequest {
        val fingerprint = deviceFingerprintProvider.currentFingerprint()
        return licenseCodec.buildRequestCode(fingerprint)
    }

    fun activate(licenseToken: String): ActivationStatus {
        val normalizedToken = licenseToken.trim()
        val existingActivation = activationLocalDataSource.readActivation()
        val preservedActivatedAt = existingActivation
            ?.takeIf { it.licenseToken == normalizedToken }
            ?.activatedAtEpochMillis

        val validated = licenseValidator.validateLicenseToken(
            licenseToken = normalizedToken,
            activatedAtEpochMillis = preservedActivatedAt,
        )

        val activationMoment = preservedActivatedAt ?: validated.activatedAtEpochMillis
        activationLocalDataSource.saveActivation(
            StoredActivation(
                licenseToken = normalizedToken,
                licenseType = validated.payload.licenseType,
                activatedAtEpochMillis = activationMoment,
                hardwareBacked = validated.hardwareBacked,
            ),
        )

        return validated.toStatus(isActivated = true)
    }

    fun getActivationStatus(nowEpochMillis: Long = nowEpochMillis()): ActivationStatus {
        val stored = activationLocalDataSource.readActivation() ?: return inactiveStatus()

        return runCatching {
            val validated = licenseValidator.validateLicenseToken(
                licenseToken = stored.licenseToken,
                activatedAtEpochMillis = stored.activatedAtEpochMillis,
                nowEpochMillis = nowEpochMillis,
            )
            validated.toStatus(isActivated = true)
        }.getOrElse { exception ->
            if (exception is ActivationException) {
                inactiveStatus()
            } else {
                throw exception
            }
        }
    }

    fun clearActivation() {
        activationLocalDataSource.clearActivation()
    }

    fun isActivated(nowEpochMillis: Long = nowEpochMillis()): Boolean =
        getActivationStatus(nowEpochMillis).isActivated

    private fun inactiveStatus(): ActivationStatus =
        ActivationStatus(
            isActivated = false,
            licenseType = null,
            activatedAtEpochMillis = null,
            expiresAtEpochMillis = null,
            hardwareBacked = false,
        )

    private fun ValidatedLicense.toStatus(isActivated: Boolean): ActivationStatus =
        ActivationStatus(
            isActivated = isActivated,
            licenseType = payload.licenseType,
            activatedAtEpochMillis = activatedAtEpochMillis,
            expiresAtEpochMillis = expiresAtEpochMillis,
            hardwareBacked = hardwareBacked,
        )
}
