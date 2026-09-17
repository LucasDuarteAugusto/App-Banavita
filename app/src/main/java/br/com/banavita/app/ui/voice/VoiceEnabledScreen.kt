package br.com.banavita.app.ui.voice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext

val VoiceListContentPadding = PaddingValues(
    start = 16.dp,
    end = 16.dp,
    top = 8.dp,
    bottom = 88.dp,
)

val VoiceFormContentPadding = PaddingValues(bottom = 88.dp)

@Composable
fun VoiceEnabledScreen(
    voiceContext: VoiceScreenContext,
    addContentDescription: String,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    onVoiceSuccess: (ConfirmResult) -> Unit = {},
    showAddFab: Boolean = true,
    viewModel: VoiceCaptureViewModel = hiltViewModel(),
    content: @Composable (Modifier) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val requestPermission = rememberAudioPermissionState()

    LaunchedEffect(Unit) {
        viewModel.prepareEngineInBackground()
    }

    val onMicPressed = {
        if (uiState.phase == VoiceCapturePhase.IDLE) {
            if (hasAudioPermission(context)) {
                viewModel.onMicPressed(voiceContext)
            } else {
                requestPermission()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            uiState.errorMessage?.takeIf { uiState.phase != VoiceCapturePhase.REVIEW }?.let { message ->
                VoiceErrorBanner(
                    message = message,
                    onDismiss = viewModel::dismissError,
                )
            }
            content(Modifier.weight(1f))
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            if (uiState.phase == VoiceCapturePhase.RECORDING) {
                VoiceRecordingBar(
                    elapsedMs = uiState.elapsedMs,
                    bars = uiState.amplitudeBars,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            VoiceFabLayout(
                onMicPressed = onMicPressed,
                onMicReleased = viewModel::onMicReleased,
                onAddClick = onAddClick,
                addContentDescription = addContentDescription,
                showAddFab = showAddFab,
                isRecording = uiState.phase == VoiceCapturePhase.RECORDING,
                isProcessing = uiState.phase == VoiceCapturePhase.PROCESSING,
            )
        }

        if (uiState.phase == VoiceCapturePhase.PROCESSING) {
            uiState.statusMessage?.let { message ->
                VoiceProcessingOverlay(message = message)
            }
        }

        if (uiState.phase == VoiceCapturePhase.REVIEW) {
            val draft = uiState.draft
            if (draft != null) {
                ExtractionReviewSheet(
                    draft = draft,
                    uiState = uiState,
                    isConfirming = uiState.statusMessage != null,
                    errorMessage = uiState.errorMessage,
                    onConfirm = { viewModel.confirmDraft(onVoiceSuccess) },
                    onDiscard = viewModel::discardDraft,
                    onDraftChange = viewModel::onDraftEdited,
                )
            }
        }
    }
}

/**
 * Envolve formulários de cadastro com o fluxo de voz (mic + revisão + salvar).
 * Em modo edição, [enabled] deve ser `false` — voz só para criação.
 */
@Composable
fun VoiceEnabledFormHost(
    voiceContext: VoiceScreenContext,
    enabled: Boolean,
    onVoiceSuccess: (ConfirmResult) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VoiceCaptureViewModel = hiltViewModel(),
    content: @Composable (Modifier) -> Unit,
) {
    if (!enabled) {
        content(modifier)
        return
    }

    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = voiceContext,
        addContentDescription = "",
        showAddFab = false,
        onAddClick = {},
        onVoiceSuccess = onVoiceSuccess,
        viewModel = viewModel,
    ) { contentModifier ->
        content(contentModifier)
    }
}
