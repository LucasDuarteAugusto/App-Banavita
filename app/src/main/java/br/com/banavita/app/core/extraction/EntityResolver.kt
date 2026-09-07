package br.com.banavita.app.core.extraction

import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.FornecedorDao
import br.com.banavita.app.data.local.dao.GrupoDao
import br.com.banavita.app.data.local.dao.ProdutoDao
import br.com.banavita.app.domain.model.extraction.EntityCandidate
import br.com.banavita.app.domain.model.extraction.ResolvedEntity
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class EntityResolver @Inject constructor(
    private val produtoDao: ProdutoDao,
    private val clienteDao: ClienteDao,
    private val fornecedorDao: FornecedorDao,
    private val grupoDao: GrupoDao,
) {
    suspend fun resolveProduto(raw: String?): ResolvedEntity? {
        if (raw.isNullOrBlank()) return null
        val produtos = produtoDao.getAll().map { it.id to it.descricao }
        return resolveAgainst(raw, produtos)
    }

    suspend fun resolveCliente(raw: String?): ResolvedEntity? {
        if (raw.isNullOrBlank()) return null
        val clientes = clienteDao.getAll().map { it.id to it.nome }
        return resolveAgainst(raw, clientes)
    }

    suspend fun resolveClienteFromTranscript(transcript: String): ResolvedEntity? {
        val clientes = clienteDao.getAll().map { it.id to it.nome }
        return findInTranscript(transcript, clientes)
    }

    suspend fun resolveFornecedor(raw: String?): ResolvedEntity? {
        if (raw.isNullOrBlank()) return null
        val fornecedores = fornecedorDao.getAll().map { it.id to it.nome }
        return resolveAgainst(raw, fornecedores)
    }

    suspend fun resolveFornecedorFromTranscript(transcript: String): ResolvedEntity? {
        val fornecedores = fornecedorDao.getAll().map { it.id to it.nome }
        return findInTranscript(transcript, fornecedores)
    }

    suspend fun resolveGrupo(raw: String?): ResolvedEntity? {
        if (raw.isNullOrBlank()) return null
        val grupos = grupoDao.getAll().map { it.id to it.nome }
        return resolveAgainst(raw, grupos)
    }

    private fun findInTranscript(
        transcript: String,
        catalog: List<Pair<UUID, String>>,
    ): ResolvedEntity? {
        if (catalog.isEmpty() || transcript.isBlank()) return null
        val normTranscript = TextNormalizer.normalize(transcript)
        val scored = catalog.mapNotNull { (id, label) ->
            val normLabel = TextNormalizer.normalize(label)
            if (normLabel.isBlank()) return@mapNotNull null
            if (normTranscript.contains(normLabel)) {
                EntityCandidate(id = id, label = label, score = 0.88f)
            } else {
                val tokens = normLabel.split(" ").filter { it.length >= 3 && !RoleKeywords.isStopWord(it) }
                val matchCount = tokens.count { token ->
                    Regex("""\b${Regex.escape(token)}\b""").containsMatchIn(normTranscript)
                }
                if (matchCount > 0 && tokens.isNotEmpty()) {
                    val score = 0.50f + 0.35f * (matchCount.toFloat() / tokens.size.toFloat())
                    EntityCandidate(id = id, label = label, score = score)
                } else null
            }
        }.sortedByDescending { it.score }

        val best = scored.firstOrNull() ?: return null
        val ambiguous = scored.count { it.score >= 0.72f } > 1
        return ResolvedEntity(
            id = if (!ambiguous) best.id else null,
            label = best.label,
            rawSpan = best.label,
            confidence = best.score,
            candidates = scored.take(5),
        )
    }

    private fun resolveAgainst(
        raw: String,
        catalog: List<Pair<UUID, String>>,
    ): ResolvedEntity {
        val query = TextNormalizer.normalize(raw)
        if (catalog.isEmpty()) {
            return ResolvedEntity(
                id = null,
                label = null,
                rawSpan = raw,
                confidence = 0.2f,
                candidates = emptyList(),
            )
        }

        val scored = catalog.map { (id, label) ->
            EntityCandidate(id = id, label = label, score = similarity(query, TextNormalizer.normalize(label)))
        }.sortedByDescending { it.score }

        val best = scored.first()
        val acceptable = best.score >= 0.50f
        val ambiguous = scored.count { it.score >= 0.72f } > 1

        return ResolvedEntity(
            id = if (acceptable && !ambiguous) best.id else null,
            label = if (acceptable) best.label else null,
            rawSpan = raw,
            confidence = best.score,
            candidates = scored.take(5).filter { it.score >= 0.35f },
        )
    }

    private fun similarity(a: String, b: String): Float {
        if (a.isBlank() || b.isBlank()) return 0f
        if (a == b) return 1f
        if (a.contains(b) || b.contains(a)) {
            val ratio = min(a.length, b.length).toFloat() / max(a.length, b.length)
            return 0.75f + 0.25f * ratio
        }
        val tokensA = a.split(" ").filter { it.isNotBlank() }.toSet()
        val tokensB = b.split(" ").filter { it.isNotBlank() }.toSet()
        if (tokensA.isNotEmpty() && tokensB.isNotEmpty()) {
            val inter = tokensA.intersect(tokensB).size.toFloat()
            val union = tokensA.union(tokensB).size.toFloat()
            val jaccard = inter / union
            if (jaccard >= 0.5f) return 0.55f + 0.4f * jaccard
        }
        val distance = levenshtein(a, b)
        val maxLen = max(a.length, b.length).toFloat()
        val lev = 1f - (distance / maxLen)
        return lev.coerceIn(0f, 1f)
    }

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost,
                )
            }
        }
        return dp[a.length][b.length]
    }
}
