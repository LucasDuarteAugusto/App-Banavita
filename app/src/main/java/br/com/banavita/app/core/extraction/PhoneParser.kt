package br.com.banavita.app.core.extraction

import br.com.banavita.app.domain.model.extraction.ExtractionField
import br.com.banavita.app.domain.model.extraction.FieldSource

data class PhoneHit(
    val digits: String,
    val rawSpan: String,
)

object PhoneParser {
    private val regex = Regex("""(?<!\d)(\(?\d{2}\)?\s*\d{4,5}[-\s]?\d{4})(?!\d)""")

    fun find(text: String): PhoneHit? {
        val match = regex.find(text) ?: return null
        val digits = match.value.filter { it.isDigit() }
        if (digits.length !in 10..11) return null
        return PhoneHit(digits = digits, rawSpan = match.value.trim())
    }

    fun toField(hit: PhoneHit?): ExtractionField<String> {
        if (hit == null) return ExtractionField(value = null, confidence = 0f)
        return ExtractionField(
            value = hit.digits,
            rawSpan = hit.rawSpan,
            confidence = 0.9f,
            source = FieldSource.HEURISTIC,
        )
    }
}
