package br.com.banavita.app.core.extraction

import br.com.banavita.app.core.money.Money
import br.com.banavita.app.domain.model.extraction.ExtractionField
import br.com.banavita.app.domain.model.extraction.FieldSource
import java.math.BigDecimal

data class MoneyHit(
    val cents: Long,
    val rawSpan: String,
    val start: Int,
    val end: Int,
)

object MoneyParser {
    private val currencyRegex = Regex(
        """(?i)(?:r\$\s*)?(\d+(?:[.,]\d{1,2})?)\s*(?:reais?|real)?\b""",
    )
    private val aPrecoRegex = Regex(
        """(?i)\b(?:a|por|custa|custo)\s+(?:r\$\s*)?(\d+(?:[.,]\d{1,2})?)\b""",
    )

    fun findAll(normalizedText: String): List<MoneyHit> {
        val hits = mutableListOf<MoneyHit>()
        aPrecoRegex.findAll(normalizedText).forEach { match ->
            parseAmount(match.groupValues[1])?.let { cents ->
                hits += MoneyHit(cents, match.value.trim(), match.range.first, match.range.last + 1)
            }
        }
        if (hits.isEmpty()) {
            currencyRegex.findAll(normalizedText).forEach { match ->
                val hasMoneyCue = match.value.contains("r$", ignoreCase = true) ||
                    match.value.contains("real", ignoreCase = true)
                if (!hasMoneyCue && match.groupValues[1].toDoubleOrNull()?.let { it < 100 } == true) {
                    // skip bare small numbers — likely quantities
                    return@forEach
                }
                parseAmount(match.groupValues[1])?.let { cents ->
                    hits += MoneyHit(cents, match.value.trim(), match.range.first, match.range.last + 1)
                }
            }
        }
        return hits.distinctBy { it.start }
    }

    fun first(normalizedText: String): MoneyHit? = findAll(normalizedText).firstOrNull()

    fun toField(hit: MoneyHit?): ExtractionField<Long> {
        if (hit == null) return ExtractionField(value = null, confidence = 0f)
        return ExtractionField(
            value = hit.cents,
            rawSpan = hit.rawSpan,
            confidence = 0.85f,
            source = FieldSource.HEURISTIC,
        )
    }

    private fun parseAmount(raw: String): Long? {
        val normalized = raw.replace(',', '.')
        val value = normalized.toBigDecimalOrNull() ?: return null
        if (value <= BigDecimal.ZERO) return null
        return Money.toCents(value)
    }
}
