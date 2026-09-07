package br.com.banavita.app.ui.settings

import androidx.lifecycle.ViewModel
import br.com.banavita.app.data.repository.AppSettingsRepository
import br.com.banavita.app.domain.model.TranscriptionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            selectedProvider = appSettingsRepository.getTranscriptionProvider(),
            groqApiKeyConfigured = appSettingsRepository.isGroqApiKeyConfigured(),
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onProviderSelected(provider: TranscriptionProvider) {
        appSettingsRepository.setTranscriptionProvider(provider)
        _uiState.update {
            it.copy(selectedProvider = provider)
        }
    }
}
