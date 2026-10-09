package br.com.banavita.app.core.time

import br.com.banavita.app.domain.model.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object DashboardPeriods {
    fun dates(kind: DashboardKind, filter: DashboardFilter, today: LocalDate): Pair<LocalDate, LocalDate> {
        val future = kind in setOf(DashboardKind.FINANCEIRO, DashboardKind.ENCOMENDAS, DashboardKind.AGENDA)
        return when (filter.period) {
            DashboardPeriod.HOJE -> today to today.plusDays(1)
            DashboardPeriod.DIAS_7, DashboardPeriod.DIAS_30 -> {
                val days = if (filter.period == DashboardPeriod.DIAS_7) 7L else 30L
                if (future) today to today.plusDays(days) else today.minusDays(days - 1) to today.plusDays(1)
            }
            DashboardPeriod.MES -> today.withDayOfMonth(1) to today.withDayOfMonth(1).plusMonths(1)
            DashboardPeriod.PERSONALIZADO -> {
                val start = requireNotNull(filter.customStart) { "Informe a data inicial." }
                val end = requireNotNull(filter.customEnd) { "Informe a data final." }
                require(!end.isBefore(start)) { "A data final deve ser igual ou posterior à inicial." }
                require(ChronoUnit.DAYS.between(start, end) <= 365) { "Selecione até 366 dias." }
                start to end.plusDays(1)
            }
        }
    }

    fun range(dates: Pair<LocalDate, LocalDate>, zone: ZoneId): EpochRange =
        EpochRange(PeriodRanges.startOfDay(dates.first, zone), PeriodRanges.startOfDay(dates.second, zone))

    fun comparison(current: Long, previous: Long): Double? =
        if (previous == 0L) null else (current - previous).toDouble() / kotlin.math.abs(previous.toDouble()) * 100
}
