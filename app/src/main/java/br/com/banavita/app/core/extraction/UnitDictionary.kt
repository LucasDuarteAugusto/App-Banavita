package br.com.banavita.app.core.extraction

object UnitDictionary {
    private val aliases = mapOf(
        "kg" to "KG",
        "quilo" to "KG",
        "quilos" to "KG",
        "kilograma" to "KG",
        "quilograma" to "KG",
        "quilogramas" to "KG",
        "un" to "UN",
        "und" to "UN",
        "unidade" to "UN",
        "unidades" to "UN",
        "cx" to "CX MADEIRA",
        "caixa" to "CX MADEIRA",
        "caixas" to "CX MADEIRA",
        "caixinha" to "CX MADEIRA",
        "caixinhas" to "CX MADEIRA",
        "caixa madeira" to "CX MADEIRA",
        "madeira" to "CX MADEIRA",
        "caixa plastica" to "CX PLASTICA",
        "caixa plástica" to "CX PLASTICA",
        "plastica" to "CX PLASTICA",
        "plástica" to "CX PLASTICA",
        "dz" to "DZ",
        "duzia" to "DZ",
        "dúzia" to "DZ",
        "duzias" to "DZ",
        "dúzias" to "DZ",
    )

    val patternAlternation: String =
        aliases.keys.sortedByDescending { it.length }.joinToString("|") { Regex.escape(it) }

    fun resolve(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val key = TextNormalizer.normalize(raw)
        return aliases[key]
    }
}
