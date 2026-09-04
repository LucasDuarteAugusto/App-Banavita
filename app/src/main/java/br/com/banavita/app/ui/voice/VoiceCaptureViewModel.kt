package br.com.banavita.app.ui.voice

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.R
import br.com.banavita.app.core.speech.AmplitudeMeter
import br.com.banavita.app.core.speech.AudioCapture
import br.com.banavita.app.data.repository.ExtractionRepository
import br.com.banavita.app.data.repository.SpeechToTextRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.exception.ExtractionConfirmException
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.domain.model.extraction.ExtractionDraft
import br.com.banavita.app.domain.model.extraction.ExtractionFields
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class VoiceCaptureViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val speechToTextRepository: SpeechToTextRepository,
    private val extractionRepository: ExtractionRepository,
    private val audioCapture: AudioCapture,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceCaptureUiState())
    val uiState: StateFlow<VoiceCaptureUiState> = _uiState.asStateFlow()

    private val finishing = AtomicBoolean(false)
    private var captureContext: VoiceScreenContext? = null
    private var pressedAtElapsed = 0L
    private var startJob: Job? = null
    private var levelsJob: Job? = null
    private var prepareJob: Job? = null

    fun prepareEngineInBackground() {
        viewModelScope.launch {
            runCatching { speechToTextRepository.prepareEngine() }
        }
    }

    fun onMicPressed(context: VoiceScreenContext) {
        if (_uiState.value.phase != VoiceCapturePhase.IDLE) return

        finishing.set(false)
        captureContext = context
        pressedAtElapsed = SystemClock.elapsedRealtime()

        _uiState.update {
            it.copy(
                phase = VoiceCapturePhase.RECORDING,
                errorMessage = null,
                statusMessage = null,
                draft = null,
                elapsedMs = 0L,
                amplitudeBars = AmplitudeMeter.idleBars(),
            )
        }

        prepareJob = viewModelScope.launch {
            runCatching { speechToTextRepository.prepareEngine() }
        }
        levelsJob = viewModelScope.launch {
            audioCapture.levels.collect { levels ->
                if (_uiState.value.phase != VoiceCapturePhase.RECORDING) return@collect
                _uiState.update { state ->
                    state.copy(
                        elapsedMs = levels.elapsedMs,
                        amplitudeBars = levels.bars,
                    )
                }
                if (levels.maxDurationReached) {
                    onMicReleased()
                }
            }
        }
        startJob = viewModelScope.launch {
            runCatching { audioCapture.start() }
                .onFailure { error ->
                    levelsJob?.cancel()
                    audioCapture.cancel()
                    _uiState.update {
                        it.copy(
                            phase = VoiceCapturePhase.IDLE,
                            statusMessage = null,
                            errorMessage = resolveErrorMessage(error),
                            elapsedMs = 0L,
                            amplitudeBars = emptyList(),
                        )
                    }
                }
        }
    }

    fun onMicReleased() {
        if (_uiState.value.phase != VoiceCapturePhase.RECORDING) return
        if (!finishing.compareAndSet(false, true)) return
        viewModelScope.launch {
            finishRecordingAndTranscribe()
        }
    }

    fun onDraftEdited(draft: ExtractionDraft) {
        extractionRepository.updateDraft(draft)
        _uiState.update { it.copy(draft = draft, errorMessage = null) }
    }

    fun confirmDraft(onSuccess: (ConfirmResult) -> Unit) {
        val draft = _uiState.value.draft ?: return
        viewModelScope.launch {
            val applying = draft.fields is ExtractionFields.ConsultasDraft ||
                (draft.fields is ExtractionFields.EventoDraft &&
                    draft.fields.recorteLista.isPresent &&
                    draft.fields.titulo.value.isNullOrBlank())
            _uiState.update {
                it.copy(
                    phase = VoiceCapturePhase.PROCESSING,
                    statusMessage = appContext.getString(
                        if (applying) R.string.voice_applying else R.string.voice_saving,
                    ),
                )
            }
            runCatching {
                extractionRepository.confirmDraft(draft.id)
            }.onSuccess { result ->
                reset()
                onSuccess(result)
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
        finishing.set(false)
        captureContext = null
    }

    override fun onCleared() {
        audioCapture.cancel()
        super.onCleared()
    }

    private suspend fun finishRecordingAndTranscribe() {
        startJob?.join()
        levelsJob?.cancel()
        levelsJob = null
        if (_uiState.value.phase != VoiceCapturePhase.RECORDING) {
            finishing.set(false)
            return
        }

        val heldMs = SystemClock.elapsedRealtime() - pressedAtElapsed
        val context = captureContext
        if (heldMs < MIN_HOLD_MS || context == null) {
            audioCapture.cancel()
            _uiState.update {
                VoiceCaptureUiState(
                    errorMessage = appContext.getString(R.string.voice_hold_to_talk),
                )
            }
            finishing.set(false)
            return
        }

        _uiState.update {
            it.copy(
                phase = VoiceCapturePhase.PROCESSING,
                statusMessage = appContext.getString(R.string.voice_transcribing),
            )
        }

        runCatching {
            val samples = audioCapture.stop()
            prepareJob?.join()
            val transcription = speechToTextRepository.transcribe(samples)
            extractionRepository.extractFromTranscription(transcription, context)
        }.onSuccess { draft ->
            _uiState.update {
                it.copy(
                    phase = VoiceCapturePhase.REVIEW,
                    draft = draft,
                    statusMessage = null,
                    errorMessage = null,
                    elapsedMs = 0L,
                    amplitudeBars = emptyList(),
                )
            }
        }.onFailure { error ->
            audioCapture.cancel()
            _uiState.update {
                it.copy(
                    phase = VoiceCapturePhase.IDLE,
                    statusMessage = null,
                    errorMessage = resolveErrorMessage(error),
                    elapsedMs = 0L,
                    amplitudeBars = emptyList(),
                )
            }
        }
        finishing.set(false)
    }

    private fun resolveErrorMessage(error: Throwable): String = when (error) {
        is BanavitaDomainException -> error.message ?: "Erro no processamento de voz."
        else -> error.message ?: "Erro no processamento de voz."
    }

    companion object {
        const val MIN_HOLD_MS = 400L
    }
}
