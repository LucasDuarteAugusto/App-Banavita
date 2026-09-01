package br.com.banavita.app.ui.voice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.extraction.ExtractionDraft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractionReviewSheet(
    draft: ExtractionDraft,
    isConfirming: Boolean,
    errorMessage: String?,
    onConfirm: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val lines = ExtractionDraftFormatter.toReviewLines(draft)

    ModalBottomSheet(
        onDismissRequest = onDiscard,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.voice_review_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = stringResource(R.string.voice_review_transcript, draft.transcript),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (draft.needsReview) {
                Text(
                    text = stringResource(R.string.voice_review_needs_review),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            lines.forEach { line ->
                ReviewLineRow(line = line)
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isConfirming,
                onClick = onConfirm,
            ) {
                Text(stringResource(R.string.voice_review_confirm))
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !isConfirming,
                onClick = onDiscard,
            ) {
                Text(stringResource(R.string.voice_review_discard))
            }

            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = stringResource(R.string.voice_review_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReviewLineRow(line: DraftReviewLine) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = line.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f),
        )
        Text(
            text = line.value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (line.confidence >= 0.7f) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(0.6f),
        )
    }
}
