package br.com.banavita.app.data.repository

import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.EventoDao
import br.com.banavita.app.data.local.dao.FornecedorDao
import br.com.banavita.app.data.local.entity.EventoEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.InvalidDocumentException
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.Evento
import br.com.banavita.app.domain.reminder.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class EventoRepository @Inject constructor(
    private val eventoDao: Provider<EventoDao>,
    private val clienteDao: Provider<ClienteDao>,
    private val fornecedorDao: Provider<FornecedorDao>,
    private val reminderScheduler: ReminderScheduler,
) {
    fun observeAll(): Flow<List<Evento>> =
        eventoDao.get().observeAll().map { rows -> rows.map { it.toDomain() } }

    fun observeFrom(fromMillis: Long): Flow<List<Evento>> =
        eventoDao.get().observeFrom(fromMillis).map { rows -> rows.map { it.toDomain() } }

    suspend fun getById(id: UUID): Evento? =
        eventoDao.get().getById(id)?.toDomain()

    suspend fun isLinkedToOrder(id: UUID): Boolean = eventoDao.get().isLinked(id) > 0

    suspend fun getPendingReminders(fromMillis: Long = nowEpochMillis()): List<Evento> =
        eventoDao.get().getPendingReminders(fromMillis).map { it.toDomain() }

    suspend fun create(
        titulo: String,
        dataHora: Long,
        observacao: String?,
        clienteId: UUID?,
        fornecedorId: UUID?,
        notificar: Boolean = true,
        antecipacaoMinutos: Int? = null,
        notifTitulo: String? = null,
        notifCorpo: String? = null,
        tipo: br.com.banavita.app.domain.model.EventoTipo = br.com.banavita.app.domain.model.EventoTipo.LEMBRETE,
        status: br.com.banavita.app.domain.model.EventoStatus = br.com.banavita.app.domain.model.EventoStatus.PENDENTE,
        prioridade: br.com.banavita.app.domain.model.Prioridade = br.com.banavita.app.domain.model.Prioridade.MEDIA,
    ): UUID {
        validate(titulo, clienteId, fornecedorId, antecipacaoMinutos)
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        eventoDao.get().insert(
            EventoEntity(
                id = id,
                createdAt = timestamp,
                updatedAt = timestamp,
                titulo = titulo.trim(),
                dataHora = dataHora,
                observacao = observacao?.trim()?.takeIf { it.isNotEmpty() },
                clienteId = clienteId,
                fornecedorId = fornecedorId,
                notificar = notificar,
                antecipacaoMinutos = antecipacaoMinutos,
                notifTitulo = notifTitulo?.trim()?.takeIf { it.isNotEmpty() },
                notifCorpo = notifCorpo?.trim()?.takeIf { it.isNotEmpty() },
                tipo = tipo.name,
                status = status.name,
                prioridade = prioridade.name,
            ),
        )
        refreshReminder(id)
        return id
    }

    suspend fun update(
        id: UUID,
        titulo: String,
        dataHora: Long,
        observacao: String?,
        clienteId: UUID?,
        fornecedorId: UUID?,
        notificar: Boolean,
        antecipacaoMinutos: Int?,
        notifTitulo: String?,
        notifCorpo: String?,
        tipo: br.com.banavita.app.domain.model.EventoTipo? = null,
        status: br.com.banavita.app.domain.model.EventoStatus? = null,
        prioridade: br.com.banavita.app.domain.model.Prioridade? = null,
    ) {
        val existing = eventoDao.get().getById(id)
            ?: throw ReferencedEntityException("Evento $id não encontrado.")
        if (eventoDao.get().isLinked(id) > 0 && (tipo != null && tipo.name != existing.tipo || status != null && status.name != existing.status)) throw InvalidDocumentException("Altere a etapa pela encomenda vinculada.")
        validate(titulo, clienteId, fornecedorId, antecipacaoMinutos)
        eventoDao.get().update(
            existing.copy(
                updatedAt = nowEpochMillis(),
                titulo = titulo.trim(),
                dataHora = dataHora,
                observacao = observacao?.trim()?.takeIf { it.isNotEmpty() },
                clienteId = clienteId,
                fornecedorId = fornecedorId,
                notificar = notificar,
                antecipacaoMinutos = antecipacaoMinutos,
                notifTitulo = notifTitulo?.trim()?.takeIf { it.isNotEmpty() },
                notifCorpo = notifCorpo?.trim()?.takeIf { it.isNotEmpty() },
                tipo = tipo?.name ?: existing.tipo,
                status = status?.name ?: existing.status,
                prioridade = prioridade?.name ?: existing.prioridade,
            ),
        )
        refreshReminder(id)
    }

    suspend fun refreshReminder(id: UUID) {
        getById(id)?.let { if (it.status == br.com.banavita.app.domain.model.EventoStatus.CONCLUIDO) reminderScheduler.cancel(id) else reminderScheduler.schedule(it) }
    }

    suspend fun delete(id: UUID) {
        if (eventoDao.get().isLinked(id) > 0) throw InvalidDocumentException("Exclua pela encomenda vinculada.")
        val existing = eventoDao.get().getById(id)
            ?: throw ReferencedEntityException("Evento $id não encontrado.")
        reminderScheduler.cancel(id)
        eventoDao.get().delete(existing)
    }

    private suspend fun validate(
        titulo: String,
        clienteId: UUID?,
        fornecedorId: UUID?,
        antecipacaoMinutos: Int?,
    ) {
        if (titulo.isBlank()) {
            throw InvalidDocumentException("Informe o título do evento.")
        }
        if (clienteId != null && fornecedorId != null) {
            throw InvalidDocumentException("Vincule o evento a cliente ou fornecedor, não aos dois.")
        }
        if (clienteId != null && clienteDao.get().getById(clienteId) == null) {
            throw ReferencedEntityException("Cliente $clienteId não encontrado.")
        }
        if (fornecedorId != null && fornecedorDao.get().getById(fornecedorId) == null) {
            throw ReferencedEntityException("Fornecedor $fornecedorId não encontrado.")
        }
        if (antecipacaoMinutos != null && antecipacaoMinutos !in setOf(15, 60, 1440)) {
            throw InvalidDocumentException("Antecipação deve ser 15 min, 1 h ou 1 dia.")
        }
    }
}
