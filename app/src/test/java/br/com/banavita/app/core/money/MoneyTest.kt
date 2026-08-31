package br.com.banavita.app.core.money

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class MoneyTest {

    @Test
    fun `toCents arredonda meio centavo para cima`() {
        assertEquals(1050L, Money.toCents(BigDecimal("10.50")))
        assertEquals(1051L, Money.toCents(BigDecimal("10.505")))
    }

    @Test
    fun `lineTotal aplica arredondamento bancario`() {
        assertEquals(350L, Money.lineTotal(350L, 1.0))
        assertEquals(175L, Money.lineTotal(350L, 0.5))
        assertEquals(1050L, Money.lineTotal(350L, 3.0))
    }

    @Test
    fun `fromCents converte de volta`() {
        assertEquals(BigDecimal("10.50"), Money.fromCents(1050L))
    }
}
