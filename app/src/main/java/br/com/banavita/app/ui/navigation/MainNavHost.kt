package br.com.banavita.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.com.banavita.app.ui.cadastros.CadastrosHubScreen
import br.com.banavita.app.ui.cadastros.clientes.ClienteFormRoute
import br.com.banavita.app.ui.cadastros.clientes.ClienteListRoute
import br.com.banavita.app.ui.cadastros.estoque.EstoqueListRoute
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormRoute
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListRoute
import br.com.banavita.app.ui.cadastros.grupos.GrupoFormRoute
import br.com.banavita.app.ui.cadastros.grupos.GrupoListRoute
import br.com.banavita.app.ui.cadastros.produtos.ProdutoFormRoute
import br.com.banavita.app.ui.cadastros.produtos.ProdutoListRoute
import br.com.banavita.app.ui.compras.CompraFormRoute
import br.com.banavita.app.ui.compras.CompraListRoute
import br.com.banavita.app.ui.home.HomeScreen
import br.com.banavita.app.ui.vendas.VendaFormRoute
import br.com.banavita.app.ui.vendas.VendaListRoute

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = BanavitaRoutes.HOME,
        modifier = modifier,
    ) {
        addTopLevelScreens(navController)
        addCadastrosNestedScreens(navController)
        addCadastroFormScreens(navController)
        addDocumentoFormScreens(navController)
    }
}

private fun NavGraphBuilder.addTopLevelScreens(navController: NavHostController) {
    composable(BanavitaRoutes.HOME) {
        HomeScreen(
            onNovaVendaClick = { navController.navigate(BanavitaRoutes.VENDA_FORM_NEW) },
        )
    }
    composable(BanavitaRoutes.VENDAS) {
        VendaListRoute(
            onAddClick = { navController.navigate(BanavitaRoutes.VENDA_FORM_NEW) },
            onItemClick = { id -> navController.navigate(BanavitaRoutes.vendaEdit(id)) },
        )
    }
    composable(BanavitaRoutes.COMPRAS) {
        CompraListRoute(
            onAddClick = { navController.navigate(BanavitaRoutes.COMPRA_FORM_NEW) },
            onItemClick = { id -> navController.navigate(BanavitaRoutes.compraEdit(id)) },
        )
    }
    composable(BanavitaRoutes.CADASTROS) {
        CadastrosHubScreen(
            onNavigate = { route -> navController.navigate(route) },
        )
    }
}

private fun NavGraphBuilder.addCadastrosNestedScreens(navController: NavHostController) {
    composable(BanavitaRoutes.CADASTROS_CLIENTES) {
        ClienteListRoute(
            onAddClick = { navController.navigate(BanavitaRoutes.CLIENTE_FORM_NEW) },
            onItemClick = { id -> navController.navigate(BanavitaRoutes.clienteEdit(id)) },
        )
    }
    composable(BanavitaRoutes.CADASTROS_FORNECEDORES) {
        FornecedorListRoute(
            onAddClick = { navController.navigate(BanavitaRoutes.FORNECEDOR_FORM_NEW) },
            onItemClick = { id -> navController.navigate(BanavitaRoutes.fornecedorEdit(id)) },
        )
    }
    composable(BanavitaRoutes.CADASTROS_PRODUTOS) {
        ProdutoListRoute(
            onAddClick = { navController.navigate(BanavitaRoutes.PRODUTO_FORM_NEW) },
            onItemClick = { id -> navController.navigate(BanavitaRoutes.produtoEdit(id)) },
        )
    }
    composable(BanavitaRoutes.CADASTROS_GRUPOS) {
        GrupoListRoute(
            onAddClick = { navController.navigate(BanavitaRoutes.GRUPO_FORM_NEW) },
            onItemClick = { id -> navController.navigate(BanavitaRoutes.grupoEdit(id)) },
        )
    }
    composable(BanavitaRoutes.CADASTROS_ESTOQUE) {
        EstoqueListRoute()
    }
}

private fun NavGraphBuilder.addCadastroFormScreens(navController: NavHostController) {
    val popBack: () -> Unit = { navController.popBackStack() }

    composable(BanavitaRoutes.GRUPO_FORM_NEW) {
        GrupoFormRoute(onDone = popBack)
    }
    composable(
        route = BanavitaRoutes.GRUPO_FORM_EDIT,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_ENTITY_ID) { type = NavType.StringType }),
    ) {
        GrupoFormRoute(onDone = popBack)
    }

    composable(BanavitaRoutes.CLIENTE_FORM_NEW) {
        ClienteFormRoute(onDone = popBack)
    }
    composable(
        route = BanavitaRoutes.CLIENTE_FORM_EDIT,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_CLIENTE_ID) { type = NavType.StringType }),
    ) {
        ClienteFormRoute(onDone = popBack)
    }

    composable(BanavitaRoutes.FORNECEDOR_FORM_NEW) {
        FornecedorFormRoute(onDone = popBack)
    }
    composable(
        route = BanavitaRoutes.FORNECEDOR_FORM_EDIT,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_FORNECEDOR_ID) { type = NavType.StringType }),
    ) {
        FornecedorFormRoute(onDone = popBack)
    }

    composable(BanavitaRoutes.PRODUTO_FORM_NEW) {
        ProdutoFormRoute(onDone = popBack)
    }
    composable(
        route = BanavitaRoutes.PRODUTO_FORM_EDIT,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_PRODUTO_ID) { type = NavType.StringType }),
    ) {
        ProdutoFormRoute(onDone = popBack)
    }
}

private fun NavGraphBuilder.addDocumentoFormScreens(navController: NavHostController) {
    val popBack: () -> Unit = { navController.popBackStack() }

    composable(BanavitaRoutes.VENDA_FORM_NEW) {
        VendaFormRoute(onDone = popBack)
    }
    composable(
        route = BanavitaRoutes.VENDA_FORM_EDIT,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_VENDA_ID) { type = NavType.StringType }),
    ) {
        VendaFormRoute(onDone = popBack)
    }

    composable(BanavitaRoutes.COMPRA_FORM_NEW) {
        CompraFormRoute(onDone = popBack)
    }
    composable(
        route = BanavitaRoutes.COMPRA_FORM_EDIT,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_COMPRA_ID) { type = NavType.StringType }),
    ) {
        CompraFormRoute(onDone = popBack)
    }
}
