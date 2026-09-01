package br.com.banavita.app.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ActivationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BanavitaAppViewModel @Inject constructor(
    private val activationRepository: ActivationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        refreshActivationState()
    }

    fun refreshActivationState() {
        viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true) }
            val activated = activationRepository.isActivated()
            _uiState.update {
                it.copy(isChecking = false, isActivated = activated)
            }
        }
    }

    fun onActivated() {
        _uiState.update { it.copy(isActivated = true) }
    }
}
