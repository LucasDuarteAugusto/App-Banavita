package br.com.banavita.app.core.extraction

import br.com.banavita.app.domain.model.extraction.ExtractionField
import br.com.banavita.app.domain.model.extraction.FieldSource

data class PhoneHit(
    val digits: String,
    val rawSpan: String,
)

object PhoneParser {
    private val contiguousRegex = Regex("""(?<!\d)(\(?\d{2}\)?\s*\d{4,5}[-\s]?\d{4})(?!\d)""")

    /** Runs of isolated digits: "11, 9, 8, 7, 6, 5, 4, 3, 2, 1" or "11 98765 4321". */
    private val isolatedRunRegex = Regex(
        """(?<!\d)\d{1,11}(?:[\s,;\-]+(?:e\s+)?\d{1,11})+(?!\d)""",
        RegexOption.IGNORE_CASE,
    )

    private val phoneCueRegex = Regex(
        """(?i)\b(?:telefone|fone|celular|whatsapp|n[uú]mero|telefoni)\b""",
    )

    fun find(text: String): PhoneHit? {
        findContiguous(text)?.let { return it }
        findIsolated(text)?.let { return it }
        findAfterCue(text)?.let { return it }
        return findExactTotalDigits(text)
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

    private fun findContiguous(text: String): PhoneHit? {
        val match = contiguousRegex.find(text) ?: return null
        val digits = match.value.filter { it.isDigit() }
        if (digits.length !in 10..11) return null
        return PhoneHit(digits = digits, rawSpan = match.value.trim())
    }

    private fun findIsolated(text: String): PhoneHit? {
        val hits = isolatedRunRegex.findAll(text).mapNotNull { match ->
            val digits = match.value.filter { it.isDigit() }
            val normalized = when {
                digits.length in 10..11 -> digits
                digits.length > 11 -> digits.takeLast(11)
                else -> return@mapNotNull null
            }
            PhoneHit(digits = normalized, rawSpan = match.value.trim())
        }.toList()
        return hits.lastOrNull()
    }

    private fun findAfterCue(text: String): PhoneHit? {
        val match = phoneCueRegex.find(text) ?: return null
        val after = text.substring(match.range.last + 1)
        val digits = after.filter { it.isDigit() }
        val normalized = when {
            digits.length in 10..11 -> digits
            digits.length > 11 -> digits.take(11)
            else -> return null
        }
        val span = after.trim().ifBlank { match.value }
        return PhoneHit(digits = normalized, rawSpan = span)
    }

    private fun findExactTotalDigits(text: String): PhoneHit? {
        val digits = text.filter { it.isDigit() }
        if (digits.length !in 10..11) return null
        return PhoneHit(digits = digits, rawSpan = digits)
    }
}
