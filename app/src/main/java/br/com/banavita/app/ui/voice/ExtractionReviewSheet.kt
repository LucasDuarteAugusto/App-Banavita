package br.com.banavita.app.ui.voice

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
            EditText("Nome", fields.nome.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(nome = fields.nome.edited(value))))
            }
            EditText("Telefone", fields.telefone.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(telefone = fields.telefone.edited(value.ifBlank { null }))))
            }
        }
        is ExtractionFields.FornecedorDraft -> {
            EditText("Nome", fields.nome.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(nome = fields.nome.edited(value))))
            }
            EditText("Telefone", fields.telefone.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(telefone = fields.telefone.edited(value.ifBlank { null }))))
            }
        }
        is ExtractionFields.GrupoDraft -> {
            EditText("Nome", fields.nome.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(nome = fields.nome.edited(value))))
            }
            EditText("Observação", fields.observacao.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(observacao = fields.observacao.edited(value.ifBlank { null }))))
            }
        }
        is ExtractionFields.ProdutoDraft -> {
            EditText("Descrição", fields.descricao.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(descricao = fields.descricao.edited(value))))
            }
            EditText("Preço", fields.precoCentavos.value?.let { formatCurrency(it) }.orEmpty()) { value ->
                val cents = MoneyInputParser.parseToCentavos(value)
                onDraftChange(draft.copy(fields = fields.copy(precoCentavos = fields.precoCentavos.edited(cents))))
            }
            EditText("Unidade", fields.unidade.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(unidade = fields.unidade.edited(value.ifBlank { null }))))
            }
        }
        is ExtractionFields.VendaDraft -> {
            val item = fields.itens.firstOrNull()
            if (item != null) {
                EditText("Produto", item.produtoResolvido?.label ?: item.produtoRaw.value.orEmpty()) { value ->
                    val updated = item.copy(produtoRaw = item.produtoRaw.edited(value))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
                EditText("Quantidade", item.quantidade.value?.toString().orEmpty()) { value ->
                    val qty = QuantityInputParser.parse(value)
                    val updated = item.copy(quantidade = item.quantidade.edited(qty))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
                EditText("Preço unit.", item.valorUnitarioCentavos.value?.let { formatCurrency(it) }.orEmpty()) { value ->
                    val cents = MoneyInputParser.parseToCentavos(value)
                    val updated = item.copy(valorUnitarioCentavos = item.valorUnitarioCentavos.edited(cents))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
            }
            EditText("Cliente", fields.cliente?.label ?: fields.clienteRaw.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(clienteRaw = fields.clienteRaw.edited(value.ifBlank { null }))))
            }
            PrazoChips(aPrazo = fields.aPrazo.value == true) { prazo ->
                onDraftChange(draft.copy(fields = fields.copy(aPrazo = fields.aPrazo.edited(prazo))))
            }
        }
        is ExtractionFields.CompraDraft -> {
            val item = fields.itens.firstOrNull()
            if (item != null) {
                EditText("Produto", item.produtoResolvido?.label ?: item.produtoRaw.value.orEmpty()) { value ->
                    val updated = item.copy(produtoRaw = item.produtoRaw.edited(value))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
                EditText("Quantidade", item.quantidade.value?.toString().orEmpty()) { value ->
                    val qty = QuantityInputParser.parse(value)
                    val updated = item.copy(quantidade = item.quantidade.edited(qty))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
                EditText("Preço unit.", item.valorUnitarioCentavos.value?.let { formatCurrency(it) }.orEmpty()) { value ->
                    val cents = MoneyInputParser.parseToCentavos(value)
                    val updated = item.copy(valorUnitarioCentavos = item.valorUnitarioCentavos.edited(cents))
                    onDraftChange(draft.copy(fields = fields.copy(itens = listOf(updated))))
                }
            }
            EditText("Fornecedor", fields.fornecedor?.label ?: fields.fornecedorRaw.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(fornecedorRaw = fields.fornecedorRaw.edited(value.ifBlank { null }))))
            }
            PrazoChips(aPrazo = fields.aPrazo.value == true) { prazo ->
                onDraftChange(draft.copy(fields = fields.copy(aPrazo = fields.aPrazo.edited(prazo))))
            }
        }
        is ExtractionFields.ConsultasDraft -> {
            Text("Modo", style = MaterialTheme.typography.labelLarge)
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
            EditText("Busca", fields.busca.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(busca = fields.busca.edited(value.ifBlank { null }))))
            }
            Text("Período", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PeriodoPreset.entries.take(3).forEach { periodo ->
                    FilterChip(
                        selected = fields.periodo.value == periodo,
                        onClick = { onDraftChange(draft.copy(fields = fields.copy(periodo = fields.periodo.edited(periodo)))) },
                        label = { Text(periodo.name.lowercase()) },
                    )
                }
            }
            Text("Visão", style = MaterialTheme.typography.labelLarge)
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
            EditText("Parceiro", fields.parceiro?.label ?: fields.parceiroRaw.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(parceiroRaw = fields.parceiroRaw.edited(value))))
            }
            EditText("Valor", fields.valorCentavos.value?.let { formatCurrency(it) }.orEmpty()) { value ->
                val cents = MoneyInputParser.parseToCentavos(value)
                onDraftChange(draft.copy(fields = fields.copy(valorCentavos = fields.valorCentavos.edited(cents))))
            }
        }
        is ExtractionFields.EventoDraft -> {
            EditText("Título", fields.titulo.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(titulo = fields.titulo.edited(value.ifBlank { null }))))
            }
            EditText("Observação", fields.observacao.value.orEmpty()) { value ->
                onDraftChange(draft.copy(fields = fields.copy(observacao = fields.observacao.edited(value.ifBlank { null }))))
            }
            PrazoChips(
                aPrazo = fields.notificar.value != false,
                vistaLabel = "Sem aviso",
                prazoLabel = "Com lembrete",
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
