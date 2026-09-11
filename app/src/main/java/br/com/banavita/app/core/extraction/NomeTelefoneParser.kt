package br.com.banavita.app.core.extraction

import br.com.banavita.app.domain.model.extraction.ExtractionField
import br.com.banavita.app.domain.model.extraction.FieldSource

/**
 * Shared nome/telefone slot filling for cadastro de cliente e fornecedor.
 * Display name keeps accents; keyword matching uses accent-stripped forms.
 */
object NomeTelefoneParser {
    private val commandKeywords = setOf(
        "cadastrar", "cadastro", "cadatrar", "cadastra", "novo",
    )
    private val phoneCueKeywords = setOf(
        "telefone", "fone", "celular", "whatsapp", "numero", "telefoni",
    )
    private val particles = setOf("de", "da", "do", "das", "dos", "e")
    private val maxNameWords = 3

    private val tokenRegex = Regex("""\p{L}+|\d+|[.,;:!?]+|[^\s\p{L}\d.,;:!?]+""")

    fun parse(transcript: String, entityKeywords: Set<String>): ParsedNomeTelefone {
        val phone = PhoneParser.find(transcript)
        var working = transcript
        phone?.rawSpan?.takeIf { it.isNotBlank() }?.let { span ->
            val index = working.indexOf(span)
            if (index >= 0) {
                working = working.substring(0, index) + " " + working.substring(index + span.length)
            }
        }
        val nome = extractName(working, entityKeywords)
        return ParsedNomeTelefone(
            nome = ExtractionField(
                value = nome,
                rawSpan = nome,
                confidence = if (nome != null) 0.8f else 0f,
                source = FieldSource.HEURISTIC,
            ),
            telefone = PhoneParser.toField(phone),
        )
    }

    private fun extractName(text: String, entityKeywords: Set<String>): String? {
        val keywords = commandKeywords + phoneCueKeywords + entityKeywords
        val tokens = tokenize(text)
        if (tokens.isEmpty()) return null

        val cueIndex = tokens.indices.lastOrNull { matchesKeyword(tokens[it].text, phoneCueKeywords) }
        val collected = if (cueIndex != null) {
            collectBeforePhoneCue(tokens, cueIndex, keywords)
        } else {
            emptyList()
        }
        val words = if (collected.isNotEmpty()) {
            collected
        } else {
            tokens.filter { it.isWord && !matchesKeyword(it.text, keywords) }
        }
        val trimmed = takeLastNameWords(words)
        val joined = trimmed.joinToString(" ") { it.text }
        return PersonNameSanitizer.sanitize(joined).takeIf { it.isNotBlank() }
    }

    private fun collectBeforePhoneCue(
        tokens: List<Token>,
        cueIndex: Int,
        keywords: Set<String>,
    ): List<Token> {
        var i = cueIndex - 1
        while (i >= 0 && !tokens[i].isWord) {
            i--
        }
        val collected = ArrayDeque<Token>()
        while (i >= 0) {
            val token = tokens[i]
            if (token.isPunct) break
            if (!token.isWord) {
                i--
                continue
            }
            if (matchesKeyword(token.text, keywords)) break
            collected.addFirst(token)
            i--
        }
        return collected.toList()
    }

    private fun takeLastNameWords(tokens: List<Token>): List<Token> {
        val nameWordIndices = tokens.indices.filter { i ->
            tokens[i].isWord && !isParticle(tokens[i].text)
        }
        if (nameWordIndices.size <= maxNameWords) return tokens
        val start = nameWordIndices[nameWordIndices.size - maxNameWords]
        return tokens.subList(start, tokens.size)
    }

    private fun tokenize(text: String): List<Token> =
        tokenRegex.findAll(text).map { match ->
            val value = match.value
            Token(
                text = value,
                isWord = value.first().isLetter(),
                isPunct = value.all { it in ".,;:!?" },
            )
        }.toList()

    private fun matchesKeyword(token: String, keywords: Set<String>): Boolean {
        val normalized = TextNormalizer.stripAccentsKeepCase(token)
            .lowercase()
            .filter { it.isLetter() }
        if (normalized.isEmpty()) return false
        if (normalized in keywords) return true
        if (normalized.length >= 6) {
            return keywords.any { keyword ->
                keyword.length >= 6 && editDistanceAtMostOne(normalized, keyword)
            }
        }
        return false
    }

    private fun isParticle(token: String): Boolean {
        val normalized = TextNormalizer.stripAccentsKeepCase(token).lowercase()
        return normalized in particles
    }

    private data class Token(
        val text: String,
        val isWord: Boolean,
        val isPunct: Boolean,
    )
}

object PersonNameSanitizer {
    private val particles = setOf("de", "da", "do", "das", "dos", "e")

    fun sanitize(raw: String): String {
        var text = raw.replace(Regex("""[-'’]"""), " ")
        text = text.replace(Regex("""[^\p{L}\s]"""), " ")
        text = text.replace(Regex("""\s+"""), " ").trim()
        if (text.isEmpty()) return ""
        return text.split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                val lower = word.lowercase()
                val key = TextNormalizer.stripAccentsKeepCase(lower).lowercase()
                if (key in particles) lower else lower.replaceFirstChar { it.titlecase() }
            }
    }
}

private fun editDistanceAtMostOne(a: String, b: String): Boolean {
    if (a == b) return true
    val la = a.length
    val lb = b.length
    if (kotlin.math.abs(la - lb) > 1) return false
    if (la == lb) {
        var diffs = 0
        for (i in a.indices) {
            if (a[i] != b[i] && ++diffs > 1) return false
        }
        return true
    }
    val shorter = if (la < lb) a else b
    val longer = if (la < lb) b else a
    var i = 0
    var j = 0
    var skipped = false
    while (i < shorter.length && j < longer.length) {
        if (shorter[i] == longer[j]) {
            i++
            j++
        } else if (!skipped) {
            skipped = true
            j++
        } else {
            return false
        }
    }
    return true
}
