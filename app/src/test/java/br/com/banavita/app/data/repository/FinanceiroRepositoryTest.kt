package br.com.banavita.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.domain.exception.OverpaymentException
import br.com.banavita.app.domain.model.DocumentoItemInput
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Provider

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class FinanceiroRepositoryTest {

    private lateinit var database: BanavitaDatabase
    private lateinit var vendaRepository: VendaRepository
    private lateinit var compraRepository: CompraRepository
    private lateinit var financeiroRepository: FinanceiroRepository
    private lateinit var produtoRepository: ProdutoRepository
    private lateinit var clienteRepository: ClienteRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, BanavitaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val graph = br.com.banavita.app.TestRepositories(context, database)
        produtoRepository = ProdutoRepository(
            Provider { database.produtoDao() },
            Provider { database.grupoDao() },
        )
        clienteRepository = ClienteRepository(Provider { database.clienteDao() })
        vendaRepository = graph.vendaRepository
        compraRepository = graph.compraRepository
        financeiroRepository = FinanceiroRepository(
            Provider { database.vendaDao() },
            Provider { database.compraDao() },
            Provider { database.recebimentoDao() },
            Provider { database.pagamentoDao() },
            Provider { database.clienteDao() },
            Provider { database.fornecedorDao() },
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `a vista nao entra em a receber`() = runBlocking {
        val produtoId = produtoRepository.create("Banana", 350, null, "KG")
        compraRepository.create(
            data = 1_700_000_000_000L,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 5.0, 200)),
        )
        val clienteId = clienteRepository.create("Joao", null)
        vendaRepository.create(
            data = 1_700_000_000_000L,
            clienteId = clienteId,
            itens = listOf(DocumentoItemInput(produtoId, 1.0)),
            aPrazo = false,
        )
        val abertos = financeiroRepository.observeSaldosAReceber().first()
        assertTrue(abertos.none { it.parceiroId == clienteId })
    }

    @Test
    fun `a prazo com baixa parcial reduz saldo`() = runBlocking {
        val produtoId = produtoRepository.create("Banana", 1000, null, "KG")
        compraRepository.create(
            data = 1_700_000_000_000L,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 5.0, 200)),
        )
        val clienteId = clienteRepository.create("Joao", null)
        val vendaId = vendaRepository.create(
            data = 1_700_000_000_000L,
            clienteId = clienteId,
            itens = listOf(DocumentoItemInput(produtoId, 1.0, 1000)),
            aPrazo = true,
            vencimento = System.currentTimeMillis() + 86_400_000L,
        )
        val total = vendaRepository.getById(vendaId)!!.valorTotalCentavos
        financeiroRepository.registrarRecebimento(vendaId, total / 2)
        val saldo = financeiroRepository.saldoAbertoVenda(vendaId)
        assertEquals(total - total / 2, saldo)
        val abertos = financeiroRepository.observeSaldosAReceber().first()
        assertEquals(1, abertos.size)
        assertEquals(saldo, abertos.first().saldoCentavos)
    }

    @Test(expected = OverpaymentException::class)
    fun testBaixaMaiorQueSaldoFalha() {
        runBlocking {
            val produtoId = produtoRepository.create("Banana", 1000, null, "KG")
            compraRepository.create(
                data = 1_700_000_000_000L,
                fornecedorId = null,
                itens = listOf(DocumentoItemInput(produtoId, 5.0, 200)),
            )
            val clienteId = clienteRepository.create("Joao", null)
            val vendaId = vendaRepository.create(
                data = 1_700_000_000_000L,
                clienteId = clienteId,
                itens = listOf(DocumentoItemInput(produtoId, 1.0, 1000)),
                aPrazo = true,
            vencimento = System.currentTimeMillis() + 86_400_000L,
            )
            val total = vendaRepository.getById(vendaId)!!.valorTotalCentavos
            financeiroRepository.registrarRecebimento(vendaId, total + 1)
        }
    }
}
