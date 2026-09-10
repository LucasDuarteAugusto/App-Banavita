package br.com.banavita.app.core.time

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/**
 * Grade de mês no estilo Google Agenda (semana começa no domingo).
 */
object MonthCalendar {
    fun previousMonth(yearMonth: YearMonth): YearMonth = yearMonth.minusMonths(1)

    fun nextMonth(yearMonth: YearMonth): YearMonth = yearMonth.plusMonths(1)

    fun gridDates(yearMonth: YearMonth): List<LocalDate> {
        val first = yearMonth.atDay(1)
        val last = yearMonth.atEndOfMonth()
        val start = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val end = last.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
        val days = ArrayList<LocalDate>((end.toEpochDay() - start.toEpochDay() + 1).toInt())
        var cursor = start
        while (!cursor.isAfter(end)) {
            days.add(cursor)
            cursor = cursor.plusDays(1)
        }
        return days
    }

    fun monthRange(yearMonth: YearMonth, zone: ZoneId = ZoneId.systemDefault()): EpochRange {
        val first = yearMonth.atDay(1)
        return EpochRange(PeriodRanges.startOfDay(first, zone), PeriodRanges.startOfDay(first.plusMonths(1), zone))
    }
}
