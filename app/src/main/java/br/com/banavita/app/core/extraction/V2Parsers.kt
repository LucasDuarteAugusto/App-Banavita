package br.com.banavita.app.core.extraction

import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.domain.model.AgendaRecorte
import br.com.banavita.app.domain.model.ConsultaModo
import br.com.banavita.app.domain.model.ConsultaVisao
import br.com.banavita.app.domain.model.PeriodoPreset
import br.com.banavita.app.domain.model.extraction.ExtractionField
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object PrazoParser {
    // Expressões que indicam que NÃO foi pago (a prazo / fiado / pendente)
    private val naoPago = Regex(
        """(?i)\b(nao\s+(?:foi\s+)?pago|nao\s+pagou|sem\s+pagar|a\s*prazo|prazo|fiado|pendente|ficou\s+devendo|pra\s+acertar|para\s+acertar|deve|devendo|anota\s+ai|anotar)\b""",
    )

    // Expressões que indicam que FOI pago (à vista / quitado / dinheiro / pix)
    private val pago = Regex(
        """(?i)\b(ja\s+pago|ja\s+pagou|pago\s+agora|pagou\s+agora|pagou|pago|a\s*vista|avista|no\s+pix|em\s+dinheiro|no\s+dinheiro|no\s+cartao|no\s+debito|quitado)\b""",
    )

    fun parse(transcript: String): ExtractionField<Boolean> {
        val text = TextNormalizer.normalize(transcript)
        return when {
            naoPago.containsMatchIn(text) -> ExtractionField(true, "a prazo", 0.9f)
            pago.containsMatchIn(text) -> ExtractionField(false, "à vista", 0.9f)
            else -> ExtractionField(false, "à vista", 0.4f)
        }
    }
}

data class ParsedConsultasSlots(
    val modo: ExtractionField<ConsultaModo>,
    val busca: ExtractionField<String>,
    val periodo: ExtractionField<PeriodoPreset>,
    val visao: ExtractionField<ConsultaVisao>,
    val somenteZerados: ExtractionField<Boolean>,
    val somenteCriticos: ExtractionField<Boolean> = ExtractionField(false),
    val navigateNovaVenda: Boolean,
    val navigateAgenda: Boolean,
)

object ConsultasExtractionParser {
    private val modoEstoque = Regex("""(?i)\b(estoque|saldo)\b""")
    private val modoVendas = Regex("""(?i)\b(vendas?|vendi|vendeu)\b""")
    private val modoCompras = Regex("""(?i)\b(compras?|comprei|comprou)\b""")
    private val modoReceber = Regex("""(?i)\b(receber|a receber|me deve|devendo|divida|dívida)\b""")
    private val modoPagar = Regex("""(?i)\b(pagar|a pagar|devemos|conta a pagar)\b""")
    private val modoPrevisao = Regex("""(?i)\b(previsao|previsão|quinta|oferta)\b""")
    private val modoPerdas = Regex("""(?i)\b(perdas?|apodreceu|podre)\b""")
    private val modoEncomendas = Regex("""(?i)\b(encomendas?|pedido)\b""")
    private val zerado = Regex("""(?i)\b(zerado|zerados|acabando|acabou|sem saldo)\b""")
    private val criticos = Regex("""(?i)\b(pintada|critico|crítico|pereciveis|perecíveis)\b""")
    private val visaoParceiro = Regex("""(?i)\b(por cliente|por fornecedor|quem)\b""")
    private val visaoProduto = Regex("""(?i)\b(por produto|o que mais|que mais)\b""")
    private val hoje = Regex("""(?i)\bhoje\b""")
    private val semana = Regex("""(?i)\b(esta semana|essa semana|semana)\b""")
    private val mes = Regex("""(?i)\b(este mes|esse mes|este mês|esse mês|mes|mês)\b""")
    private val novaVenda = Regex("""(?i)\b(nova venda|registrar venda)\b""")
    private val agenda = Regex("""(?i)\b(agenda|compromisso|evento|lembrar)\b""")

    private val stop = Regex(
        """(?i)\b(estoque|vendas?|compras?|receber|pagar|hoje|semana|mes|mês|zerado|zerados|acabando|""" +
            """consulta|consultas|mostrar|ver|quanto|quem|por|cliente|fornecedor|produto|""" +
            """esta|essa|este|esse|a|o|de|do|da|me|deve|devendo)\b""",
    )

    fun parse(transcript: String, preferInicio: Boolean = false): ParsedConsultasSlots {
        val normalized = TextNormalizer.normalize(transcript)
        val modo = when {
            modoReceber.containsMatchIn(normalized) -> ConsultaModo.RECEBER
            modoPagar.containsMatchIn(normalized) -> ConsultaModo.PAGAR
            modoPrevisao.containsMatchIn(normalized) -> ConsultaModo.PREVISAO
            modoEncomendas.containsMatchIn(normalized) -> ConsultaModo.ENCOMENDAS
            modoPerdas.containsMatchIn(normalized) -> ConsultaModo.PERDAS
            modoEstoque.containsMatchIn(normalized) -> ConsultaModo.ESTOQUE
            modoVendas.containsMatchIn(normalized) -> ConsultaModo.VENDAS
            modoCompras.containsMatchIn(normalized) -> ConsultaModo.COMPRAS
            else -> null
        }
        val periodo = when {
            hoje.containsMatchIn(normalized) -> PeriodoPreset.HOJE
            semana.containsMatchIn(normalized) -> PeriodoPreset.SEMANA
            mes.containsMatchIn(normalized) -> PeriodoPreset.MES
            else -> null
        }
        val visao = when {
            visaoParceiro.containsMatchIn(normalized) -> ConsultaVisao.POR_PARCEIRO
            visaoProduto.containsMatchIn(normalized) -> ConsultaVisao.POR_PRODUTO
            else -> null
        }
        val busca = normalized
            .replace(stop, " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .takeIf { it.length >= 2 }

        return ParsedConsultasSlots(
            modo = ExtractionField(modo, modo?.name?.lowercase(), if (modo != null) 0.85f else 0f),
            busca = ExtractionField(busca, busca, if (busca != null) 0.7f else 0f),
            periodo = ExtractionField(periodo, periodo?.name?.lowercase(), if (periodo != null) 0.8f else 0f),
            visao = ExtractionField(visao, visao?.name?.lowercase(), if (visao != null) 0.75f else 0f),
            somenteZerados = ExtractionField(
                zerado.containsMatchIn(normalized),
                if (zerado.containsMatchIn(normalized)) "zerado" else null,
                if (zerado.containsMatchIn(normalized)) 0.85f else 0.3f,
            ),
            somenteCriticos = ExtractionField(
                criticos.containsMatchIn(normalized),
                if (criticos.containsMatchIn(normalized)) "criticos" else null,
                if (criticos.containsMatchIn(normalized)) 0.85f else 0.3f,
            ),
            navigateNovaVenda = preferInicio && novaVenda.containsMatchIn(normalized),
            navigateAgenda = preferInicio && agenda.containsMatchIn(normalized),
        )
    }
}

data class ParsedBaixaSlots(
    val valorCentavos: ExtractionField<Long>,
    val parceiroRaw: ExtractionField<String>,
)

object BaixaExtractionParser {
    private val receberCue = Regex("""(?i)\b(?:receber|recebi|recebe)\b""")
    private val pagarCue = Regex("""(?i)\b(?:pagar|paguei|paga)\b""")
    private val parceiroCue = Regex(
        """(?i)\b(?:do|da|de|ao|a|pro|pra|para|cliente|fornecedor)\s+([a-zà-ú][\wà-ú]*(?:\s+[a-zà-ú][\wà-ú]*){0,2})""",
    )

    fun looksLikeBaixa(transcript: String): Boolean {
        val n = TextNormalizer.normalize(transcript)
        if (MoneyParser.first(n) != null) return true
        val hasVerb = receberCue.containsMatchIn(n) || pagarCue.containsMatchIn(n)
        return hasVerb && Regex("""\d+""").containsMatchIn(n)
    }

    fun isReceber(transcript: String): Boolean = looksLikeBaixa(transcript)

    fun isPagar(transcript: String): Boolean = looksLikeBaixa(transcript)

    fun parse(transcript: String): ParsedBaixaSlots {
        val normalized = TextNormalizer.normalize(transcript)
        val money = MoneyParser.first(normalized) ?: firstBareAmount(normalized)
        val parceiro = RoleKeywords.extractAfterCue(normalized, parceiroCue)
            ?: RoleKeywords.extractAfterCue(normalized, RoleKeywords.clienteCue)
        return ParsedBaixaSlots(
            valorCentavos = MoneyParser.toField(money),
            parceiroRaw = ExtractionField(parceiro, parceiro, if (parceiro != null) 0.8f else 0f),
        )
    }

    private fun firstBareAmount(normalized: String): MoneyHit? {
        val hit = QuantityParser.findAll(normalized).firstOrNull() ?: return null
        val cents = br.com.banavita.app.core.money.Money.toCents(java.math.BigDecimal.valueOf(hit.quantity))
        return MoneyHit(cents, hit.rawSpan, hit.start, hit.end)
    }
}

data class ParsedEventoSlots(
    val titulo: ExtractionField<String>,
    val dataHoraMillis: ExtractionField<Long>,
    val observacao: ExtractionField<String>,
    val clienteRaw: ExtractionField<String>,
    val fornecedorRaw: ExtractionField<String>,
    val notificar: ExtractionField<Boolean>,
    val antecipacaoMinutos: ExtractionField<Int>,
    val recorteLista: ExtractionField<AgendaRecorte>,
)

object EventoExtractionParser {
    private val recorteHoje = Regex("""(?i)\bhoje\b""")
    private val recorte7 = Regex("""(?i)\b(proximos|próximos|sete dias|7 dias|semana)\b""")
    private val amanha = Regex("""(?i)\bamanha\b""")
    private val meAvisa = Regex("""(?i)\b(me avisa|avisar|lembrar|lembrete|notifica)\b""")
    private val quinzeMin = Regex("""(?i)\b(15|quinze)\s*(min|minutos?)?\b""")
    private val umaHora = Regex("""(?i)\b(1|uma)\s*(h|hora)s?\b""")
    private val umDia = Regex("""(?i)\b(1|um)\s*dia\b""")
    private val horaCue = Regex("""(?i)\b(?:as|às)\s+(\d{1,2})(?:\s*(?:e|:)\s*(\d{2}))?\b""")
    private val horaAlt = Regex("""\b(\d{1,2})h(\d{2})?\b""")
    private val stop = Regex(
        """(?i)\b(lembrar|lembrete|agenda|evento|compromisso|visita|me avisa|avisar|notifica|""" +
            """amanha|hoje|as|às|horas?|minutos?|antes|criar|novo)\b""",
    )

    fun looksLikeFilterOnly(transcript: String): Boolean {
        val n = TextNormalizer.normalize(transcript)
        val hasCreateCue = Regex("""(?i)\b(lembrar|criar|novo|visita|compromisso)\b""").containsMatchIn(n)
        return !hasCreateCue && (
            recorteHoje.containsMatchIn(n) ||
                recorte7.containsMatchIn(n) ||
                amanha.containsMatchIn(n)
            )
    }

    fun parse(transcript: String, nowMillis: Long = nowEpochMillis(), zone: ZoneId = ZoneId.systemDefault()): ParsedEventoSlots {
        val normalized = TextNormalizer.normalize(transcript)
        val today = java.time.Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val date = when {
            amanha.containsMatchIn(normalized) -> today.plusDays(1)
            recorteHoje.containsMatchIn(normalized) -> today
            else -> today
        }
        val time = parseTime(normalized) ?: LocalTime.of(9, 0)
        val millis = PeriodRanges.atTime(date, time, zone)
        val cliente = RoleKeywords.extractAfterCue(normalized, RoleKeywords.clienteCue)
        val titulo = normalized
            .replace(horaCue, " ")
            .replace(horaAlt, " ")
            .replace(stop, " ")
            .replace(Regex("\\s+"), " ")
            .trim()
            .takeIf { it.isNotBlank() }
        val recorte = when {
            looksLikeFilterOnly(transcript) && recorte7.containsMatchIn(normalized) -> AgendaRecorte.PROXIMOS_7_DIAS
            looksLikeFilterOnly(transcript) && amanha.containsMatchIn(normalized) -> AgendaRecorte.AMANHA
            looksLikeFilterOnly(transcript) && recorteHoje.containsMatchIn(normalized) -> AgendaRecorte.HOJE
            else -> null
        }
        val tituloFiltro = if (recorte != null) null else titulo
        val antecipacao = when {
            quinzeMin.containsMatchIn(normalized) -> 15
            umaHora.containsMatchIn(normalized) -> 60
            umDia.containsMatchIn(normalized) -> 1440
            else -> null
        }
        val notificar = meAvisa.containsMatchIn(normalized) || antecipacao != null
        return ParsedEventoSlots(
            titulo = ExtractionField(tituloFiltro, tituloFiltro, if (tituloFiltro != null) 0.75f else 0f),
            dataHoraMillis = ExtractionField(millis, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(
                java.time.Instant.ofEpochMilli(millis).atZone(zone),
            ), 0.7f),
            observacao = ExtractionField(null, null, 0f),
            clienteRaw = ExtractionField(cliente, cliente, if (cliente != null) 0.7f else 0f),
            fornecedorRaw = ExtractionField(null, null, 0f),
            notificar = ExtractionField(notificar, if (notificar) "sim" else "não", if (notificar) 0.8f else 0.4f),
            antecipacaoMinutos = ExtractionField(antecipacao, antecipacao?.toString(), if (antecipacao != null) 0.8f else 0f),
            recorteLista = ExtractionField(recorte, recorte?.name, if (recorte != null) 0.8f else 0f),
        )
    }

    private fun parseTime(normalized: String): LocalTime? {
        horaCue.find(normalized)?.let { match ->
            val hour = match.groupValues[1].toIntOrNull() ?: return@let null
            val minute = match.groupValues.getOrNull(2)?.toIntOrNull() ?: 0
            if (hour in 0..23 && minute in 0..59) return LocalTime.of(hour, minute)
        }
        horaAlt.find(normalized)?.let { match ->
            val hour = match.groupValues[1].toIntOrNull() ?: return@let null
            val minute = match.groupValues.getOrNull(2)?.toIntOrNull() ?: 0
            if (hour in 0..23 && minute in 0..59) return LocalTime.of(hour, minute)
        }
        return null
    }
}

object InicioIntentParser {
    private val vendaVerb = Regex("""(?i)\b(vende|vender|vendi|vendeu)\b""")
    private val novaVendaOnly = Regex("""(?i)\b(nova venda|registrar venda)\b""")
    private val consultaCue = Regex("""(?i)\b(estoque|saldo|consulta|zerado)\b""")
    private val praCue = Regex("""(?i)\b(pra|para|pro)\b""")

    fun looksLikeVenda(transcript: String): Boolean {
        val n = TextNormalizer.normalize(transcript)
        val qtyHits = QuantityParser.findAll(n)
        val qtyWithUnit = qtyHits.any { it.unit != null }
        if (novaVendaOnly.containsMatchIn(n) && !qtyWithUnit) {
            return false
        }
        if (consultaCue.containsMatchIn(n) && !praCue.containsMatchIn(n) && !vendaVerb.containsMatchIn(n)) {
            return false
        }
        if (qtyWithUnit && praCue.containsMatchIn(n)) {
            return true
        }
        if (qtyWithUnit) {
            val product = VendaExtractionParser.parse(transcript).item.produtoRaw.value
            if (!product.isNullOrBlank()) return true
        }
        return vendaVerb.containsMatchIn(n) &&
            (qtyHits.isNotEmpty() || n.contains("de "))
    }

    fun looksLikeEvento(transcript: String): Boolean {
        val n = TextNormalizer.normalize(transcript)
        return Regex("""(?i)\b(lembrar|agenda|compromisso|evento|visita)\b""").containsMatchIn(n)
    }
}
