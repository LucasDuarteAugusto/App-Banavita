package br.com.banavita.app.ui.common

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.PerdaMotivo

fun MaturacaoEstado.labelRes(): Int = when (this) {
    MaturacaoEstado.CRUA -> R.string.estado_crua
    MaturacaoEstado.EM_MATURACAO -> R.string.estado_maturacao
    MaturacaoEstado.VERDE -> R.string.estado_verde
    MaturacaoEstado.VERDOLENGA -> R.string.estado_verdolenga
    MaturacaoEstado.AMARELA -> R.string.estado_amarela
    MaturacaoEstado.PINTADA -> R.string.estado_pintada
    MaturacaoEstado.PODRE -> R.string.estado_podre
}

fun Camara.labelRes(): Int = when (this) {
    Camara.FRIA -> R.string.camara_fria
    Camara.ETILENO -> R.string.camara_etileno
}

fun PerdaMotivo.labelRes(): Int = when (this) {
    PerdaMotivo.APODRECEU -> R.string.perda_motivo_apodreceu
    PerdaMotivo.MANUSEIO -> R.string.perda_motivo_manuseio
    PerdaMotivo.OUTRO -> R.string.perda_motivo_outro
}

@Composable
fun EstadoChips(
    selected: MaturacaoEstado?,
    onSelected: (MaturacaoEstado) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    includePodre: Boolean = true,
) {
    val estados = if (includePodre) {
        MaturacaoEstado.entries
    } else {
        MaturacaoEstado.entries.filter { it != MaturacaoEstado.PODRE && it != MaturacaoEstado.EM_MATURACAO }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        estados.forEach { estado ->
            FilterChip(
                selected = selected == estado,
                enabled = enabled,
                onClick = { onSelected(estado) },
                label = { Text(stringResource(estado.labelRes())) },
            )
        }
    }
}

@Composable
fun QueimaPrecoDialog(
    precoInput: String,
    onPrecoChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    errorMessage: String? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.home_queima_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = stringResource(R.string.home_queima_message))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = precoInput,
                    onValueChange = onPrecoChange,
                    label = { Text(stringResource(R.string.form_preco_unit_short)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                errorMessage?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.home_queima_aplicar))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.form_cancel))
            }
        },
    )
}
