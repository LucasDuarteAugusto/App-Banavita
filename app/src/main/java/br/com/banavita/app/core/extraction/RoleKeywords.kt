package br.com.banavita.app.core.extraction

object RoleKeywords {
    private val stopWords = setOf(
        "de", "da", "do", "das", "dos", "a", "o", "as", "os", "e", "em", "no", "na",
        "um", "uma", "uns", "umas", "pro", "pra", "para", "por", "com", "sem",
        "vende", "vender", "venda", "vendi", "vendeu",
        "compra", "comprar", "comprei", "comprou",
        "cliente", "fornecedor", "produto", "grupo", "cadastro", "cadastrar",
        "quilo", "quilos", "kg", "unidade", "unidades", "caixa", "caixas", "duzia", "dúzia",
        "reais", "real", "r",
    )

    val clienteCue = Regex("""(?i)\b(?:pra|para|pro|cliente)\s+([a-zà-ú][\wà-ú]*(?:\s+[a-zà-ú][\wà-ú]*){0,2})""")
    val fornecedorCue = Regex("""(?i)\b(?:do|da|fornecedor|com)\s+([a-zà-ú][\wà-ú]*(?:\s+[a-zà-ú][\wà-ú]*){0,2})""")
    val produtoDeCue = Regex("""(?i)\b(?:de|do|da)\s+([a-zà-ú][\wà-ú]*(?:\s+[a-zà-ú][\wà-ú]*){0,3})""")

    fun isStopWord(token: String): Boolean =
        stopWords.contains(TextNormalizer.normalize(token))

    fun extractAfterCue(text: String, cue: Regex): String? {
        val match = cue.find(text) ?: return null
        return match.groupValues[1].trim().takeIf { it.isNotBlank() }
    }

    fun cleanPartnerCandidate(candidate: String): String {
        val cleaned = candidate
            .replace(Regex("""(?i)\b(a\s*vista|avista|a\s*prazo|fiado|pago|pagou|ja\s+pago|ja\s+pagou|nao\s+pago|nao\s+pagou|no\s+pix|em\s+dinheiro|no\s+dinheiro|no\s+cartao|no\s+debito|pendente)\b"""), " ")
            .replace(Regex("""(?i)\b(reais?|real|r\$)\b"""), " ")
            .replace(Regex("""\b\d+([.,]\d+)?\b"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
        val tokens = cleaned.split(" ").filter { it.isNotBlank() && !isStopWord(it) }
        return tokens.joinToString(" ")
    }
}
