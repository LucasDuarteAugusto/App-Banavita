package br.com.banavita.app.ui.settings

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.core.reminder.PereciveisMorningScheduler
import br.com.banavita.app.data.backup.BackupManifest
import br.com.banavita.app.data.repository.AppSettingsRepository
import br.com.banavita.app.data.repository.DatabaseBackupRepository
import br.com.banavita.app.data.repository.SpeechToTextRepository
import br.com.banavita.app.domain.exception.BackupException
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.model.ThemeMode
import br.com.banavita.app.domain.model.TranscriptionProvider
import br.com.banavita.app.ui.common.QuantityInputParser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appSettingsRepository: AppSettingsRepository,
    private val databaseBackupRepository: DatabaseBackupRepository,
    private val morningScheduler: PereciveisMorningScheduler,
    private val speechToTextRepository: SpeechToTextRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            selectedProvider = appSettingsRepository.getTranscriptionProvider(),
            selectedTheme = appSettingsRepository.getThemeMode(),
            groqApiKeyConfigured = appSettingsRepository.isGroqApiKeyConfigured(),
            etilenoCapacidadeInput = formatKg(appSettingsRepository.getEtilenoCapacidadeKg()),
            etilenoSoltarDiaInput = formatKg(appSettingsRepository.getEtilenoSoltarPorDiaKg()),
            avisoMatinalInput = formatMinutes(appSettingsRepository.getAvisoMatinalMinutos()),
            selectedRingtoneName = getRingtoneName(appSettingsRepository.getRingtoneUri()),
            selectedOfflineModelId = appSettingsRepository.getOfflineModelId(),
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

    fun onThemeSelected(mode: ThemeMode) {
        if (_uiState.value.isBackupBusy) return
        appSettingsRepository.setThemeMode(mode)
        _uiState.update {
            it.copy(selectedTheme = mode, errorMessage = null, statusMessage = null)
        }
    }

    fun onOfflineModelSelected(modelId: String) {
        if (_uiState.value.isBackupBusy) return
        appSettingsRepository.setOfflineModelId(modelId)
        _uiState.update {
            it.copy(selectedOfflineModelId = modelId, errorMessage = null, statusMessage = null)
        }
        // Force reload engine on next use
        speechToTextRepository.release()
    }

    fun onEtilenoCapacidadeChange(value: String) {
        _uiState.update { it.copy(etilenoCapacidadeInput = value, errorMessage = null) }
        val parsed = QuantityInputParser.parse(value) ?: return
        appSettingsRepository.setEtilenoCapacidadeKg(parsed.coerceAtLeast(0.0))
    }

    fun onEtilenoSoltarDiaChange(value: String) {
        _uiState.update { it.copy(etilenoSoltarDiaInput = value, errorMessage = null) }
        val parsed = QuantityInputParser.parse(value) ?: return
        appSettingsRepository.setEtilenoSoltarPorDiaKg(parsed.coerceAtLeast(0.0))
    }

    fun onAvisoMatinalChange(value: String) {
        _uiState.update { it.copy(avisoMatinalInput = value, errorMessage = null) }
        parseHhMm(value)?.let {
            appSettingsRepository.setAvisoMatinalMinutos(it)
            morningScheduler.scheduleNext()
        }
    }

    fun onRingtoneSelected(uri: Uri?) {
        val uriString = uri?.toString()
        appSettingsRepository.setRingtoneUri(uriString)
        _uiState.update {
            it.copy(selectedRingtoneName = getRingtoneName(uriString))
        }
    }

    private fun getRingtoneName(uriString: String?): String? {
        if (uriString == null) return null
        return try {
            val uri = Uri.parse(uriString)
            RingtoneManager.getRingtone(context, uri)?.getTitle(context)
        } catch (e: Exception) {
            null
        }
    }

    private fun formatKg(value: Double): String =
        if (value == 0.0) "0" else value.toString().replace('.', ',')

    private fun formatMinutes(total: Int): String {
        val h = total / 60
        val m = total % 60
        return "%02d:%02d".format(h, m)
    }

    private fun parseHhMm(raw: String): Int? {
        val match = Regex("""^(\d{1,2}):(\d{2})$""").find(raw.trim()) ?: return null
        val hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].toInt()
        if (hour !in 0..23 || minute !in 0..59) return null
        return hour * 60 + minute
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
