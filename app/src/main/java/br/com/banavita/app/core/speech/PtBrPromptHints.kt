package br.com.banavita.app.core.speech

/**
 * Biases Whisper decoder toward PT-BR interior paulista / Banavita vocabulary.
 * There is no dedicated "caipira" Whisper model; this is lexical adaptation only.
 */
object PtBrPromptHints {
    val initialPrompt: String = listOf(
        "Venda de banana nanica graúda, nanica miúda, prata graúda, prata média, prata miúda.",
        "Caixinha, quilo, dúzia, cliente, fornecedor, fiado, a prazo.",
        "Compra de mercadoria, preço, quantidade, total.",
        "Português do Brasil, interior de São Paulo.",
    ).joinToString(" ")

    private val corrections: Map<String, String> = mapOf(
        "caxinha" to "caixinha",
        "caxinhas" to "caixinhas",
        "kilograma" to "quilograma",
        "kilo" to "quilo",
        "duzia" to "dúzia",
        "duzias" to "dúzias",
        "na nica grauda" to "nanica graúda",
        "nanica grauda" to "nanica graúda",
        "na nica miuda" to "nanica miúda",
        "nanica miuda" to "nanica miúda",
        "prata grauda" to "prata graúda",
        "prata media" to "prata média",
        "prata miuda" to "prata miúda",
    )

    fun normalizeTranscript(raw: String): String {
        var text = raw.trim().replace(Regex("\\s+"), " ")
        if (text.isEmpty()) return text

        // Primeiro substitui expressões compostas sem forçar bordas de palavras rígidas (\b) se contiverem espaços
        val compoundCorrections = corrections.filter { it.key.contains(" ") }
        val singleCorrections = corrections.filter { !it.key.contains(" ") }

        compoundCorrections.forEach { (wrong, right) ->
            text = text.replace(Regex(Regex.escape(wrong), RegexOption.IGNORE_CASE), right)
        }

        singleCorrections.forEach { (wrong, right) ->
            text = text.replace(Regex("\\b${Regex.escape(wrong)}\\b", RegexOption.IGNORE_CASE), right)
        }
        return text
    }
}
