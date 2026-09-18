package br.com.banavita.app.ui.consultas

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import br.com.banavita.app.ui.common.BanavitaIcons
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.ConsultaVisao
import br.com.banavita.app.domain.model.EstoqueOrdenacao
import br.com.banavita.app.domain.model.PeriodoPreset
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.ListEmptyState
import br.com.banavita.app.ui.common.ListItemCard
import br.com.banavita.app.ui.common.ListLoadingState
import br.com.banavita.app.ui.common.formatCurrency
import br.com.banavita.app.ui.common.formatDate
import br.com.banavita.app.ui.common.labelRes
import br.com.banavita.app.ui.voice.VoiceEnabledScreen
import br.com.banavita.app.ui.voice.VoiceListContentPadding

@Composable
fun ConsultasRoute(
    onNavEvent: (ConsultasNavEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConsultasViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.navEvents.collect(onNavEvent)
    }
    ConsultasScreen(
        uiState = uiState,
        modifier = modifier,
        onModo = viewModel::onModo,
        onBusca = viewModel::onBusca,
        onPeriodo = viewModel::onPeriodo,
        onVisao = viewModel::onVisao,
        onIntervaloInicio = viewModel::onIntervaloInicio,
        onIntervaloFim = viewModel::onIntervaloFim,
        onEstoqueClick = viewModel::onEstoqueClick,
        onDocumentoClick = viewModel::onDocumentoClick,
        onFinanceiroClick = viewModel::onFinanceiroClick,
        onNovaEncomenda = viewModel::onNovaEncomenda,
        onVoiceResult = viewModel::applyVoice,
    )
}

@Composable
fun ConsultasScreen(
    uiState: ConsultasUiState,
    onModo: (ConsultaModo) -> Unit,
    onBusca: (String) -> Unit,
    onPeriodo: (PeriodoPreset) -> Unit,
    onVisao: (ConsultaVisao) -> Unit,
    onIntervaloInicio: (String) -> Unit,
    onIntervaloFim: (String) -> Unit,
    onEstoqueClick: (java.util.UUID) -> Unit,
    onDocumentoClick: (java.util.UUID) -> Unit,
    onFinanceiroClick: (java.util.UUID?) -> Unit,
    onNovaEncomenda: () -> Unit,
    onVoiceResult: (ConfirmResult) -> Unit,
    modifier: Modifier = Modifier,
) {
    val voiceContext = when (uiState.filters.modo) {
        ConsultaModo.RECEBER -> VoiceScreenContext.RECEBER
        ConsultaModo.PAGAR -> VoiceScreenContext.PAGAR
        else -> VoiceScreenContext.CONSULTAS
    }
    VoiceEnabledScreen(
        modifier = modifier,
        voiceContext = voiceContext,
        addContentDescription = "",
        showAddFab = false,
        onAddClick = {},
        onVoiceSuccess = onVoiceResult,
    ) { contentModifier ->
        Column(
            modifier = contentModifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            when (uiState.filters.modo) {
                ConsultaModo.VENDAS, ConsultaModo.COMPRAS -> {
                    PeriodoChips(selected = uiState.filters.periodo, onPeriodo = onPeriodo)
                    IntervaloFields(uiState, onIntervaloInicio, onIntervaloFim)
                    VisaoChips(selected = uiState.filters.visao, onVisao = onVisao)
                }
                ConsultaModo.PERDAS, ConsultaModo.ENCOMENDAS -> {
                    PeriodoChips(selected = uiState.filters.periodo, onPeriodo = onPeriodo)
                    IntervaloFields(uiState, onIntervaloInicio, onIntervaloFim)
                    if (uiState.filters.modo == ConsultaModo.ENCOMENDAS) {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FilterChip(
                                selected = false,
                                onClick = onNovaEncomenda,
                                label = { Text(stringResource(R.string.consultas_nova_encomenda)) },
                            )
                        }
                    }
                }
                ConsultaModo.ESTOQUE -> {
                    Text(
                        text = "VARIEDADES DISPONÍVEIS",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                ConsultaModo.RECEBER, ConsultaModo.PAGAR, ConsultaModo.PREVISAO -> Unit
            }
            uiState.hint?.let { hint ->
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            when {
                uiState.isLoading -> ListLoadingState()
                else -> ConsultasList(
                    uiState = uiState,
                    onEstoqueClick = onEstoqueClick,
                    onDocumentoClick = onDocumentoClick,
                    onFinanceiroClick = onFinanceiroClick,
                )
            }
        }
    }
}

@Composable
private fun IntervaloFields(
    uiState: ConsultasUiState,
    onIntervaloInicio: (String) -> Unit,
    onIntervaloFim: (String) -> Unit,
) {
    if (uiState.filters.periodo != PeriodoPreset.INTERVALO) return
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.filters.intervaloInicioInput,
        onValueChange = onIntervaloInicio,
        label = { Text(stringResource(R.string.consultas_intervalo_inicio)) },
        singleLine = true,
    )
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = uiState.filters.intervaloFimInput,
        onValueChange = onIntervaloFim,
        label = { Text(stringResource(R.string.consultas_intervalo_fim)) },
        singleLine = true,
    )
}

@Composable
private fun ConsultasList(
    uiState: ConsultasUiState,
    onEstoqueClick: (java.util.UUID) -> Unit,
    onDocumentoClick: (java.util.UUID) -> Unit,
    onFinanceiroClick: (java.util.UUID?) -> Unit,
) {
    val emptyMessage = when (uiState.filters.modo) {
        ConsultaModo.ESTOQUE -> stringResource(R.string.estoque_empty)
        ConsultaModo.VENDAS -> stringResource(R.string.consultas_vendas_empty)
        ConsultaModo.COMPRAS -> stringResource(R.string.consultas_compras_empty)
        ConsultaModo.RECEBER -> stringResource(R.string.consultas_receber_empty)
        ConsultaModo.PAGAR -> stringResource(R.string.consultas_pagar_empty)
        ConsultaModo.PREVISAO -> stringResource(R.string.consultas_previsao_empty)
        ConsultaModo.PERDAS -> stringResource(R.string.consultas_perdas_empty)
        ConsultaModo.ENCOMENDAS -> stringResource(R.string.consultas_encomendas_empty)
    }
    LazyColumn(
        contentPadding = VoiceListContentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (uiState.filters.modo) {
            ConsultaModo.ESTOQUE -> {
                if (uiState.estoqueItems.isEmpty()) {
                    item { ListEmptyState(message = emptyMessage) }
                } else {
                    items(uiState.estoqueItems, key = { it.key }) { item ->
                        ProductStockCard(
                            title = item.descricao,
                            quantityLabel = "${item.saldoFormatado} em estoque",
                            onClick = { onEstoqueClick(item.produtoId) }
                        )
                    }
                }
            }
            ConsultaModo.PREVISAO -> {
                if (uiState.previsaoItems.isEmpty()) {
                    item { ListEmptyState(message = emptyMessage) }
                } else {
                    items(uiState.previsaoItems, key = { it.key }) { item ->
                        ListItemCard(
                            title = "${item.descricao} · ${stringResource(item.estado.labelRes())}",
                            subtitle = stringResource(R.string.consultas_previsao_3d, item.kg3),
                            trailing = stringResource(R.string.consultas_previsao_7d, item.kg7),
                        )
                    }
                }
            }
            ConsultaModo.PERDAS -> {
                if (uiState.perdaItems.isEmpty()) {
                    item { ListEmptyState(message = emptyMessage) }
                } else {
                    items(uiState.perdaItems, key = { it.produtoId }) { item ->
                        ListItemCard(
                            title = item.descricao,
                            subtitle = item.detalhe,
                            trailing = item.valorFormatado,
                            onClick = { onEstoqueClick(item.produtoId) },
                        )
                    }
                }
            }
            ConsultaModo.ENCOMENDAS -> {
                if (uiState.encomendaItems.isEmpty()) {
                    item { ListEmptyState(message = emptyMessage) }
                } else {
                    items(uiState.encomendaItems, key = { it.id }) { item ->
                        ListItemCard(
                            title = item.titulo,
                            subtitle = item.subtitle,
                            trailing = item.trailing,
                            onClick = { onDocumentoClick(item.id) },
                        )
                    }
                }
            }
            ConsultaModo.VENDAS, ConsultaModo.COMPRAS -> {
                if (uiState.filters.visao == ConsultaVisao.DOCUMENTOS) {
                    if (uiState.documentos.isEmpty()) {
                        item { ListEmptyState(message = emptyMessage) }
                    } else {
                        items(uiState.documentos, key = { it.id }) { item ->
                            ListItemCard(
                                title = item.parceiro,
                                subtitle = formatDate(item.data),
                                trailing = formatCurrency(item.valorCentavos),
                                onClick = { onDocumentoClick(item.id) },
                            )
                        }
                    }
                } else {
                    if (uiState.agregados.isEmpty()) {
                        item { ListEmptyState(message = emptyMessage) }
                    } else {
                        items(uiState.agregados, key = { it.nome }) { item ->
                            ListItemCard(
                                title = item.nome,
                                subtitle = item.quantidadeLabel,
                                trailing = formatCurrency(item.valorCentavos),
                            )
                        }
                    }
                }
            }
            ConsultaModo.RECEBER, ConsultaModo.PAGAR -> {
                if (uiState.financeiroItems.isEmpty()) {
                    item { ListEmptyState(message = emptyMessage) }
                } else {
                    items(uiState.financeiroItems, key = { it.parceiroId ?: it.nome }) { item ->
                        ListItemCard(
                            title = item.nome,
                            subtitle = item.telefone ?: item.dataMaisAntiga?.let { formatDate(it) },
                            trailing = formatCurrency(item.saldoCentavos),
                            onClick = { onFinanceiroClick(item.parceiroId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StockSummaryCard(totalValue: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E7D32)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "VOLUME TOTAL",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = totalValue,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Somatória de todas as variedades ativas no pátio.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ProductStockCard(
    title: String,
    quantityLabel: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFE8F5E9),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = BanavitaIcons.Inventory,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = quantityLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Detalhes",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF2E7D32)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
private fun ModeChips(selected: ConsultaModo, onModo: (ConsultaModo) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ConsultaModo.entries.forEach { modo ->
            val label = when (modo) {
                ConsultaModo.ESTOQUE -> stringResource(R.string.consultas_mode_estoque)
                ConsultaModo.VENDAS -> stringResource(R.string.consultas_mode_vendas)
                ConsultaModo.COMPRAS -> stringResource(R.string.consultas_mode_compras)
                ConsultaModo.RECEBER -> stringResource(R.string.consultas_mode_receber)
                ConsultaModo.PAGAR -> stringResource(R.string.consultas_mode_pagar)
                ConsultaModo.PREVISAO -> stringResource(R.string.consultas_mode_previsao)
                ConsultaModo.PERDAS -> stringResource(R.string.consultas_mode_perdas)
                ConsultaModo.ENCOMENDAS -> stringResource(R.string.consultas_mode_encomendas)
            }
            FilterChip(
                selected = selected == modo,
                onClick = { onModo(modo) },
                label = { Text(label) },
            )
        }
    }
}

@Composable
private fun PeriodoChips(selected: PeriodoPreset, onPeriodo: (PeriodoPreset) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PeriodoPreset.entries.forEach { periodo ->
            val label = when (periodo) {
                PeriodoPreset.HOJE -> stringResource(R.string.consultas_periodo_hoje)
                PeriodoPreset.SEMANA -> stringResource(R.string.consultas_periodo_semana)
                PeriodoPreset.MES -> stringResource(R.string.consultas_periodo_mes)
                PeriodoPreset.INTERVALO -> stringResource(R.string.consultas_periodo_intervalo)
            }
            FilterChip(
                selected = selected == periodo,
                onClick = { onPeriodo(periodo) },
                label = { Text(label) },
            )
        }
    }
}

@Composable
private fun VisaoChips(selected: ConsultaVisao, onVisao: (ConsultaVisao) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ConsultaVisao.entries.forEach { visao ->
            val label = when (visao) {
                ConsultaVisao.DOCUMENTOS -> stringResource(R.string.consultas_visao_lista)
                ConsultaVisao.POR_PARCEIRO -> stringResource(R.string.consultas_visao_parceiro)
                ConsultaVisao.POR_PRODUTO -> stringResource(R.string.consultas_visao_produto)
            }
            FilterChip(
                selected = selected == visao,
                onClick = { onVisao(visao) },
                label = { Text(label) },
            )
        }
    }
}
