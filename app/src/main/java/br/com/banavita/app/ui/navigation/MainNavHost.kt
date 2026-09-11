package br.com.banavita.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import br.com.banavita.app.core.reminder.AgendaNotifications
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.ui.agenda.AgendaListRoute
import br.com.banavita.app.ui.agenda.EventoFormRoute
import br.com.banavita.app.ui.cadastros.CadastrosHubScreen
import br.com.banavita.app.ui.cadastros.clientes.ClienteFormRoute
import br.com.banavita.app.ui.cadastros.clientes.ClienteListRoute
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormRoute
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListRoute
import br.com.banavita.app.ui.cadastros.grupos.GrupoFormRoute
import br.com.banavita.app.ui.cadastros.grupos.GrupoListRoute
import br.com.banavita.app.ui.cadastros.produtos.ProdutoFormRoute
import br.com.banavita.app.ui.cadastros.produtos.ProdutoListRoute
import br.com.banavita.app.ui.compras.CompraFormRoute
import br.com.banavita.app.ui.compras.CompraListRoute
import br.com.banavita.app.ui.consultas.ConsultasNavEvent
import br.com.banavita.app.ui.consultas.ConsultasRoute
import br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheRoute
import br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheRoute
import br.com.banavita.app.ui.home.HomeNavEvent
import br.com.banavita.app.ui.home.HomeRoute
import br.com.banavita.app.ui.settings.SettingsRoute
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
        addSettingsScreen()
        addCadastrosNestedScreens(navController)
        addCadastroFormScreens(navController)
        addDocumentoFormScreens(navController)
        addConsultasScreens(navController)
        addAgendaScreens(navController)
    }
}

private fun NavGraphBuilder.addSettingsScreen() {
    composable(BanavitaRoutes.SETTINGS) {
        SettingsRoute()
    }
}

private fun NavGraphBuilder.addTopLevelScreens(navController: NavHostController) {
    composable(BanavitaRoutes.HOME) {
        HomeRoute(
            onNavEvent = { event ->
                when (event) {
                    HomeNavEvent.NovaVenda -> navController.navigate(BanavitaRoutes.VENDA_FORM_NEW)
                    is HomeNavEvent.Consultas -> navController.navigate(
                        BanavitaRoutes.consultas(event.modo, event.busca, event.periodo),
                    )
                    is HomeNavEvent.Agenda -> navController.navigate(
                        BanavitaRoutes.agenda(event.epochDay, event.monthOverviewHint),
                    )
                    is HomeNavEvent.Evento -> navController.navigate(BanavitaRoutes.eventoEdit(event.eventoId))
                    is HomeNavEvent.EstoqueDetalhe ->
                        navController.navigate(BanavitaRoutes.estoqueDetalhe(event.produtoId))
                    is HomeNavEvent.ReceberDetalhe ->
                        navController.navigate(BanavitaRoutes.receberDetalhe(event.clienteId))
                    is HomeNavEvent.PagarDetalhe ->
                        navController.navigate(BanavitaRoutes.pagarDetalhe(event.fornecedorId))
                }
            },
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
}

private fun NavGraphBuilder.addConsultasScreens(navController: NavHostController) {
    composable(
        route = BanavitaRoutes.CONSULTAS,
        arguments = listOf(
            navArgument(BanavitaRoutes.ARG_MODO) {
                type = NavType.StringType
                defaultValue = ConsultaModo.ESTOQUE.route
            },
            navArgument(BanavitaRoutes.ARG_BUSCA) {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument(BanavitaRoutes.ARG_PERIODO) {
                type = NavType.StringType
                defaultValue = ""
            },
        ),
    ) {
        ConsultasRoute(
            onNavEvent = { event ->
                when (event) {
                    is ConsultasNavEvent.EstoqueDetalhe ->
                        navController.navigate(BanavitaRoutes.estoqueDetalhe(event.produtoId))
                    is ConsultasNavEvent.Venda ->
                        navController.navigate(BanavitaRoutes.vendaEdit(event.vendaId))
                    is ConsultasNavEvent.Compra ->
                        navController.navigate(BanavitaRoutes.compraEdit(event.compraId))
                    is ConsultasNavEvent.ProdutoEdit ->
                        navController.navigate(BanavitaRoutes.produtoEdit(event.produtoId))
                    is ConsultasNavEvent.ReceberDetalhe ->
                        navController.navigate(BanavitaRoutes.receberDetalhe(event.clienteId))
                    is ConsultasNavEvent.PagarDetalhe ->
                        navController.navigate(BanavitaRoutes.pagarDetalhe(event.fornecedorId))
                }
            },
        )
    }
    composable(
        route = BanavitaRoutes.ESTOQUE_DETALHE,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_PRODUTO_ID) { type = NavType.StringType }),
    ) {
        EstoqueDetalheRoute(
            onEditProduto = { id -> navController.navigate(BanavitaRoutes.produtoEdit(id)) },
        )
    }
    composable(
        route = BanavitaRoutes.RECEBER_DETALHE,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_CLIENTE_ID) { type = NavType.StringType }),
    ) {
        FinanceiroDetalheRoute(receber = true)
    }
    composable(
        route = BanavitaRoutes.PAGAR_DETALHE,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_FORNECEDOR_ID) { type = NavType.StringType }),
    ) {
        FinanceiroDetalheRoute(receber = false)
    }
}

private fun NavGraphBuilder.addAgendaScreens(navController: NavHostController) {
    val popBack: () -> Unit = { navController.popBackStack() }
    composable(
        route = BanavitaRoutes.AGENDA,
        arguments = listOf(
            navArgument(BanavitaRoutes.ARG_AGENDA_DATE) {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument(BanavitaRoutes.ARG_AGENDA_HINT) {
                type = NavType.StringType
                defaultValue = ""
            },
        ),
    ) {
        AgendaListRoute(
            onAddClick = { epochDay ->
                navController.navigate(BanavitaRoutes.agendaFormNew(epochDay))
            },
            onItemClick = { id -> navController.navigate(BanavitaRoutes.eventoEdit(id)) },
        )
    }
    composable(
        route = BanavitaRoutes.AGENDA_FORM_NEW,
        arguments = listOf(
            navArgument(BanavitaRoutes.ARG_AGENDA_DATE) {
                type = NavType.StringType
                defaultValue = ""
            },
        ),
    ) {
        EventoFormRoute(
            onDone = popBack,
            onVoiceSuccess = { result ->
                if (result is ConfirmResult.EventoCriado) {
                    val epochDay = java.time.Instant.ofEpochMilli(result.dataHoraMillis)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                        .toEpochDay()
                    navController.navigate(BanavitaRoutes.agenda(epochDay)) {
                        popUpTo(BanavitaRoutes.AGENDA) { inclusive = true }
                    }
                } else {
                    popBack()
                }
            },
        )
    }
    composable(
        route = BanavitaRoutes.AGENDA_FORM_EDIT,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_EVENTO_ID) { type = NavType.StringType }),
        deepLinks = listOf(
            navDeepLink { uriPattern = AgendaNotifications.DEEP_LINK_PREFIX + "{${BanavitaRoutes.ARG_EVENTO_ID}}" },
        ),
    ) {
        EventoFormRoute(onDone = popBack)
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
