package br.com.banavita.app.core.time

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneOffset

class MonthCalendarTest {

    @Test
    fun `grade comeca no domingo e termina no sabado`() {
        val month = YearMonth.of(2026, 9)
        val grid = MonthCalendar.gridDates(month)
        assertEquals(DayOfWeek.SUNDAY, grid.first().dayOfWeek)
        assertEquals(DayOfWeek.SATURDAY, grid.last().dayOfWeek)
        assertEquals(0, grid.size % 7)
        assertTrue(grid.contains(LocalDate.of(2026, 9, 1)))
        assertTrue(grid.contains(LocalDate.of(2026, 9, 30)))
        assertTrue(YearMonth.from(grid.first()) != month)
        assertTrue(YearMonth.from(grid.last()) != month)
    }

    @Test
    fun `setas viram o ano em dezembro e janeiro`() {
        val december = YearMonth.of(2026, 12)
        assertEquals(YearMonth.of(2027, 1), MonthCalendar.nextMonth(december))
        assertEquals(YearMonth.of(2026, 12), MonthCalendar.previousMonth(YearMonth.of(2027, 1)))
    }

    @Test
    fun `intervalo do mes e meia noite local inclusive exclusive`() {
        val zone = ZoneOffset.ofHours(-3)
        val month = YearMonth.of(2026, 9)
        val range = MonthCalendar.monthRange(month, zone)
        val first = PeriodRanges.startOfDay(LocalDate.of(2026, 9, 1), zone)
        val next = PeriodRanges.startOfDay(LocalDate.of(2026, 10, 1), zone)
        assertEquals(first, range.startInclusive)
        assertEquals(next, range.endExclusive)
        assertTrue(first in range)
        assertFalse(next in range)
    }

    @Test
    fun `eventos do dia ordenam por horario e titulo`() {
        val zone = ZoneOffset.ofHours(-3)
        val day = LocalDate.of(2026, 9, 10)
        val range = PeriodRanges.day(day, zone)
        val morning = PeriodRanges.atTime(day, java.time.LocalTime.of(9, 0), zone)
        val afternoon = PeriodRanges.atTime(day, java.time.LocalTime.of(15, 0), zone)
        val sameTimeA = PeriodRanges.atTime(day, java.time.LocalTime.of(15, 0), zone)
        data class Item(val dataHora: Long, val titulo: String)
        val items = listOf(
            Item(afternoon, "Zebra"),
            Item(morning, "Cafe"),
            Item(sameTimeA, "Almoco"),
        )
        val sorted = items
            .filter { it.dataHora in range }
            .sortedWith(compareBy({ it.dataHora }, { it.titulo }))
        assertEquals(listOf("Cafe", "Almoco", "Zebra"), sorted.map { it.titulo })
    }
}
