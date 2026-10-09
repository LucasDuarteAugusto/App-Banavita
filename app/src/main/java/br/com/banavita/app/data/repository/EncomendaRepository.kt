package br.com.banavita.app.data.repository

import androidx.room.withTransaction
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.EncomendaDao
import br.com.banavita.app.data.local.entity.EncomendaEntity
import br.com.banavita.app.data.local.entity.EncomendaItemEntity
import br.com.banavita.app.data.local.relation.EncomendaWithItems
import br.com.banavita.app.domain.exception.InsufficientStockException
import br.com.banavita.app.domain.exception.InvalidDocumentException
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.domain.model.Encomenda
import br.com.banavita.app.domain.model.EncomendaStatus
import br.com.banavita.app.domain.model.MaturacaoEstado
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class EncomendaRepository @Inject constructor(
    private val database: Provider<BanavitaDatabase>,
    private val encomendaDao: Provider<EncomendaDao>,
    private val clienteDao: Provider<ClienteDao>,
    private val produtoDao: Provider<br.com.banavita.app.data.local.dao.ProdutoDao>,
    private val loteRepository: LoteRepository,
    private val vendaRepository: VendaRepository,
    private val eventoRepository: Provider<EventoRepository>,
) {
    fun observeAll(): Flow<List<Encomenda>> =
        encomendaDao.get().observeAllWithItems().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: UUID): Encomenda? =
        encomendaDao.get().getWithItems(id)?.toDomain()

    suspend fun create(
        clienteId: UUID,
        dataEntrega: Long,
        itens: List<DocumentoItemInput>,
        notificar: Boolean = false,
        antecipacaoMinutos: Int? = null,
    ): UUID {
        if (clienteDao.get().getById(clienteId) == null) {
            throw ReferencedEntityException("Cliente $clienteId não encontrado.")
        }
        if (itens.isEmpty()) {
            throw InvalidDocumentException("Informe ao menos um item na encomenda.")
        }
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        val itemEntities = itens.map { input ->
            val estado = input.estado
                ?: throw InvalidDocumentException("Informe o estado de cada item da encomenda.")
            if (input.quantidade <= 0) {
                throw InvalidDocumentException("Quantidade deve ser maior que zero.")
            }
            EncomendaItemEntity(
                id = UUID.randomUUID(),
                createdAt = timestamp,
                updatedAt = timestamp,
                encomendaId = id,
                produtoId = input.produtoId,
                estado = estado.name,
                quantidade = input.quantidade,
                valorUnitarioCentavos = input.valorUnitarioCentavos,
                unidade = input.unidade ?: produtoDao.get().getById(input.produtoId)?.unidade,
            )
        }

        database.get().withTransaction {
            val eventoId = gerarEventoDaEncomenda(clienteId, dataEntrega, itens, notificar, antecipacaoMinutos)
            encomendaDao.get().insertComItens(
                EncomendaEntity(
                    id = id,
                    createdAt = timestamp,
                    updatedAt = timestamp,
                    clienteId = clienteId,
                    dataEntrega = dataEntrega,
                    status = EncomendaStatus.ABERTO.name,
                    vendaId = null,
                    eventoId = eventoId,
                ),
                itemEntities,
            )
        }
        return id
    }

    suspend fun update(
        id: UUID,
        clienteId: UUID,
        dataEntrega: Long,
        itens: List<DocumentoItemInput>,
        notificar: Boolean = false,
        antecipacaoMinutos: Int? = null,
    ) {
        val existing = encomendaDao.get().getWithItems(id)
            ?: throw ReferencedEntityException("Encomenda $id não encontrada.")
        if (existing.encomenda.status == EncomendaStatus.ENTREGUE.name || existing.itens.any { it.quantidadeEntregue > 0 }) {
            throw InvalidDocumentException("Encomenda já atendida não pode ser alterada.")
        }
        database.get().withTransaction {
            val timestamp = nowEpochMillis()
            val itemEntities = itens.map { input ->
                val estado = input.estado
                    ?: throw InvalidDocumentException("Informe o estado de cada item da encomenda.")
                if (input.quantidade <= 0) {
                    throw InvalidDocumentException("Quantidade deve ser maior que zero.")
                }
                EncomendaItemEntity(
                    id = UUID.randomUUID(),
                    createdAt = timestamp,
                    updatedAt = timestamp,
                    encomendaId = id,
                    produtoId = input.produtoId,
                    estado = estado.name,
                    quantidade = input.quantidade,
                    valorUnitarioCentavos = input.valorUnitarioCentavos,
                unidade = input.unidade ?: produtoDao.get().getById(input.produtoId)?.unidade,
                )
            }

            val eventoId = gerarEventoDaEncomenda(
                clienteId, 
                dataEntrega, 
                itens, 
                notificar, 
                antecipacaoMinutos, 
                existing.encomenda.eventoId
            )

            encomendaDao.get().deleteItens(id)
            encomendaDao.get().update(
                existing.encomenda.copy(
                    clienteId = clienteId,
                    dataEntrega = dataEntrega,
                    updatedAt = timestamp,
                    eventoId = eventoId,
                ),
            )
            encomendaDao.get().insertItens(itemEntities)
        }
    }

    private suspend fun gerarEventoDaEncomenda(
        clienteId: UUID,
        dataEntrega: Long,
        itens: List<DocumentoItemInput>,
        notificar: Boolean,
        antecipacaoMinutos: Int?,
        existingEventoId: UUID? = null
    ): UUID {
        val cliente = clienteDao.get().getById(clienteId)
        val titulo = "Encomenda - ${cliente?.nome ?: "Cliente"}"
        val lines = mutableListOf<String>()
        for (input in itens) {
            val p = produtoDao.get().getById(input.produtoId)
            lines += "${p?.descricao ?: "Produto"} - ${input.estado?.name ?: ""}: ${input.quantidade} ${p?.unidade ?: ""}"
        }
        val obs = lines.joinToString("\n")
        
        return if (existingEventoId == null) {
            eventoRepository.get().create(
                titulo = titulo,
                dataHora = dataEntrega,
                observacao = obs,
                clienteId = clienteId,
                fornecedorId = null,
                notificar = notificar,
                antecipacaoMinutos = antecipacaoMinutos,
                notifTitulo = titulo,
                tipo = br.com.banavita.app.domain.model.EventoTipo.ENTREGA,
                notifCorpo = obs
            )
        } else {
            eventoRepository.get().update(
                id = existingEventoId,
                titulo = titulo,
                dataHora = dataEntrega,
                observacao = obs,
                clienteId = clienteId,
                fornecedorId = null,
                notificar = notificar,
                antecipacaoMinutos = antecipacaoMinutos,
                notifTitulo = titulo,
                notifCorpo = obs
            )
            existingEventoId
        }
    }

    suspend fun delete(id: UUID) {
        val existing = encomendaDao.get().getWithItems(id) ?: return
        if (existing.encomenda.status != EncomendaStatus.ABERTO.name || existing.itens.any { it.quantidadeEntregue > 0 }) {
            throw InvalidDocumentException("Encomenda já atendida não pode ser excluída.")
        }
        database.get().withTransaction {
            encomendaDao.get().delete(id)
            existing.encomenda.eventoId?.let { eventoRepository.get().delete(it) }
        }
    }

    /**
     * Gera uma venda com os itens da encomenda.
     * [permitirParcial]: se faltar saldo, reduz a quantidade ao disponível (com confirmação na UI).
     */
    suspend fun mudarEtapa(id: UUID, status: EncomendaStatus) {
        if (status == EncomendaStatus.ENTREGUE) throw InvalidDocumentException("Confirme a entrega para gerar a venda.")
        database.get().withTransaction {
            val current = encomendaDao.get().getWithItems(id) ?: throw ReferencedEntityException("Encomenda não encontrada.")
            if (!EncomendaStatus.valueOf(current.encomenda.status).pendente) throw InvalidDocumentException("Encomenda entregue.")
            if (status.ordinal != EncomendaStatus.valueOf(current.encomenda.status).ordinal + 1) throw InvalidDocumentException("Avance uma etapa por vez.")
            encomendaDao.get().update(current.encomenda.copy(status = status.name, updatedAt = nowEpochMillis()))
            current.encomenda.eventoId?.let { database.get().dashboardDao().syncDeliveryEvent(it, if (status == EncomendaStatus.EM_ROTA) "EM_ANDAMENTO" else "PENDENTE", nowEpochMillis()) }
        }
    }

    suspend fun atender(
        id: UUID, aPrazo: Boolean, permitirParcial: Boolean,
        vencimento: Long? = null, requestId: UUID = UUID.randomUUID(),
        quantidades: Map<UUID, Double>? = null,
    ): UUID {
        val result = database.get().withTransaction {
            val history = database.get().dashboardDao().delivery(requestId)
            if (history.isNotEmpty()) return@withTransaction history.first().vendaId
            val existing = encomendaDao.get().getWithItems(id) ?: throw ReferencedEntityException("Encomenda não encontrada.")
            if (existing.encomenda.status == EncomendaStatus.ENTREGUE.name) throw InvalidDocumentException("Esta encomenda já foi entregue.")
            val now = nowEpochMillis()
            if (quantidades != null && quantidades.any { (itemId, qty) -> !qty.isFinite() || qty < 0 || existing.itens.none { it.id == itemId } }) {
                throw InvalidDocumentException("Quantidades de entrega inválidas.")
            }
            val inputs = mutableListOf<DocumentoItemInput>()
            val delivered = mutableMapOf<UUID, Double>()
            val available = mutableMapOf<Triple<UUID, String, String?>, Double>()
            existing.itens.forEach { item ->
                val pending = (item.quantidade - item.quantidadeEntregue).coerceAtLeast(0.0)
                if (pending <= 1e-9) return@forEach
                val requested = quantidades?.get(item.id) ?: if (quantidades == null) pending else 0.0
                if (requested > pending + 1e-9) throw InvalidDocumentException("A entrega excede a quantidade pendente.")
                if (requested <= 1e-9) return@forEach
                val key = Triple(item.produtoId, item.estado, item.unidade)
                val stock = available[key] ?: loteRepository.saldoPorEstado(item.produtoId, MaturacaoEstado.valueOf(item.estado), item.unidade)
                val qty = if (permitirParcial) minOf(requested, stock) else requested
                if (qty > stock + 1e-9) throw InsufficientStockException(item.produtoId.toString(), stock, requested)
                available[key] = (stock - qty).coerceAtLeast(0.0)
                if (qty > 1e-9) {
                    inputs += DocumentoItemInput(item.produtoId, qty, item.valorUnitarioCentavos, MaturacaoEstado.valueOf(item.estado), item.unidade)
                    delivered[item.id] = qty
                }
            }
            if (inputs.isEmpty()) throw InvalidDocumentException("Não há saldo para entregar os itens pendentes.")
            val sale = vendaRepository.create(now, existing.encomenda.clienteId, inputs, aPrazo, vencimento)
            existing.itens.forEach { item -> delivered[item.id]?.let { qty ->
                encomendaDao.get().updateItem(item.copy(quantidadeEntregue = item.quantidadeEntregue + qty, updatedAt = now))
            } }
            val complete = existing.itens.all { it.quantidadeEntregue + (delivered[it.id] ?: 0.0) >= it.quantidade - 1e-9 }
            encomendaDao.get().update(existing.encomenda.copy(status = if (complete) "ENTREGUE" else "ABERTO", vendaId = sale, updatedAt = now))
            database.get().dashboardDao().insertDelivery(delivered.map { (item, qty) -> br.com.banavita.app.data.local.entity.EntregaItemEntity(requestId, item, sale, qty, now) })
            existing.encomenda.eventoId?.let { database.get().dashboardDao().syncDeliveryEvent(it, if (complete) "CONCLUIDO" else "PENDENTE", now) }
            sale
        }
        getById(id)?.eventoId?.let { eventoRepository.get().refreshReminder(it) }
        return result
    }

    suspend fun obterTotalEncomendadoAberto(produtoId: UUID, estado: MaturacaoEstado): Double =
        encomendaDao.get().totalAbertoPorProdutoEstado(produtoId, estado.name)

    suspend fun historicoEntregas(id: UUID): List<br.com.banavita.app.data.local.entity.EntregaItemEntity> =
        database.get().dashboardDao().deliveryHistory(id)

    suspend fun getAbertasDoProduto(produtoId: UUID): List<Encomenda> =
        encomendaDao.get().observeAllWithItems().map { list -> 
            list.map { it.toDomain() }
                .filter { it.status.pendente }
                .filter { it.itens.any { item -> item.produtoId == produtoId } }
        }.first()
}

fun EncomendaWithItems.toDomain(): Encomenda =
    Encomenda(
        id = encomenda.id,
        createdAt = encomenda.createdAt,
        updatedAt = encomenda.updatedAt,
        clienteId = encomenda.clienteId,
        dataEntrega = encomenda.dataEntrega,
        status = EncomendaStatus.valueOf(encomenda.status),
        vendaId = encomenda.vendaId,
        eventoId = encomenda.eventoId,
        itens = itens.map { item ->
            br.com.banavita.app.domain.model.EncomendaItem(
                id = item.id,
                encomendaId = item.encomendaId,
                produtoId = item.produtoId,
                estado = MaturacaoEstado.valueOf(item.estado),
                quantidade = item.quantidade,
                quantidadeEntregue = item.quantidadeEntregue,
                unidade = item.unidade,
                valorUnitarioCentavos = item.valorUnitarioCentavos,
            )
        },
    )
