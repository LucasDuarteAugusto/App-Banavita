package br.com.banavita.app.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import br.com.banavita.app.R
import br.com.banavita.app.domain.model.Produto
import java.util.UUID
import androidx.compose.ui.unit.dp

@Composable
fun DocumentDateField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.form_data)) },
        placeholder = { Text(stringResource(R.string.form_data_hint)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        enabled = enabled,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentEntityDropdown(
    label: String,
    noneLabel: String,
    options: List<Pair<UUID, String>>,
    selectedId: UUID?,
    onSelected: (UUID?) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.find { it.first == selectedId }?.second ?: noneLabel

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable,
                    enabled = enabled,
                ),
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            enabled = enabled,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(noneLabel) },
                onClick = {
                    onSelected(null)
                    expanded = false
                },
            )
            options.forEach { (id, nome) ->
                DropdownMenuItem(
                    text = { Text(nome) },
                    onClick = {
                        onSelected(id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableEntityDropdown(
    label: String,
    options: List<Pair<UUID, String>>,
    selectedId: UUID?,
    onSelected: (UUID?) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.form_search_hint),
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = options.find { it.first == selectedId }
    var searchQuery by remember(selectedId, selectedOption) {
        mutableStateOf(selectedOption?.second.orEmpty())
    }

    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) {
            options
        } else {
            val query = searchQuery.trim()
            options.filter {
                it.second.contains(query, ignoreCase = true)
            }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = MenuAnchorType.PrimaryEditable,
                    enabled = enabled,
                ),
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                expanded = true
                val exact = options.find { it.second.equals(query.trim(), ignoreCase = true) }
                onSelected(exact?.first)
            },
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty() && enabled) {
                    IconButton(onClick = {
                        searchQuery = ""
                        onSelected(null)
                        expanded = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpar",
                        )
                    }
                } else {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            enabled = enabled,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                val current = options.find { it.first == selectedId }
                searchQuery = current?.second.orEmpty()
            },
        ) {
            if (filteredOptions.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.form_nenhum_encontrado)) },
                    onClick = { },
                    enabled = false,
                )
            } else {
                filteredOptions.forEach { (id, nome) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = nome,
                                fontWeight = if (id == selectedId) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        onClick = {
                            searchQuery = nome
                            onSelected(id)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentLineItemCard(
    index: Int,
    item: DocumentLineItemFormState,
    produtos: List<Produto>,
    precoObrigatorio: Boolean,
    showEstoque: Boolean,
    enabled: Boolean,
    canRemove: Boolean,
    onProdutoSelected: (UUID?) -> Unit,
    onQuantidadeChange: (String) -> Unit,
    onPrecoChange: (String) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var produtoExpanded by remember { mutableStateOf(false) }
    val produtoSelecionado = produtos.find { it.id == item.produtoId }
    val produtoLabel = produtoSelecionado?.descricao ?: stringResource(R.string.form_produto_select)
    val lineTotal = item.lineTotalCentavos(produtos, precoObrigatorio)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.form_item_label, index + 1),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )

            ExposedDropdownMenuBox(
                expanded = produtoExpanded,
                onExpandedChange = { if (enabled) produtoExpanded = !produtoExpanded },
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = enabled,
                        ),
                    value = produtoLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.form_produto)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = produtoExpanded) },
                    enabled = enabled,
                )
                ExposedDropdownMenu(
                    expanded = produtoExpanded,
                    onDismissRequest = { produtoExpanded = false },
                ) {
                    produtos.forEach { produto ->
                        DropdownMenuItem(
                            text = { Text(produto.descricao) },
                            onClick = {
                                onProdutoSelected(produto.id)
                                produtoExpanded = false
                            },
                        )
                    }
                }
            }

            if (showEstoque && item.produtoId != null && item.estoqueDisponivel != null) {
                val unidade = produtoSelecionado?.unidade ?: "UN"
                Text(
                    text = stringResource(
                        R.string.form_estoque_disponivel,
                        formatQuantity(item.estoqueDisponivel, unidade),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = item.quantidadeInput,
                onValueChange = onQuantidadeChange,
                label = { Text(stringResource(R.string.form_quantidade)) },
                placeholder = {
                    Text(produtoSelecionado?.unidade?.let { "0 $it" } ?: "0")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = enabled,
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = item.precoInput,
                onValueChange = onPrecoChange,
                label = {
                    Text(
                        if (precoObrigatorio) {
                            stringResource(R.string.form_preco_unitario)
                        } else {
                            stringResource(R.string.form_preco_unitario_opcional)
                        },
                    )
                },
                placeholder = { Text(stringResource(R.string.form_preco_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                enabled = enabled,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(
                        R.string.form_item_total,
                        lineTotal?.let { formatCurrency(it) } ?: "—",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                if (canRemove) {
                    OutlinedButton(onClick = onRemove, enabled = enabled) {
                        Text(stringResource(R.string.form_remove_item))
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentTotalRow(
    totalCentavos: Long,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.form_total),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = formatCurrency(totalCentavos),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
