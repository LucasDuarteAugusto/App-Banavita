package br.com.banavita.app.core.extraction

import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.domain.model.extraction.BaixaTipo
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
            VoiceScreenContext.CONSULTAS -> buildConsultas(trimmed, preferInicio = false)
            VoiceScreenContext.INICIO -> buildInicio(trimmed)
            VoiceScreenContext.RECEBER -> buildReceber(trimmed)
            VoiceScreenContext.PAGAR -> buildPagar(trimmed)
            VoiceScreenContext.AGENDA -> buildEvento(trimmed)
        }

        val confidences = collectConfidences(fields)
        val overall = if (confidences.isEmpty()) 0f else confidences.average().toFloat()
        val needsReview = computeNeedsReview(fields)

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

    private suspend fun buildInicio(transcript: String): ExtractionFields = when {
        InicioIntentParser.looksLikeVenda(transcript) -> buildVenda(transcript)
        InicioIntentParser.looksLikeEvento(transcript) -> buildEvento(transcript)
        else -> buildConsultas(transcript, preferInicio = true)
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
            aPrazo = parsed.aPrazo,
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
            aPrazo = parsed.aPrazo,
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

    private suspend fun buildConsultas(
        transcript: String,
        preferInicio: Boolean,
    ): ExtractionFields.ConsultasDraft {
        val parsed = ConsultasExtractionParser.parse(transcript, preferInicio)
        val modo = parsed.modo.value
        val produto = entityResolver.resolveProduto(parsed.busca.value)
        val cliente = entityResolver.resolveCliente(parsed.busca.value)
        val fornecedor = entityResolver.resolveFornecedor(parsed.busca.value)
        val openEstoque = produto?.id != null &&
            produto.confidence >= 0.72f &&
            (modo == null || modo == br.com.banavita.app.domain.model.ConsultaModo.ESTOQUE)
        return ExtractionFields.ConsultasDraft(
            modo = parsed.modo,
            busca = parsed.busca,
            periodo = parsed.periodo,
            visao = parsed.visao,
            somenteZerados = parsed.somenteZerados,
            navigateNovaVenda = parsed.navigateNovaVenda,
            navigateAgenda = parsed.navigateAgenda,
            openDetailIfResolved = openEstoque,
            produtoResolvido = produto,
            clienteResolvido = cliente,
            fornecedorResolvido = fornecedor,
        )
    }

    private suspend fun buildReceber(transcript: String): ExtractionFields {
        if (BaixaExtractionParser.isReceber(transcript)) {
            val parsed = BaixaExtractionParser.parse(transcript)
            val parceiro = entityResolver.resolveCliente(parsed.parceiroRaw.value)
            return ExtractionFields.BaixaDraft(
                tipo = BaixaTipo.RECEBER,
                parceiro = parceiro,
                parceiroRaw = parsed.parceiroRaw,
                valorCentavos = parsed.valorCentavos,
            )
        }
        return buildConsultas(transcript, preferInicio = false).copy(
            modo = br.com.banavita.app.domain.model.extraction.ExtractionField(
                br.com.banavita.app.domain.model.ConsultaModo.RECEBER,
                "receber",
                0.8f,
            ),
        )
    }

    private suspend fun buildPagar(transcript: String): ExtractionFields {
        if (BaixaExtractionParser.isPagar(transcript)) {
            val parsed = BaixaExtractionParser.parse(transcript)
            val parceiro = entityResolver.resolveFornecedor(parsed.parceiroRaw.value)
            return ExtractionFields.BaixaDraft(
                tipo = BaixaTipo.PAGAR,
                parceiro = parceiro,
                parceiroRaw = parsed.parceiroRaw,
                valorCentavos = parsed.valorCentavos,
            )
        }
        return buildConsultas(transcript, preferInicio = false).copy(
            modo = br.com.banavita.app.domain.model.extraction.ExtractionField(
                br.com.banavita.app.domain.model.ConsultaModo.PAGAR,
                "pagar",
                0.8f,
            ),
        )
    }

    private suspend fun buildEvento(transcript: String): ExtractionFields.EventoDraft {
        val parsed = EventoExtractionParser.parse(transcript)
        val cliente = entityResolver.resolveCliente(parsed.clienteRaw.value)
        val fornecedor = entityResolver.resolveFornecedor(parsed.fornecedorRaw.value)
        return ExtractionFields.EventoDraft(
            titulo = parsed.titulo,
            dataHoraMillis = parsed.dataHoraMillis,
            observacao = parsed.observacao,
            cliente = cliente,
            clienteRaw = parsed.clienteRaw,
            fornecedor = fornecedor,
            fornecedorRaw = parsed.fornecedorRaw,
            notificar = parsed.notificar,
            antecipacaoMinutos = parsed.antecipacaoMinutos,
            notifTitulo = br.com.banavita.app.domain.model.extraction.ExtractionField(null),
            notifCorpo = br.com.banavita.app.domain.model.extraction.ExtractionField(null),
            recorteLista = parsed.recorteLista,
        )
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
            is ExtractionFields.ConsultasDraft -> listOf(
                fields.modo.confidence,
                fields.busca.confidence,
                fields.periodo.confidence,
            )
            is ExtractionFields.BaixaDraft -> listOf(
                fields.valorCentavos.confidence,
                fields.parceiroRaw.confidence,
            )
            is ExtractionFields.EventoDraft -> listOf(
                fields.titulo.confidence,
                fields.dataHoraMillis.confidence,
            )
        }

    private fun computeNeedsReview(fields: ExtractionFields): Boolean =
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
            is ExtractionFields.ConsultasDraft ->
                !fields.modo.isPresent &&
                    !fields.busca.isPresent &&
                    !fields.periodo.isPresent &&
                    fields.somenteZerados.value != true &&
                    !fields.navigateNovaVenda &&
                    !fields.navigateAgenda
            is ExtractionFields.BaixaDraft ->
                fields.parceiro?.id == null || !fields.valorCentavos.isPresent
            is ExtractionFields.EventoDraft -> {
                val filtro = fields.recorteLista.isPresent
                if (filtro && !fields.titulo.isPresent) {
                    false
                } else {
                    !fields.titulo.isPresent || !fields.dataHoraMillis.isPresent
                }
            }
        }
}
