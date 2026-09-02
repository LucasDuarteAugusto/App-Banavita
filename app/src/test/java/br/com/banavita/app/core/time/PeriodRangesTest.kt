package br.com.banavita.app.core.time

import br.com.banavita.app.domain.model.PeriodoPreset
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneOffset

class PeriodRangesTest {
    @Test
    fun `hoje contem instante do mesmo dia`() {
        val noon = 1_700_000_000_000L
        val range = PeriodRanges.of(PeriodoPreset.HOJE, nowMillis = noon, zone = ZoneOffset.UTC)
        assertTrue(noon in range)
    }
}
