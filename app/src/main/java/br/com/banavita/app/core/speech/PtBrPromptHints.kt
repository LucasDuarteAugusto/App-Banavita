package br.com.banavita.app.core.speech

/**
 * Biases Whisper decoder toward PT-BR interior paulista / Banavita vocabulary.
 * There is no dedicated "caipira" Whisper model; this is lexical adaptation only.
 */
object PtBrPromptHints {
    val initialPrompt: String = listOf(
        "Venda de banana, caixinha, quilo, dúzia, cliente, fornecedor.",
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
    )

    fun normalizeTranscript(raw: String): String {
        var text = raw.trim().replace(Regex("\\s+"), " ")
        if (text.isEmpty()) return text

        corrections.forEach { (wrong, right) ->
            text = text.replace(Regex("\\b${Regex.escape(wrong)}\\b", RegexOption.IGNORE_CASE), right)
        }
        return text
    }
}
