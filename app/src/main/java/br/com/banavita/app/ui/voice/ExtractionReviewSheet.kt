package br.com.banavita.app.ui.voice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractionReviewSheet(
    draft: ExtractionDraft,
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

            ReviewEditor(draft = draft, onDraftChange = onDraftChange)

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
private fun ReviewEditor(
    draft: ExtractionDraft,
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
            EditText(stringResource(R.string.form_preco), fields.precoCentavos.value?.let { formatCurrency(it) }.orEmpty()) { value ->
                val cents = MoneyInputParser.parseToCentavos(value)
                onDraftChange(draft.copy(fields = fields.copy(precoCentavos = fields.precoCentavos.edited(cents))))
            }
            EditText(stringResource(R.string.form_unidade), fields.unidade.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(unidade = fields.unidade.edited(value.ifBlank { null }))))
            }
        }
        is ExtractionFields.VendaDraft -> {
            val item = fields.itens.firstOrNull()
            if (item != null) {
                EditText(stringResource(R.string.form_produto), item.produtoResolvido?.label ?: item.produtoRaw.value.orEmpty()) { value ->
                    val updated = item.copy(produtoRaw = item.produtoRaw.edited(value))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
                EditText(stringResource(R.string.form_quantidade), item.quantidade.value?.toString().orEmpty()) { value ->
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
                EditText(stringResource(R.string.form_preco_unit_short), item.valorUnitarioCentavos.value?.let { formatCurrency(it) }.orEmpty()) { value ->
                    val cents = MoneyInputParser.parseToCentavos(value)
                    val updated = item.copy(valorUnitarioCentavos = item.valorUnitarioCentavos.edited(cents))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
            }
            EditText(stringResource(R.string.form_cliente), fields.cliente?.label ?: fields.clienteRaw.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(clienteRaw = fields.clienteRaw.edited(value.ifBlank { null }))))
            }
            val clienteCandidates = fields.cliente?.candidates.orEmpty()
            if (clienteCandidates.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.voice_review_cliente_suggestions),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    clienteCandidates.forEach { cand ->
                        FilterChip(
                            selected = fields.cliente?.id == cand.id,
                            onClick = {
                                val resolved = br.com.banavita.app.domain.model.extraction.ResolvedEntity(
                                    id = cand.id,
                                    label = cand.label,
                                    rawSpan = cand.label,
                                    confidence = 1f,
                                    candidates = clienteCandidates,
                                )
                                onDraftChange(
                                    draft.copy(
                                        fields = fields.copy(
                                            cliente = resolved,
                                            clienteRaw = fields.clienteRaw.edited(cand.label),
                                        ),
                                    ),
                                )
                            },
                            label = { Text(cand.label) },
                        )
                    }
                }
            }
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
                EditText(stringResource(R.string.form_produto), item.produtoResolvido?.label ?: item.produtoRaw.value.orEmpty()) { value ->
                    val updated = item.copy(produtoRaw = item.produtoRaw.edited(value))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
                EditText(stringResource(R.string.form_quantidade), item.quantidade.value?.toString().orEmpty()) { value ->
                    val qty = QuantityInputParser.parse(value)
                    val updated = item.copy(quantidade = item.quantidade.edited(qty))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
                EditText(stringResource(R.string.form_preco_unit_short), item.valorUnitarioCentavos.value?.let { formatCurrency(it) }.orEmpty()) { value ->
                    val cents = MoneyInputParser.parseToCentavos(value)
                    val updated = item.copy(valorUnitarioCentavos = item.valorUnitarioCentavos.edited(cents))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
            }
            EditText(stringResource(R.string.form_fornecedor), fields.fornecedor?.label ?: fields.fornecedorRaw.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(fornecedorRaw = fields.fornecedorRaw.edited(value.ifBlank { null }))))
            }
            val fornecedorCandidates = fields.fornecedor?.candidates.orEmpty()
            if (fornecedorCandidates.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.voice_review_fornecedor_suggestions),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    fornecedorCandidates.forEach { cand ->
                        FilterChip(
                            selected = fields.fornecedor?.id == cand.id,
                            onClick = {
                                val resolved = br.com.banavita.app.domain.model.extraction.ResolvedEntity(
                                    id = cand.id,
                                    label = cand.label,
                                    rawSpan = cand.label,
                                    confidence = 1f,
                                    candidates = fornecedorCandidates,
                                )
                                onDraftChange(
                                    draft.copy(
                                        fields = fields.copy(
                                            fornecedor = resolved,
                                            fornecedorRaw = fields.fornecedorRaw.edited(cand.label),
                                        ),
                                    ),
                                )
                            },
                            label = { Text(cand.label) },
                        )
                    }
                }
            }
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
            EditText(stringResource(R.string.form_parceiro), fields.parceiro?.label ?: fields.parceiroRaw.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(parceiroRaw = fields.parceiroRaw.edited(value))))
            }
            EditText(stringResource(R.string.form_valor), fields.valorCentavos.value?.let { formatCurrency(it) }.orEmpty()) { value ->
                val cents = MoneyInputParser.parseToCentavos(value)
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
private fun EditText(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
    )
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
