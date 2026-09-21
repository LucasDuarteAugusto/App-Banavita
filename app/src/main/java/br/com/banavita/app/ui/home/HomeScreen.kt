package br.com.banavita.app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.BanavitaIcons
import br.com.banavita.app.ui.common.QueimaPrecoDialog
import br.com.banavita.app.ui.common.formatCurrency
import br.com.banavita.app.ui.common.formatDateTime
import br.com.banavita.app.ui.voice.VoiceEnabledScreen
import br.com.banavita.app.ui.voice.VoiceListContentPadding

@Composable
fun HomeRoute(
    onNavEvent: (HomeNavEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.navEvents.collect(onNavEvent)
    }
    HomeScreen(
        uiState = uiState,
        onConsultasClick = { modo -> onNavEvent(HomeNavEvent.Consultas(modo)) },
        onPereciveisClick = viewModel::onPereciveisClick,
        onEncomendasClick = viewModel::onEncomendasClick,
        onQueimaClick = viewModel::onQueimaClick,
        onQueimaPreco = viewModel::onQueimaPreco,
        onConfirmQueima = viewModel::confirmQueima,
        onDismissQueima = viewModel::dismissQueima,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onConsultasClick: (ConsultaModo) -> Unit,
    onPereciveisClick: () -> Unit,
    onEncomendasClick: () -> Unit,
    onQueimaClick: () -> Unit,
    onQueimaPreco: (String) -> Unit,
    onConfirmQueima: () -> Unit,
    onDismissQueima: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.showQueimaDialog) {
        QueimaPrecoDialog(
            precoInput = uiState.queimaPrecoInput,
            onPrecoChange = onQueimaPreco,
            onConfirm = onConfirmQueima,
            onDismiss = onDismissQueima,
            errorMessage = uiState.queimaError,
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.home_welcome),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ShortcutCard(
            title = stringResource(R.string.home_card_estoque_title),
            description = if (uiState.zerados == 0) {
                stringResource(R.string.home_card_estoque_ok)
            } else {
                stringResource(R.string.home_card_estoque_zerados, uiState.zerados)
            },
            icon = BanavitaIcons.Inventory,
            onClick = { onConsultasClick(ConsultaModo.ESTOQUE) },
        )
        PereciveisCard(
            uiState = uiState,
            onClick = onPereciveisClick,
            onQueimaClick = onQueimaClick,
        )
        ShortcutCard(
            title = stringResource(R.string.home_card_encomendas_title),
            description = if (uiState.encomendasAbertasSemana == 0) {
                stringResource(R.string.home_card_encomendas_empty)
            } else {
                stringResource(R.string.home_card_encomendas_count, uiState.encomendasAbertasSemana)
            },
            icon = BanavitaIcons.Event,
            onClick = onEncomendasClick,
        )
        ShortcutCard(
            title = stringResource(R.string.home_card_receber_title),
            description = if (uiState.receberCentavos == 0L) {
                stringResource(R.string.consultas_receber_empty)
            } else {
                formatCurrency(uiState.receberCentavos)
            },
            icon = BanavitaIcons.Payments,
            onClick = { onConsultasClick(ConsultaModo.RECEBER) },
        )
        ShortcutCard(
            title = stringResource(R.string.home_card_pagar_title),
            description = if (uiState.pagarCentavos == 0L) {
                stringResource(R.string.consultas_pagar_empty)
            } else {
                formatCurrency(uiState.pagarCentavos)
            },
            icon = BanavitaIcons.Payments,
            onClick = { onConsultasClick(ConsultaModo.PAGAR) },
        )
    }
}

@Composable
private fun PereciveisCard(
    uiState: HomeUiState,
    onClick: () -> Unit,
    onQueimaClick: () -> Unit,
) {
    val context = LocalContext.current
    val description = when {
        uiState.kgPintada <= 1e-9 && uiState.kgPodreOuAmanha <= 1e-9 && uiState.moverParaArmazenagem.isEmpty() ->
            stringResource(R.string.home_card_pereciveis_empty)
        else -> buildString {
            if (uiState.kgPintada > 1e-9) {
                append(
                    context.getString(
                        R.string.home_card_pereciveis_pintada,
                        formatKg(uiState.kgPintada),
                        uiState.unidadePintada ?: "Kg",
                        uiState.descricaoPintada.orEmpty(),
                    ),
                )
            }
            if (uiState.kgPodreOuAmanha > 1e-9) {
                if (isNotEmpty()) append(" · ")
                append(
                    context.getString(
                        R.string.home_card_pereciveis_podre,
                        formatKg(uiState.kgPodreOuAmanha),
                        uiState.unidadePodre ?: "Kg",
                    ),
                )
            }
            uiState.moverParaArmazenagem.forEach { advice ->
                if (isNotEmpty()) append(" · ")
                append(
                    context.getString(
                        R.string.home_card_pereciveis_mover,
                        formatKg(advice.quantidade),
                        advice.unidade,
                        advice.descricao,
                    ),
                )
            }
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = BanavitaIcons.Inventory,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.home_card_pereciveis_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (uiState.kgPintada > 1e-9 && uiState.produtoIdPintada != null) {
                TextButton(onClick = onQueimaClick) {
                    Text(stringResource(R.string.estoque_aplicar_queima))
                }
            }
        }
    }
}

private fun formatKg(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else String.format(java.util.Locale.getDefault(), "%.1f", value)

@Composable
private fun ShortcutCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
