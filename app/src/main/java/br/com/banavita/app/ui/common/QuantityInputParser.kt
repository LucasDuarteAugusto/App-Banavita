package br.com.banavita.app.ui.common

object QuantityInputParser {
    fun parse(input: String): Double? {
        val cleaned = input.trim().replace(",", ".")
        if (cleaned.isBlank()) return null
        return cleaned.toDoubleOrNull()?.takeIf { it > 0 }
    }
}
