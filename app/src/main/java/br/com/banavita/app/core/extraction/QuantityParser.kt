package br.com.banavita.app.core.extraction

import br.com.banavita.app.domain.model.extraction.ExtractionField
import br.com.banavita.app.domain.model.extraction.FieldSource

data class QuantityHit(
    val quantity: Double,
    val unit: String?,
    val rawSpan: String,
    val start: Int,
    val end: Int,
)

object QuantityParser {
    private val regex = Regex(
        """(?i)(\d+(?:[.,]\d+)?)\s*(${UnitDictionary.patternAlternation})?\b""",
    )

    fun findAll(normalizedText: String): List<QuantityHit> =
        regex.findAll(normalizedText).mapNotNull { match ->
            val numberRaw = match.groupValues[1].replace(',', '.')
            val qty = numberRaw.toDoubleOrNull() ?: return@mapNotNull null
            if (qty <= 0) return@mapNotNull null
            val unitRaw = match.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() }
            QuantityHit(
                quantity = qty,
                unit = UnitDictionary.resolve(unitRaw),
                rawSpan = match.value.trim(),
                start = match.range.first,
                end = match.range.last + 1,
            )
        }.toList()

    fun toField(hit: QuantityHit?): ExtractionField<Double> {
        if (hit == null) {
            return ExtractionField(value = null, confidence = 0f)
        }
        return ExtractionField(
            value = hit.quantity,
            rawSpan = hit.rawSpan,
            confidence = if (hit.unit != null) 0.9f else 0.75f,
            source = FieldSource.HEURISTIC,
        )
    }

    fun unitField(hit: QuantityHit?): ExtractionField<String> {
        if (hit?.unit == null) {
            return ExtractionField(value = "UN", rawSpan = null, confidence = 0.4f, source = FieldSource.DICTIONARY)
        }
        return ExtractionField(
            value = hit.unit,
            rawSpan = hit.rawSpan,
            confidence = 0.9f,
            source = FieldSource.DICTIONARY,
        )
    }
}
