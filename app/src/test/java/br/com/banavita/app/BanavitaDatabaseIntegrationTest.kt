package br.com.banavita.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.CompraDao
import br.com.banavita.app.data.local.dao.EstoqueDao
import br.com.banavita.app.data.local.dao.FornecedorDao
import br.com.banavita.app.data.local.dao.GrupoDao
import br.com.banavita.app.data.local.dao.ProdutoDao
import br.com.banavita.app.data.local.dao.VendaDao
import br.com.banavita.app.data.local.entity.ClienteEntity
import br.com.banavita.app.data.local.entity.CompraEntity
import br.com.banavita.app.data.local.entity.CompraItemEntity
import br.com.banavita.app.data.local.entity.FornecedorEntity
import br.com.banavita.app.data.local.entity.GrupoEntity
import br.com.banavita.app.data.local.entity.ProdutoEntity
import br.com.banavita.app.data.local.entity.VendaEntity
import br.com.banavita.app.data.local.entity.VendaItemEntity
import br.com.banavita.app.data.repository.ClienteRepository
import br.com.banavita.app.data.repository.CompraRepository
import br.com.banavita.app.data.repository.DocumentoItemFactory
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.EstoqueValidator
import br.com.banavita.app.data.repository.GrupoRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.data.repository.VendaRepository
import br.com.banavita.app.domain.exception.InsufficientStockException
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.DocumentoItemInput
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
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class BanavitaDatabaseIntegrationTest {

    private lateinit var database: BanavitaDatabase
    private lateinit var grupoDao: GrupoDao
    private lateinit var produtoDao: ProdutoDao
    private lateinit var clienteDao: ClienteDao
    private lateinit var fornecedorDao: FornecedorDao
    private lateinit var vendaDao: VendaDao
    private lateinit var compraDao: CompraDao
    private lateinit var estoqueDao: EstoqueDao

    private lateinit var produtoRepository: ProdutoRepository
    private lateinit var grupoRepository: GrupoRepository
    private lateinit var clienteRepository: ClienteRepository
    private lateinit var compraRepository: CompraRepository
    private lateinit var vendaRepository: VendaRepository
    private lateinit var estoqueRepository: EstoqueRepository

    private val timestamp = 1_700_000_000_000L

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, BanavitaDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        grupoDao = database.grupoDao()
        produtoDao = database.produtoDao()
        clienteDao = database.clienteDao()
        fornecedorDao = database.fornecedorDao()
        vendaDao = database.vendaDao()
        compraDao = database.compraDao()
        estoqueDao = database.estoqueDao()

        val documentoItemFactory = DocumentoItemFactory(produtoDao)
        val estoqueValidator = EstoqueValidator(estoqueDao)

        grupoRepository = GrupoRepository(grupoDao)
        produtoRepository = ProdutoRepository(produtoDao, grupoDao)
        clienteRepository = ClienteRepository(clienteDao)
        compraRepository = CompraRepository(compraDao, fornecedorDao, documentoItemFactory)
        vendaRepository = VendaRepository(vendaDao, clienteDao, documentoItemFactory, estoqueValidator)
        estoqueRepository = EstoqueRepository(estoqueDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `compra aumenta saldo e venda consome estoque`() = runBlocking {
        val produtoId = produtoRepository.create("Banana", 350, null, "KG")

        compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(
                DocumentoItemInput(
                    produtoId = produtoId,
                    quantidade = 10.0,
                    valorUnitarioCentavos = 200,
                ),
            ),
        )

        assertEquals(10.0, estoqueRepository.getSaldo(produtoId), 0.0001)

        vendaRepository.create(
            data = timestamp,
            clienteId = null,
            itens = listOf(
                DocumentoItemInput(
                    produtoId = produtoId,
                    quantidade = 3.5,
                ),
            ),
        )

        assertEquals(6.5, estoqueRepository.getSaldo(produtoId), 0.0001)
    }

    @Test(expected = InsufficientStockException::class)
    fun `venda bloqueia estoque insuficiente`() = runBlocking {
        val produtoId = produtoRepository.create("Banana", 350, null, "KG")

        vendaRepository.create(
            data = timestamp,
            clienteId = null,
            itens = listOf(
                DocumentoItemInput(
                    produtoId = produtoId,
                    quantidade = 1.0,
                ),
            ),
        )
    }

    @Test
    fun `delete venda restaura saldo via view`() = runBlocking {
        val produtoId = produtoRepository.create("Banana", 350, null, "KG")
        compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(
                DocumentoItemInput(produtoId, 5.0, 200),
            ),
        )
        val vendaId = vendaRepository.create(
            data = timestamp,
            clienteId = null,
            itens = listOf(
                DocumentoItemInput(produtoId, 2.0),
            ),
        )
        assertEquals(3.0, estoqueRepository.getSaldo(produtoId), 0.0001)

        vendaRepository.delete(vendaId)

        assertEquals(5.0, estoqueRepository.getSaldo(produtoId), 0.0001)
    }

    @Test
    fun `delete compra remove itens em cascade`() = runBlocking {
        val produtoId = produtoRepository.create("Banana", 350, null, "KG")
        val compraId = compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(
                DocumentoItemInput(produtoId, 4.0, 200),
            ),
        )

        compraRepository.delete(compraId)

        val compra = compraDao.getWithItems(compraId)
        assertEquals(null, compra)
        assertEquals(0.0, estoqueRepository.getSaldo(produtoId), 0.0001)
    }

    @Test(expected = ReferencedEntityException::class)
    fun `delete produto bloqueado quando referenciado`() = runBlocking {
        val produtoId = produtoRepository.create("Banana", 350, null, "KG")
        compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(
                DocumentoItemInput(produtoId, 1.0, 200),
            ),
        )

        produtoRepository.delete(produtoId)
    }

    @Test(expected = ReferencedEntityException::class)
    fun `delete grupo bloqueado com produtos vinculados`() = runBlocking {
        val grupoId = grupoRepository.create("Frutas", null)
        produtoRepository.create("Banana", 350, grupoId, "KG")

        grupoRepository.delete(grupoId)
    }

    @Test
    fun `transacao persiste cabecalho e itens`() = runBlocking {
        val produtoId = produtoRepository.create("Banana", 350, null, "KG")
        compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(
                DocumentoItemInput(produtoId, 2.0, 200),
            ),
        )
        val clienteId = clienteRepository.create("Maria", "11999999999")
        val vendaId = vendaRepository.create(
            data = timestamp,
            clienteId = clienteId,
            itens = listOf(
                DocumentoItemInput(produtoId, 1.0),
            ),
        )

        val venda = vendaRepository.getById(vendaId)
        assertNotNull(venda)
        assertEquals(1, venda!!.itens.size)
        assertEquals(clienteId, venda.clienteId)
        assertTrue(venda.valorTotalCentavos > 0)
    }
}
