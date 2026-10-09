package br.com.banavita.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.banavita.app.domain.model.*
import br.com.banavita.app.ui.common.BanavitaIcons
import br.com.banavita.app.ui.common.DocumentDateField
import br.com.banavita.app.ui.common.DateInputParser
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardRoute(onNavigate: (String) -> Unit, modifier: Modifier = Modifier, viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    DashboardScreen(state, viewModel::changeFilter, onNavigate, viewModel::retry, viewModel::completeTask, viewModel::saveCash, modifier)
}

@Composable
fun DashboardScreen(state: DashboardUiState, onFilter: (DashboardFilter) -> Unit, onNavigate: (String) -> Unit,
    onRetry: () -> Unit, onComplete: (String, Boolean) -> Unit, onCash: (String, String, () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    var cashDialog by rememberSaveable { mutableStateOf(false) }
    if (cashDialog) CashBaseDialog({ cashDialog = false }, { date, balance -> onCash(date, balance) { cashDialog = false } }, state.error)
    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).padding(bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(state.kind.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(state.kind.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("pt-BR"))), style = MaterialTheme.typography.labelMedium)
        if (state.kind != DashboardKind.GERAL) DashboardFilters(state, onFilter)
        if (state.error != null) {
            Text(state.error, color = MaterialTheme.colorScheme.error)
            OutlinedButton(onClick = onRetry) { Text("Tentar novamente") }
        }
        if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.data.kpis.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { kpi ->
                    ElevatedCard(Modifier.weight(1f)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                kpiIcon(kpi.icon)?.let { Icon(it, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary) }
                                Text(kpi.label, style = MaterialTheme.typography.labelLarge)
                            }
                            Text(kpi.value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = if (kpi.label == "Diferença comercial" && (kpi.trend ?: 0.0) < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                            if (kpi.detail.isNotBlank()) Text(kpi.detail, style = MaterialTheme.typography.bodySmall)
                            kpi.trend?.takeIf { kpi.label != "Diferença comercial" }?.let { Text("${if (it >= 0) "+" else ""}${String.format(Locale.forLanguageTag("pt-BR"), "%.1f", it)}% vs. período anterior", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        if (state.kind == DashboardKind.GERAL) {
            Text("Dashboards", style = MaterialTheme.typography.titleLarge)
            DashboardKind.entries.filter { it != DashboardKind.GERAL }.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { kind -> Card(Modifier.weight(1f).clickable { onNavigate("dashboards/${kind.name}") }) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(kindIcon(kind), contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
                                Text("${kind.title} ›", style = MaterialTheme.typography.titleMedium)
                            }
                            Text(kind.subtitle, style = MaterialTheme.typography.bodySmall)
                        }
                    } }
                }
            }
        }
        if (state.kind == DashboardKind.FINANCEIRO) OutlinedButton(onClick = { cashDialog = true }) { Text(if (state.data.cashConfigured) "Editar saldo e data base do caixa" else "Configurar caixa") }
        state.data.charts.forEach { chart ->
            DashboardChartCard(chart) { point ->
                when {
                    state.kind in setOf(DashboardKind.VENDAS, DashboardKind.COMPRAS) && chart.title in setOf("Top clientes", "Top fornecedores") && point.id.isNotBlank() -> onFilter(state.filter.copy(partner = point.id))
                    state.kind in setOf(DashboardKind.VENDAS, DashboardKind.COMPRAS) && chart.title in setOf("Valor por variedade", "Compras por variedade") && point.id != "others" -> onFilter(state.filter.copy(product = point.id))
                    state.kind == DashboardKind.ESTOQUE && chart.title == "Estoque por variedade" && point.id != "others" -> onFilter(state.filter.copy(product = point.id))
                    state.kind == DashboardKind.ESTOQUE && chart.style == ChartStyle.DONUT -> MaturacaoEstado.entries.find { it.name == point.id }?.let { onFilter(state.filter.copy(state = it)) }
                    point.route != null -> onNavigate(point.route)
                }
            }
        }
        state.data.sections.forEach { section ->
            Text(section.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (section.entries.isEmpty()) Text("Nenhum registro neste período.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            section.entries.forEach { entry ->
                val tone = toneColor(entry.tone)
                OutlinedCard(Modifier.fillMaxWidth().clickable(enabled = entry.route != null) { entry.route?.let(onNavigate) }, border = if (tone == null) CardDefaults.outlinedCardBorder() else androidx.compose.foundation.BorderStroke(2.dp, tone)) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (tone != null) Box(Modifier.size(12.dp).background(tone, CircleShape))
                        entry.eventId?.let { id -> Checkbox(entry.complete, { onComplete(id, it) }) }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(entry.title, style = MaterialTheme.typography.titleSmall)
                            Text(entry.detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (entry.route != null) Text("›")
                    }
                }
            }
        }
        if (state.data.insights.isNotEmpty()) {
            Text("Insights", style = MaterialTheme.typography.titleMedium)
            state.data.insights.forEach { insight -> Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) { Text(insight, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium) } }
        }
        Operations(state.kind, onNavigate)
    }
}

@Composable
private fun DashboardFilters(state: DashboardUiState, onFilter: (DashboardFilter) -> Unit) {
    val f = state.filter
    val future = state.kind in setOf(DashboardKind.FINANCEIRO, DashboardKind.AGENDA, DashboardKind.ENCOMENDAS)
    if (state.kind != DashboardKind.ESTOQUE) {
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DashboardPeriod.entries.forEach { period -> FilterChip(f.period == period, { onFilter(f.copy(period = period)) }, { Text(if (future && period in setOf(DashboardPeriod.DIAS_7, DashboardPeriod.DIAS_30)) "Próximos ${period.label}" else period.label) }) }
        }
        if (f.period == DashboardPeriod.PERSONALIZADO) {
            var start by rememberSaveable { mutableStateOf(DateInputParser.todayInput()) }
            var end by rememberSaveable { mutableStateOf(DateInputParser.todayInput()) }
            DocumentDateField(start, { start = it }, label = "Data inicial")
            DocumentDateField(end, { end = it }, label = "Data final")
            val zone = ZoneId.systemDefault()
            OutlinedButton(onClick = { onFilter(f.copy(customStart = DateInputParser.parseToEpochMillis(start)?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() }, customEnd = DateInputParser.parseToEpochMillis(end)?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDate() })) }) { Text("Aplicar período") }
        }
    }
    if (state.kind in setOf(DashboardKind.VENDAS, DashboardKind.COMPRAS, DashboardKind.ESTOQUE)) {
        Choice("Unidade", f.unit, listOf("KG", "UN", "DZ", "CX MADEIRA", "CX PLASTICA").map { it to it }) { onFilter(f.copy(unit = it, quantity = if (it == null) false else f.quantity, ticketByUnit = if (it == null) false else f.ticketByUnit)) }
        Choice("Variedade", f.product, state.products.sortedBy { it.descricao }.map { it.id.toString() to it.descricao }) { onFilter(f.copy(product = it)) }
    }
    if (state.kind == DashboardKind.VENDAS) Choice("Cliente", f.partner, state.clients.sortedBy { it.nome }.map { it.id.toString() to it.nome }) { onFilter(f.copy(partner = it)) }
    if (state.kind == DashboardKind.COMPRAS) Choice("Fornecedor", f.partner, state.suppliers.sortedBy { it.nome }.map { it.id.toString() to it.nome }) { onFilter(f.copy(partner = it)) }
    if (state.kind in setOf(DashboardKind.VENDAS, DashboardKind.COMPRAS)) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(!f.quantity, { onFilter(f.copy(quantity = false)) }, { Text("Valor") })
        FilterChip(f.quantity, { onFilter(f.copy(quantity = true)) }, { Text("Quantidade") }, enabled = f.unit != null)
    }
    if (state.kind == DashboardKind.VENDAS) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(!f.ticketByUnit, { onFilter(f.copy(ticketByUnit = false)) }, { Text("Ticket por operação") })
        FilterChip(f.ticketByUnit, { onFilter(f.copy(ticketByUnit = true)) }, { Text("Ticket por unidade") }, enabled = f.unit != null)
    }
    if (state.kind == DashboardKind.ESTOQUE) Choice("Estado", f.state?.name, MaturacaoEstado.entries.map { it.name to it.name.lowercase().replaceFirstChar { it.uppercase() } }) { onFilter(f.copy(state = it?.let(MaturacaoEstado::valueOf))) }
    if (state.kind == DashboardKind.ENCOMENDAS) {
        Choice("Etapa", f.orderStatus?.name, EncomendaStatus.entries.map { it.name to it.label }) { onFilter(f.copy(orderStatus = it?.let(EncomendaStatus::valueOf))) }
        FilterChip(f.overdueOnly, { onFilter(f.copy(overdueOnly = !f.overdueOnly)) }, { Text("Somente atrasadas") })
    }
    if (state.kind == DashboardKind.AGENDA) Choice("Tipo", f.eventType?.name, EventoTipo.entries.map { it.name to it.label }) { onFilter(f.copy(eventType = it?.let(EventoTipo::valueOf))) }
}

@Composable
private fun Choice(label: String, selected: String?, options: List<Pair<String, String>>, onSelected: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    Box {
        OutlinedButton({ expanded = true }, Modifier.fillMaxWidth()) { Text("$label: ${options.find { it.first == selected }?.second ?: "Todas"} ▾") }
        DropdownMenu(expanded, { expanded = false }) {
            OutlinedTextField(query, { query = it }, Modifier.padding(8.dp), label = { Text("Buscar $label") }, singleLine = true)
            DropdownMenuItem({ Text("Todas") }, { onSelected(null); expanded = false })
            options.filter { it.second.contains(query, true) }.forEach { option -> DropdownMenuItem({ Text(option.second) }, { onSelected(option.first); expanded = false }) }
        }
    }
}

@Composable
private fun Operations(kind: DashboardKind, navigate: (String) -> Unit) {
    Text("Operações e consultas", style = MaterialTheme.typography.titleMedium)
    val links = when (kind) {
        DashboardKind.VENDAS -> listOf("Nova venda" to BanavitaRoutes.VENDA_FORM_NEW, "Consultar vendas" to BanavitaRoutes.consultas(ConsultaModo.VENDAS))
        DashboardKind.COMPRAS -> listOf("Nova compra" to BanavitaRoutes.COMPRA_FORM_NEW, "Consultar compras" to BanavitaRoutes.consultas(ConsultaModo.COMPRAS))
        DashboardKind.ESTOQUE -> listOf("Consultar estoque" to BanavitaRoutes.consultas(ConsultaModo.ESTOQUE), "Câmaras e transferências" to BanavitaRoutes.ETILENO_FILA, "Registrar perda" to BanavitaRoutes.perdaForm())
        DashboardKind.FINANCEIRO -> listOf("A receber" to BanavitaRoutes.consultas(ConsultaModo.RECEBER), "A pagar" to BanavitaRoutes.consultas(ConsultaModo.PAGAR))
        DashboardKind.ENCOMENDAS -> listOf("Nova encomenda" to BanavitaRoutes.ENCOMENDA_FORM_NEW, "Consultar encomendas" to BanavitaRoutes.consultas(ConsultaModo.ENCOMENDAS))
        DashboardKind.AGENDA -> listOf("Novo evento" to BanavitaRoutes.agendaFormNewForDashboard(), "Calendário" to BanavitaRoutes.agenda())
        DashboardKind.GERAL -> listOf("Nova venda" to BanavitaRoutes.VENDA_FORM_NEW, "Nova compra" to BanavitaRoutes.COMPRA_FORM_NEW, "Cadastros" to BanavitaRoutes.CADASTROS, "Consultar estoque" to BanavitaRoutes.consultas(ConsultaModo.ESTOQUE))
    }
    links.forEach { (label, route) -> OutlinedButton({ navigate(route) }, Modifier.fillMaxWidth()) { Text(label) } }
}

private fun BanavitaRoutes.agendaFormNewForDashboard() = "agenda/novo?date="

@Composable
private fun CashBaseDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit, error: String?) {
    var date by rememberSaveable { mutableStateOf(DateInputParser.todayInput()) }
    var balance by rememberSaveable { mutableStateOf("") }
    AlertDialog(onDismiss, title = { Text("Saldo inicial do caixa") }, text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Informe o saldo no início da data base. Movimentos dessa data em diante serão somados.")
        DocumentDateField(date, { date = it }, label = "Data base")
        OutlinedTextField(balance, { balance = it }, label = { Text("Saldo em R$") }, singleLine = true)
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
    } }, confirmButton = { TextButton({ onSave(date, balance) }) { Text("Salvar") } }, dismissButton = { TextButton(onDismiss) { Text("Cancelar") } })
}

@Composable
private fun kpiIcon(key: String): ImageVector? = when (key) {
    "sales" -> Icons.Default.ShoppingCart
    "purchases", "stock", "operations" -> BanavitaIcons.Inventory
    "balance", "receive", "pay", "projected", "delinquency", "ticket", "cost", "value" -> BanavitaIcons.Payments
    "loss", "risk", "critical" -> BanavitaIcons.Visibility
    "orders", "late", "today" -> BanavitaIcons.LocalShipping
    "meetings", "tasks" -> BanavitaIcons.Event
    "quantity", "margin" -> BanavitaIcons.Category
    else -> null
}

@Composable
private fun kindIcon(kind: DashboardKind): ImageVector = when (kind) {
    DashboardKind.VENDAS -> Icons.Default.ShoppingCart
    DashboardKind.COMPRAS -> BanavitaIcons.Inventory
    DashboardKind.ESTOQUE -> BanavitaIcons.Category
    DashboardKind.FINANCEIRO -> BanavitaIcons.Payments
    DashboardKind.ENCOMENDAS -> BanavitaIcons.LocalShipping
    DashboardKind.AGENDA -> BanavitaIcons.Event
    DashboardKind.GERAL -> Icons.Default.ShoppingCart
}

private fun toneColor(tone: DashboardTone): Color? = when (tone) {
    DashboardTone.POSITIVE -> Color(0xFF2E7D32)
    DashboardTone.ATTENTION -> Color(0xFFF9A825)
    DashboardTone.RISK -> Color(0xFFEF6C00)
    DashboardTone.LOSS -> Color(0xFFE53935)
    DashboardTone.NEUTRAL -> null
}
