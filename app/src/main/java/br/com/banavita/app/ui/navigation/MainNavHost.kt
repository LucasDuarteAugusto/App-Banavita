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
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import br.com.banavita.app.ui.voice.VoiceEnabledScreen
import java.time.Instant
import java.time.ZoneId
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
import br.com.banavita.app.ui.consultas.ConsultasNavEvent
import br.com.banavita.app.ui.consultas.ConsultasRoute
import br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheRoute
import br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheRoute
import br.com.banavita.app.ui.pereciveis.encomenda.EncomendaFormRoute
import br.com.banavita.app.ui.pereciveis.etileno.EtilenoFilaRoute
import br.com.banavita.app.ui.pereciveis.perda.PerdaFormRoute
import br.com.banavita.app.ui.settings.SettingsRoute
import br.com.banavita.app.ui.vendas.VendaFormRoute

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onTitleChange: (String?) -> Unit = {},
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
        addConsultasScreens(navController, onTitleChange)
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
        br.com.banavita.app.ui.voice.VoiceEnabledScreen(
            voiceContext = VoiceScreenContext.INICIO,
            addContentDescription = "Nova venda", onAddClick = { navController.navigate(BanavitaRoutes.VENDA_FORM_NEW) },
            onVoiceSuccess = { result ->
                if (result is ConfirmResult.ConsultaAplicada) {
                    val route = when {
                        result.navigateNovaVenda -> BanavitaRoutes.VENDA_FORM_NEW
                        result.navigateAgenda -> BanavitaRoutes.agenda()
                        result.openProdutoId != null -> BanavitaRoutes.estoqueDetalhe(result.openProdutoId, result.somenteCriticos)
                        result.openClienteId != null -> BanavitaRoutes.receberDetalhe(result.openClienteId)
                        result.openFornecedorId != null -> BanavitaRoutes.pagarDetalhe(result.openFornecedorId)
                        else -> BanavitaRoutes.consultas(result.modo ?: ConsultaModo.ESTOQUE, result.busca, result.periodo, result.somenteCriticos)
                    }
                    navController.navigate(route)
                }
            },
        ) { dashboardModifier ->
            br.com.banavita.app.ui.dashboard.DashboardRoute(onNavigate = { navController.navigate(it) }, modifier = dashboardModifier)
        }
    }
    composable("dashboards/{kind}", arguments = listOf(navArgument("kind") { type = NavType.StringType })) { entry ->
        val kind = entry.arguments?.getString("kind")
        val voice = when (kind) {
            "VENDAS" -> VoiceScreenContext.VENDA
            "COMPRAS" -> VoiceScreenContext.COMPRA
            "ESTOQUE", "ENCOMENDAS" -> VoiceScreenContext.CONSULTAS
            "AGENDA" -> VoiceScreenContext.AGENDA
            else -> VoiceScreenContext.INICIO
        }
        val (addLabel, addRoute) = when (kind) {
            "VENDAS" -> "Nova venda" to BanavitaRoutes.VENDA_FORM_NEW
            "COMPRAS" -> "Nova compra" to BanavitaRoutes.COMPRA_FORM_NEW
            "ESTOQUE" -> "Registrar perda" to BanavitaRoutes.perdaForm()
            "FINANCEIRO" -> "Contas a receber" to BanavitaRoutes.consultas(ConsultaModo.RECEBER)
            "ENCOMENDAS" -> "Nova encomenda" to BanavitaRoutes.ENCOMENDA_FORM_NEW
            "AGENDA" -> "Novo evento" to BanavitaRoutes.agendaFormNew()
            else -> "Nova venda" to BanavitaRoutes.VENDA_FORM_NEW
        }
        VoiceEnabledScreen(
            voiceContext = voice,
            addContentDescription = addLabel,
            onAddClick = { navController.navigate(addRoute) },
            onVoiceSuccess = { result -> voiceDestination(result)?.let(navController::navigate) },
        ) { dashboardModifier ->
            br.com.banavita.app.ui.dashboard.DashboardRoute(onNavigate = { navController.navigate(it) }, modifier = dashboardModifier)
        }
    }
    composable(
        route = BanavitaRoutes.VENDAS,
        arguments = listOf(
            navArgument(BanavitaRoutes.ARG_MODO) {
                type = NavType.StringType
                defaultValue = ConsultaModo.VENDAS.route
            }
        )
    ) {
        ConsultasRoute(
            onNavEvent = { event -> handleConsultasNavEvent(navController, event) },
        )
    }
    composable(
        route = BanavitaRoutes.COMPRAS,
        arguments = listOf(
            navArgument(BanavitaRoutes.ARG_MODO) {
                type = NavType.StringType
                defaultValue = ConsultaModo.COMPRAS.route
            }
        )
    ) {
        ConsultasRoute(
            onNavEvent = { event -> handleConsultasNavEvent(navController, event) },
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

private fun NavGraphBuilder.addConsultasScreens(
    navController: NavHostController,
    onTitleChange: (String?) -> Unit,
) {
    val popBack: () -> Unit = {
        onTitleChange(null)
        navController.popBackStack()
    }
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
            navArgument(BanavitaRoutes.ARG_CRITICOS) {
                type = NavType.StringType
                defaultValue = ""
            },
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "banavita://consultas?modo={modo}&busca={busca}&periodo={periodo}&criticos={criticos}"
            },
        ),
    ) {
        ConsultasRoute(
            onNavEvent = { event -> handleConsultasNavEvent(navController, event) },
        )
    }
    composable(
        route = BanavitaRoutes.ESTOQUE_DETALHE,
        arguments = listOf(
            navArgument(BanavitaRoutes.ARG_PRODUTO_ID) { type = NavType.StringType },
            navArgument(BanavitaRoutes.ARG_CRITICOS) { 
                type = NavType.StringType
                defaultValue = ""
            }
        ),
    ) {
        EstoqueDetalheRoute(
            onEditProduto = { id -> navController.navigate(BanavitaRoutes.produtoEdit(id)) },
            onLancarPerda = { loteId, produtoId ->
                navController.navigate(BanavitaRoutes.perdaForm(loteId, produtoId))
            },
            onTitleChange = onTitleChange,
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
    composable(BanavitaRoutes.ETILENO_FILA) {
        EtilenoFilaRoute()
    }
    composable(
        route = BanavitaRoutes.PERDA_FORM,
        arguments = listOf(
            navArgument(BanavitaRoutes.ARG_LOTE_ID) {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument(BanavitaRoutes.ARG_PRODUTO_ID) {
                type = NavType.StringType
                defaultValue = ""
            },
        ),
    ) {
        PerdaFormRoute(onDone = popBack)
    }
    composable(BanavitaRoutes.ENCOMENDA_FORM_NEW) {
        EncomendaFormRoute(onDone = popBack)
    }
    composable(
        route = BanavitaRoutes.ENCOMENDA_FORM_EDIT,
        arguments = listOf(navArgument(BanavitaRoutes.ARG_ENCOMENDA_ID) { type = NavType.StringType }),
    ) {
        EncomendaFormRoute(onDone = popBack)
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

private fun handleConsultasNavEvent(navController: NavHostController, event: ConsultasNavEvent) {
    when (event) {
        is ConsultasNavEvent.EstoqueDetalhe ->
            navController.navigate(BanavitaRoutes.estoqueDetalhe(event.produtoId, event.somenteCriticos))
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
        is ConsultasNavEvent.Encomenda ->
            navController.navigate(BanavitaRoutes.encomendaEdit(event.encomendaId))
        ConsultasNavEvent.NovaVenda ->
            navController.navigate(BanavitaRoutes.VENDA_FORM_NEW)
        ConsultasNavEvent.NovaCompra ->
            navController.navigate(BanavitaRoutes.COMPRA_FORM_NEW)
        ConsultasNavEvent.NovaEncomenda ->
            navController.navigate(BanavitaRoutes.ENCOMENDA_FORM_NEW)
        ConsultasNavEvent.FilaEtileno ->
            navController.navigate(BanavitaRoutes.ETILENO_FILA)
        ConsultasNavEvent.NovaPerda ->
            navController.navigate(BanavitaRoutes.perdaForm())
    }
}

private fun voiceDestination(result: ConfirmResult): String? = when (result) {
    is ConfirmResult.ConsultaAplicada -> when {
        result.navigateNovaVenda -> BanavitaRoutes.VENDA_FORM_NEW
        result.navigateAgenda -> BanavitaRoutes.agenda()
        result.openProdutoId != null -> BanavitaRoutes.estoqueDetalhe(result.openProdutoId, result.somenteCriticos)
        result.openClienteId != null -> BanavitaRoutes.receberDetalhe(result.openClienteId)
        result.openFornecedorId != null -> BanavitaRoutes.pagarDetalhe(result.openFornecedorId)
        else -> BanavitaRoutes.consultas(result.modo ?: ConsultaModo.ESTOQUE, result.busca, result.periodo, result.somenteCriticos)
    }
    is ConfirmResult.VendaCriada -> BanavitaRoutes.vendaEdit(result.vendaId)
    is ConfirmResult.CompraCriada -> BanavitaRoutes.compraEdit(result.compraId)
    is ConfirmResult.EventoCriado -> BanavitaRoutes.agenda(
        Instant.ofEpochMilli(result.dataHoraMillis).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay(),
    )
    is ConfirmResult.AgendaFiltroAplicado -> BanavitaRoutes.agenda(result.epochDay, result.monthOverviewHint)
    is ConfirmResult.ClienteCriado -> BanavitaRoutes.clienteEdit(result.clienteId)
    is ConfirmResult.FornecedorCriado -> BanavitaRoutes.fornecedorEdit(result.fornecedorId)
    is ConfirmResult.ProdutoCriado -> BanavitaRoutes.produtoEdit(result.produtoId)
    is ConfirmResult.GrupoCriado -> BanavitaRoutes.grupoEdit(result.grupoId)
    is ConfirmResult.RecebimentoCriado, is ConfirmResult.PagamentoCriado -> null
}
