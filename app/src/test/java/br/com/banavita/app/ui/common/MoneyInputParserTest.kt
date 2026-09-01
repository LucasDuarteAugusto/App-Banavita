package br.com.banavita.app.core.money

import br.com.banavita.app.ui.common.MoneyInputParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyInputParserTest {

    @Test
    fun `parse aceita formato brasileiro`() {
        assertEquals(4500L, MoneyInputParser.parseToCentavos("45,00"))
        assertEquals(650L, MoneyInputParser.parseToCentavos("6,50"))
        assertEquals(2890L, MoneyInputParser.parseToCentavos("28,90"))
    }

    @Test
    fun `parse aceita formato com milhar`() {
        assertEquals(245000L, MoneyInputParser.parseToCentavos("2.450,00"))
    }

    @Test
    fun `parse retorna null para vazio ou invalido`() {
        assertNull(MoneyInputParser.parseToCentavos(""))
        assertNull(MoneyInputParser.parseToCentavos("abc"))
    }
}
