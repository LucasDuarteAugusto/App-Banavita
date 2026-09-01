package br.com.banavita.app.ui.activation

data class ActivationUiState(
    val requestCode: String = "",
    val hardwareBacked: Boolean = false,
    val licenseToken: String = "",
    val isLicenseVisible: Boolean = false,
    val isLoadingRequest: Boolean = true,
    val isActivating: Boolean = false,
    val errorMessage: String? = null,
    val copyConfirmed: Boolean = false,
)
