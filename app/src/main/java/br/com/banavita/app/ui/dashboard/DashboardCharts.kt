package br.com.banavita.app.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.com.banavita.app.domain.model.*
import br.com.banavita.app.ui.common.formatCurrency
import java.util.Locale
import kotlin.math.*

private val chartColors = listOf(Color(0xFF2E7D32), Color(0xFF1976D2), Color(0xFFEF6C00), Color(0xFF8E24AA), Color(0xFFF9A825), Color(0xFFE53935), Color(0xFF607D8B))

private fun chartColor(label: String, index: Int): Color = when (label.uppercase(Locale.ROOT)) {
    "CRUA" -> Color(0xFF607D8B)
    "EM_MATURACAO", "EM MATURAÇÃO" -> Color(0xFF1976D2)
    "VERDE" -> Color(0xFF2E7D32)
    "VERDOLENGA" -> Color(0xFF9E9D24)
    "AMARELA" -> Color(0xFFF9A825)
    "PINTADA" -> Color(0xFFEF6C00)
    "PODRE" -> Color(0xFFE53935)
    else -> chartColors[index % chartColors.size]
}

@Composable
fun DashboardChartCard(chart: DashboardChart, onPoint: (ChartPoint) -> Unit) {
    var selected by remember(chart.title) { mutableIntStateOf(0) }
    val points = chart.series.firstOrNull()?.points.orEmpty()
    LaunchedEffect(points.size) { selected = selected.coerceIn(0, maxOf(0, points.lastIndex)) }
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(chart.title, style = MaterialTheme.typography.titleMedium)
            if (points.isEmpty() || chart.series.all { series -> series.points.all { it.value == 0.0 } }) {
                Text("Sem dados para os filtros selecionados.", style = MaterialTheme.typography.bodyMedium)
            } else when (chart.style) {
                ChartStyle.BARS -> {
                    val max = points.maxOf { abs(it.value) }.takeIf { it > 0 } ?: 1.0
                    points.forEach { point ->
                        Column(Modifier.fillMaxWidth().clickable { onPoint(point) }.semantics { contentDescription = "${point.label}: ${chartValue(point.value, chart.money)}" }) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(point.label, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                                Text(chartValue(point.value, chart.money), style = MaterialTheme.typography.labelLarge)
                            }
                            val color = if (point.value < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            Canvas(Modifier.fillMaxWidth().height(16.dp).padding(vertical = 4.dp)) {
                                drawLine(color.copy(alpha = .12f), Offset.Zero, Offset(size.width, 0f), size.height, StrokeCap.Round)
                                drawLine(color, Offset.Zero, Offset((size.width * abs(point.value) / max).toFloat(), 0f), size.height, StrokeCap.Round)
                            }
                            if (point.detail.isNotBlank()) Text(point.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                ChartStyle.DONUT -> {
                    val total = points.sumOf { maxOf(0.0, it.value) }
                    Canvas(Modifier.fillMaxWidth().height(180.dp).semantics { contentDescription = points.joinToString { "${it.label}: ${chartValue(it.value, chart.money)}" } }.pointerInput(points) {
                        detectTapGestures { pos ->
                            var angle = Math.toDegrees(atan2((pos.y - size.height / 2).toDouble(), (pos.x - size.width / 2).toDouble())) + 90
                            if (angle < 0) angle += 360
                            var sum = 0.0
                            points.forEachIndexed { index, p -> sum += maxOf(0.0, p.value) / total * 360; if (angle <= sum) { selected = index; return@detectTapGestures } }
                        }
                    }) {
                        val side = min(size.width, size.height) - 32.dp.toPx(); var angle = -90f
                        points.forEachIndexed { index, point ->
                            val sweep = (maxOf(0.0, point.value) / total * 360).toFloat()
                            drawArc(chartColor(point.id, index), angle, sweep, false, Offset((size.width - side) / 2, (size.height - side) / 2), Size(side, side), style = Stroke(if (selected == index) 27.dp.toPx() else 20.dp.toPx()))
                            angle += sweep
                        }
                    }
                }
                ChartStyle.LINE, ChartStyle.STACKED -> {
                    val allValues = chart.series.flatMap { it.points }.map { it.value }
                    val top = if (chart.style == ChartStyle.STACKED) points.indices.maxOf { i -> chart.series.sumOf { it.points.getOrNull(i)?.value ?: 0.0 } } else allValues.maxOrNull() ?: 1.0
                    val max = maxOf(1.0, top); val min = minOf(0.0, allValues.minOrNull() ?: 0.0)
                    val gridColor = MaterialTheme.colorScheme.outlineVariant
                    Text("${chartValue(min, chart.money)} — ${chartValue(max, chart.money)}", style = MaterialTheme.typography.labelSmall)
                    Canvas(Modifier.fillMaxWidth().height(180.dp).semantics { contentDescription = chart.series.joinToString { "${it.label}: " + it.points.joinToString { p -> "${p.label} ${chartValue(p.value, chart.money)}" } } }.pointerInput(points) {
                        detectTapGestures { selected = (it.x / size.width * maxOf(1, points.size - 1)).roundToInt().coerceIn(0, points.lastIndex) }
                    }) {
                        fun y(value: Double) = (size.height - (value - min) / (max - min) * size.height).toFloat()
                        repeat(4) { i -> val lineY = size.height * i / 3; drawLine(gridColor, Offset(0f, lineY), Offset(size.width, lineY), 1.dp.toPx()) }
                        if (chart.style == ChartStyle.STACKED) {
                            val step = size.width / points.size
                            points.indices.forEach { index ->
                                var base = 0.0
                                chart.series.forEachIndexed { seriesIndex, series ->
                                    val value = series.points.getOrNull(index)?.value ?: 0.0
                                    drawRect(chartColor(series.label, seriesIndex), Offset(step * index + step * .15f, y(base + value)), Size(step * .7f, maxOf(0f, y(base) - y(base + value))))
                                    base += value
                                }
                            }
                        } else chart.series.forEachIndexed { seriesIndex, series ->
                            series.points.forEachIndexed { index, p ->
                                val x = if (points.size == 1) size.width / 2 else index * size.width / (points.size - 1)
                                if (index > 0) drawLine(chartColors[seriesIndex % chartColors.size], Offset((index - 1) * size.width / (points.size - 1), y(series.points[index - 1].value)), Offset(x, y(p.value)), 3.dp.toPx(), StrokeCap.Round)
                                if (index == selected || points.size == 1) drawCircle(chartColors[seriesIndex % chartColors.size], 5.dp.toPx(), Offset(x, y(p.value)))
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(points.first().label, style = MaterialTheme.typography.labelSmall)
                        Text(points[points.size / 2].label, style = MaterialTheme.typography.labelSmall)
                        Text(points.last().label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            if (chart.style != ChartStyle.BARS && points.isNotEmpty()) {
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    points.forEachIndexed { i, point -> FilterChip(selected == i, { selected = i }, { Text(point.label) }) }
                }
                chart.series.forEachIndexed { index, series ->
                    series.points.getOrNull(selected)?.let { p ->
                        Text("● ${series.label} · ${p.label}: ${chartValue(p.value, chart.money)}", color = chartColors[index % chartColors.size], style = MaterialTheme.typography.bodyMedium, modifier = Modifier.clickable { onPoint(p) })
                    }
                }
            }
            if (chart.note.isNotBlank()) Text(chart.note, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun chartValue(value: Double, money: Boolean): String = if (money) formatCurrency(value.roundToLong()) else String.format(Locale.forLanguageTag("pt-BR"), "%,.1f", value)
