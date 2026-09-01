package br.com.banavita.app.ui.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ExtractionRepository
import br.com.banavita.app.data.repository.SpeechToTextRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.exception.ExtractionConfirmException
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoiceCaptureViewModel @Inject constructor(
    private val speechToTextRepository: SpeechToTextRepository,
    private val extractionRepository: ExtractionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceCaptureUiState())
    val uiState: StateFlow<VoiceCaptureUiState> = _uiState.asStateFlow()

    fun startCapture(context: VoiceScreenContext, recordingSeconds: Int = DEFAULT_RECORDING_SECONDS) {
        if (_uiState.value.phase != VoiceCapturePhase.IDLE) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    phase = VoiceCapturePhase.RECORDING,
                    errorMessage = null,
                    statusMessage = "Gravando… fale agora",
                    draft = null,
                )
            }

            runCatching {
                val transcription = speechToTextRepository.transcribeMicrophone(recordingSeconds)
                _uiState.update {
                    it.copy(phase = VoiceCapturePhase.PROCESSING, statusMessage = "Transcrevendo…")
                }
                extractionRepository.extractFromTranscription(transcription, context)
            }.onSuccess { draft ->
                _uiState.update {
                    it.copy(
                        phase = VoiceCapturePhase.REVIEW,
                        draft = draft,
                        statusMessage = null,
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        phase = VoiceCapturePhase.IDLE,
                        statusMessage = null,
                        errorMessage = resolveErrorMessage(error),
                    )
                }
            }
        }
    }

    fun confirmDraft(onSuccess: () -> Unit) {
        val draft = _uiState.value.draft ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(phase = VoiceCapturePhase.PROCESSING, statusMessage = "Salvando…") }
            runCatching {
                extractionRepository.confirmDraft(draft.id)
            }.onSuccess {
                reset()
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        phase = VoiceCapturePhase.REVIEW,
                        statusMessage = null,
                        errorMessage = when (error) {
                            is ExtractionConfirmException -> error.message ?: "Não foi possível confirmar."
                            is BanavitaDomainException -> error.message ?: "Erro ao salvar."
                            else -> error.message ?: "Erro ao salvar."
                        },
                    )
                }
            }
        }
    }

    fun discardDraft() {
        val draft = _uiState.value.draft
        if (draft != null) {
            runCatching { extractionRepository.discardDraft(draft.id) }
        }
        reset()
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun reset() {
        _uiState.value = VoiceCaptureUiState()
    }

    private fun resolveErrorMessage(error: Throwable): String = when (error) {
        is BanavitaDomainException -> error.message ?: "Erro no processamento de voz."
        else -> error.message ?: "Erro no processamento de voz."
    }

    companion object {
        const val DEFAULT_RECORDING_SECONDS = 8
    }
}
