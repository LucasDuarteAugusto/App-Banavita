package br.com.banavita.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.domain.reminder.FakeReminderScheduler
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Provider

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class EventoRepositoryTest {

    private lateinit var database: BanavitaDatabase
    private lateinit var repository: EventoRepository
    private lateinit var scheduler: FakeReminderScheduler

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, BanavitaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        scheduler = FakeReminderScheduler()
        repository = EventoRepository(
            Provider { database.eventoDao() },
            Provider { database.clienteDao() },
            Provider { database.fornecedorDao() },
            scheduler,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `create agenda reminder e delete cancela`() = runBlocking {
        val id = repository.create(
            titulo = "Visita Joao",
            dataHora = System.currentTimeMillis() + 60_000,
            observacao = null,
            clienteId = null,
            fornecedorId = null,
            notificar = true,
            antecipacaoMinutos = 15,
        )
        assertNotNull(repository.getById(id))
        assertTrue(scheduler.scheduled.contains(id))
        repository.delete(id)
        assertTrue(scheduler.cancelled.contains(id))
        assertEquals(0, repository.observeAll().first().size)
    }
}
