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

        val documentoFactory = DocumentoItemFactory(database.produtoDao())
        val estoqueValidator = EstoqueValidator(database.estoqueDao())

        produtoRepository = ProdutoRepository(database.produtoDao(), database.grupoDao())
        clienteRepository = ClienteRepository(database.clienteDao())
        val fornecedorRepository = FornecedorRepository(database.fornecedorDao())
        val grupoRepository = GrupoRepository(database.grupoDao())
        val vendaRepository = VendaRepository(
            database.vendaDao(),
            database.clienteDao(),
            documentoFactory,
            estoqueValidator,
        )
        val compraRepository = CompraRepository(
            database.compraDao(),
            database.fornecedorDao(),
            documentoFactory,
        )

        val entityResolver = EntityResolver(
            database.produtoDao(),
            database.clienteDao(),
            database.fornecedorDao(),
            database.grupoDao(),
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
            database.compraDao(),
            database.fornecedorDao(),
            DocumentoItemFactory(database.produtoDao()),
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
}
