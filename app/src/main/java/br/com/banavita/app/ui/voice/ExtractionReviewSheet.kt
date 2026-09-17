package br.com.banavita.app.ui.voice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.ConsultaVisao
import br.com.banavita.app.domain.model.PeriodoPreset
import br.com.banavita.app.domain.model.extraction.ExtractionDraft
import br.com.banavita.app.domain.model.extraction.ExtractionField
import br.com.banavita.app.domain.model.extraction.ExtractionFields
import br.com.banavita.app.domain.model.extraction.FieldSource
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.common.EstadoChips
import br.com.banavita.app.ui.common.MoneyInputParser
import br.com.banavita.app.ui.common.QuantityInputParser
import br.com.banavita.app.ui.common.formatCurrency
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractionReviewSheet(
    draft: ExtractionDraft,
    uiState: VoiceCaptureUiState,
    isConfirming: Boolean,
    errorMessage: String?,
    onConfirm: () -> Unit,
    onDiscard: () -> Unit,
    onDraftChange: (ExtractionDraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

            Text(
                text = stringResource(voicePatternRes(draft.context)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (draft.needsReview) {
                Text(
                    text = stringResource(R.string.voice_review_needs_review),
                    color = MaterialTheme.colorScheme.tertiary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            ReviewEditor(
                draft = draft,
                uiState = uiState,
                onDraftChange = onDraftChange
            )

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewEditor(
    draft: ExtractionDraft,
    uiState: VoiceCaptureUiState,
    onDraftChange: (ExtractionDraft) -> Unit,
) {
    when (val fields = draft.fields) {
        is ExtractionFields.ClienteDraft -> {
            EditText(stringResource(R.string.form_nome), fields.nome.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(nome = fields.nome.edited(value))))
            }
            EditText(stringResource(R.string.form_telefone), fields.telefone.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(telefone = fields.telefone.edited(value.ifBlank { null }))))
            }
        }
        is ExtractionFields.FornecedorDraft -> {
            EditText(stringResource(R.string.form_nome), fields.nome.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(nome = fields.nome.edited(value))))
            }
            EditText(stringResource(R.string.form_telefone), fields.telefone.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(telefone = fields.telefone.edited(value.ifBlank { null }))))
            }
        }
        is ExtractionFields.GrupoDraft -> {
            EditText(stringResource(R.string.form_nome), fields.nome.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(nome = fields.nome.edited(value))))
            }
            EditText(stringResource(R.string.form_observacao), fields.observacao.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(observacao = fields.observacao.edited(value.ifBlank { null }))))
            }
        }
        is ExtractionFields.ProdutoDraft -> {
            EditText(stringResource(R.string.form_descricao), fields.descricao.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(descricao = fields.descricao.edited(value))))
            }
            MoneyEdit(stringResource(R.string.form_preco), fields.precoCentavos.value) { cents ->
                onDraftChange(draft.copy(fields = fields.copy(precoCentavos = fields.precoCentavos.edited(cents))))
            }
            UnitDropdown(fields.unidade.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(unidade = fields.unidade.edited(value))))
            }
        }
        is ExtractionFields.VendaDraft -> {
            val item = fields.itens.firstOrNull()
            if (item != null) {
                EntityDropdown(
                    label = stringResource(R.string.form_produto),
                    selectedId = item.produtoResolvido?.id,
                    selectedLabel = item.produtoResolvido?.label ?: item.produtoRaw.value.orEmpty(),
                    options = uiState.produtos.map { it.id to it.descricao },
                    onSelected = { id, label ->
                        val resolved = br.com.banavita.app.domain.model.extraction.ResolvedEntity(
                            id = id, label = label, rawSpan = label, confidence = 1f
                        )
                        val updated = item.copy(
                            produtoResolvido = resolved,
                            produtoRaw = item.produtoRaw.edited(label)
                        )
                        onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                    }
                )
                EditText(stringResource(R.string.form_quantidade), item.quantidade.value?.toString().orEmpty(), keyboardType = KeyboardType.Decimal) { value ->
                    val qty = QuantityInputParser.parse(value)
                    val updated = item.copy(quantidade = item.quantidade.edited(qty))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
                Text(text = stringResource(R.string.form_estado), style = MaterialTheme.typography.labelLarge)
                EstadoChips(
                    selected = item.estado.value,
                    onSelected = { estado ->
                        val updated = item.copy(estado = item.estado.edited(estado))
                        onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                    },
                    includePodre = false,
                )
                MoneyEdit(stringResource(R.string.form_preco_unit_short), item.valorUnitarioCentavos.value) { cents ->
                    val updated = item.copy(valorUnitarioCentavos = item.valorUnitarioCentavos.edited(cents))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
            }
            EntityDropdown(
                label = stringResource(R.string.form_cliente),
                selectedId = fields.cliente?.id,
                selectedLabel = fields.cliente?.label ?: fields.clienteRaw.value.orEmpty(),
                options = uiState.clientes.map { it.id to it.nome },
                onSelected = { id, label ->
                    val resolved = br.com.banavita.app.domain.model.extraction.ResolvedEntity(
                        id = id, label = label, rawSpan = label, confidence = 1f
                    )
                    onDraftChange(draft.copy(fields = fields.copy(cliente = resolved, clienteRaw = fields.clienteRaw.edited(label))))
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val novoPrazo = fields.aPrazo.value != true
                        onDraftChange(draft.copy(fields = fields.copy(aPrazo = fields.aPrazo.edited(novoPrazo))))
                    }
                    .padding(vertical = 4.dp),
            ) {
                Checkbox(
                    checked = fields.aPrazo.value != true,
                    onCheckedChange = { isPago ->
                        onDraftChange(draft.copy(fields = fields.copy(aPrazo = fields.aPrazo.edited(!isPago))))
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.form_pago_checkbox),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        is ExtractionFields.CompraDraft -> {
            val item = fields.itens.firstOrNull()
            if (item != null) {
                EntityDropdown(
                    label = stringResource(R.string.form_produto),
                    selectedId = item.produtoResolvido?.id,
                    selectedLabel = item.produtoResolvido?.label ?: item.produtoRaw.value.orEmpty(),
                    options = uiState.produtos.map { it.id to it.descricao },
                    onSelected = { id, label ->
                        val resolved = br.com.banavita.app.domain.model.extraction.ResolvedEntity(
                            id = id, label = label, rawSpan = label, confidence = 1f
                        )
                        val updated = item.copy(
                            produtoResolvido = resolved,
                            produtoRaw = item.produtoRaw.edited(label)
                        )
                        onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                    }
                )
                EditText(stringResource(R.string.form_quantidade), item.quantidade.value?.toString().orEmpty(), keyboardType = KeyboardType.Decimal) { value ->
                    val qty = QuantityInputParser.parse(value)
                    val updated = item.copy(quantidade = item.quantidade.edited(qty))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
                MoneyEdit(stringResource(R.string.form_preco_unit_short), item.valorUnitarioCentavos.value) { cents ->
                    val updated = item.copy(valorUnitarioCentavos = item.valorUnitarioCentavos.edited(cents))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
            }
            EntityDropdown(
                label = stringResource(R.string.form_fornecedor),
                selectedId = fields.fornecedor?.id,
                selectedLabel = fields.fornecedor?.label ?: fields.fornecedorRaw.value.orEmpty(),
                options = uiState.fornecedores.map { it.id to it.nome },
                onSelected = { id, label ->
                    val resolved = br.com.banavita.app.domain.model.extraction.ResolvedEntity(
                        id = id, label = label, rawSpan = label, confidence = 1f
                    )
                    onDraftChange(draft.copy(fields = fields.copy(fornecedor = resolved, fornecedorRaw = fields.fornecedorRaw.edited(label))))
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val novoPrazo = fields.aPrazo.value != true
                        onDraftChange(draft.copy(fields = fields.copy(aPrazo = fields.aPrazo.edited(novoPrazo))))
                    }
                    .padding(vertical = 4.dp),
            ) {
                Checkbox(
                    checked = fields.aPrazo.value != true,
                    onCheckedChange = { isPago ->
                        onDraftChange(draft.copy(fields = fields.copy(aPrazo = fields.aPrazo.edited(!isPago))))
                    },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.form_pago_compra_checkbox),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        is ExtractionFields.ConsultasDraft -> {
            Text(stringResource(R.string.consultas_modo_label), style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ConsultaModo.entries.forEach { modo ->
                    FilterChip(
                        selected = fields.modo.value == modo,
                        onClick = { onDraftChange(draft.copy(fields = fields.copy(modo = fields.modo.edited(modo)))) },
                        label = { Text(modo.route) },
                    )
                }
            }
            EditText(stringResource(R.string.consultas_busca_label), fields.busca.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(busca = fields.busca.edited(value.ifBlank { null }))))
            }
            Text(stringResource(R.string.consultas_periodo_label), style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PeriodoPreset.entries.take(3).forEach { periodo ->
                    FilterChip(
                        selected = fields.periodo.value == periodo,
                        onClick = { onDraftChange(draft.copy(fields = fields.copy(periodo = fields.periodo.edited(periodo)))) },
                        label = { Text(periodo.name.lowercase()) },
                    )
                }
            }
            Text(stringResource(R.string.consultas_visao_label), style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ConsultaVisao.entries.forEach { visao ->
                    FilterChip(
                        selected = fields.visao.value == visao,
                        onClick = { onDraftChange(draft.copy(fields = fields.copy(visao = fields.visao.edited(visao)))) },
                        label = { Text(visao.name.lowercase()) },
                    )
                }
            }
        }
        is ExtractionFields.BaixaDraft -> {
            EntityDropdown(
                label = stringResource(R.string.form_parceiro),
                selectedId = fields.parceiro?.id,
                selectedLabel = fields.parceiro?.label ?: fields.parceiroRaw.value.orEmpty(),
                options = (uiState.clientes.map { it.id to it.nome } + uiState.fornecedores.map { it.id to it.nome }),
                onSelected = { id, label ->
                    val resolved = br.com.banavita.app.domain.model.extraction.ResolvedEntity(
                        id = id, label = label, rawSpan = label, confidence = 1f
                    )
                    onDraftChange(draft.copy(fields = fields.copy(parceiro = resolved, parceiroRaw = fields.parceiroRaw.edited(label))))
                }
            )
            MoneyEdit(stringResource(R.string.form_valor), fields.valorCentavos.value) { cents ->
                onDraftChange(draft.copy(fields = fields.copy(valorCentavos = fields.valorCentavos.edited(cents))))
            }
        }
        is ExtractionFields.EventoDraft -> {
            EditText(stringResource(R.string.form_nome), fields.titulo.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(titulo = fields.titulo.edited(value.ifBlank { null }))))
            }
            EditText(stringResource(R.string.form_observacao), fields.observacao.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(observacao = fields.observacao.edited(value.ifBlank { null }))))
            }
            PrazoChips(
                aPrazo = fields.notificar.value != false,
                vistaLabel = stringResource(R.string.agenda_notif_off),
                prazoLabel = stringResource(R.string.agenda_notif_on),
            ) { on ->
                onDraftChange(draft.copy(fields = fields.copy(notificar = fields.notificar.edited(on))))
            }
        }
    }
}

@Composable
private fun EditText(
    label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onChange: (String) -> Unit
) {
    var text by remember(value) { mutableStateOf(value) }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        onValueChange = { 
            text = it
            onChange(it) 
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}

@Composable
private fun MoneyEdit(
    label: String,
    centavos: Long?,
    onChange: (Long?) -> Unit
) {
    val initialText = centavos?.let { MoneyInputParser.formatForInput(it) } ?: ""
    var text by remember(centavos) { mutableStateOf(initialText) }
    
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        onValueChange = { input ->
            // Permite digitar livremente, valida ao final ou via parse
            text = input
            val cents = MoneyInputParser.parseToCentavos(input)
            onChange(cents)
        },
        label = { Text(label) },
        placeholder = { Text("0,00") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    )
}

@Composable
private fun UnitDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    val options = listOf("UN", "KG", "CX MADEIRA", "CX PLASTICA", "DZ")
    var expanded by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.form_unidade)) },
            trailingIcon = { 
                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) 
            },
            colors = @OptIn(ExperimentalMaterial3Api::class) ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            enabled = true
        )
        // Usando DropdownMenu comum para evitar bugs de anchor em BottomSheet
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntityDropdown(
    label: String,
    selectedId: UUID?,
    selectedLabel: String,
    options: List<Pair<UUID, String>>,
    onSelected: (UUID?, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember(selectedLabel) { mutableStateOf(selectedLabel) }
    
    val filteredOptions = remember(searchText, options) {
        if (searchText.isBlank()) {
            options.take(20)
        } else {
            options.map { it to calculateSimilarity(searchText, it.second) }
                .filter { it.second > 0.1 || it.first.second.contains(searchText, ignoreCase = true) }
                .sortedByDescending { it.second }
                .map { it.first }
                .take(15)
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchText,
            onValueChange = { 
                searchText = it
                expanded = true
                onSelected(null, it) 
            },
            label = { Text(label) },
            trailingIcon = { 
                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) 
            },
            colors = @OptIn(ExperimentalMaterial3Api::class) ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            // Não deve ser focado para permitir digitação no campo de texto
            properties = androidx.compose.ui.window.PopupProperties(focusable = false),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            if (filteredOptions.isEmpty() && searchText.isNotBlank()) {
                DropdownMenuItem(
                    text = { Text("Manter: \"$searchText\"") },
                    onClick = { expanded = false }
                )
            }
            
            filteredOptions.forEach { (id, name) ->
                val isSelected = selectedId == id
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = name,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ) 
                    },
                    onClick = {
                        searchText = name
                        onSelected(id, name)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun calculateSimilarity(s1: String, s2: String): Float {
    val n1 = s1.lowercase().trim()
    val n2 = s2.lowercase().trim()
    if (n1 == n2) return 1.0f
    if (n2.contains(n1) || n1.contains(n2)) return 0.8f
    
    // Levenshtein simplificado ou apenas interseção de caracteres
    val common = n1.toSet().intersect(n2.toSet()).size
    return common.toFloat() / maxOf(n1.length, n2.length)
}

@Composable
private fun PrazoChips(
    aPrazo: Boolean,
    vistaLabel: String = "À vista",
    prazoLabel: String = "A prazo",
    onChange: (Boolean) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = !aPrazo,
            onClick = { onChange(false) },
            label = { Text(vistaLabel) },
        )
        FilterChip(
            selected = aPrazo,
            onClick = { onChange(true) },
            label = { Text(prazoLabel) },
        )
    }
}

private fun <T> ExtractionField<T>.edited(value: T?): ExtractionField<T> =
    copy(value = value, confidence = if (value != null) 1f else 0f, source = FieldSource.HEURISTIC)

private fun voicePatternRes(context: VoiceScreenContext): Int = when (context) {
    VoiceScreenContext.CLIENTE -> R.string.voice_pattern_cliente
    VoiceScreenContext.FORNECEDOR -> R.string.voice_pattern_fornecedor
    VoiceScreenContext.PRODUTO -> R.string.voice_pattern_produto
    VoiceScreenContext.GRUPO -> R.string.voice_pattern_grupo
    VoiceScreenContext.VENDA -> R.string.voice_pattern_venda
    VoiceScreenContext.COMPRA -> R.string.voice_pattern_compra
    VoiceScreenContext.CONSULTAS -> R.string.voice_pattern_consultas
    VoiceScreenContext.INICIO -> R.string.voice_pattern_inicio
    VoiceScreenContext.RECEBER -> R.string.voice_pattern_receber
    VoiceScreenContext.PAGAR -> R.string.voice_pattern_pagar
    VoiceScreenContext.AGENDA -> R.string.voice_pattern_agenda
}
