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
        onSomenteZerados = viewModel::onSomenteZerados,
        onSomenteCriticos = viewModel::onSomenteCriticos,
        onOrdenacao = viewModel::onOrdenacao,
        onIntervaloInicio = viewModel::onIntervaloInicio,
        onIntervaloFim = viewModel::onIntervaloFim,
        onEstoqueClick = viewModel::onEstoqueClick,
        onDocumentoClick = viewModel::onDocumentoClick,
        onFinanceiroClick = viewModel::onFinanceiroClick,
        onFilaEtileno = viewModel::onFilaEtileno,
        onNovaPerda = viewModel::onNovaPerda,
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
    onSomenteZerados: (Boolean) -> Unit,
    onSomenteCriticos: (Boolean) -> Unit,
    onOrdenacao: (EstoqueOrdenacao) -> Unit,
    onIntervaloInicio: (String) -> Unit,
    onIntervaloFim: (String) -> Unit,
    onEstoqueClick: (java.util.UUID) -> Unit,
    onDocumentoClick: (java.util.UUID) -> Unit,
    onFinanceiroClick: (java.util.UUID?) -> Unit,
    onFilaEtileno: () -> Unit,
    onNovaPerda: () -> Unit,
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
            ModeChips(selected = uiState.filters.modo, onModo = onModo)
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                value = uiState.filters.busca,
                onValueChange = onBusca,
                singleLine = true,
                label = { Text(stringResource(R.string.consultas_busca)) },
            )
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
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = uiState.filters.ordenacaoEstoque == EstoqueOrdenacao.SALDO_ASC,
                            onClick = { onOrdenacao(EstoqueOrdenacao.SALDO_ASC) },
                            label = { Text(stringResource(R.string.consultas_ordenar_saldo)) },
                        )
                        FilterChip(
                            selected = uiState.filters.ordenacaoEstoque == EstoqueOrdenacao.NOME_ASC,
                            onClick = { onOrdenacao(EstoqueOrdenacao.NOME_ASC) },
                            label = { Text(stringResource(R.string.consultas_ordenar_nome)) },
                        )
                        FilterChip(
                            selected = uiState.filters.somenteZerados,
                            onClick = { onSomenteZerados(!uiState.filters.somenteZerados) },
                            label = { Text(stringResource(R.string.consultas_so_zerados)) },
                        )
                        FilterChip(
                            selected = uiState.filters.somenteCriticos,
                            onClick = { onSomenteCriticos(!uiState.filters.somenteCriticos) },
                            label = { Text(stringResource(R.string.consultas_estoque_criticos)) },
                        )
                        FilterChip(
                            selected = false,
                            onClick = onFilaEtileno,
                            label = { Text(stringResource(R.string.consultas_fila_etileno)) },
                        )
                        FilterChip(
                            selected = false,
                            onClick = onNovaPerda,
                            label = { Text(stringResource(R.string.consultas_nova_perda)) },
                        )
                    }
                }
                ConsultaModo.RECEBER, ConsultaModo.PAGAR, ConsultaModo.PREVISAO -> Unit
            }
            Text(
                text = "${uiState.totalLabel}: ${uiState.totalValue}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp),
            )
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
                        val estadoCamara = listOfNotNull(
                            item.estado?.let { stringResource(it.labelRes()) },
                            item.camara?.let { stringResource(it.labelRes()) },
                        ).joinToString(" · ")
                        val subtitle = if (estadoCamara.isBlank()) {
                            stringResource(R.string.estoque_unit_label, item.unidade)
                        } else {
                            stringResource(R.string.estoque_linha_subtitle, estadoCamara, item.unidade)
                        }
                        ListItemCard(
                            title = item.descricao,
                            subtitle = subtitle,
                            trailing = item.saldoFormatado,
                            onClick = { onEstoqueClick(item.produtoId) },
                            highlighted = item.zerado || item.critico,
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
