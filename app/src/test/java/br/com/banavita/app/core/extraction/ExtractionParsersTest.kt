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
    fun `money N reais sem a prefixo`() {
        val text = TextNormalizer.normalize("nanica 8 reais quilo")
        val hit = MoneyParser.first(text)
        assertNotNull(hit)
        assertEquals(800L, hit!!.cents)
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
            "comprei 10 caixas de banana do Sitio Alegre a 8 reais",
        )
        assertEquals(10.0, parsed.item.quantidade.value!!, 0.001)
        assertEquals("CX MADEIRA", parsed.item.unidade.value)
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
        val parsed = FornecedorExtractionParser.parse("cadastrar fornecedor Sitio Alegre telefone 11987654321")
        assertEquals("Sitio Alegre", parsed.nome.value)
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
        val sitio = FornecedorExtractionParser.parse("cadastrar fornecedor Sítio Alegre telefone 11987654321")
        assertEquals("Sítio Alegre", sitio.nome.value)
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
        val parsed = CompraExtractionParser.parse("comprei 10 caixas de banana do Sitio Alegre a prazo a 10 reais")
        assertEquals("sitio alegre", parsed.fornecedorRaw.value)
        assertEquals(true, parsed.aPrazo.value)
    }

    @Test
    fun `evento parser recorte hoje nao exige titulo`() {
        assertTrue(EventoExtractionParser.looksLikeFilterOnly("hoje"))
        val parsed = EventoExtractionParser.parse("hoje")
        assertEquals(br.com.banavita.app.domain.model.AgendaRecorte.HOJE, parsed.recorteLista.value)
        assertEquals(null, parsed.titulo.value)
    }

    @Test
    fun `evento parser recorte amanha seleciona o dia seguinte`() {
        assertTrue(EventoExtractionParser.looksLikeFilterOnly("amanhã"))
        val parsed = EventoExtractionParser.parse("amanha")
        assertEquals(br.com.banavita.app.domain.model.AgendaRecorte.AMANHA, parsed.recorteLista.value)
        assertEquals(null, parsed.titulo.value)
    }

    @Test
    fun `evento parser proximos 7 dias permanece visao de mes`() {
        assertTrue(EventoExtractionParser.looksLikeFilterOnly("próximos 7 dias"))
        val parsed = EventoExtractionParser.parse("esta semana")
        assertEquals(br.com.banavita.app.domain.model.AgendaRecorte.PROXIMOS_7_DIAS, parsed.recorteLista.value)
        assertEquals(null, parsed.titulo.value)
    }

    @Test
    fun `inicio reconhece nova venda sem item`() {
        assertTrue(ConsultasExtractionParser.parse("nova venda", preferInicio = true).navigateNovaVenda)
        assertTrue(!InicioIntentParser.looksLikeVenda("nova venda"))
    }

    @Test
    fun `cliente parser padrao curto nome e telefone`() {
        val parsed = ClienteExtractionParser.parse("Armando 14936586721")
        assertEquals("Armando", parsed.nome.value)
        assertEquals("14936586721", parsed.telefone.value)
    }

    @Test
    fun `cliente parser padrao curto nome composto e telefone`() {
        val parsed = ClienteExtractionParser.parse("João da Silva 11987654321")
        assertEquals("João da Silva", parsed.nome.value)
        assertEquals("11987654321", parsed.telefone.value)
    }

    @Test
    fun `cliente parser padrao curto so nome`() {
        val parsed = ClienteExtractionParser.parse("Armando")
        assertEquals("Armando", parsed.nome.value)
        assertEquals(null, parsed.telefone.value)
    }

    @Test
    fun `fornecedor parser padrao curto nome e telefone`() {
        val parsed = FornecedorExtractionParser.parse("Sítio Alegre 14936586721")
        assertEquals("Sítio Alegre", parsed.nome.value)
        assertEquals("14936586721", parsed.telefone.value)
    }

    @Test
    fun `venda parser padrao curto com estado preco e fiado`() {
        val parsed = VendaExtractionParser.parse(
            "20 quilos nanica amarela pra padaria 8 reais fiado",
        )
        assertEquals(20.0, parsed.item.quantidade.value!!, 0.001)
        assertEquals("KG", parsed.item.unidade.value)
        assertTrue(parsed.item.produtoRaw.value!!.contains("nanica", ignoreCase = true))
        assertEquals(br.com.banavita.app.domain.model.MaturacaoEstado.AMARELA, parsed.item.estado.value)
        assertTrue(parsed.clienteRaw.value!!.contains("padaria", ignoreCase = true))
        assertEquals(800L, parsed.item.valorUnitarioCentavos.value)
        assertEquals(true, parsed.aPrazo.value)
    }

    @Test
    fun `venda parser padrao minimo sem preco e sem estado`() {
        val parsed = VendaExtractionParser.parse("20 quilos nanica pra padaria")
        assertEquals(20.0, parsed.item.quantidade.value!!, 0.001)
        assertEquals("KG", parsed.item.unidade.value)
        assertTrue(parsed.item.produtoRaw.value!!.contains("nanica", ignoreCase = true))
        assertEquals(null, parsed.item.estado.value)
        assertTrue(parsed.clienteRaw.value!!.contains("padaria", ignoreCase = true))
        assertEquals(null, parsed.item.valorUnitarioCentavos.value)
        assertEquals(false, parsed.aPrazo.value)
    }

    @Test
    fun `inicio reconhece padrao curto de venda sem a palavra vende`() {
        assertTrue(InicioIntentParser.looksLikeVenda("20 quilos nanica pra padaria"))
        assertTrue(InicioIntentParser.looksLikeVenda("20 quilos nanica amarela pra padaria 8 reais fiado"))
        assertTrue(!InicioIntentParser.looksLikeVenda("estoque nanica"))
        assertTrue(!InicioIntentParser.looksLikeVenda("vendas semana"))
    }

    @Test
    fun `compra parser padrao curto`() {
        val parsed = CompraExtractionParser.parse("10 caixas nanica do Sitio Alegre 6 reais")
        assertEquals(10.0, parsed.item.quantidade.value!!, 0.001)
        assertEquals("CX MADEIRA", parsed.item.unidade.value)
        assertTrue(parsed.item.produtoRaw.value!!.contains("nanica", ignoreCase = true))
        assertTrue(parsed.fornecedorRaw.value!!.contains("sitio", ignoreCase = true))
        assertEquals(600L, parsed.item.valorUnitarioCentavos.value)
        assertEquals(null, parsed.item.estado.value)
        assertEquals(false, parsed.aPrazo.value)
    }

    @Test
    fun `produto parser padrao curto nome preco e unidade`() {
        val parsed = ProdutoExtractionParser.parse("Nanica 8 reais quilo")
        assertTrue(parsed.descricao.value!!.contains("nanica", ignoreCase = true))
        assertEquals(800L, parsed.precoCentavos.value)
        assertEquals("KG", parsed.unidade.value)
        assertEquals(null, parsed.grupoRaw.value)
    }

    @Test
    fun `produto parser padrao curto com grupo`() {
        val parsed = ProdutoExtractionParser.parse("Nanica 8 reais quilo grupo banana")
        assertTrue(parsed.descricao.value!!.contains("nanica", ignoreCase = true))
        assertTrue(!parsed.descricao.value!!.contains("grupo", ignoreCase = true))
        assertEquals(800L, parsed.precoCentavos.value)
        assertEquals("KG", parsed.unidade.value)
        assertTrue(parsed.grupoRaw.value!!.contains("banana", ignoreCase = true))
    }

    @Test
    fun `grupo parser padrao curto com obs`() {
        val soNome = GrupoExtractionParser.parse("Banana")
        assertEquals("banana", soNome.nome.value)
        assertEquals(null, soNome.observacao.value)

        val comObs = GrupoExtractionParser.parse("Banana obs frutas")
        assertEquals("banana", comObs.nome.value)
        assertEquals("frutas", comObs.observacao.value)
    }

    @Test
    fun `consultas parser frases curtas`() {
        val estoque = ConsultasExtractionParser.parse("estoque nanica")
        assertEquals(br.com.banavita.app.domain.model.ConsultaModo.ESTOQUE, estoque.modo.value)
        assertTrue(estoque.busca.value!!.contains("nanica", ignoreCase = true))

        val vendas = ConsultasExtractionParser.parse("vendas semana")
        assertEquals(br.com.banavita.app.domain.model.ConsultaModo.VENDAS, vendas.modo.value)
        assertEquals(br.com.banavita.app.domain.model.PeriodoPreset.SEMANA, vendas.periodo.value)

        val previsao = ConsultasExtractionParser.parse("previsão")
        assertEquals(br.com.banavita.app.domain.model.ConsultaModo.PREVISAO, previsao.modo.value)

        val perdas = ConsultasExtractionParser.parse("perdas")
        assertEquals(br.com.banavita.app.domain.model.ConsultaModo.PERDAS, perdas.modo.value)

        val encomenda = ConsultasExtractionParser.parse("encomenda")
        assertEquals(br.com.banavita.app.domain.model.ConsultaModo.ENCOMENDAS, encomenda.modo.value)

        val criticos = ConsultasExtractionParser.parse("pintada")
        assertEquals(true, criticos.somenteCriticos.value)
    }

    @Test
    fun `baixa parser padrao curto sem a palavra receber`() {
        val parsed = BaixaExtractionParser.parse("50 reais da Maria")
        assertEquals(5000L, parsed.valorCentavos.value)
        assertTrue(parsed.parceiroRaw.value!!.contains("maria", ignoreCase = true))
        assertTrue(BaixaExtractionParser.isReceber("50 reais da Maria"))
        assertTrue(BaixaExtractionParser.isPagar("200 reais do Sitio Verde"))
        assertTrue(!BaixaExtractionParser.looksLikeBaixa("hoje"))
        assertTrue(!BaixaExtractionParser.looksLikeBaixa("Maria"))
    }

    @Test
    fun `evento parser padrao curto cria compromisso`() {
        val parsed = EventoExtractionParser.parse("visita João amanhã às 15")
        assertTrue(!EventoExtractionParser.looksLikeFilterOnly("visita João amanhã às 15"))
        assertTrue(parsed.titulo.value!!.contains("joao", ignoreCase = true))
        val zdt = java.time.Instant.ofEpochMilli(parsed.dataHoraMillis.value!!)
            .atZone(java.time.ZoneId.systemDefault())
        assertEquals(15, zdt.hour)
        assertEquals(0, zdt.minute)
        assertEquals(java.time.LocalDate.now(java.time.ZoneId.systemDefault()).plusDays(1), zdt.toLocalDate())
    }
}
