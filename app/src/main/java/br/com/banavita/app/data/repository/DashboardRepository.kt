package br.com.banavita.app.data.repository

import androidx.room.withTransaction
import androidx.sqlite.db.SimpleSQLiteQuery
import br.com.banavita.app.core.time.*
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.dao.*
import br.com.banavita.app.data.local.entity.*
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.model.*
import br.com.banavita.app.ui.common.formatCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.math.roundToLong

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Singleton
class DashboardRepository @Inject constructor(
    private val database: Provider<BanavitaDatabase>,
    private val products: ProdutoRepository,
    private val clients: ClienteRepository,
    private val lots: LoteRepository,
    private val events: EventoRepository,
) {
    private val dao get() = database.get().dashboardDao()

    fun observe(kind: DashboardKind, filter: DashboardFilter, today: LocalDate = LocalDate.now()): Flow<DashboardData> {
        val dates = DashboardPeriods.dates(kind, filter, today)
        val zone = ZoneId.systemDefault()
        val range = DashboardPeriods.range(dates, zone)
        return when (kind) {
            DashboardKind.VENDAS, DashboardKind.COMPRAS -> commerce(kind == DashboardKind.VENDAS, filter, dates, zone)
            DashboardKind.ESTOQUE -> inventory(filter, today)
            DashboardKind.FINANCEIRO -> finance(filter, dates, zone, today)
            DashboardKind.ENCOMENDAS -> orders(filter, range, today, zone)
            DashboardKind.AGENDA -> agenda(filter, range, today, zone)
            DashboardKind.GERAL -> general(today, zone)
        }.flowOn(Dispatchers.Default)
    }

    private fun aggregate(sales: Boolean, filter: DashboardFilter, range: EpochRange, group: String): Flow<List<DashboardAggregate>> {
        val table = if (sales) "venda" else "compra"
        val doc = if (sales) "VEN" else "COM"
        val item = if (sales) "VEI" else "COI"
        val partnerId = if (sales) "VEN_CLIENTE" else "COM_FORNECEDOR"
        val partnerTable = if (sales) "cliente" else "fornecedor"
        val partnerKey = if (sales) "CLI" else "FOR"
        val itemDoc = if (sales) "VEI_VENDA" else "COI_COMPRA"
        val args = mutableListOf<Any>(range.startInclusive, range.endExclusive)
        val conditions = mutableListOf("d.${doc}_DATA >= ?", "d.${doc}_DATA < ?")
        filter.unit?.let { conditions += "COALESCE(i.${item}_UNIDADE, p.PRO_UNIDADE) = ?"; args += it }
        filter.product?.let { conditions += "i.${item}_PRODUTO = ?"; args += it }
        filter.partner?.let { conditions += "d.$partnerId = ?"; args += it }
        val unitCost = if (sales) """(SELECT SUM(ci.COI_VLRTOT) * 1.0 / NULLIF(SUM(ci.COI_QTDE), 0)
            FROM compra_item ci JOIN compra cd ON cd.COM_ID = ci.COI_COMPRA
            WHERE ci.COI_PRODUTO = i.VEI_PRODUTO AND cd.COM_DATA <= d.VEN_DATA
            AND COALESCE(ci.COI_UNIDADE, p.PRO_UNIDADE) = COALESCE(i.VEI_UNIDADE, p.PRO_UNIDADE))""" else "i.COI_VLRTOT * 1.0 / NULLIF(i.COI_QTDE, 0)"
        val base = """SELECT d.${doc}_ID AS docId, d.${doc}_DATA AS date,
            i.${item}_PRODUTO AS productId, p.PRO_DESCRICAO AS product,
            COALESCE(d.$partnerId, '') AS partnerId, COALESCE(a.${partnerKey}_NOME, 'Sem parceiro') AS partner,
            COALESCE(i.${item}_UNIDADE, p.PRO_UNIDADE) AS unit,
            i.${item}_VLRTOT AS amount, i.${item}_QTDE AS qty, $unitCost AS unitCost
            FROM ${table}_item i JOIN $table d ON d.${doc}_ID = i.$itemDoc
            JOIN produto p ON p.PRO_ID = i.${item}_PRODUTO LEFT JOIN $partnerTable a ON a.${partnerKey}_ID = d.$partnerId
            WHERE ${conditions.joinToString(" AND ")}"""
        val key = when (group) { "day" -> "date(date / 1000, 'unixepoch', 'localtime')"; "partner" -> "partnerId"; "product" -> "productId"; "operation" -> "docId"; "unit" -> "unit"; else -> "'total'" }
        val label = when (group) { "partner", "operation" -> "partner"; "product" -> "product"; else -> key }
        val sql = """SELECT $key AS id, COALESCE($label, '') AS label, COALESCE(SUM(amount), 0) AS value,
            COALESCE(SUM(qty), 0) AS quantity, COUNT(DISTINCT docId) AS operations,
            COALESCE(SUM(CASE WHEN unitCost IS NOT NULL THEN amount ELSE 0 END), 0) AS coveredRevenue,
            COALESCE(SUM(qty * unitCost), 0) AS cost FROM ($base)
            ${if (group == "total") "" else "GROUP BY $key"} ORDER BY value DESC, id"""
        return dao.aggregates(SimpleSQLiteQuery(sql, args.toTypedArray()))
    }

    private fun commerce(sales: Boolean, filter: DashboardFilter, dates: Pair<LocalDate, LocalDate>, zone: ZoneId): Flow<DashboardData> {
        val range = DashboardPeriods.range(dates, zone)
        val days = ChronoUnit.DAYS.between(dates.first, dates.second)
        val previous = DashboardPeriods.range(dates.first.minusDays(days) to dates.first, zone)
        val groups = listOf("total", "day", "partner", "product", "operation", "unit")
        return combine(groups.map { aggregate(sales, filter, range, it) } + aggregate(sales, filter, previous, "total")) { result ->
            val total = result[0].first()
            val before = result[6].first()
            val qtyDetail = result[5].joinToString(" · ") { "${number(it.quantity)} ${it.label}" }
            val coverage = if (total.value > 0) total.coveredRevenue * 100.0 / total.value else 0.0
            val margin = if (total.coveredRevenue > 0) (total.coveredRevenue - total.cost) * 100 / total.coveredRevenue else null
            val ticket = when {
                sales && filter.ticketByUnit && filter.unit != null && total.quantity > 0 -> money((total.value / total.quantity).roundToLong())
                sales && filter.ticketByUnit -> "Selecione unidade"
                sales -> money(if (total.operations > 0) total.value / total.operations else 0)
                filter.unit != null && total.quantity > 0 -> money((total.value / total.quantity).roundToLong())
                else -> "Selecione unidade"
            }
            val kpis = listOf(
                DashboardKpi(if (sales) "Total vendido" else "Total comprado", money(total.value), trend = DashboardPeriods.comparison(total.value, before.value), icon = if (sales) "sales" else "purchases"),
                DashboardKpi("Quantidade", if (filter.unit != null) "${number(total.quantity)} ${filter.unit}" else "Por unidade", qtyDetail, icon = "quantity"),
                DashboardKpi(if (sales) "Ticket médio" else "Custo médio", ticket, if (sales && filter.ticketByUnit) "Por ${filter.unit ?: "unidade"}" else if (sales) "Por operação" else "Por ${filter.unit ?: "unidade"}", icon = if (sales) "ticket" else "cost"),
                if (sales) DashboardKpi("Margem bruta estimada", margin?.let { "${number(it)}%" } ?: "Sem custo conhecido", "Cobertura de receita: ${number(coverage)}%", icon = "margin") else DashboardKpi("Operações", total.operations.toString(), icon = "operations"),
            )
            val daily = result[1].associateBy { it.id }
            val points = (0 until days.toInt()).map { offset ->
                val day = dates.first.plusDays(offset.toLong()); val row = daily[day.toString()]
                ChartPoint(day.toString(), day.format(DateTimeFormatter.ofPattern("dd/MM")), if (filter.quantity && filter.unit != null) row?.quantity ?: 0.0 else (row?.value ?: 0L).toDouble())
            }
            val partnerPoints = result[2].take(5).map { row -> ChartPoint(row.id, row.label, row.value.toDouble(), "${money(row.value)} · ${number(if (total.value > 0) row.value * 100.0 / total.value else 0.0)}%", partnerRoute(sales, row.id)) }
            val productPoints = categories(result[3].map { row -> ChartPoint(row.id, row.label, row.value.toDouble(), money(row.value), productRoute(row.id)) })
            val costPoints = categories(result[3].filter { it.quantity > 0 && (!sales || it.coveredRevenue > 0) }.map { row ->
                val value = if (sales) (row.coveredRevenue - row.cost) * 100 / row.coveredRevenue else row.value / row.quantity
                ChartPoint(row.id, row.label, value, if (sales) "${number(value)}% · cobertura ${number(row.coveredRevenue * 100.0 / row.value)}%" else "${money(value.roundToLong())} / ${filter.unit}", productRoute(row.id))
            }, sumOthers = false)
            val operations = result[4]
            val charts = mutableListOf(
                DashboardChart(if (sales) "Evolução de vendas" else "Evolução de compras", ChartStyle.LINE, listOf(ChartSeries("Total", points)), money = !filter.quantity || filter.unit == null),
                DashboardChart(if (sales) "Top clientes" else "Top fornecedores", ChartStyle.BARS, listOf(ChartSeries("Valor", partnerPoints)), money = true),
                DashboardChart(if (sales) "Valor por variedade" else "Compras por variedade", ChartStyle.BARS, listOf(ChartSeries("Valor", productPoints)), money = true),
            )
            if (sales || filter.unit != null) charts += DashboardChart(if (sales) "Margem estimada por variedade" else "Custo médio por variedade", ChartStyle.BARS, listOf(ChartSeries("${if (sales) "%" else "R$"}", costPoints)), money = !sales)
            val marginRows = result[3].filter { it.coveredRevenue > 0 && it.value > 0 }.map { row ->
                row to (row.coveredRevenue - row.cost) * 100 / row.coveredRevenue
            }.sortedByDescending { it.second }
            val marginHighlights = (marginRows.take(3) + marginRows.takeLast(3)).distinctBy { it.first.id }
            val costly = result[3].filter { it.quantity > 0 }.maxByOrNull { it.value / it.quantity }
            val change = DashboardPeriods.comparison(total.value, before.value)
            DashboardData(kpis, charts, listOfNotNull(
                DashboardSection("Resumo de operações", listOfNotNull(
                    DashboardEntry("Operações no período", total.operations.toString()),
                    operations.firstOrNull()?.let { DashboardEntry("Maior operação · ${it.label}", money(it.value), documentRoute(sales, it.id)) },
                    operations.lastOrNull()?.let { DashboardEntry("Menor operação · ${it.label}", money(it.value), documentRoute(sales, it.id)) },
                    if (!sales) result[2].maxByOrNull { it.operations }?.takeIf { it.operations > 0 }?.let { DashboardEntry("Fornecedor com mais operações · ${it.label}", "${it.operations} compras", partnerRoute(false, it.id)) } else null,
                )),
                if (sales && marginHighlights.isNotEmpty()) DashboardSection("Maior e menor margem", marginHighlights.map { (row, value) -> DashboardEntry(row.label, "${number(value)}%", productRoute(row.id)) }) else null,
            ), listOfNotNull(
                result[2].firstOrNull()?.takeIf { total.value > 0 }?.let { "${it.label} concentra ${number(it.value * 100.0 / total.value)}% do valor no período." },
                change?.let { if (sales) "O valor ${if (it >= 0) "cresceu" else "caiu"} ${number(kotlin.math.abs(it))}% em relação ao período anterior." else "As compras ${if (it >= 0) "subiram" else "caíram"} ${number(kotlin.math.abs(it))}% em relação ao período anterior." },
                if (sales && total.value > 0) marginRows.firstOrNull()?.let { (row, value) -> val share = row.value * 100.0 / total.value; "${row.label} responde por ${number(share)}% do valor, com margem estimada de ${number(value)}%." } else null,
                if (sales) marginRows.lastOrNull()?.takeIf { marginRows.size > 1 }?.let { (row, value) -> "${row.label} tem a menor margem estimada do período: ${number(value)}%." } else null,
                if (!sales && filter.unit != null) costly?.let { "${it.label} tem o maior custo médio: ${money((it.value / it.quantity).roundToLong())} por ${filter.unit}." } else null,
            ).take(2) + listOfNotNull(if (sales) "A margem considera apenas custos de compra conhecidos. Despesas operacionais não estão incluídas." else null))
        }
    }

    private fun inventory(filter: DashboardFilter, today: LocalDate): Flow<DashboardData> {
        val allTime = EpochRange(0, PeriodRanges.startOfDay(today.plusDays(1), ZoneId.systemDefault()))
        val costFilter = filter.copy(partner = null)
        return combine(dao.inventory(), dao.lots(), products.observeAll(), aggregate(false, costFilter, allTime, "product"), dao.snapshots(today.minusDays(30).toEpochDay(), today.toEpochDay())) { allRows, lotRows, productRows, costs, history ->
            val productMap = productRows.associateBy { it.id }
            val rows = allRows.filter { (filter.unit == null || it.unit == filter.unit) && (filter.product == null || it.productId.toString() == filter.product) && (filter.state == null || it.state == filter.state.name) }
            val unit = filter.unit
            val critical = setOf("AMARELA", "PINTADA", "PODRE")
            val qtySummary = rows.groupBy { it.unit }.entries.joinToString(" · ") { (u, r) -> "${number(r.sumOf { it.quantity })} $u" }
            // A stock valuation needs unit-specific costs; never mix the denominators.
            val costMap = costs.associateBy { it.id }
            var knownQty = 0.0; var value = 0.0
            if (unit != null) rows.forEach { row -> costMap[row.productId.toString()]?.takeIf { it.quantity > 0 }?.let { knownQty += row.quantity; value += row.quantity * it.value / it.quantity } }
            val selectedLots = lotRows.map { it.toDomain() }.filter { (unit == null || it.unidade == unit || it.unidade == null && productMap[it.produtoId]?.unidade == unit) && (filter.product == null || it.produtoId.toString() == filter.product) && (filter.state == null || it.estado == filter.state) }
            fun stateAt(lot: Lote, day: LocalDate): MaturacaoEstado = RipeningClock.estadoEm(lot.camara, lot.estado, lot.etapaInicioEpochDay, productMap[lot.produtoId]?.curva ?: RipeningCurve.DEFAULT, day.toEpochDay())
            val risk = selectedLots.filter { it.estado == MaturacaoEstado.PODRE || stateAt(it, today.plusDays(3)) == MaturacaoEstado.PODRE }
            val projections = if (unit == null) emptyList() else listOf(MaturacaoEstado.AMARELA, MaturacaoEstado.PINTADA, MaturacaoEstado.PODRE).map { state -> ChartSeries(state.name.lowercase().replaceFirstChar { it.uppercase() }, (0..5).map { offset -> ChartPoint(offset.toString(), if (offset == 0) "Hoje" else "+$offset", selectedLots.filter { stateAt(it, today.plusDays(offset.toLong())) == state }.sumOf { it.quantidadeRestante }) }) }
            val byProduct = rows.groupBy { it.productId }.map { (id, r) -> ChartPoint(id.toString(), r.first().name, r.sumOf { it.quantity }, "${number(r.sumOf { it.quantity })} ${unit.orEmpty()}", productRoute(id.toString())) }
            val byState = MaturacaoEstado.entries.map { state -> ChartPoint(state.name, state.name.lowercase().replaceFirstChar { it.uppercase() }, rows.filter { it.state == state.name }.sumOf { it.quantity }) }
            val topProducts = byProduct.sortedByDescending { it.value }.take(7)
            val maturityByProduct = MaturacaoEstado.entries.map { state ->
                ChartSeries(state.name.lowercase().replaceFirstChar { it.uppercase() }, topProducts.map { product ->
                    product.copy(value = rows.filter { it.productId.toString() == product.id && it.state == state.name }.sumOf { it.quantity }, detail = state.name)
                })
            }
            val charts = if (unit == null) emptyList() else listOf(
                DashboardChart("Estoque por variedade", ChartStyle.BARS, listOf(ChartSeries(unit, categories(byProduct)))),
                DashboardChart("Estoque por estado", ChartStyle.DONUT, listOf(ChartSeries(unit, byState))),
                DashboardChart("Maturação por variedade", ChartStyle.STACKED, maturityByProduct, note = "Até sete variedades com maior estoque, na unidade selecionada."),
                DashboardChart("Curva de maturação · próximos 5 dias", ChartStyle.STACKED, projections, note = "Estoque atual, sem novas compras, vendas ou transferências."),
            )
            val criticalRows = selectedLots.filter { it.estado.name in critical || it in risk }
            val entries = criticalRows.groupBy { it.produtoId to it.estado }.map { (key, lots) ->
                val (id, estado) = key
                val projected = lots.map { stateAt(it, today.plusDays(2)) }.maxBy { it.ordinal }
                val next = estado.next()
                val qty = lots.groupBy { it.unidade ?: productMap[id]?.unidade.orEmpty() }.entries.joinToString(" · ") { (u, ll) -> "${number(ll.sumOf { it.quantidadeRestante })} $u" }
                val transition = if (next != null) "${stateLabel(estado)} → ${stateLabel(next)}" else stateLabel(estado)
                DashboardEntry(productMap[id]?.descricao ?: "Produto", "$qty · $transition", productRoute(id.toString()), tone = ripeningTone(estado, projected))
            }.sortedByDescending { it.tone.ordinal }
            val historyDays = history.map { it.dia }.distinct().size
            val filteredHistory = history.filter { (unit == null || it.unidade == unit) && (filter.product == null || it.produtoId.toString() == filter.product) && (filter.state == null || it.estado == filter.state.name) }
            val averageStock = if (historyDays >= 30) filteredHistory.sumOf { it.quantidade } / historyDays else null
            val stockComparison = if (unit != null && averageStock != null && averageStock > 0) "O estoque atual está ${number((rows.sumOf { it.quantity } - averageStock) / averageStock * 100)}% em relação à média dos 30 dias observados ($unit)." else null
            val watchedQty = criticalRows.sumOf { it.quantidadeRestante }
            val stockQty = if (unit != null) selectedLots.sumOf { it.quantidadeRestante } else 0.0
            DashboardData(
                listOf(DashboardKpi("Total em estoque", if (unit != null) "${number(rows.sumOf { it.quantity })} $unit" else "Por unidade", qtySummary, icon = "stock"),
                    DashboardKpi("Valor estimado a custo", if (unit == null) "Selecione unidade" else money(value.roundToLong()), "Quantidade com custo: ${number(knownQty)} ${unit.orEmpty()}", icon = "value"),
                    DashboardKpi("Estado crítico", if (unit == null) "Selecione unidade" else "${number(rows.filter { it.state in critical }.sumOf { it.quantity })} $unit", icon = "critical"),
                    DashboardKpi("Risco em 3 dias", if (unit == null) "Selecione unidade" else "${number(risk.sumOf { it.quantidadeRestante })} $unit", "Projeção, não perda já registrada", icon = "risk")),
                charts, listOf(DashboardSection("Maturação crítica hoje", entries), DashboardSection("Estoque por unidade", rows.map { DashboardEntry(it.name, "${number(it.quantity)} ${it.unit} · ${it.state ?: "Não perecível"} · ${it.chamber.orEmpty()}", productRoute(it.productId.toString())) })),
                listOfNotNull(
                    if (unit != null && stockQty > 0 && watchedQty > 0) "${number(watchedQty * 100 / stockQty)}% do estoque em $unit está em atenção ou chega a estado crítico em até 2 dias. Priorize a saída desses lotes." else null,
                    if (historyDays < 30) "Histórico de estoque insuficiente: $historyDays de 30 dias observados. Não são preenchidos dias sem observação." else stockComparison,
                    "Depois de iniciada, a curva avança na fria e na etileno. Lotes crus aguardam iniciação.",
                ).take(3),
            )
        }
    }

    private fun finance(filter: DashboardFilter, dates: Pair<LocalDate, LocalDate>, zone: ZoneId, today: LocalDate): Flow<DashboardData> {
        val range = DashboardPeriods.range(dates, zone)
        val todayStart = PeriodRanges.startOfDay(today, zone)
        return dao.cashBase().flatMapLatest { base ->
            combine(dao.finance(), dao.cash(base?.data ?: range.startInclusive, maxOf(range.endExclusive, PeriodRanges.startOfDay(today.plusDays(1), zone)))) { all, movements ->
                val period = all.filter { it.due != null && it.due >= range.startInclusive && it.due < range.endExclusive }
                val receive = period.filter { it.incoming }.sumOf { it.balance }; val pay = period.filter { !it.incoming }.sumOf { it.balance }
                val overdue = all.filter { it.due != null && it.due < todayStart }
                val undated = all.filter { it.due == null }
                val validBase = base != null && base.data <= range.startInclusive
                val initial = if (validBase) base!!.saldoCentavos + movements.filter { it.date < range.startInclusive }.sumOf { it.value } else null
                val days = ChronoUnit.DAYS.between(dates.first, dates.second).toInt()
                fun series(incoming: Boolean) = ChartSeries(if (incoming) "A receber" else "A pagar", (0 until days).map { offset ->
                    val day = dates.first.plusDays(offset.toLong()); val daily = PeriodRanges.day(day, zone)
                    ChartPoint(day.toString(), day.format(DateTimeFormatter.ofPattern("dd/MM")), period.filter { it.incoming == incoming && it.due!! in daily }.sumOf { it.balance }.toDouble())
                })
                val entries = all.sortedByDescending { it.balance }.map { DashboardEntry("${it.partner} · ${if (it.incoming) "A receber" else "A pagar"}", "${money(it.balance)} · ${it.due?.let { due -> date(due, zone) } ?: "Sem vencimento"}", partnerRoute(it.incoming, it.partnerId?.toString().orEmpty())) }
                val weekEnd = PeriodRanges.startOfDay(today.plusDays(7), zone)
                fun dueSoon(incoming: Boolean) = all.filter { it.incoming == incoming && it.due != null && it.due >= todayStart && it.due < weekEnd }.sumOf { it.balance }
                val projected = initial?.let { it + movements.filter { m -> m.date in range }.sumOf { m -> m.value } + receive - pay }
                val receiveOpen = all.filter { it.incoming }.sumOf { it.balance }
                val receiveLate = overdue.filter { it.incoming }.sumOf { it.balance }
                val delinquency = if (receiveOpen > 0) receiveLate * 100.0 / receiveOpen else 0.0
                fun top(incoming: Boolean): DashboardSection = DashboardSection(if (incoming) "Top clientes a receber" else "Top fornecedores a pagar", all.filter { it.incoming == incoming }.groupBy { it.partnerId }.entries.sortedByDescending { it.value.sumOf { row -> row.balance } }.take(5).map { (_, rows) ->
                    val balance = rows.sumOf { it.balance }
                    val late = rows.filter { it.due != null && it.due < todayStart }.sumOf { it.balance }
                    DashboardEntry(rows.first().partner, "${money(balance)} · ${rows.size} títulos · ${number(if (balance > 0) late * 100.0 / balance else 0.0)}% vencido", partnerRoute(incoming, rows.first().partnerId?.toString().orEmpty()))
                })
                val receiveGroups = all.filter { it.incoming }.groupBy { it.partnerId }.values.sortedByDescending { it.sumOf { row -> row.balance } }
                val peakPay = (0 until days).map { offset -> dates.first.plusDays(offset.toLong()) }.maxByOrNull { day -> period.filter { !it.incoming && it.due!! in PeriodRanges.day(day, zone) }.sumOf { it.balance } }
                val peakValue = peakPay?.let { day -> period.filter { !it.incoming && it.due!! in PeriodRanges.day(day, zone) }.sumOf { it.balance } } ?: 0L
                DashboardData(listOf(DashboardKpi("A receber no período", money(receive), icon = "receive"), DashboardKpi("A pagar no período", money(pay), icon = "pay"), DashboardKpi("Saldo projetado", projected?.let(::money) ?: "Configure o caixa", "Inclui caixa inicial e movimentos realizados", icon = "projected"), DashboardKpi("Inadimplência", "${number(delinquency)}%", money(receiveLate), icon = "delinquency")),
                    listOf(DashboardChart("Fluxo de caixa projetado", ChartStyle.LINE, listOf(series(true), series(false)), money = true), DashboardChart("Movimentos realizados", ChartStyle.LINE, listOf(ChartSeries("Entradas", (0 until days).map { i -> val day = dates.first.plusDays(i.toLong()); ChartPoint(day.toString(), day.format(DateTimeFormatter.ofPattern("dd/MM")), movements.filter { it.date in PeriodRanges.day(day, zone) && it.value > 0 }.sumOf { it.value }.toDouble()) }), ChartSeries("Saídas", (0 until days).map { i -> val day = dates.first.plusDays(i.toLong()); ChartPoint(day.toString(), day.format(DateTimeFormatter.ofPattern("dd/MM")), -movements.filter { it.date in PeriodRanges.day(day, zone) && it.value < 0 }.sumOf { it.value }.toDouble()) })), money = true)),
                    listOf(DashboardSection("Resumo do caixa", listOf(DashboardEntry("Saldo inicial", initial?.let(::money) ?: "Configure saldo e data base anteriores ao período"), DashboardEntry("Entradas previstas", money(receive)), DashboardEntry("Saídas previstas", money(pay)), DashboardEntry("A receber em 7 dias", money(dueSoon(true))), DashboardEntry("A pagar em 7 dias", money(dueSoon(false))), DashboardEntry("Saldo final projetado", projected?.let(::money) ?: "Configure o caixa"))), top(true), top(false), DashboardSection("Vencidos · pendências sem nova data", overdue.map { DashboardEntry(it.partner, money(it.balance), partnerRoute(it.incoming, it.partnerId?.toString().orEmpty())) }), DashboardSection("Sem vencimento", undated.map { DashboardEntry(it.partner, money(it.balance), documentRoute(it.incoming, it.id.toString())) }), DashboardSection("Títulos em aberto", entries)),
                    listOfNotNull("Vencidos permanecem em pendências; não recebem uma data fictícia na projeção.", if (receiveGroups.size >= 3 && receiveOpen > 0) "Os 3 maiores clientes concentram ${number(receiveGroups.take(3).sumOf { rows -> rows.sumOf { it.balance } } * 100.0 / receiveOpen)}% do valor a receber." else null, if (peakPay != null && peakValue > 0) "O maior pagamento previsto no período é ${money(peakValue)} em ${peakPay.format(DateTimeFormatter.ofPattern("dd/MM"))}." else null).take(3), cashConfigured = base != null)
            }
        }
    }

    private fun orders(filter: DashboardFilter, range: EpochRange, today: LocalDate, zone: ZoneId): Flow<DashboardData> =
        combine(dao.orders(range.startInclusive, range.endExclusive, true), products.observeAll(), clients.observeAll()) { relations, productRows, clientRows ->
            val p = productRows.associateBy { it.id }; val c = clientRows.associateBy { it.id }; val now = nowEpochMillis()
            val all = relations.map { it.toDomain() }
            val rows = all.filter { (filter.orderStatus == null || it.status == filter.orderStatus) && (!filter.overdueOnly || it.status.pendente && it.dataEntrega < now) }
            val pending = rows.filter { it.status.pendente }; val day = PeriodRanges.day(today, zone)
            val late = pending.filter { it.dataEntrega < now }
            val items = pending.flatMap { it.itens }.filter { it.quantidadePendente > 1e-9 }
            val known = items.filter { it.valorUnitarioCentavos != null }
            val value = known.sumOf { (it.quantidadePendente * it.valorUnitarioCentavos!!).roundToLong() }
            fun entry(order: Encomenda) = DashboardEntry(c[order.clienteId]?.nome ?: "Cliente", "${date(order.dataEntrega, zone)} · ${order.status.label}\n" + order.itens.joinToString(" · ") { "${p[it.produtoId]?.descricao.orEmpty()}: ${number(it.quantidadePendente)} ${(it.unidade ?: p[it.produtoId]?.unidade).orEmpty()} pendentes" }, "pereciveis/encomenda/edit/${order.id}")
            val statuses = EncomendaStatus.entries.map { status -> ChartPoint(status.name, status.label, rows.count { it.status == status }.toDouble()) }
            val rankings = rows.groupBy { it.clienteId }.entries.sortedByDescending { it.value.size }.take(5).map { (id, orders) -> ChartPoint(id.toString(), c[id]?.nome ?: "Cliente", orders.size.toDouble(), "${orders.size} encomendas") }
            val lateKnown = late.flatMap { it.itens }.filter { it.quantidadePendente > 1e-9 && it.valorUnitarioCentavos != null }
            val lateValue = lateKnown.sumOf { (it.quantidadePendente * it.valorUnitarioCentavos!!).roundToLong() }
            val topClient = rankings.firstOrNull()
            DashboardData(listOf(DashboardKpi("Encomendas pendentes", pending.size.toString(), icon = "orders"), DashboardKpi("A entregar hoje", pending.count { it.dataEntrega in day }.toString(), icon = "today"), DashboardKpi("Atrasadas", late.size.toString(), icon = "late"), DashboardKpi("Valor pendente conhecido", money(value), "Preço conhecido: ${known.size}/${items.size} itens", icon = "value")),
                listOf(DashboardChart("Encomendas por etapa", ChartStyle.DONUT, listOf(ChartSeries("Pedidos", statuses))), DashboardChart("Top clientes em encomendas", ChartStyle.BARS, listOf(ChartSeries("Pedidos", rankings)))),
                listOf(DashboardSection("Entregas de hoje", pending.filter { it.dataEntrega in day }.map(::entry)), DashboardSection("Encomendas atrasadas", late.map(::entry)), DashboardSection("Pedidos no período e pendências anteriores", rows.map(::entry))),
                listOfNotNull(if (late.isNotEmpty()) "${late.size} encomendas estão atrasadas, com valor conhecido de ${money(lateValue)}." else "Não há encomendas atrasadas neste recorte.", if (topClient != null && rows.isNotEmpty()) "${topClient.label} concentra ${number(topClient.value * 100 / rows.size)}% das encomendas do período." else null, "A venda e a baixa de estoque ocorrem na confirmação de entrega, sem reserva nas etapas anteriores.").take(3))
        }

    private fun agenda(filter: DashboardFilter, range: EpochRange, today: LocalDate, zone: ZoneId): Flow<DashboardData> = dao.events(range.startInclusive, range.endExclusive).map { entities ->
        val rows = entities.map { it.toDomain() }.filter { filter.eventType == null || it.tipo == filter.eventType }
        val todayRows = rows.filter { it.dataHora in PeriodRanges.day(today, zone) }
        val tasks = rows.filter { it.tipo == EventoTipo.TAREFA && it.status != EventoStatus.CONCLUIDO }
        val soonEnd = PeriodRanges.startOfDay(today.plusDays(3), zone)
        val soonTasks = tasks.filter { it.dataHora < soonEnd }
        fun dayTitle(day: LocalDate) = when (day) {
            today -> "Hoje"
            today.plusDays(1) -> "Amanhã"
            today.plusDays(2) -> "Depois de amanhã"
            else -> day.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
        fun entry(event: Evento) = DashboardEntry("${time(event.dataHora, zone)} · ${event.titulo}", "${event.tipo.label} · ${event.status.label}${if (event.tipo == EventoTipo.TAREFA) " · ${event.prioridade.label}" else ""}\n${event.observacao.orEmpty()}", "agenda/edit/${event.id}", if (event.tipo == EventoTipo.TAREFA) event.id.toString() else null, event.status == EventoStatus.CONCLUIDO)
        val upcoming = rows.filter { it.dataHora !in PeriodRanges.day(today, zone) && it.dataHora in range }.groupBy { Instant.ofEpochMilli(it.dataHora).atZone(zone).toLocalDate() }
        val peak = upcoming.filterKeys { it.isAfter(today) && !it.isAfter(today.plusDays(7)) }.maxByOrNull { (_, events) -> events.count { it.tipo == EventoTipo.ENTREGA } }
        val peakCount = peak?.value?.count { it.tipo == EventoTipo.ENTREGA } ?: 0
        DashboardData(listOf(DashboardKpi("Entregas hoje", todayRows.count { it.tipo == EventoTipo.ENTREGA }.toString(), icon = "orders"), DashboardKpi("Reuniões hoje", todayRows.count { it.tipo == EventoTipo.REUNIAO }.toString(), icon = "meetings"), DashboardKpi("Tarefas pendentes", tasks.size.toString(), icon = "tasks")),
            listOf(DashboardChart("Compromissos por tipo", ChartStyle.BARS, listOf(ChartSeries("Eventos", EventoTipo.entries.map { type -> ChartPoint(type.name, type.label, rows.count { it.tipo == type }.toDouble()) })))),
            listOf(DashboardSection("Hoje", todayRows.map(::entry)), DashboardSection("Tarefas pendentes", tasks.sortedByDescending { it.prioridade.ordinal }.map(::entry))) + upcoming.map { (day, events) -> DashboardSection(dayTitle(day), events.map(::entry)) },
            listOfNotNull("${todayRows.count { it.status != EventoStatus.CONCLUIDO }} compromissos ainda pendentes hoje.", if (soonTasks.isNotEmpty()) "${soonTasks.size} tarefas vencem até depois de amanhã." else null, if (peak != null && peakCount > 0) "${dayTitle(peak.key)} concentra $peakCount entregas. Vale organizar a rota." else "Entregas vinculadas acompanham as etapas e o saldo da encomenda.").take(3))
    }

    private fun general(today: LocalDate, zone: ZoneId): Flow<DashboardData> {
        val filter = DashboardFilter(period = DashboardPeriod.HOJE)
        val dates = today to today.plusDays(1)
        val range = DashboardPeriods.range(dates, zone)
        return combine(commerce(true, filter, dates, zone), commerce(false, filter, dates, zone), dao.losses(range.startInclusive, range.endExclusive), inventory(filter, today)) { sales, purchases, loss, stock ->
            DashboardData(listOf(sales.kpis.first().copy(label = "Vendas do dia", detail = sales.kpis[1].detail, icon = "sales"), purchases.kpis.first().copy(label = "Compras do dia", icon = "purchases"), DashboardKpi("Diferença comercial", "—", icon = "balance"), DashboardKpi("Perdas registradas", money(loss ?: 0L), "Custo dos lotes perdidos hoje", icon = "loss")), sections = stock.sections.take(1).map { it.copy(entries = it.entries.take(4)) }, insights = listOf("Vendas − compras é diferença comercial; o caixa é acompanhado no Financeiro."))
        }.combine(combine(aggregate(true, filter, range, "total"), aggregate(false, filter, range, "total")) { sales, buys -> sales.first().value - buys.first().value }) { data, difference -> data.copy(kpis = data.kpis.map { if (it.label == "Diferença comercial") it.copy(value = money(difference), trend = difference.toDouble()) else it }) }
    }

    suspend fun refreshStockSnapshot(today: LocalDate = LocalDate.now()) {
        lots.avancarMaturacao()
        val rows = dao.inventory().first()
        database.get().withTransaction {
            dao.deleteSnapshot(today.toEpochDay())
            dao.insertSnapshots(rows.map { EstoqueSnapshotEntity(today.toEpochDay(), it.productId, it.unit, it.state.orEmpty(), it.chamber.orEmpty(), it.quantity) })
        }
    }

    suspend fun setCashBase(date: Long, cents: Long) = dao.setCashBase(CaixaBaseEntity(data = date, saldoCentavos = cents))

    suspend fun completeTask(id: UUID, complete: Boolean) {
        val event = events.getById(id) ?: return
        require(event.tipo == EventoTipo.TAREFA) { "Somente tarefas podem ser concluídas aqui." }
        events.update(event.id, event.titulo, event.dataHora, event.observacao, event.clienteId, event.fornecedorId, event.notificar, event.antecipacaoMinutos, event.notifTitulo, event.notifCorpo, status = if (complete) EventoStatus.CONCLUIDO else EventoStatus.PENDENTE)
    }

    private fun categories(points: List<ChartPoint>, sumOthers: Boolean = true): List<ChartPoint> {
        val sorted = points.sortedByDescending { it.value }
        return if (sorted.size <= 7 || !sumOthers) sorted.take(7) else sorted.take(6) + ChartPoint("others", "Outros", sorted.drop(6).sumOf { it.value })
    }
    private fun productRoute(id: String) = if (id.isBlank()) null else "consultas/estoque/$id?criticos="
    private fun partnerRoute(sales: Boolean, id: String) = if (id.isBlank()) null else "consultas/${if (sales) "receber" else "pagar"}/$id"
    private fun documentRoute(sales: Boolean, id: String) = "${if (sales) "vendas" else "compras"}/edit/$id"
    private fun stateLabel(state: MaturacaoEstado) = when (state) {
        MaturacaoEstado.CRUA -> "Crua"
        MaturacaoEstado.EM_MATURACAO -> "Em maturação"
        MaturacaoEstado.VERDE -> "Verde"
        MaturacaoEstado.VERDOLENGA -> "Verdolenga"
        MaturacaoEstado.AMARELA -> "Amarela"
        MaturacaoEstado.PINTADA -> "Pintada"
        MaturacaoEstado.PODRE -> "Podre"
    }

    private fun ripeningTone(state: MaturacaoEstado, projected: MaturacaoEstado): DashboardTone = when {
        state == MaturacaoEstado.PODRE || projected == MaturacaoEstado.PODRE -> DashboardTone.LOSS
        state == MaturacaoEstado.PINTADA || projected == MaturacaoEstado.PINTADA -> DashboardTone.RISK
        state == MaturacaoEstado.AMARELA || projected == MaturacaoEstado.AMARELA -> DashboardTone.ATTENTION
        else -> DashboardTone.POSITIVE
    }

    private fun money(value: Long) = formatCurrency(value)
    private fun number(value: Double) = String.format(Locale.forLanguageTag("pt-BR"), "%,.1f", value)
    private fun date(value: Long, zone: ZoneId) = Instant.ofEpochMilli(value).atZone(zone).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    private fun time(value: Long, zone: ZoneId) = Instant.ofEpochMilli(value).atZone(zone).format(DateTimeFormatter.ofPattern("HH:mm"))
}
