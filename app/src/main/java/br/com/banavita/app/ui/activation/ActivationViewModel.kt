package br.com.banavita.app.ui.activation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ActivationRepository
import br.com.banavita.app.domain.exception.DeviceMismatchException
import br.com.banavita.app.domain.exception.InvalidLicenseException
import br.com.banavita.app.domain.exception.LicenseExpiredException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivationViewModel @Inject constructor(
    private val activationRepository: ActivationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivationUiState())
    val uiState: StateFlow<ActivationUiState> = _uiState.asStateFlow()

    init {
        loadRequestCode()
    }

    private fun loadRequestCode() {
        viewModelScope.launch {
            runCatching {
                activationRepository.buildActivationRequest()
            }.onSuccess { request ->
                _uiState.update {
                    it.copy(
                        requestCode = request.requestCode,
                        hardwareBacked = request.hardwareBacked,
                        isLoadingRequest = false,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingRequest = false,
                        errorMessage = error.message ?: "Erro ao gerar código de solicitação.",
                    )
                }
            }
        }
    }

    fun onLicenseTokenChange(value: String) {
        _uiState.update { it.copy(licenseToken = value, errorMessage = null) }
    }

    fun onToggleLicenseVisibility() {
        _uiState.update { it.copy(isLicenseVisible = !it.isLicenseVisible) }
    }

    fun onRequestCodeCopied() {
        _uiState.update { it.copy(copyConfirmed = true) }
        viewModelScope.launch {
            delay(COPY_FEEDBACK_MS)
            _uiState.update { it.copy(copyConfirmed = false) }
        }
    }

    fun activate(onSuccess: () -> Unit) {
        val token = _uiState.value.licenseToken.trim()
        if (token.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Informe a licença enviada pelo administrador.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isActivating = true, errorMessage = null) }
            runCatching {
                activationRepository.activate(token)
            }.onSuccess { status ->
                if (status.isActivated) {
                    _uiState.update { it.copy(isActivating = false) }
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isActivating = false,
                            errorMessage = "Não foi possível ativar o aplicativo.",
                        )
                    }
                }
            }.onFailure { error ->
                val message = when (error) {
                    is InvalidLicenseException -> error.message ?: "Licença inválida."
                    is DeviceMismatchException -> "Esta licença não pertence a este dispositivo."
                    is LicenseExpiredException -> "Licença expirada. Solicite uma nova ao administrador."
                    else -> error.message ?: "Erro ao ativar o aplicativo."
                }
                _uiState.update {
                    it.copy(isActivating = false, errorMessage = message)
                }
            }
        }
    }

    companion object {
        private const val COPY_FEEDBACK_MS = 2_000L
    }
}
