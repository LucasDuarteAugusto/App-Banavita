package br.com.banavita.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.core.extraction.EntityResolver
import br.com.banavita.app.core.extraction.ExtractionPipeline
import br.com.banavita.app.data.cache.ExtractionSessionCache
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.domain.model.extraction.ConfirmResult
import br.com.banavita.app.domain.model.extraction.ExtractionFields
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class ExtractionRepositoryTest {

    private lateinit var database: BanavitaDatabase
    private lateinit var repository: ExtractionRepository
    private lateinit var produtoRepository: ProdutoRepository
    private lateinit var clienteRepository: ClienteRepository
    private lateinit var sessionCache: ExtractionSessionCache

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, BanavitaDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        val documentoFactory = DocumentoItemFactory(Provider { database.produtoDao() })
        val estoqueValidator = EstoqueValidator(Provider { database.estoqueDao() })

        produtoRepository = ProdutoRepository(
            Provider { database.produtoDao() },
            Provider { database.grupoDao() },
        )
        clienteRepository = ClienteRepository(Provider { database.clienteDao() })
        val fornecedorRepository = FornecedorRepository(Provider { database.fornecedorDao() })
        val grupoRepository = GrupoRepository(Provider { database.grupoDao() })
        val vendaRepository = VendaRepository(
            Provider { database },
            Provider { database.vendaDao() },
            Provider { database.clienteDao() },
            documentoFactory,
            estoqueValidator,
            Provider { database.recebimentoDao() },
        )
        val compraRepository = CompraRepository(
            Provider { database },
            Provider { database.compraDao() },
            Provider { database.fornecedorDao() },
            documentoFactory,
            Provider { database.pagamentoDao() },
        )
        val financeiroRepository = FinanceiroRepository(
            Provider { database.vendaDao() },
            Provider { database.compraDao() },
            Provider { database.recebimentoDao() },
            Provider { database.pagamentoDao() },
            Provider { database.clienteDao() },
            Provider { database.fornecedorDao() },
        )
        val eventoRepository = EventoRepository(
            Provider { database.eventoDao() },
            Provider { database.clienteDao() },
            Provider { database.fornecedorDao() },
            br.com.banavita.app.domain.reminder.FakeReminderScheduler(),
        )

        val entityResolver = EntityResolver(
            Provider { database.produtoDao() },
            Provider { database.clienteDao() },
            Provider { database.fornecedorDao() },
            Provider { database.grupoDao() },
        )
        val pipeline = ExtractionPipeline(entityResolver)
        sessionCache = ExtractionSessionCache()

        repository = ExtractionRepository(
            pipeline = pipeline,
            sessionCache = sessionCache,
            vendaRepository = vendaRepository,
            compraRepository = compraRepository,
            clienteRepository = clienteRepository,
            fornecedorRepository = fornecedorRepository,
            produtoRepository = produtoRepository,
            grupoRepository = grupoRepository,
            financeiroRepository = financeiroRepository,
            eventoRepository = eventoRepository,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `extract venda resolve produto e cliente e nao grava no room ate confirm`() = runBlocking {
        val produtoId = produtoRepository.create("Banana Nanica", 1200, null, "KG")
        val clienteId = clienteRepository.create("Maria", null)

        // estoque: compra antes da venda
        val compraRepo = CompraRepository(
            Provider { database },
            Provider { database.compraDao() },
            Provider { database.fornecedorDao() },
            DocumentoItemFactory(Provider { database.produtoDao() }),
            Provider { database.pagamentoDao() },
        )
        compraRepo.create(
            data = System.currentTimeMillis(),
            fornecedorId = null,
            itens = listOf(
                br.com.banavita.app.domain.model.DocumentoItemInput(produtoId, 10.0, 800),
            ),
        )

        val draft = repository.extract(
            transcript = "vende tres quilos de banana nanica pra Maria a doze reais",
            context = VoiceScreenContext.VENDA,
        )

        val fields = draft.fields as ExtractionFields.VendaDraft
        assertEquals(produtoId, fields.itens.first().produtoResolvido?.id)
        assertEquals(clienteId, fields.cliente?.id)
        assertEquals(3.0, fields.itens.first().quantidade.value!!, 0.001)
        assertNotNull(sessionCache.get())

        val result = repository.confirmDraft(draft.id) as ConfirmResult.VendaCriada
        assertNotNull(database.vendaDao().getWithItems(result.vendaId))
        assertEquals(null, sessionCache.get())
    }

    @Test
    fun `extract cliente e confirm cria registro`() = runBlocking {
        val draft = repository.extract(
            transcript = "cadastrar cliente Ana Souza 11999887766",
            context = VoiceScreenContext.CLIENTE,
        )
        assertFalse(draft.needsReview)
        val result = repository.confirmDraft(draft.id) as ConfirmResult.ClienteCriado
        val cliente = clienteRepository.getById(result.clienteId)
        assertNotNull(cliente)
        assertTrue(cliente!!.nome.contains("Ana", ignoreCase = true))
        assertEquals("11999887766", cliente.telefone)
    }

    @Test
    fun `discard limpa sessao sem gravar`() = runBlocking {
        val draft = repository.extract(
            transcript = "grupo frutas",
            context = VoiceScreenContext.GRUPO,
        )
        repository.discardDraft(draft.id)
        assertEquals(null, sessionCache.get())
    }

    @Test
    fun `consulta por voz aplica modo sem gravar venda`() = runBlocking {
        val draft = repository.extract(
            transcript = "vendas desta semana",
            context = VoiceScreenContext.CONSULTAS,
        )
        val result = repository.confirmDraft(draft.id) as ConfirmResult.ConsultaAplicada
        assertEquals(br.com.banavita.app.domain.model.ConsultaModo.VENDAS, result.modo)
        assertEquals(br.com.banavita.app.domain.model.PeriodoPreset.SEMANA, result.periodo)
        assertEquals(null, sessionCache.get())
    }

    @Test
    fun `editar draft no sheet altera busca da consulta`() = runBlocking {
        val draft = repository.extract(
            transcript = "estoque",
            context = VoiceScreenContext.CONSULTAS,
        )
        val fields = draft.fields as ExtractionFields.ConsultasDraft
        val edited = draft.copy(
            fields = fields.copy(
                busca = fields.busca.copy(value = "banana", confidence = 1f),
            ),
        )
        repository.updateDraft(edited)
        val result = repository.confirmDraft(draft.id) as ConfirmResult.ConsultaAplicada
        assertEquals(br.com.banavita.app.domain.model.ConsultaModo.ESTOQUE, result.modo)
        assertEquals("banana", result.busca)
        assertEquals(null, sessionCache.get())
    }
}
