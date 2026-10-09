package br.com.banavita.app.ui.navigation

import android.net.Uri
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.PeriodoPreset
import java.util.UUID

object BanavitaRoutes {
    const val HOME = "home"
    const val SETTINGS = "configuracoes"
    const val VENDAS = "vendas"
    const val COMPRAS = "compras"
    const val CADASTROS = "cadastros"
    const val CADASTROS_CLIENTES = "cadastros/clientes"
    const val CADASTROS_FORNECEDORES = "cadastros/fornecedores"
    const val CADASTROS_PRODUTOS = "cadastros/produtos"
    const val CADASTROS_GRUPOS = "cadastros/grupos"

    const val CONSULTAS = "consultas?modo={modo}&busca={busca}&periodo={periodo}&criticos={criticos}"
    const val ARG_MODO = "modo"
    const val ARG_BUSCA = "busca"
    const val ARG_PERIODO = "periodo"
    const val ARG_CRITICOS = "criticos"
    const val ESTOQUE_DETALHE = "consultas/estoque/{produtoId}?criticos={criticos}"
    const val RECEBER_DETALHE = "consultas/receber/{clienteId}"
    const val PAGAR_DETALHE = "consultas/pagar/{fornecedorId}"
    const val ETILENO_FILA = "pereciveis/etileno"
    const val PERDA_FORM = "pereciveis/perda?loteId={loteId}&produtoId={produtoId}"
    const val ENCOMENDA_FORM_NEW = "pereciveis/encomenda/novo"
    const val ENCOMENDA_FORM_EDIT = "pereciveis/encomenda/edit/{encomendaId}"

    const val AGENDA = "agenda?date={date}&hint={hint}"
    const val AGENDA_FORM_NEW = "agenda/novo?date={date}"
    const val AGENDA_FORM_EDIT = "agenda/edit/{eventoId}"
    const val ARG_AGENDA_DATE = "date"
    const val ARG_AGENDA_HINT = "hint"

    const val GRUPO_FORM_NEW = "cadastros/grupos/novo"
    const val GRUPO_FORM_EDIT = "cadastros/grupos/edit/{grupoId}"
    const val CLIENTE_FORM_NEW = "cadastros/clientes/novo"
    const val CLIENTE_FORM_EDIT = "cadastros/clientes/edit/{clienteId}"
    const val FORNECEDOR_FORM_NEW = "cadastros/fornecedores/novo"
    const val FORNECEDOR_FORM_EDIT = "cadastros/fornecedores/edit/{fornecedorId}"
    const val PRODUTO_FORM_NEW = "cadastros/produtos/novo"
    const val PRODUTO_FORM_EDIT = "cadastros/produtos/edit/{produtoId}"

    const val VENDA_FORM_NEW = "vendas/novo"
    const val VENDA_FORM_EDIT = "vendas/edit/{vendaId}"
    const val COMPRA_FORM_NEW = "compras/novo"
    const val COMPRA_FORM_EDIT = "compras/edit/{compraId}"

    const val ARG_ENTITY_ID = "grupoId"
    const val ARG_CLIENTE_ID = "clienteId"
    const val ARG_FORNECEDOR_ID = "fornecedorId"
    const val ARG_PRODUTO_ID = "produtoId"
    const val ARG_VENDA_ID = "vendaId"
    const val ARG_COMPRA_ID = "compraId"
    const val ARG_EVENTO_ID = "eventoId"
    const val ARG_LOTE_ID = "loteId"
    const val ARG_ENCOMENDA_ID = "encomendaId"

    val topLevelRoutes = setOf(HOME, VENDAS, COMPRAS, CADASTROS)

    fun isTopLevel(route: String?): Boolean {
        if (route == null) return false
        val base = route.substringBefore("?")
        return base in topLevelRoutes
    }

    fun topLevelRouteFor(route: String?): String? = when {
        route == null -> null
        route == HOME || route.startsWith("$HOME/") -> HOME
        route.startsWith("dashboards/") || route == SETTINGS || route.startsWith("consultas") || route.startsWith("agenda") ||
            route.startsWith("pereciveis") -> HOME
        route == VENDAS || route.startsWith("$VENDAS/") -> VENDAS
        route == COMPRAS || route.startsWith("$COMPRAS/") -> COMPRAS
        route == CADASTROS || route.startsWith("$CADASTROS/") -> CADASTROS
        else -> null
    }

    fun consultas(
        modo: ConsultaModo = ConsultaModo.ESTOQUE,
        busca: String? = null,
        periodo: PeriodoPreset? = null,
        criticos: Boolean = false,
    ): String {
        val encodedBusca = Uri.encode(busca.orEmpty())
        val periodoName = periodo?.name.orEmpty()
        val criticosFlag = if (criticos) "1" else ""
        return "consultas?modo=${modo.route}&busca=$encodedBusca&periodo=$periodoName&criticos=$criticosFlag"
    }

    fun estoqueDetalhe(produtoId: UUID, criticos: Boolean = false): String {
        val flag = if (criticos) "1" else ""
        return "consultas/estoque/$produtoId?criticos=$flag"
    }
    fun receberDetalhe(clienteId: UUID): String = "consultas/receber/$clienteId"
    fun pagarDetalhe(fornecedorId: UUID): String = "consultas/pagar/$fornecedorId"
    fun perdaForm(loteId: UUID? = null, produtoId: UUID? = null): String =
        "pereciveis/perda?loteId=${loteId?.toString().orEmpty()}&produtoId=${produtoId?.toString().orEmpty()}"
    fun encomendaEdit(encomendaId: UUID): String = "pereciveis/encomenda/edit/$encomendaId"

    fun grupoEdit(grupoId: UUID): String = "cadastros/grupos/edit/$grupoId"
    fun clienteEdit(clienteId: UUID): String = "cadastros/clientes/edit/$clienteId"
    fun fornecedorEdit(fornecedorId: UUID): String = "cadastros/fornecedores/edit/$fornecedorId"
    fun produtoEdit(produtoId: UUID): String = "cadastros/produtos/edit/$produtoId"
    fun vendaEdit(vendaId: UUID): String = "vendas/edit/$vendaId"
    fun compraEdit(compraId: UUID): String = "compras/edit/$compraId"
    fun eventoEdit(eventoId: UUID): String = "agenda/edit/$eventoId"

    fun agenda(epochDay: Long? = null, monthOverviewHint: Boolean = false): String {
        val date = epochDay?.toString().orEmpty()
        val hint = if (monthOverviewHint) "1" else ""
        return "agenda?date=$date&hint=$hint"
    }

    fun agendaFormNew(epochDay: Long? = null): String {
        val date = epochDay?.toString().orEmpty()
        return "agenda/novo?date=$date"
    }
}
