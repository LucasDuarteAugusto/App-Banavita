package br.com.banavita.app.core.extraction

object TextNormalizer {
    private val diacritics = Regex("\\p{InCombiningDiacriticalMarks}+")

    fun normalize(raw: String): String {
        var text = raw.trim().lowercase()
        text = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
            .replace(diacritics, "")
        text = text
            .replace('ç', 'c')
            .replace(Regex("[^a-z0-9\\s.,]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        return SpokenNumberExpander.expand(text)
    }

    fun stripAccentsKeepCase(raw: String): String {
        val nfd = java.text.Normalizer.normalize(raw, java.text.Normalizer.Form.NFD)
        return nfd.replace(diacritics, "").replace('ç', 'c').replace('Ç', 'C')
    }
}

object SpokenNumberExpander {
    private val map = linkedMapOf(
        "meia" to "0.5",
        "meio" to "0.5",
        "zero" to "0",
        "um" to "1",
        "uma" to "1",
        "dois" to "2",
        "duas" to "2",
        "tres" to "3",
        "três" to "3",
        "quatro" to "4",
        "cinco" to "5",
        "seis" to "6",
        "sete" to "7",
        "oito" to "8",
        "nove" to "9",
        "dez" to "10",
        "onze" to "11",
        "doze" to "12",
        "treze" to "13",
        "quatorze" to "14",
        "quinze" to "15",
        "vinte" to "20",
        "trinta" to "30",
        "quarenta" to "40",
        "cinquenta" to "50",
        "sessenta" to "60",
        "setenta" to "70",
        "oitenta" to "80",
        "noventa" to "90",
        "cem" to "100",
        "cento" to "100",
    )

    fun expand(normalizedLower: String): String {
        var result = " $normalizedLower "
        // Longer phrases first
        map.entries.sortedByDescending { it.key.length }.forEach { (word, digits) ->
            val pattern = Regex("\\b${Regex.escape(word)}\\b", RegexOption.IGNORE_CASE)
            result = result.replace(pattern, " $digits ")
        }
        return result.replace(Regex("\\s+"), " ").trim()
    }
}
