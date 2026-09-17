package br.com.banavita.app.ui.cadastros.produtos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.R
import br.com.banavita.app.ui.common.DeleteConfirmDialog
import br.com.banavita.app.ui.common.FormLoadingState
import br.com.banavita.app.ui.common.FormScaffold
import androidx.compose.foundation.layout.PaddingValues
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.voice.VoiceEnabledFormHost
import br.com.banavita.app.ui.voice.VoiceFormContentPadding
import java.util.UUID

@Composable
fun ProdutoFormRoute(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProdutoFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showDeleteDialog) {
        DeleteConfirmDialog(
            title = stringResource(R.string.produto_delete_title),
            message = stringResource(R.string.produto_delete_message),
            isDeleting = uiState.isDeleting,
            onConfirm = { viewModel.delete(onDone) },
            onDismiss = viewModel::onDismissDeleteDialog,
        )
    }

    if (uiState.isLoading) {
        FormLoadingState(modifier = modifier)
        return
    }

    VoiceEnabledFormHost(
        voiceContext = VoiceScreenContext.PRODUTO,
        enabled = !uiState.isEditMode,
        onVoiceSuccess = { onDone() },
        modifier = modifier,
    ) { formModifier ->
        ProdutoFormScreen(
            uiState = uiState,
            modifier = formModifier,
            voiceEnabled = !uiState.isEditMode,
            onDescricaoChange = viewModel::onDescricaoChange,
            onPrecoChange = viewModel::onPrecoChange,
            onUnidadeChange = viewModel::onUnidadeChange,
            onGrupoSelected = viewModel::onGrupoSelected,
            onPerecivelChange = viewModel::onPerecivelChange,
            onDiasEmMaturacaoChange = viewModel::onDiasEmMaturacaoChange,
            onDiasVerdeChange = viewModel::onDiasVerdeChange,
            onDiasVerdolengaChange = viewModel::onDiasVerdolengaChange,
            onDiasAmarelaChange = viewModel::onDiasAmarelaChange,
            onDiasPintadaChange = viewModel::onDiasPintadaChange,
            onPrecoCruaChange = viewModel::onPrecoCruaChange,
            onPrecoMaturacaoChange = viewModel::onPrecoMaturacaoChange,
            onPrecoVerdeChange = viewModel::onPrecoVerdeChange,
            onPrecoVerdolengaChange = viewModel::onPrecoVerdolengaChange,
            onPrecoAmarelaChange = viewModel::onPrecoAmarelaChange,
            onPrecoPintadaChange = viewModel::onPrecoPintadaChange,
            onSave = { viewModel.save(onDone) },
            onDelete = viewModel::onDeleteClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdutoFormScreen(
    uiState: ProdutoFormUiState,
    onDescricaoChange: (String) -> Unit,
    onPrecoChange: (String) -> Unit,
    onUnidadeChange: (String) -> Unit,
    onGrupoSelected: (UUID?) -> Unit,
    onPerecivelChange: (Boolean) -> Unit,
    onDiasEmMaturacaoChange: (String) -> Unit,
    onDiasVerdeChange: (String) -> Unit,
    onDiasVerdolengaChange: (String) -> Unit,
    onDiasAmarelaChange: (String) -> Unit,
    onDiasPintadaChange: (String) -> Unit,
    onPrecoCruaChange: (String) -> Unit,
    onPrecoMaturacaoChange: (String) -> Unit,
    onPrecoVerdeChange: (String) -> Unit,
    onPrecoVerdolengaChange: (String) -> Unit,
    onPrecoAmarelaChange: (String) -> Unit,
    onPrecoPintadaChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    voiceEnabled: Boolean = false,
) {
    var unidadeExpanded by remember { mutableStateOf(value = false) }
    var grupoExpanded by remember { mutableStateOf(value = false) }

    FormScaffold(
        modifier = modifier,
        errorMessage = uiState.errorMessage,
        isSaving = uiState.isSaving,
        showDelete = uiState.isEditMode,
        contentPadding = if (voiceEnabled) VoiceFormContentPadding else PaddingValues(),
        onSave = onSave,
        onDelete = onDelete,
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.descricao,
            onValueChange = onDescricaoChange,
            label = { Text(stringResource(R.string.form_descricao)) },
            singleLine = true,
            enabled = !uiState.isSaving,
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.precoInput,
            onValueChange = onPrecoChange,
            label = { Text(stringResource(R.string.form_preco)) },
            placeholder = { Text(stringResource(R.string.form_preco_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            enabled = !uiState.isSaving,
        )

        ExposedDropdownMenuBox(
            expanded = unidadeExpanded,
            onExpandedChange = { unidadeExpanded = !unidadeExpanded },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = !uiState.isSaving,
                    ),
                value = uiState.unidade,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.form_unidade)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unidadeExpanded) },
                enabled = !uiState.isSaving,
            )
            ExposedDropdownMenu(
                expanded = unidadeExpanded,
                onDismissRequest = { unidadeExpanded = false },
            ) {
                uiState.unidades.forEach { unidade ->
                    DropdownMenuItem(
                        text = { Text(unidade) },
                        onClick = {
                            onUnidadeChange(unidade)
                            unidadeExpanded = false
                        },
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = grupoExpanded,
            onExpandedChange = { grupoExpanded = !grupoExpanded },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = !uiState.isSaving,
                    ),
                value = uiState.grupoNomeSelecionado ?: stringResource(R.string.form_grupo_none),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.form_grupo)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = grupoExpanded) },
                enabled = !uiState.isSaving,
            )
            ExposedDropdownMenu(
                expanded = grupoExpanded,
                onDismissRequest = { grupoExpanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.form_grupo_none)) },
                    onClick = {
                        onGrupoSelected(null)
                        grupoExpanded = false
                    },
                )
                uiState.grupos.forEach { grupo ->
                    DropdownMenuItem(
                        text = { Text(grupo.nome) },
                        onClick = {
                            onGrupoSelected(grupo.id)
                            grupoExpanded = false
                        },
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.produto_perecivel),
                style = MaterialTheme.typography.bodyLarge,
            )
            Switch(
                checked = uiState.perecivel,
                onCheckedChange = onPerecivelChange,
                enabled = !uiState.isSaving,
            )
        }

        if (uiState.perecivel) {
            Text(
                text = stringResource(R.string.produto_curva_title),
                style = MaterialTheme.typography.titleMedium,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.diasEmMaturacao,
                onValueChange = onDiasEmMaturacaoChange,
                label = { Text(stringResource(R.string.produto_dias_maturacao)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.diasVerde,
                onValueChange = onDiasVerdeChange,
                label = { Text(stringResource(R.string.produto_dias_verde)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.diasVerdolenga,
                onValueChange = onDiasVerdolengaChange,
                label = { Text(stringResource(R.string.produto_dias_verdolenga)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.diasAmarela,
                onValueChange = onDiasAmarelaChange,
                label = { Text(stringResource(R.string.produto_dias_amarela)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.diasPintada,
                onValueChange = onDiasPintadaChange,
                label = { Text(stringResource(R.string.produto_dias_pintada)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !uiState.isSaving,
            )
            Text(
                text = stringResource(R.string.produto_precos_estado_title),
                style = MaterialTheme.typography.titleMedium,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.precoCruaInput,
                onValueChange = onPrecoCruaChange,
                label = { Text(stringResource(R.string.produto_preco_crua)) },
                placeholder = { Text(stringResource(R.string.form_preco_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.precoMaturacaoInput,
                onValueChange = onPrecoMaturacaoChange,
                label = { Text(stringResource(R.string.produto_preco_maturacao)) },
                placeholder = { Text(stringResource(R.string.form_preco_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.precoVerdeInput,
                onValueChange = onPrecoVerdeChange,
                label = { Text(stringResource(R.string.produto_preco_verde)) },
                placeholder = { Text(stringResource(R.string.form_preco_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.precoVerdolengaInput,
                onValueChange = onPrecoVerdolengaChange,
                label = { Text(stringResource(R.string.produto_preco_verdolenga)) },
                placeholder = { Text(stringResource(R.string.form_preco_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.precoAmarelaInput,
                onValueChange = onPrecoAmarelaChange,
                label = { Text(stringResource(R.string.produto_preco_amarela)) },
                placeholder = { Text(stringResource(R.string.form_preco_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !uiState.isSaving,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.precoPintadaInput,
                onValueChange = onPrecoPintadaChange,
                label = { Text(stringResource(R.string.produto_preco_pintada)) },
                placeholder = { Text(stringResource(R.string.form_preco_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = !uiState.isSaving,
            )
            Text(
                text = stringResource(R.string.produto_preco_podre_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
