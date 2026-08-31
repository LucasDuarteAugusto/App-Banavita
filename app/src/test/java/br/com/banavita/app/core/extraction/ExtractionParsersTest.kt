package br.com.banavita.app.core.extraction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExtractionParsersTest {

    @Test
    fun `quantity e unidade em quilos`() {
        val text = TextNormalizer.normalize("vende tres quilos de banana")
        val hits = QuantityParser.findAll(text)
        assertTrue(hits.isNotEmpty())
        assertEquals(3.0, hits.first().quantity, 0.001)
        assertEquals("KG", hits.first().unit)
    }

    @Test
    fun `money a doze reais vira centavos`() {
        val text = TextNormalizer.normalize("banana a doze reais")
        val hit = MoneyParser.first(text)
        assertNotNull(hit)
        assertEquals(1200L, hit!!.cents)
    }

    @Test
    fun `phone extrai digitos`() {
        val hit = PhoneParser.find("cliente Joao telefone 11987654321")
        assertNotNull(hit)
        assertEquals("11987654321", hit!!.digits)
    }

    @Test
    fun `venda parser extrai produto cliente qtde e preco`() {
        val parsed = VendaExtractionParser.parse(
            "vende tres quilos de banana nanica pra Maria a doze reais",
        )
        assertEquals(3.0, parsed.item.quantidade.value!!, 0.001)
        assertEquals("KG", parsed.item.unidade.value)
        assertEquals(1200L, parsed.item.valorUnitarioCentavos.value)
        assertTrue(parsed.item.produtoRaw.value!!.contains("banana"))
        assertTrue(parsed.clienteRaw.value!!.contains("maria", ignoreCase = true))
    }

    @Test
    fun `compra parser exige preco no texto tipico`() {
        val parsed = CompraExtractionParser.parse(
            "comprei 10 caixas de banana do Sitio Verde a 8 reais",
        )
        assertEquals(10.0, parsed.item.quantidade.value!!, 0.001)
        assertEquals("CX", parsed.item.unidade.value)
        assertEquals(800L, parsed.item.valorUnitarioCentavos.value)
        assertTrue(parsed.item.produtoRaw.value!!.contains("banana"))
    }

    @Test
    fun `cliente parser nome e telefone`() {
        val parsed = ClienteExtractionParser.parse("cadastrar cliente Ana Souza 11999887766")
        assertTrue(parsed.nome.value!!.contains("Ana", ignoreCase = true) || parsed.nome.value!!.contains("ana"))
        assertEquals("11999887766", parsed.telefone.value)
    }

    @Test
    fun `unit dictionary resolve caixinha`() {
        assertEquals("CX", UnitDictionary.resolve("caixinha"))
        assertEquals("DZ", UnitDictionary.resolve("dúzia"))
    }
}
