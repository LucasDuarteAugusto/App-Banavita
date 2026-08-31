package br.com.banavita.app.domain.exception

import br.com.banavita.app.domain.model.LicenseType

open class ActivationException(message: String) : BanavitaDomainException(message)

class InvalidLicenseException(message: String) : ActivationException(message)

class LicenseExpiredException(
    val licenseType: LicenseType,
    val expiredAtEpochMillis: Long,
) : ActivationException(
    "Licença ${licenseType.name} expirou em $expiredAtEpochMillis.",
)

class DeviceMismatchException(message: String) : ActivationException(message)
