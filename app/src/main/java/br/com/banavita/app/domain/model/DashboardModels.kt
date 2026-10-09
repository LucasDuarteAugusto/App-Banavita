package br.com.banavita.app.domain.model

import java.time.LocalDate

enum class DashboardKind(val title: String, val subtitle: String) {
    GERAL("Dashboard Geral", "Visão rápida do seu negócio hoje"),
    VENDAS("Vendas", "Desempenho, clientes e margens"),
    COMPRAS("Compras", "Fornecedores, custos e evolução"),
    ESTOQUE("Estoque & Maturação", "Estoque por variedade, estado e câmara"),
    FINANCEIRO("Financeiro", "Contas e fluxo de caixa"),
    ENCOMENDAS("Encomendas", "Pedidos, entregas e status"),
    AGENDA("Agenda", "Entregas, reuniões, tarefas e lembretes"),
}

enum class DashboardPeriod(val label: String) { HOJE("Hoje"), DIAS_7("7 dias"), DIAS_30("30 dias"), MES("Mês atual"), PERSONALIZADO("Personalizado") }

data class DashboardFilter(
    val period: DashboardPeriod = DashboardPeriod.DIAS_30,
    val customStart: LocalDate? = null,
    val customEnd: LocalDate? = null,
    val unit: String? = null,
    val product: String? = null,
    val partner: String? = null,
    val state: MaturacaoEstado? = null,
    val orderStatus: EncomendaStatus? = null,
    val overdueOnly: Boolean = false,
    val eventType: EventoTipo? = null,
    val quantity: Boolean = false,
    val ticketByUnit: Boolean = false,
)

enum class DashboardTone { NEUTRAL, POSITIVE, ATTENTION, RISK, LOSS }

data class DashboardKpi(val label: String, val value: String, val detail: String = "", val trend: Double? = null, val icon: String = "")
data class ChartPoint(val id: String, val label: String, val value: Double, val detail: String = "", val route: String? = null)
enum class ChartStyle { LINE, BARS, DONUT, STACKED }
data class ChartSeries(val label: String, val points: List<ChartPoint>)
data class DashboardChart(val title: String, val style: ChartStyle, val series: List<ChartSeries>, val money: Boolean = false, val note: String = "")
data class DashboardEntry(val title: String, val detail: String, val route: String? = null, val eventId: String? = null, val complete: Boolean = false, val tone: DashboardTone = DashboardTone.NEUTRAL)
data class DashboardSection(val title: String, val entries: List<DashboardEntry>)
data class DashboardData(
    val kpis: List<DashboardKpi> = emptyList(),
    val charts: List<DashboardChart> = emptyList(),
    val sections: List<DashboardSection> = emptyList(),
    val insights: List<String> = emptyList(),
    val cashConfigured: Boolean = false,
)
