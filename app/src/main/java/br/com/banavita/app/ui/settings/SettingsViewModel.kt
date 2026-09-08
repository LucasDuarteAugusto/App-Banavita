package br.com.banavita.app.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.backup.BackupManifest
import br.com.banavita.app.data.repository.AppSettingsRepository
import br.com.banavita.app.data.repository.DatabaseBackupRepository
import br.com.banavita.app.domain.exception.BackupException
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.model.TranscriptionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val databaseBackupRepository: DatabaseBackupRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            selectedProvider = appSettingsRepository.getTranscriptionProvider(),
            groqApiKeyConfigured = appSettingsRepository.isGroqApiKeyConfigured(),
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var pendingRestoreUri: Uri? = null

    fun onProviderSelected(provider: TranscriptionProvider) {
        if (_uiState.value.isBackupBusy) return
        appSettingsRepository.setTranscriptionProvider(provider)
        _uiState.update {
            it.copy(selectedProvider = provider, errorMessage = null, statusMessage = null)
        }
    }

    fun suggestedBackupFileName(): String = BackupManifest.suggestedFileName()

    fun exportTo(uri: Uri) {
        if (_uiState.value.isBackupBusy) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(isBackupBusy = true, errorMessage = null, statusMessage = null)
            }
            runCatching { databaseBackupRepository.exportTo(uri) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isBackupBusy = false,
                            statusMessage = SUCCESS_EXPORT,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isBackupBusy = false,
                            errorMessage = mapBackupError(error, fallback = "Erro ao exportar os dados."),
                        )
                    }
                }
        }
    }

    fun onRestoreFileChosen(uri: Uri) {
        if (_uiState.value.isBackupBusy) return
        pendingRestoreUri = uri
        _uiState.update {
            it.copy(showRestoreConfirm = true, errorMessage = null, statusMessage = null)
        }
    }

    fun onDismissRestoreConfirm() {
        pendingRestoreUri = null
        _uiState.update { it.copy(showRestoreConfirm = false) }
    }

    fun confirmRestore() {
        val uri = pendingRestoreUri ?: return
        if (_uiState.value.isBackupBusy) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showRestoreConfirm = false,
                    isBackupBusy = true,
                    errorMessage = null,
                    statusMessage = null,
                )
            }
            runCatching { databaseBackupRepository.restoreFrom(uri) }
                .onSuccess {
                    pendingRestoreUri = null
                    _uiState.update {
                        it.copy(
                            isBackupBusy = false,
                            statusMessage = SUCCESS_RESTORE,
                        )
                    }
                }
                .onFailure { error ->
                    pendingRestoreUri = null
                    _uiState.update {
                        it.copy(
                            isBackupBusy = false,
                            errorMessage = mapBackupError(error, fallback = "Erro ao restaurar os dados."),
                        )
                    }
                }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(errorMessage = null, statusMessage = null) }
    }

    private fun mapBackupError(error: Throwable, fallback: String): String =
        when (error) {
            is BackupException -> error.message ?: fallback
            is BanavitaDomainException -> error.message ?: fallback
            else -> error.message ?: fallback
        }

    companion object {
        const val SUCCESS_EXPORT = "EXPORT_OK"
        const val SUCCESS_RESTORE = "RESTORE_OK"
    }
}
