package br.com.banavita.app.core.extraction

import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.extraction.ExtractionField
import br.com.banavita.app.domain.model.extraction.FieldSource

data class ParsedLineItem(
    val produtoRaw: ExtractionField<String>,
    val quantidade: ExtractionField<Double>,
    val unidade: ExtractionField<String>,
    val valorUnitarioCentavos: ExtractionField<Long>,
    val estado: ExtractionField<MaturacaoEstado> = ExtractionField(null),
)

data class ParsedVendaSlots(
    val item: ParsedLineItem,
    val clienteRaw: ExtractionField<String>,
    val aPrazo: ExtractionField<Boolean> = ExtractionField(false, "à vista", 0.4f),
)

data class ParsedCompraSlots(
    val item: ParsedLineItem,
    val fornecedorRaw: ExtractionField<String>,
    val aPrazo: ExtractionField<Boolean> = ExtractionField(false, "à vista", 0.4f),
)

data class ParsedNomeTelefone(
    val nome: ExtractionField<String>,
    val telefone: ExtractionField<String>,
)

data class ParsedProdutoSlots(
    val descricao: ExtractionField<String>,
    val precoCentavos: ExtractionField<Long>,
    val unidade: ExtractionField<String>,
    val grupoRaw: ExtractionField<String>,
)

data class ParsedGrupoSlots(
    val nome: ExtractionField<String>,
    val observacao: ExtractionField<String>,
)

enum class LineItemRole {
    VENDA,
    COMPRA,
}

object DocumentLineParser {
    private val estadoMask = Regex("""(?i)\b(verdolenga|pintada|amarela|podre|verde)\b""")
    private val prazoMask = Regex(
        """(?i)\b(nao\s+(?:foi\s+)?pago|nao\s+pagou|sem\s+pagar|a\s*prazo|prazo|fiado|pendente|""" +
            """ficou\s+devendo|pra\s+acertar|para\s+acertar|ja\s+pago|ja\s+pagou|pago\s+agora|""" +
            """pagou\s+agora|pagou|pago|a\s*vista|avista|no\s+pix|em\s+dinheiro|no\s+dinheiro|""" +
            """no\s+cartao|no\s+debito|quitado)\b""",
    )
    private val unitMask = Regex("(?i)\\b(?:${UnitDictionary.patternAlternation})\\b")

    fun parseItem(normalized: String, role: LineItemRole): ParsedLineItem {
        val qtyHits = QuantityParser.findAll(normalized)
        val moneyHits = MoneyParser.findAll(normalized)
        val moneyRanges = moneyHits.map { it.start until it.end }

        val qtyHit = qtyHits.firstOrNull { hit ->
            hit.unit != null && moneyRanges.none { hit.start in it }
        } ?: qtyHits.firstOrNull { hit ->
            moneyRanges.none { hit.start in it }
        } ?: qtyHits.firstOrNull()

        val moneyHit = MoneyParser.first(normalized)
        val partnerMatch = when (role) {
            LineItemRole.VENDA -> RoleKeywords.clienteCue.find(normalized)
            LineItemRole.COMPRA -> RoleKeywords.fornecedorCue.find(normalized)
        }
        val estadoSource = partnerMatch?.let { normalized.substring(0, it.range.first) } ?: normalized
        val estado = MaturacaoEstadoParser.parse(estadoSource)
        val produtoRaw = extractProductName(normalized, qtyHit, moneyHit, partnerMatch)
        return ParsedLineItem(
            produtoRaw = produtoRaw,
            quantidade = QuantityParser.toField(qtyHit),
            unidade = QuantityParser.unitField(qtyHit),
            valorUnitarioCentavos = MoneyParser.toField(moneyHit),
            estado = estado,
        )
    }

    private fun extractProductName(
        normalized: String,
        qtyHit: QuantityHit?,
        moneyHit: MoneyHit?,
        partnerMatch: MatchResult?,
    ): ExtractionField<String> {
        var working = normalized
        qtyHit?.let { working = maskRange(working, it.start, it.end) }
        moneyHit?.let { working = maskRange(working, it.start, it.end) }
        partnerMatch?.let { working = maskRange(working, it.range.first, it.range.last + 1) }
        working = working
            .replace(estadoMask, " ")
            .replace(prazoMask, " ")
            .replace(unitMask, " ")
            .replace(Regex("(?i)\\b(reais?|real|r)\\b"), " ")
        val tokens = working.split(" ").filter { token ->
            token.isNotBlank() &&
                !SpokenLeadIn.isJunkToken(token) &&
                !RoleKeywords.isStopWord(token) &&
                !token.matches(Regex("\\d+([.,]\\d+)?"))
        }
        val name = tokens.take(4).joinToString(" ")
        if (name.isBlank()) {
            return ExtractionField(null, null, 0f)
        }
        return ExtractionField(name, name, 0.8f, FieldSource.HEURISTIC)
    }
}

object MaturacaoEstadoParser {
    fun parse(normalized: String): ExtractionField<MaturacaoEstado> {
        val match = when {
            Regex("""\bverdolenga\b""").containsMatchIn(normalized) -> MaturacaoEstado.VERDOLENGA
            Regex("""\bpintada\b""").containsMatchIn(normalized) -> MaturacaoEstado.PINTADA
            Regex("""\bamarela\b""").containsMatchIn(normalized) -> MaturacaoEstado.AMARELA
            Regex("""\bpodre\b""").containsMatchIn(normalized) -> MaturacaoEstado.PODRE
            Regex("""\bverde\b""").containsMatchIn(normalized) -> MaturacaoEstado.VERDE
            else -> null
        }
        return ExtractionField(match, match?.name?.lowercase(), if (match != null) 0.9f else 0f)
    }
}

object VendaExtractionParser {
    fun parse(transcript: String): ParsedVendaSlots {
        val normalized = SpokenLeadIn.stripLeading(TextNormalizer.normalize(transcript))
        val item = DocumentLineParser.parseItem(normalized, LineItemRole.VENDA)
        val clienteCue = RoleKeywords.extractAfterCue(normalized, RoleKeywords.clienteCue)
        val cleaned = clienteCue?.let { RoleKeywords.cleanPartnerCandidate(it) }?.takeIf { it.isNotBlank() }
        val clienteRaw: ExtractionField<String> = if (!cleaned.isNullOrBlank()) {
            ExtractionField(cleaned, cleaned, 0.8f, FieldSource.HEURISTIC)
        } else {
            ExtractionField(null, null, 0f)
        }
        return ParsedVendaSlots(
            item = item,
            clienteRaw = clienteRaw,
            aPrazo = PrazoParser.parse(transcript),
        )
    }
}

object CompraExtractionParser {
    fun parse(transcript: String): ParsedCompraSlots {
        val normalized = SpokenLeadIn.stripLeading(TextNormalizer.normalize(transcript))
        val item = DocumentLineParser.parseItem(normalized, LineItemRole.COMPRA)
        val fornecedorCue = RoleKeywords.extractAfterCue(normalized, RoleKeywords.fornecedorCue)
        val cleaned = fornecedorCue?.let { RoleKeywords.cleanPartnerCandidate(it) }?.takeIf { it.isNotBlank() }
        val fornecedorRaw: ExtractionField<String> = if (!cleaned.isNullOrBlank()) {
            ExtractionField(cleaned, cleaned, 0.75f, FieldSource.HEURISTIC)
        } else {
            ExtractionField(null, null, 0f)
        }
        return ParsedCompraSlots(
            item = item,
            fornecedorRaw = fornecedorRaw,
            aPrazo = PrazoParser.parse(transcript),
        )
    }
}

object ClienteExtractionParser {
    fun parse(transcript: String): ParsedNomeTelefone =
        NomeTelefoneParser.parse(transcript, entityKeywords = setOf("cliente"))
}

object FornecedorExtractionParser {
    fun parse(transcript: String): ParsedNomeTelefone =
        NomeTelefoneParser.parse(transcript, entityKeywords = setOf("fornecedor"))
}

object ProdutoExtractionParser {
    private val grupoRegex = Regex("""(?i)\bgrupo\s+([a-z0-9][\w]*(?:\s+[a-z0-9][\w]*){0,2})""")

    fun parse(transcript: String): ParsedProdutoSlots {
        val normalized = SpokenLeadIn.stripLeading(TextNormalizer.normalize(transcript))
        val money = MoneyParser.first(normalized)
        val grupoMatch = grupoRegex.find(normalized)
        val grupoCue = grupoMatch?.groupValues?.getOrNull(1)
        val moneyRange = money?.let { it.start until it.end }

        val qtyWithUnit = QuantityParser.findAll(normalized).firstOrNull { hit ->
            hit.unit != null && (moneyRange == null || hit.start !in moneyRange)
        }

        var working = normalized
        money?.let { working = maskRange(working, it.start, it.end) }
        grupoMatch?.let { working = maskRange(working, it.range.first, it.range.last + 1) }
        qtyWithUnit?.let { working = maskRange(working, it.start, it.end) }

        val standaloneUnit = working.split(Regex("\\s+"))
            .firstNotNullOfOrNull { token ->
                val unit = UnitDictionary.resolve(token) ?: return@firstNotNullOfOrNull null
                token to unit
            }
        val unit = when {
            qtyWithUnit?.unit != null -> QuantityParser.unitField(qtyWithUnit)
            standaloneUnit != null ->
                ExtractionField(standaloneUnit.second, standaloneUnit.first, 0.9f, FieldSource.DICTIONARY)
            else -> ExtractionField("UN", null, 0.4f, FieldSource.DICTIONARY)
        }
        standaloneUnit?.let { working = working.replace(it.first, " ", ignoreCase = true) }

        val descricao = working.split(" ")
            .filter { token ->
                token.isNotBlank() &&
                    !SpokenLeadIn.isJunkToken(token) &&
                    !token.matches(Regex("\\d+([.,]\\d+)?")) &&
                    UnitDictionary.resolve(token) == null
            }
            .joinToString(" ")
            .trim()
            .takeIf { it.isNotBlank() }

        return ParsedProdutoSlots(
            descricao = ExtractionField(descricao, descricao, if (descricao != null) 0.75f else 0f),
            precoCentavos = MoneyParser.toField(money),
            unidade = unit,
            grupoRaw = ExtractionField(grupoCue, grupoCue, if (grupoCue != null) 0.7f else 0f),
        )
    }
}

object GrupoExtractionParser {
    fun parse(transcript: String): ParsedGrupoSlots {
        val normalized = SpokenLeadIn.stripLeading(TextNormalizer.normalize(transcript))
            .replace(Regex("(?i)\\b(grupo)\\b"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        val parts = normalized.split(Regex("\\bobs(?:ervacao|ervação)?\\b"), limit = 2)
        val nome = parts.getOrNull(0)?.trim()?.takeIf { it.isNotBlank() }
        val obs = parts.getOrNull(1)?.trim()?.takeIf { it.isNotBlank() }
        return ParsedGrupoSlots(
            nome = ExtractionField(nome, nome, if (nome != null) 0.8f else 0f),
            observacao = ExtractionField(obs, obs, if (obs != null) 0.7f else 0f),
        )
    }
}
