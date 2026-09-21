package br.com.banavita.app.ui.voice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
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
    enabled: Boolean = true,
    viewModel: VoiceCaptureViewModel = hiltViewModel(),
    content: @Composable (Modifier) -> Unit,
) {
    if (!enabled) {
        Box(modifier = modifier.fillMaxSize()) {
            content(Modifier.fillMaxSize())
            if (showAddFab) {
                SmallFloatingActionButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = addContentDescription,
                    )
                }
            }
        }
        return
    }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val requestPermission = rememberAudioPermissionState()
    var showHelpSheet by remember { mutableStateOf(false) }

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
                val info = VoiceHints.getInfo(voiceContext)
                VoiceRecordingBar(
                    elapsedMs = uiState.elapsedMs,
                    bars = uiState.amplitudeBars,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    hint = stringResource(R.string.voice_hint_prefix) + stringResource(info.hintRes)
                )
            }
            VoiceFabLayout(
                onMicPressed = onMicPressed,
                onMicReleased = viewModel::onMicReleased,
                onAddClick = onAddClick,
                onHelpClick = { showHelpSheet = true },
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

        if (showHelpSheet) {
            VoiceHelpBottomSheet(
                context = voiceContext,
                onDismiss = { showHelpSheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VoiceHelpBottomSheet(
    context: VoiceScreenContext,
    onDismiss: () -> Unit
) {
    val info = VoiceHints.getInfo(context)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.voice_sheet_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.voice_sheet_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.voice_sheet_pattern_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = info.pattern,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.voice_sheet_example_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "\"${info.example}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
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
