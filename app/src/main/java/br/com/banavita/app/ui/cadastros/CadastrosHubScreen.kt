package br.com.banavita.app.ui.cadastros

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category // ERROR - Unresolved reference 'Category'.
import androidx.compose.material.icons.filled.LocalShipping // ERROR - Unresolved reference 'LocalShipping'.
import androidx.compose.material.icons.filled.People // ERROR - Unresolved reference 'People'.
import androidx.compose.material.icons.filled.ShoppingBag // ERROR - Unresolved reference 'ShoppingBag'.
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.banavita.app.R
import br.com.banavita.app.ui.navigation.BanavitaRoutes

private data class CadastroEntry(
    val route: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
)

@Composable
fun CadastrosHubScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entries = listOf(
        CadastroEntry(
            route = BanavitaRoutes.CADASTROS_CLIENTES,
            titleRes = R.string.cadastros_clientes,
            descriptionRes = R.string.cadastros_clientes_desc,
            icon = Icons.Default.People, // ERROR - Unresolved reference 'People'.
        ),
        CadastroEntry(
            route = BanavitaRoutes.CADASTROS_FORNECEDORES,
            titleRes = R.string.cadastros_fornecedores,
            descriptionRes = R.string.cadastros_fornecedores_desc,
            icon = Icons.Default.LocalShipping, // ERROR - Unresolved reference 'LocalShipping'.
        ),
        CadastroEntry(
            route = BanavitaRoutes.CADASTROS_PRODUTOS,
            titleRes = R.string.cadastros_produtos,
            descriptionRes = R.string.cadastros_produtos_desc,
            icon = Icons.Default.ShoppingBag, // ERROR - Unresolved reference 'ShoppingBag'.
        ),
        CadastroEntry(
            route = BanavitaRoutes.CADASTROS_GRUPOS,
            titleRes = R.string.cadastros_grupos,
            descriptionRes = R.string.cadastros_grupos_desc,
            icon = Icons.Default.Category, // ERROR - Unresolved reference 'Category'.
        ),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = stringResource(R.string.cadastros_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(entries, key = { it.route }) { entry ->
                CadastroCard(
                    title = stringResource(entry.titleRes),
                    description = stringResource(entry.descriptionRes),
                    icon = entry.icon,
                    onClick = { onNavigate(entry.route) },
                )
            }
        }
    }
}

@Composable
private fun CadastroCard(
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
