package br.com.banavita.app.ui.shell

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.banavita.app.R
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import br.com.banavita.app.ui.navigation.MainNavHost

private data class TopLevelTab(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = BanavitaRoutes.isTopLevel(currentRoute)

    val topLevelTabs = listOf(
        TopLevelTab(BanavitaRoutes.HOME, R.string.nav_home, Icons.Default.Home),
        TopLevelTab(BanavitaRoutes.VENDAS, R.string.nav_vendas, Icons.Default.ShoppingCart),
        TopLevelTab(BanavitaRoutes.COMPRAS, R.string.nav_compras, Icons.Default.Inventory),
        TopLevelTab(BanavitaRoutes.CADASTROS, R.string.nav_cadastros, Icons.Default.Apps),
    )

    val selectedTabRoute = BanavitaRoutes.topLevelRouteFor(currentRoute) ?: BanavitaRoutes.HOME

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(titleForRoute(currentRoute)) },
                navigationIcon = {
                    if (!showBottomBar) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.nav_back),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelTabs.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTabRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = stringResource(tab.titleRes),
                                )
                            },
                            label = { Text(stringResource(tab.titleRes)) },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        MainNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun titleForRoute(route: String?): String {
    val titleRes = when {
        route == BanavitaRoutes.HOME -> R.string.home_title
        route == BanavitaRoutes.VENDAS -> R.string.vendas_title
        route == BanavitaRoutes.COMPRAS -> R.string.compras_title
        route == BanavitaRoutes.CADASTROS -> R.string.cadastros_title
        route == BanavitaRoutes.CADASTROS_CLIENTES -> R.string.cadastros_clientes_title
        route == BanavitaRoutes.CADASTROS_FORNECEDORES -> R.string.cadastros_fornecedores_title
        route == BanavitaRoutes.CADASTROS_PRODUTOS -> R.string.cadastros_produtos_title
        route == BanavitaRoutes.CADASTROS_GRUPOS -> R.string.cadastros_grupos_title
        route == BanavitaRoutes.CADASTROS_ESTOQUE -> R.string.cadastros_estoque_title
        route == BanavitaRoutes.GRUPO_FORM_NEW -> R.string.grupo_form_new_title
        route?.startsWith("cadastros/grupos/edit/") == true -> R.string.grupo_form_edit_title
        route == BanavitaRoutes.CLIENTE_FORM_NEW -> R.string.cliente_form_new_title
        route?.startsWith("cadastros/clientes/edit/") == true -> R.string.cliente_form_edit_title
        route == BanavitaRoutes.FORNECEDOR_FORM_NEW -> R.string.fornecedor_form_new_title
        route?.startsWith("cadastros/fornecedores/edit/") == true -> R.string.fornecedor_form_edit_title
        route == BanavitaRoutes.PRODUTO_FORM_NEW -> R.string.produto_form_new_title
        route?.startsWith("cadastros/produtos/edit/") == true -> R.string.produto_form_edit_title
        route == BanavitaRoutes.VENDA_FORM_NEW -> R.string.venda_form_new_title
        route?.startsWith("vendas/edit/") == true -> R.string.venda_form_edit_title
        route == BanavitaRoutes.COMPRA_FORM_NEW -> R.string.compra_form_new_title
        route?.startsWith("compras/edit/") == true -> R.string.compra_form_edit_title
        else -> R.string.home_title
    }
    return stringResource(titleRes)
}
