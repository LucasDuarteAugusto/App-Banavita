package br.com.banavita.app.data.repository

import br.com.banavita.app.core.extraction.ExtractionPipeline
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.cache.ExtractionSessionCache
import br.com.banavita.app.domain.exception.ExtractionConfirmException
import br.com.banavita.app.domain.exception.ExtractionDraftNotFoundException
import br.com.banavita.app.domain.exception.ExtractionParseException
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.domain.model.TranscriptionResult
import br.com.banavita.app.domain.model.extraction.BaixaTipo
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.domain.model.extraction.ExtractionDraft
import br.com.banavita.app.domain.model.extraction.ExtractionFields
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtractionRepository @Inject constructor(
    private val pipeline: ExtractionPipeline,
    private val sessionCache: ExtractionSessionCache,
    private val vendaRepository: VendaRepository,
    private val compraRepository: CompraRepository,
    private val clienteRepository: ClienteRepository,
    private val fornecedorRepository: FornecedorRepository,
    private val produtoRepository: ProdutoRepository,
    private val grupoRepository: GrupoRepository,
    private val financeiroRepository: FinanceiroRepository,
    private val eventoRepository: EventoRepository,
) {
    suspend fun extract(
        transcript: String,
        context: VoiceScreenContext,
    ): ExtractionDraft {
        if (transcript.isBlank()) {
            throw ExtractionParseException("Transcript vazio.")
        }
        val draft = pipeline.extract(transcript.trim(), context)
        sessionCache.put(draft)
        return draft
    }

    suspend fun extractFromTranscription(
        transcription: TranscriptionResult,
        context: VoiceScreenContext,
    ): ExtractionDraft = extract(transcription.text, context)

    fun getCurrentDraft(): ExtractionDraft? = sessionCache.get()

    fun updateDraft(draft: ExtractionDraft) {
        sessionCache.put(draft)
    }

    fun discardDraft(draftId: UUID) {
        sessionCache.getIfId(draftId)
            ?: throw ExtractionDraftNotFoundException("Rascunho $draftId não encontrado na sessão.")
        sessionCache.clear()
    }

    suspend fun confirmDraft(draftId: UUID): ConfirmResult {
        val draft = sessionCache.getIfId(draftId)
            ?: throw ExtractionDraftNotFoundException("Rascunho $draftId não encontrado na sessão.")

        val result = when (val fields = draft.fields) {
            is ExtractionFields.VendaDraft -> confirmVenda(fields)
            is ExtractionFields.CompraDraft -> confirmCompra(fields)
            is ExtractionFields.ClienteDraft -> confirmCliente(fields)
            is ExtractionFields.FornecedorDraft -> confirmFornecedor(fields)
            is ExtractionFields.ProdutoDraft -> confirmProduto(fields)
            is ExtractionFields.GrupoDraft -> confirmGrupo(fields)
            is ExtractionFields.ConsultasDraft -> confirmConsultas(fields)
            is ExtractionFields.BaixaDraft -> confirmBaixa(fields)
            is ExtractionFields.EventoDraft -> confirmEvento(fields)
        }
        sessionCache.clear()
        return result
    }

    private suspend fun confirmVenda(fields: ExtractionFields.VendaDraft): ConfirmResult.VendaCriada {
        val item = fields.itens.firstOrNull()
            ?: throw ExtractionConfirmException("Venda sem itens.")
        val produtoId = item.produtoResolvido?.id
            ?: throw ExtractionConfirmException("Produto não resolvido. Revise antes de confirmar.")
        val quantidade = item.quantidade.value
            ?: throw ExtractionConfirmException("Quantidade ausente.")
        if (quantidade <= 0) {
            throw ExtractionConfirmException("Quantidade inválida.")
        }
        val vendaId = vendaRepository.create(
            data = nowEpochMillis(),
            clienteId = fields.cliente?.id,
            itens = listOf(
                DocumentoItemInput(
                    produtoId = produtoId,
                    quantidade = quantidade,
                    valorUnitarioCentavos = item.valorUnitarioCentavos.value,
                ),
            ),
            aPrazo = fields.aPrazo.value == true,
        )
        return ConfirmResult.VendaCriada(vendaId)
    }

    private suspend fun confirmCompra(fields: ExtractionFields.CompraDraft): ConfirmResult.CompraCriada {
        val item = fields.itens.firstOrNull()
            ?: throw ExtractionConfirmException("Compra sem itens.")
        val produtoId = item.produtoResolvido?.id
            ?: throw ExtractionConfirmException("Produto não resolvido. Revise antes de confirmar.")
        val quantidade = item.quantidade.value
            ?: throw ExtractionConfirmException("Quantidade ausente.")
        val valor = item.valorUnitarioCentavos.value
            ?: throw ExtractionConfirmException("Valor unitário obrigatório na compra.")
        val compraId = compraRepository.create(
            data = nowEpochMillis(),
            fornecedorId = fields.fornecedor?.id,
            itens = listOf(
                DocumentoItemInput(
                    produtoId = produtoId,
                    quantidade = quantidade,
                    valorUnitarioCentavos = valor,
                ),
            ),
            aPrazo = fields.aPrazo.value == true,
        )
        return ConfirmResult.CompraCriada(compraId)
    }

    private suspend fun confirmCliente(fields: ExtractionFields.ClienteDraft): ConfirmResult.ClienteCriado {
        val nome = fields.nome.value?.trim()
            ?: throw ExtractionConfirmException("Nome do cliente ausente.")
        val id = clienteRepository.create(nome = nome, telefone = fields.telefone.value)
        return ConfirmResult.ClienteCriado(id)
    }

    private suspend fun confirmFornecedor(fields: ExtractionFields.FornecedorDraft): ConfirmResult.FornecedorCriado {
        val nome = fields.nome.value?.trim()
            ?: throw ExtractionConfirmException("Nome do fornecedor ausente.")
        val id = fornecedorRepository.create(nome = nome, telefone = fields.telefone.value)
        return ConfirmResult.FornecedorCriado(id)
    }

    private suspend fun confirmProduto(fields: ExtractionFields.ProdutoDraft): ConfirmResult.ProdutoCriado {
        val descricao = fields.descricao.value?.trim()
            ?: throw ExtractionConfirmException("Descrição do produto ausente.")
        val preco = fields.precoCentavos.value
            ?: throw ExtractionConfirmException("Preço do produto ausente.")
        val unidade = fields.unidade.value ?: "UN"
        val id = produtoRepository.create(
            descricao = descricao,
            precoCentavos = preco,
            grupoId = fields.grupo?.id,
            unidade = unidade,
        )
        return ConfirmResult.ProdutoCriado(id)
    }

    private suspend fun confirmGrupo(fields: ExtractionFields.GrupoDraft): ConfirmResult.GrupoCriado {
        val nome = fields.nome.value?.trim()
            ?: throw ExtractionConfirmException("Nome do grupo ausente.")
        val id = grupoRepository.create(nome = nome, observacao = fields.observacao.value)
        return ConfirmResult.GrupoCriado(id)
    }

    private fun confirmConsultas(fields: ExtractionFields.ConsultasDraft): ConfirmResult.ConsultaAplicada {
        return ConfirmResult.ConsultaAplicada(
            modo = fields.modo.value,
            busca = fields.busca.value,
            periodo = fields.periodo.value,
            visao = fields.visao.value,
            somenteZerados = fields.somenteZerados.value == true,
            openProdutoId = if (fields.openDetailIfResolved) fields.produtoResolvido?.id else null,
            openClienteId = fields.clienteResolvido?.id.takeIf {
                fields.modo.value == br.com.banavita.app.domain.model.ConsultaModo.RECEBER
            },
            openFornecedorId = fields.fornecedorResolvido?.id.takeIf {
                fields.modo.value == br.com.banavita.app.domain.model.ConsultaModo.PAGAR
            },
            navigateNovaVenda = fields.navigateNovaVenda,
            navigateAgenda = fields.navigateAgenda,
        )
    }

    private suspend fun confirmBaixa(fields: ExtractionFields.BaixaDraft): ConfirmResult {
        val parceiroId = fields.parceiro?.id
            ?: throw ExtractionConfirmException("Parceiro não resolvido. Revise antes de confirmar.")
        val valor = fields.valorCentavos.value
            ?: throw ExtractionConfirmException("Valor ausente.")
        return when (fields.tipo) {
            BaixaTipo.RECEBER -> ConfirmResult.RecebimentoCriado(
                financeiroRepository.registrarRecebimentoCliente(parceiroId, valor),
            )
            BaixaTipo.PAGAR -> ConfirmResult.PagamentoCriado(
                financeiroRepository.registrarPagamentoFornecedor(parceiroId, valor),
            )
        }
    }

    private suspend fun confirmEvento(fields: ExtractionFields.EventoDraft): ConfirmResult {
        val recorte = fields.recorteLista.value
        if (recorte != null && fields.titulo.value.isNullOrBlank()) {
            return ConfirmResult.AgendaFiltroAplicado(recorte)
        }
        val titulo = fields.titulo.value?.trim()
            ?: throw ExtractionConfirmException("Título do evento ausente.")
        val dataHora = fields.dataHoraMillis.value
            ?: throw ExtractionConfirmException("Data e hora ausentes.")
        val id = eventoRepository.create(
            titulo = titulo,
            dataHora = dataHora,
            observacao = fields.observacao.value,
            clienteId = fields.cliente?.id,
            fornecedorId = fields.fornecedor?.id,
            notificar = fields.notificar.value != false,
            antecipacaoMinutos = fields.antecipacaoMinutos.value,
            notifTitulo = fields.notifTitulo.value,
            notifCorpo = fields.notifCorpo.value,
        )
        return ConfirmResult.EventoCriado(id)
    }
}
