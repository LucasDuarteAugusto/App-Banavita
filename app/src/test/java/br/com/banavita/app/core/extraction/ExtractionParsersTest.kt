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
        assertEquals("Ana Souza", parsed.nome.value)
        assertEquals("11999887766", parsed.telefone.value)
    }

    @Test
    fun `cliente parser groq ignora pontuacao e variante cadastrar`() {
        val parsed = ClienteExtractionParser.parse("Cadatrar cliente Alfredo. Telefone 11987654321.")
        assertEquals("Alfredo", parsed.nome.value)
        assertEquals("11987654321", parsed.telefone.value)
    }

    @Test
    fun `cliente parser tiny recupera telefone em digitos isolados`() {
        val parsed = ClienteExtractionParser.parse(
            "Cadê a extracta e a gente Alfredo, telefoni 11, 9, 8, 7, 6, 5, 4, 3, 2, 1.",
        )
        assertEquals("11987654321", parsed.telefone.value)
        val nome = parsed.nome.value.orEmpty()
        assertTrue(nome.contains("Alfredo", ignoreCase = true))
        assertTrue(nome.none { it.isDigit() })
        assertTrue(!nome.contains("telefoni", ignoreCase = true))
    }

    @Test
    fun `cliente parser preserva acentos e descarta pontuacao`() {
        val joao = ClienteExtractionParser.parse("cadastrar cliente João da Silva telefone 11987654321")
        assertEquals("João da Silva", joao.nome.value)
        assertEquals("11987654321", joao.telefone.value)

        val marcia = ClienteExtractionParser.parse("novo cliente Márcia.")
        assertEquals("Márcia", marcia.nome.value)
        assertEquals(null, marcia.telefone.value)
    }

    @Test
    fun `phone extrai formato parenteses e hifen`() {
        val hit = PhoneParser.find("cliente Joao telefone (11) 98765-4321")
        assertNotNull(hit)
        assertEquals("11987654321", hit!!.digits)
    }

    @Test
    fun `phone extrai digitos isolados por virgula`() {
        val hit = PhoneParser.find("telefoni 11, 9, 8, 7, 6, 5, 4, 3, 2, 1")
        assertNotNull(hit)
        assertEquals("11987654321", hit!!.digits)
    }

    @Test
    fun `fornecedor parser nome e telefone`() {
        val parsed = FornecedorExtractionParser.parse("cadastrar fornecedor Sitio Verde telefone 11987654321")
        assertEquals("Sitio Verde", parsed.nome.value)
        assertEquals("11987654321", parsed.telefone.value)
    }

    @Test
    fun `fornecedor parser groq ignora pontuacao e variante cadastrar`() {
        val parsed = FornecedorExtractionParser.parse("Cadatrar fornecedor Alfredo. Telefone 11987654321.")
        assertEquals("Alfredo", parsed.nome.value)
        assertEquals("11987654321", parsed.telefone.value)
    }

    @Test
    fun `fornecedor parser tiny recupera telefone em digitos isolados`() {
        val parsed = FornecedorExtractionParser.parse(
            "Cadê a extracta e a gente Alfredo, telefoni 11, 9, 8, 7, 6, 5, 4, 3, 2, 1.",
        )
        assertEquals("11987654321", parsed.telefone.value)
        val nome = parsed.nome.value.orEmpty()
        assertTrue(nome.contains("Alfredo", ignoreCase = true))
        assertTrue(nome.none { it.isDigit() })
        assertTrue(!nome.contains("telefoni", ignoreCase = true))
    }

    @Test
    fun `fornecedor parser preserva acentos e descarta pontuacao`() {
        val sitio = FornecedorExtractionParser.parse("cadastrar fornecedor Sítio Verde telefone 11987654321")
        assertEquals("Sítio Verde", sitio.nome.value)
        assertEquals("11987654321", sitio.telefone.value)

        val marcia = FornecedorExtractionParser.parse("novo fornecedor Márcia.")
        assertEquals("Márcia", marcia.nome.value)
        assertEquals(null, marcia.telefone.value)
    }

    @Test
    fun `consultas parser extrai modo vendas e periodo semana`() {
        val parsed = ConsultasExtractionParser.parse("vendas desta semana")
        assertEquals(br.com.banavita.app.domain.model.ConsultaModo.VENDAS, parsed.modo.value)
        assertEquals(br.com.banavita.app.domain.model.PeriodoPreset.SEMANA, parsed.periodo.value)
    }

    @Test
    fun `prazo parser reconhece fiado`() {
        val field = PrazoParser.parse("vende banana fiado")
        assertEquals(true, field.value)
        val parsed = VendaExtractionParser.parse("vende tres quilos de banana fiado")
        assertEquals(true, parsed.aPrazo.value)
    }

    @Test
    fun `prazo parser reconhece pago e nao pago com precisao`() {
        assertEquals(true, PrazoParser.parse("vende banana nao pagou").value)
        assertEquals(true, PrazoParser.parse("vende banana ficou devendo").value)
        assertEquals(true, PrazoParser.parse("vende banana a prazo").value)
        assertEquals(false, PrazoParser.parse("vende banana pago").value)
        assertEquals(false, PrazoParser.parse("vende banana pagou no pix").value)
        assertEquals(false, PrazoParser.parse("vende banana a vista").value)
    }

    @Test
    fun `venda parser limpa termos de pagamento do clienteRaw`() {
        val parsed = VendaExtractionParser.parse("vende 5 quilos de banana nanica pro Joao a vista")
        assertEquals("joao", parsed.clienteRaw.value)
        assertEquals(false, parsed.aPrazo.value)
    }

    @Test
    fun `compra parser limpa termos de pagamento do fornecedorRaw`() {
        val parsed = CompraExtractionParser.parse("comprei 10 caixas de banana do Sitio Verde a prazo a 10 reais")
        assertEquals("sitio verde", parsed.fornecedorRaw.value)
        assertEquals(true, parsed.aPrazo.value)
    }

    @Test
    fun `evento parser recorte hoje nao exige titulo`() {
        assertTrue(EventoExtractionParser.looksLikeFilterOnly("hoje"))
        val parsed = EventoExtractionParser.parse("hoje")
        assertEquals(br.com.banavita.app.domain.model.AgendaRecorte.HOJE, parsed.recorteLista.value)
    }

    @Test
    fun `inicio reconhece nova venda sem item`() {
        assertTrue(ConsultasExtractionParser.parse("nova venda", preferInicio = true).navigateNovaVenda)
        assertTrue(!InicioIntentParser.looksLikeVenda("nova venda"))
    }
}
