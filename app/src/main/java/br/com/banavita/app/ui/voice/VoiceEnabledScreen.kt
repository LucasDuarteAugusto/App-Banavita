package br.com.banavita.app.ui.voice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    onVoiceSuccess: () -> Unit = {},
    modifier: Modifier = Modifier,
    showAddFab: Boolean = true,
    viewModel: VoiceCaptureViewModel = hiltViewModel(),
    content: @Composable (Modifier) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val requestPermission = rememberAudioPermissionState {
        viewModel.startCapture(voiceContext)
    }

    val onVoiceClick = {
        if (uiState.phase == VoiceCapturePhase.IDLE) {
            if (hasAudioPermission(context)) {
                viewModel.startCapture(voiceContext)
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

        VoiceFabLayout(
            modifier = Modifier.align(Alignment.BottomCenter),
            onVoiceClick = onVoiceClick,
            onAddClick = onAddClick,
            addContentDescription = addContentDescription,
            showAddFab = showAddFab,
            isVoiceBusy = uiState.phase == VoiceCapturePhase.RECORDING ||
                uiState.phase == VoiceCapturePhase.PROCESSING,
        )

        uiState.statusMessage?.let { message ->
            VoiceProcessingOverlay(message = message)
        }

        if (uiState.phase == VoiceCapturePhase.REVIEW && uiState.draft != null) {
            ExtractionReviewSheet(
                draft = uiState.draft, // ERROR - Smart cast to 'ExtractionDraft' is impossible, because 'draft' is a delegated property.
                isConfirming = uiState.statusMessage != null,
                errorMessage = uiState.errorMessage,
                onConfirm = { viewModel.confirmDraft(onVoiceSuccess) },
                onDiscard = viewModel::discardDraft,
            )
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
    onVoiceSuccess: () -> Unit,
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
