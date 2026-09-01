package br.com.banavita.app.ui.navigation

import java.util.UUID

object BanavitaRoutes {
    const val HOME = "home"
    const val VENDAS = "vendas"
    const val COMPRAS = "compras"
    const val CADASTROS = "cadastros"
    const val CADASTROS_CLIENTES = "cadastros/clientes"
    const val CADASTROS_FORNECEDORES = "cadastros/fornecedores"
    const val CADASTROS_PRODUTOS = "cadastros/produtos"
    const val CADASTROS_GRUPOS = "cadastros/grupos"
    const val CADASTROS_ESTOQUE = "cadastros/estoque"

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

    val topLevelRoutes = setOf(HOME, VENDAS, COMPRAS, CADASTROS)

    fun isTopLevel(route: String?): Boolean = route in topLevelRoutes

    fun topLevelRouteFor(route: String?): String? = when {
        route == null -> null
        route == HOME || route.startsWith("$HOME/") -> HOME
        route == VENDAS || route.startsWith("$VENDAS/") -> VENDAS
        route == COMPRAS || route.startsWith("$COMPRAS/") -> COMPRAS
        route == CADASTROS || route.startsWith("$CADASTROS/") -> CADASTROS
        else -> null
    }

    fun grupoEdit(grupoId: UUID): String = "cadastros/grupos/edit/$grupoId"
    fun clienteEdit(clienteId: UUID): String = "cadastros/clientes/edit/$clienteId"
    fun fornecedorEdit(fornecedorId: UUID): String = "cadastros/fornecedores/edit/$fornecedorId"
    fun produtoEdit(produtoId: UUID): String = "cadastros/produtos/edit/$produtoId"
    fun vendaEdit(vendaId: UUID): String = "vendas/edit/$vendaId"
    fun compraEdit(compraId: UUID): String = "compras/edit/$compraId"
}
