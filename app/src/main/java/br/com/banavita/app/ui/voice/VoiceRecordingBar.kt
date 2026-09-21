package br.com.banavita.app.ui.voice

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.com.banavita.app.R
import br.com.banavita.app.core.speech.AmplitudeMeter

@Composable
fun VoiceRecordingBar(
    elapsedMs: Long,
    bars: List<Float>,
    modifier: Modifier = Modifier,
    hint: String? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (hint != null) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = hint,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        val recordingLabel = stringResource(R.string.voice_recording)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = recordingLabel },
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                )
                Text(
                    text = AmplitudeMeter.formatElapsed(elapsedMs),
                    style = MaterialTheme.typography.titleMedium,
                )
                AmplitudeBars(
                    levels = bars,
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp),
                )
            }
        }
    }
}

@Composable
private fun AmplitudeBars(
    levels: List<Float>,
    modifier: Modifier = Modifier,
) {
    val barColor = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.75f)
    val count = AmplitudeMeter.BAR_COUNT
    Canvas(modifier = modifier) {
        val gap = 3.dp.toPx()
        val barWidth = ((size.width - gap * (count - 1)) / count).coerceAtLeast(2.dp.toPx())
        val minH = 4.dp.toPx()
        val maxH = size.height
        repeat(count) { index ->
            val level = levels.getOrElse(index) { AmplitudeMeter.MIN_BAR }
                .coerceIn(AmplitudeMeter.MIN_BAR, 1f)
            val height = minH + (maxH - minH) * level
            val x = index * (barWidth + gap)
            val y = (size.height - height) / 2f
            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
            )
        }
    }
}
