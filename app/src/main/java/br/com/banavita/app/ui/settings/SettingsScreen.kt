package br.com.banavita.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.TranscriptionProvider

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    SettingsScreen(
        uiState = uiState,
        onProviderSelected = viewModel::onProviderSelected,
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onProviderSelected: (TranscriptionProvider) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = stringResource(R.string.settings_transcription_section),
            style = MaterialTheme.typography.titleMedium,
        )

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProviderOptionCard(
                title = stringResource(R.string.settings_provider_groq_title),
                description = stringResource(R.string.settings_provider_groq_desc),
                selected = uiState.selectedProvider == TranscriptionProvider.GROQ,
                enabled = uiState.groqApiKeyConfigured,
                footnote = if (uiState.groqApiKeyConfigured) {
                    stringResource(R.string.settings_provider_groq_ready)
                } else {
                    stringResource(R.string.settings_provider_groq_missing_key)
                },
                onClick = { onProviderSelected(TranscriptionProvider.GROQ) },
            )
            ProviderOptionCard(
                title = stringResource(R.string.settings_provider_local_title),
                description = stringResource(R.string.settings_provider_local_desc),
                selected = uiState.selectedProvider == TranscriptionProvider.LOCAL,
                enabled = true,
                footnote = stringResource(R.string.settings_provider_local_note),
                onClick = { onProviderSelected(TranscriptionProvider.LOCAL) },
            )
        }
    }
}

@Composable
private fun ProviderOptionCard(
    title: String,
    description: String,
    selected: Boolean,
    enabled: Boolean,
    footnote: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.RadioButton,
                onClick = onClick,
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                enabled = enabled,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    },
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = footnote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
