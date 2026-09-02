package br.com.banavita.app.core.extraction

import br.com.banavita.app.domain.model.extraction.ExtractionField
import br.com.banavita.app.domain.model.extraction.FieldSource

data class ParsedLineItem(
    val produtoRaw: ExtractionField<String>,
    val quantidade: ExtractionField<Double>,
    val unidade: ExtractionField<String>,
    val valorUnitarioCentavos: ExtractionField<Long>,
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

object DocumentLineParser {
    fun parseItem(normalized: String, original: String): ParsedLineItem {
        val qtyHits = QuantityParser.findAll(normalized)
        // Prefer quantity that has an explicit unit, else first positive number that isn't money-like
        val moneyHits = MoneyParser.findAll(normalized)
        val moneyRanges = moneyHits.map { it.start until it.end }

        val qtyHit = qtyHits.firstOrNull { hit ->
            moneyRanges.none { hit.start in it }
        } ?: qtyHits.firstOrNull()

        val moneyHit = MoneyParser.first(normalized)

        val produtoRaw = extractProductName(normalized, original, qtyHit, moneyHit)
        return ParsedLineItem(
            produtoRaw = produtoRaw,
            quantidade = QuantityParser.toField(qtyHit),
            unidade = QuantityParser.unitField(qtyHit),
            valorUnitarioCentavos = MoneyParser.toField(moneyHit),
        )
    }

    private fun extractProductName(
        normalized: String,
        original: String,
        qtyHit: QuantityHit?,
        moneyHit: MoneyHit?,
    ): ExtractionField<String> {
        val fromDe = RoleKeywords.extractAfterCue(normalized, RoleKeywords.produtoDeCue)
        if (!fromDe.isNullOrBlank()) {
            val cleaned = cleanProductCandidate(fromDe, qtyHit, moneyHit)
            if (cleaned.isNotBlank()) {
                return ExtractionField(cleaned, cleaned, 0.8f, FieldSource.HEURISTIC)
            }
        }

        var working = normalized
        qtyHit?.let { working = working.replaceRange(it.start, it.end, " ") }
        moneyHit?.let {
            if (it.start < working.length) {
                val end = minOf(it.end, working.length)
                val start = minOf(it.start, end)
                working = working.replaceRange(start, end, " ")
            }
        }
        working = working
            .replace(Regex("(?i)\\b(vende|venda|vender|vendeu|compra|comprar|comprei|comprou|a|por|reais|real|r)\\b"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        // Remove cliente/fornecedor cue tails
        working = working.replace(RoleKeywords.clienteCue, " ")
        working = working.replace(RoleKeywords.fornecedorCue, " ")
        working = working.replace(Regex("\\s+"), " ").trim()

        val tokens = working.split(" ").filter { it.isNotBlank() && !RoleKeywords.isStopWord(it) && !it.matches(Regex("\\d+([.,]\\d+)?")) }
        val name = tokens.take(4).joinToString(" ")
        if (name.isBlank()) {
            return ExtractionField(null, null, 0f)
        }
        return ExtractionField(name, name, 0.65f, FieldSource.HEURISTIC)
    }

    private fun cleanProductCandidate(
        candidate: String,
        qtyHit: QuantityHit?,
        moneyHit: MoneyHit?,
    ): String {
        var text = candidate
        qtyHit?.rawSpan?.let { text = text.replace(it, " ", ignoreCase = true) }
        moneyHit?.rawSpan?.let { text = text.replace(it, " ", ignoreCase = true) }
        text = text.replace(Regex("(?i)\\b(reais?|real)\\b"), " ")
        return text.split(" ")
            .filter { it.isNotBlank() && !RoleKeywords.isStopWord(it) && !it.matches(Regex("\\d+([.,]\\d+)?")) }
            .take(4)
            .joinToString(" ")
            .trim()
    }
}

object VendaExtractionParser {
    fun parse(transcript: String): ParsedVendaSlots {
        val normalized = TextNormalizer.normalize(transcript)
        val item = DocumentLineParser.parseItem(normalized, transcript)
        val clienteCue = RoleKeywords.extractAfterCue(normalized, RoleKeywords.clienteCue)
        val clienteRaw: ExtractionField<String> = if (!clienteCue.isNullOrBlank()) {
            ExtractionField(clienteCue, clienteCue, 0.8f, FieldSource.HEURISTIC)
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
        val normalized = TextNormalizer.normalize(transcript)
        val item = DocumentLineParser.parseItem(normalized, transcript)
        val fornecedorCue = RoleKeywords.extractAfterCue(normalized, RoleKeywords.fornecedorCue)
        val fornecedorRaw: ExtractionField<String> = if (!fornecedorCue.isNullOrBlank()) {
            ExtractionField(fornecedorCue, fornecedorCue, 0.75f, FieldSource.HEURISTIC)
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
    fun parse(transcript: String): ParsedNomeTelefone {
        val phone = PhoneParser.find(transcript)
        var nomeText = transcript
        phone?.rawSpan?.let { nomeText = nomeText.replace(it, " ") }
        nomeText = TextNormalizer.normalize(nomeText)
            .replace(Regex("(?i)\\b(cliente|cadastrar|cadastro|novo|telefone|fone|whatsapp)\\b"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        val nome = nomeText.takeIf { it.isNotBlank() }?.replaceFirstChar { it.uppercase() }
        return ParsedNomeTelefone(
            nome = ExtractionField(
                value = nome,
                rawSpan = nome,
                confidence = if (nome != null) 0.8f else 0f,
                source = FieldSource.HEURISTIC,
            ),
            telefone = PhoneParser.toField(phone),
        )
    }
}

object FornecedorExtractionParser {
    fun parse(transcript: String): ParsedNomeTelefone {
        val phone = PhoneParser.find(transcript)
        var nomeText = transcript
        phone?.rawSpan?.let { nomeText = nomeText.replace(it, " ") }
        nomeText = TextNormalizer.normalize(nomeText)
            .replace(Regex("(?i)\\b(fornecedor|cadastrar|cadastro|novo|telefone|fone)\\b"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        val nome = nomeText.takeIf { it.isNotBlank() }?.replaceFirstChar { it.uppercase() }
        return ParsedNomeTelefone(
            nome = ExtractionField(
                value = nome,
                rawSpan = nome,
                confidence = if (nome != null) 0.8f else 0f,
                source = FieldSource.HEURISTIC,
            ),
            telefone = PhoneParser.toField(phone),
        )
    }
}

object ProdutoExtractionParser {
    fun parse(transcript: String): ParsedProdutoSlots {
        val normalized = TextNormalizer.normalize(transcript)
        val money = MoneyParser.first(normalized)
        val qty = QuantityParser.findAll(normalized).firstOrNull { it.unit != null }
        val unit = QuantityParser.unitField(qty).let {
            if (it.confidence >= 0.7f) it
            else ExtractionField("UN", null, 0.4f, FieldSource.DICTIONARY)
        }

        var desc = normalized
        money?.let { desc = desc.replaceRange(it.start, minOf(it.end, desc.length), " ") }
        qty?.let {
            if (it.start < desc.length) {
                desc = desc.replaceRange(it.start, minOf(it.end, desc.length), " ")
            }
        }
        desc = desc
            .replace(Regex("(?i)\\b(produto|cadastrar|cadastro|novo|preco|preço|grupo)\\b"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        val descricao = desc.takeIf { it.isNotBlank() }

        val grupoCue = Regex("""(?i)\bgrupo\s+([a-z0-9][\w]*(?:\s+[a-z0-9][\w]*){0,2})""")
            .find(normalized)?.groupValues?.getOrNull(1)

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
        val normalized = TextNormalizer.normalize(transcript)
            .replace(Regex("(?i)\\b(grupo|cadastrar|cadastro|novo)\\b"), " ")
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
