package br.com.banavita.app.core.time

import br.com.banavita.app.domain.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class DashboardPeriodsTest {
    private val today = LocalDate.of(2026, 10, 9)

    @Test fun `sete dias anteriores incluem hoje`() {
        assertEquals(today.minusDays(6) to today.plusDays(1), DashboardPeriods.dates(DashboardKind.VENDAS, DashboardFilter(period = DashboardPeriod.DIAS_7), today))
    }

    @Test fun `sete dias futuros incluem hoje`() {
        assertEquals(today to today.plusDays(7), DashboardPeriods.dates(DashboardKind.FINANCEIRO, DashboardFilter(period = DashboardPeriod.DIAS_7), today))
    }

    @Test fun `fim de mes respeita calendario e fuso`() {
        val dates = DashboardPeriods.dates(DashboardKind.COMPRAS, DashboardFilter(period = DashboardPeriod.MES), today)
        val range = DashboardPeriods.range(dates, ZoneId.of("America/Sao_Paulo"))
        assertTrue(PeriodRanges.startOfDay(LocalDate.of(2026, 10, 31), ZoneId.of("America/Sao_Paulo")) in range)
        assertFalse(range.endExclusive in range)
    }

    @Test fun `sem base anterior nao inventa crescimento percentual`() {
        assertNull(DashboardPeriods.comparison(100, 0))
        assertEquals(-50.0, DashboardPeriods.comparison(50, 100)!!, 0.001)
    }

    @Test(expected = IllegalArgumentException::class) fun `intervalo invertido e recusado`() {
        DashboardPeriods.dates(DashboardKind.VENDAS, DashboardFilter(period = DashboardPeriod.PERSONALIZADO, customStart = today, customEnd = today.minusDays(1)), today)
    }
}
