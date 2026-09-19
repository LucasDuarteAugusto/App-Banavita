package br.com.banavita.app.ui.consultas.estoque

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.ui.common.EstadoChips
import br.com.banavita.app.ui.common.ListLoadingState
import br.com.banavita.app.ui.common.QueimaPrecoDialog
import br.com.banavita.app.ui.common.formatQuantity
import br.com.banavita.app.ui.common.labelRes
import java.util.UUID

@Composable
fun EstoqueDetalheRoute(
    onEditProduto: (UUID) -> Unit,
    onLancarPerda: (loteId: UUID, produtoId: UUID) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EstoqueDetalheViewModel = hiltViewModel(),
    onTitleChange: (String?) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.descricao) {
        if (uiState.descricao.isNotBlank()) {
            onTitleChange(uiState.descricao)
        }
    }
    
    EstoqueDetalheScreen(
        uiState = uiState,
        onEditProduto = { uiState.produtoId?.let(onEditProduto) },
        onStageClick = viewModel::onStageClick,
        onLoteSelect = viewModel::onLoteSelectForAction,
        onDismissActions = viewModel::dismissStageActions,
        onSoltar = viewModel::onSoltarClick,
        onCorrigir = viewModel::onCorrigirClick,
        onPerda = { loteId ->
            uiState.produtoId?.let { onLancarPerda(loteId, it) }
        },
        onQueima = viewModel::onQueimaClick,
        onTransferQty = viewModel::onTransferQty,
        onConfirmTransfer = viewModel::confirmTransfer,
        onDismissTransfer = viewModel::dismissTransfer,
        onCorrigirEstado = viewModel::corrigirEstado,
        onDismissCorrigir = viewModel::dismissCorrigir,
        onCorrigirQtdeChange = viewModel::onCorrigirQtdeChange,
        onEntradaManualClick = viewModel::onEntradaManualClick,
        onManualEstadoChange = viewModel::onManualEstadoChange,
        onManualQtdeChange = viewModel::onManualQtdeChange,
        onManualUnidadeChange = viewModel::onManualUnidadeChange,
        onManualCustoChange = viewModel::onManualCustoChange,
        onConfirmEntradaManual = viewModel::confirmEntradaManual,
        onDismissEntradaManual = viewModel::dismissEntradaManual,
        onQueimaPreco = viewModel::onQueimaPreco,
        onConfirmQueima = viewModel::confirmQueima,
        onDismissQueima = viewModel::dismissQueima,
        modifier = modifier,
    )
}

@Composable
fun EstoqueDetalheScreen(
    uiState: EstoqueDetalheUiState,
    onEditProduto: () -> Unit,
    onStageClick: (MaturacaoEstado) -> Unit,
    onLoteSelect: (UUID) -> Unit,
    onDismissActions: () -> Unit,
    onSoltar: (UUID) -> Unit,
    onCorrigir: (UUID) -> Unit,
    onPerda: (UUID) -> Unit,
    onQueima: () -> Unit,
    onTransferQty: (String) -> Unit,
    onConfirmTransfer: () -> Unit,
    onDismissTransfer: () -> Unit,
    onCorrigirEstado: (MaturacaoEstado) -> Unit,
    onDismissCorrigir: () -> Unit,
    onCorrigirQtdeChange: (String) -> Unit,
    onEntradaManualClick: () -> Unit,
    onManualEstadoChange: (MaturacaoEstado) -> Unit,
    onManualQtdeChange: (String) -> Unit,
    onManualUnidadeChange: (String) -> Unit,
    onManualCustoChange: (String) -> Unit,
    onConfirmEntradaManual: () -> Unit,
    onDismissEntradaManual: () -> Unit,
    onQueimaPreco: (String) -> Unit,
    onConfirmQueima: () -> Unit,
    onDismissQueima: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.transferLoteId != null) {
        AlertDialog(
            onDismissRequest = onDismissTransfer,
            title = { Text(stringResource(R.string.estoque_soltar_etileno)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.transferQtyInput,
                        onValueChange = onTransferQty,
                        label = { Text(stringResource(R.string.estoque_soltar_qtde)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmTransfer) {
                    Text(stringResource(R.string.etileno_soltar))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissTransfer) {
                    Text(stringResource(R.string.form_cancel))
                }
            },
        )
    }
    if (uiState.corrigirLoteId != null) {
        AlertDialog(
            onDismissRequest = onDismissCorrigir,
            title = { Text(stringResource(R.string.estoque_corrigir_estado)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    uiState.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
                    
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.corrigirQtdeInput,
                        onValueChange = onCorrigirQtdeChange,
                        label = { Text("Quantidade a alterar") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )

                    Text(text = "Selecione o novo estado:", style = MaterialTheme.typography.labelLarge)
                    
                    EstadoChips(
                        selected = null,
                        onSelected = onCorrigirEstado,
                        includePodre = true,
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismissCorrigir) {
                    Text(stringResource(R.string.form_cancel))
                }
            },
        )
    }

    if (uiState.showEntradaManual) {
        EntradaManualPopup(
            uiState = uiState,
            onEstadoChange = onManualEstadoChange,
            onQtdeChange = onManualQtdeChange,
            onUnidadeChange = onManualUnidadeChange,
            onCustoChange = onManualCustoChange,
            onConfirm = onConfirmEntradaManual,
            onDismiss = onDismissEntradaManual
        )
    }

    if (uiState.showQueima) {
        QueimaPrecoDialog(
            precoInput = uiState.queimaPrecoInput,
            onPrecoChange = onQueimaPreco,
            onConfirm = onConfirmQueima,
            onDismiss = onDismissQueima,
            errorMessage = uiState.errorMessage,
        )
    }

    if (uiState.selectedStageForActions != null) {
        EstoqueAcoesPopup(
            estado = uiState.selectedStageForActions,
            lotes = uiState.lotes.filter { it.lote.estado == uiState.selectedStageForActions },
            selectedLoteId = uiState.selectedLoteIdForAction,
            onLoteSelect = onLoteSelect,
            onSoltar = onSoltar,
            onCorrigir = onCorrigir,
            onPerda = onPerda,
            onDismiss = onDismissActions
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (uiState.isLoading) {
            ListLoadingState()
            return
        }

        VarietySummaryCard(
            totalQuantity = uiState.saldoFormatado,
            stagesCount = uiState.estadosResumo.size
        )

        uiState.errorMessage
            ?.takeIf { uiState.transferLoteId == null && !uiState.showQueima && !uiState.showEntradaManual && uiState.corrigirLoteId == null }
            ?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            if (uiState.perecivel && uiState.estadosResumo.isNotEmpty()) {
                items(uiState.estadosResumo, key = { it.estado.name }) { resumo ->
                    StateQuantityCard(
                        estado = resumo.estado,
                        quantity = resumo.quantidadeFormatada,
                        unit = uiState.unidade,
                        onClick = { onStageClick(resumo.estado) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onEditProduto) {
                        Text("Editar Cadastro")
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onEntradaManualClick,
            ) {
                Text("Entrada manual")
            }
            if (uiState.perecivel && uiState.lotes.any { it.lote.estado == MaturacaoEstado.PINTADA }) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onQueima,
                ) {
                    Text(stringResource(R.string.estoque_aplicar_queima))
                }
            }
        }
    }
}

@Composable
private fun EntradaManualPopup(
    uiState: EstoqueDetalheUiState,
    onEstadoChange: (MaturacaoEstado) -> Unit,
    onQtdeChange: (String) -> Unit,
    onUnidadeChange: (String) -> Unit,
    onCustoChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Entrada Manual de Estoque") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiState.errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

                Text("Estado de maturação:", style = MaterialTheme.typography.labelLarge)
                EstadoChips(
                    selected = uiState.manualEstado,
                    onSelected = onEstadoChange,
                    includePodre = true
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.manualQuantidadeInput,
                    onValueChange = onQtdeChange,
                    label = { Text("Quantidade") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Text("Unidade:", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("CX PLASTICA", "CX MADEIRA").forEach { un ->
                        FilterChip(
                            selected = uiState.manualUnidade == un,
                            onClick = { onUnidadeChange(un) },
                            label = { Text(un) }
                        )
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.manualCustoInput,
                    onValueChange = onCustoChange,
                    label = { Text("Custo Unitário (Opcional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun VarietySummaryCard(totalQuantity: String, stagesCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Lote total ativo",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = totalQuantity,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
            SuggestionChip(
                onClick = {},
                label = { Text("$stagesCount Estágios", color = Color(0xFF2E7D32)) },
                colors = androidx.compose.material3.SuggestionChipDefaults.suggestionChipColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                border = null
            )
        }
    }
}

@Composable
private fun StateQuantityCard(
    estado: MaturacaoEstado,
    quantity: String,
    unit: String,
    onClick: () -> Unit
) {
    val color = when (estado) {
        MaturacaoEstado.CRUA -> Color(0xFF9E9E9E)
        MaturacaoEstado.EM_MATURACAO -> Color(0xFFCDDC39)
        MaturacaoEstado.VERDE -> Color(0xFF4CAF50)
        MaturacaoEstado.VERDOLENGA -> Color(0xFF8BC34A)
        MaturacaoEstado.AMARELA -> Color(0xFFFFEB3B)
        MaturacaoEstado.PINTADA -> Color(0xFFFF9800)
        MaturacaoEstado.PODRE -> Color(0xFF795548)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color, CircleShape)
                    .border(1.dp, Color.Black.copy(alpha = 0.1f), CircleShape)
            )
            Text(
                text = stringResource(estado.labelRes()),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = quantity.substringBefore(" "),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun EstoqueAcoesPopup(
    estado: MaturacaoEstado,
    lotes: List<EstoqueLoteUi>,
    selectedLoteId: UUID?,
    onLoteSelect: (UUID) -> Unit,
    onSoltar: (UUID) -> Unit,
    onCorrigir: (UUID) -> Unit,
    onPerda: (UUID) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ações para ${stringResource(estado.labelRes())}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (lotes.size > 1) {
                    Text("Selecione o lote:", style = MaterialTheme.typography.labelMedium)
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(lotes, key = { it.lote.id }) { item ->
                            FilterChip(
                                selected = selectedLoteId == item.lote.id,
                                onClick = { onLoteSelect(item.lote.id) },
                                label = { Text("${item.lote.rotulo} (${item.quantidadeFormatada})") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val loteId = selectedLoteId
                    if (loteId != null) {
                        val lote = lotes.find { it.lote.id == loteId }?.lote
                        if (lote?.camara == Camara.FRIA) {
                            Button(
                                onClick = { onSoltar(loteId) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.estoque_soltar_etileno))
                            }
                        }
                        OutlinedButton(
                            onClick = { onCorrigir(loteId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.estoque_corrigir_estado))
                        }
                        OutlinedButton(
                            onClick = { onPerda(loteId) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(stringResource(R.string.estoque_lancar_perda))
                        }
                    } else {
                        Text(
                            "Por favor, selecione um lote acima para realizar ações.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}
