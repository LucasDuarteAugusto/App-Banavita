package br.com.banavita.app.ui.common

import br.com.banavita.app.core.money.Money
import java.math.BigDecimal

object MoneyInputParser {
    fun parseToCentavos(input: String): Long? {
        val cleaned = input.trim()
            .replace("R$", "", ignoreCase = true)
            .replace(" ", "")
        if (cleaned.isBlank()) return null

        val normalized = when {
            cleaned.contains(',') -> cleaned.replace(".", "").replace(",", ".")
            else -> cleaned
        }

        return runCatching {
            Money.toCents(BigDecimal(normalized))
        }.getOrNull()
    }

    fun formatForInput(centavos: Long): String =
        Money.fromCents(centavos).toPlainString().replace(".", ",")
}
