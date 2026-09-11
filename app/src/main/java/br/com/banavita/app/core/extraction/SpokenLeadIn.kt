package br.com.banavita.app.core.extraction

/**
 * Removes tolerated command words after the screen context is already known.
 * Only strips from the start so ASR leftovers in the middle stay available to name parsers.
 */
object SpokenLeadIn {
    private val junk = setOf(
        "cadastrar", "cadastro", "cadatrar", "cadastra",
        "novo", "cliente", "fornecedor", "produto", "grupo",
        "vende", "vender", "venda", "vendeu", "vendi",
        "comprei", "compra", "comprar", "comprou",
    )

    fun stripLeading(text: String): String {
        if (text.isBlank()) return text
        val tokens = text.trim().split(Regex("\\s+"))
        return tokens.dropWhile { isJunkToken(it) }.joinToString(" ")
    }

    fun isJunkToken(token: String): Boolean {
        val n = TextNormalizer.stripAccentsKeepCase(token)
            .lowercase()
            .filter { it.isLetter() }
        return n.isNotEmpty() && n in junk
    }
}

internal fun maskRange(text: String, start: Int, end: Int): String {
    if (start >= end) return text
    val lo = start.coerceIn(0, text.length)
    val hi = end.coerceIn(lo, text.length)
    return text.replaceRange(lo, hi, " ".repeat(hi - lo))
}
