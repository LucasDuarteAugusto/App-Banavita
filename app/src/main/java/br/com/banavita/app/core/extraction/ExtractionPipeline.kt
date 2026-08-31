package br.com.banavita.app.core.extraction

import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.domain.model.extraction.DraftLineItem
import br.com.banavita.app.domain.model.extraction.ExtractionDraft
import br.com.banavita.app.domain.model.extraction.ExtractionFields
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtractionPipeline @Inject constructor(
    private val entityResolver: EntityResolver,
) {
    suspend fun extract(transcript: String, context: VoiceScreenContext): ExtractionDraft {
        val trimmed = transcript.trim()
        require(trimmed.isNotEmpty()) { "Transcript vazio" }

        val fields = when (context) {
            VoiceScreenContext.VENDA -> buildVenda(trimmed)
            VoiceScreenContext.COMPRA -> buildCompra(trimmed)
            VoiceScreenContext.CLIENTE -> buildCliente(trimmed)
            VoiceScreenContext.FORNECEDOR -> buildFornecedor(trimmed)
            VoiceScreenContext.PRODUTO -> buildProduto(trimmed)
            VoiceScreenContext.GRUPO -> buildGrupo(trimmed)
        }

        val confidences = collectConfidences(fields)
        val overall = if (confidences.isEmpty()) 0f else confidences.average().toFloat()
        val needsReview = computeNeedsReview(context, fields)

        return ExtractionDraft(
            id = UUID.randomUUID(),
            context = context,
            transcript = trimmed,
            createdAtEpochMillis = nowEpochMillis(),
            fields = fields,
            unresolvedTokens = emptyList(),
            overallConfidence = overall,
            needsReview = needsReview,
        )
    }

    private suspend fun buildVenda(transcript: String): ExtractionFields.VendaDraft {
        val parsed = VendaExtractionParser.parse(transcript)
        val produtoResolvido = entityResolver.resolveProduto(parsed.item.produtoRaw.value)
        val clienteResolvido = entityResolver.resolveCliente(parsed.clienteRaw.value)
        return ExtractionFields.VendaDraft(
            itens = listOf(
                DraftLineItem(
                    produtoRaw = parsed.item.produtoRaw,
                    produtoResolvido = produtoResolvido,
                    quantidade = parsed.item.quantidade,
                    unidade = parsed.item.unidade,
                    valorUnitarioCentavos = parsed.item.valorUnitarioCentavos,
                ),
            ),
            cliente = clienteResolvido,
            clienteRaw = parsed.clienteRaw,
        )
    }

    private suspend fun buildCompra(transcript: String): ExtractionFields.CompraDraft {
        val parsed = CompraExtractionParser.parse(transcript)
        val produtoResolvido = entityResolver.resolveProduto(parsed.item.produtoRaw.value)
        val fornecedorResolvido = entityResolver.resolveFornecedor(parsed.fornecedorRaw.value)
        return ExtractionFields.CompraDraft(
            itens = listOf(
                DraftLineItem(
                    produtoRaw = parsed.item.produtoRaw,
                    produtoResolvido = produtoResolvido,
                    quantidade = parsed.item.quantidade,
                    unidade = parsed.item.unidade,
                    valorUnitarioCentavos = parsed.item.valorUnitarioCentavos,
                ),
            ),
            fornecedor = fornecedorResolvido,
            fornecedorRaw = parsed.fornecedorRaw,
        )
    }

    private fun buildCliente(transcript: String): ExtractionFields.ClienteDraft {
        val parsed = ClienteExtractionParser.parse(transcript)
        return ExtractionFields.ClienteDraft(nome = parsed.nome, telefone = parsed.telefone)
    }

    private fun buildFornecedor(transcript: String): ExtractionFields.FornecedorDraft {
        val parsed = FornecedorExtractionParser.parse(transcript)
        return ExtractionFields.FornecedorDraft(nome = parsed.nome, telefone = parsed.telefone)
    }

    private suspend fun buildProduto(transcript: String): ExtractionFields.ProdutoDraft {
        val parsed = ProdutoExtractionParser.parse(transcript)
        val grupo = entityResolver.resolveGrupo(parsed.grupoRaw.value)
        return ExtractionFields.ProdutoDraft(
            descricao = parsed.descricao,
            precoCentavos = parsed.precoCentavos,
            unidade = parsed.unidade,
            grupo = grupo,
            grupoRaw = parsed.grupoRaw,
        )
    }

    private fun buildGrupo(transcript: String): ExtractionFields.GrupoDraft {
        val parsed = GrupoExtractionParser.parse(transcript)
        return ExtractionFields.GrupoDraft(nome = parsed.nome, observacao = parsed.observacao)
    }

    private fun collectConfidences(fields: ExtractionFields): List<Float> =
        when (fields) {
            is ExtractionFields.VendaDraft -> buildList {
                fields.itens.forEach { item ->
                    add(item.produtoRaw.confidence)
                    add(item.quantidade.confidence)
                    item.produtoResolvido?.let { add(it.confidence) }
                    fields.cliente?.let { add(it.confidence) }
                }
            }
            is ExtractionFields.CompraDraft -> buildList {
                fields.itens.forEach { item ->
                    add(item.produtoRaw.confidence)
                    add(item.quantidade.confidence)
                    add(item.valorUnitarioCentavos.confidence)
                    item.produtoResolvido?.let { add(it.confidence) }
                }
            }
            is ExtractionFields.ClienteDraft -> listOf(fields.nome.confidence, fields.telefone.confidence)
            is ExtractionFields.FornecedorDraft -> listOf(fields.nome.confidence, fields.telefone.confidence)
            is ExtractionFields.ProdutoDraft -> listOf(
                fields.descricao.confidence,
                fields.precoCentavos.confidence,
                fields.unidade.confidence,
            )
            is ExtractionFields.GrupoDraft -> listOf(fields.nome.confidence)
        }

    private fun computeNeedsReview(context: VoiceScreenContext, fields: ExtractionFields): Boolean =
        when (fields) {
            is ExtractionFields.VendaDraft -> {
                val item = fields.itens.firstOrNull()
                item == null ||
                    !item.quantidade.isPresent ||
                    item.produtoResolvido?.id == null
            }
            is ExtractionFields.CompraDraft -> {
                val item = fields.itens.firstOrNull()
                item == null ||
                    !item.quantidade.isPresent ||
                    !item.valorUnitarioCentavos.isPresent ||
                    item.produtoResolvido?.id == null
            }
            is ExtractionFields.ClienteDraft -> !fields.nome.isStrong
            is ExtractionFields.FornecedorDraft -> !fields.nome.isStrong
            is ExtractionFields.ProdutoDraft ->
                !fields.descricao.isStrong || !fields.precoCentavos.isPresent
            is ExtractionFields.GrupoDraft -> !fields.nome.isStrong
        }
}
