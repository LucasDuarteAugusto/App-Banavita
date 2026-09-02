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
import javax.inject.Singleton

@Singleton
class EventoRepository @Inject constructor(
    private val eventoDao: EventoDao,
    private val clienteDao: ClienteDao,
    private val fornecedorDao: FornecedorDao,
    private val reminderScheduler: ReminderScheduler,
) {
    fun observeAll(): Flow<List<Evento>> =
        eventoDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    fun observeFrom(fromMillis: Long): Flow<List<Evento>> =
        eventoDao.observeFrom(fromMillis).map { rows -> rows.map { it.toDomain() } }

    suspend fun getById(id: UUID): Evento? =
        eventoDao.getById(id)?.toDomain()

    suspend fun getPendingReminders(fromMillis: Long = nowEpochMillis()): List<Evento> =
        eventoDao.getPendingReminders(fromMillis).map { it.toDomain() }

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
    ): UUID {
        validate(titulo, clienteId, fornecedorId, antecipacaoMinutos)
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        eventoDao.insert(
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
            ),
        )
        getById(id)?.let { reminderScheduler.schedule(it) }
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
    ) {
        val existing = eventoDao.getById(id)
            ?: throw ReferencedEntityException("Evento $id não encontrado.")
        validate(titulo, clienteId, fornecedorId, antecipacaoMinutos)
        eventoDao.update(
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
            ),
        )
        getById(id)?.let { reminderScheduler.schedule(it) }
    }

    suspend fun delete(id: UUID) {
        val existing = eventoDao.getById(id)
            ?: throw ReferencedEntityException("Evento $id não encontrado.")
        reminderScheduler.cancel(id)
        eventoDao.delete(existing)
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
        if (clienteId != null && clienteDao.getById(clienteId) == null) {
            throw ReferencedEntityException("Cliente $clienteId não encontrado.")
        }
        if (fornecedorId != null && fornecedorDao.getById(fornecedorId) == null) {
            throw ReferencedEntityException("Fornecedor $fornecedorId não encontrado.")
        }
        if (antecipacaoMinutos != null && antecipacaoMinutos !in setOf(15, 60, 1440)) {
            throw InvalidDocumentException("Antecipação deve ser 15 min, 1 h ou 1 dia.")
        }
    }
}
