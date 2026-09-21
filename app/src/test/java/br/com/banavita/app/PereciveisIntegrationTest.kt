package br.com.banavita.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.core.time.RipeningClock
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.repository.EncomendaRepository
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.domain.exception.InsufficientStockException
import br.com.banavita.app.domain.exception.InvalidDocumentException
import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.PerdaMotivo
import br.com.banavita.app.domain.model.RipeningCurve
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Provider

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PereciveisIntegrationTest {

    private lateinit var database: BanavitaDatabase
    private lateinit var graph: TestRepositories
    private lateinit var produtoRepository: ProdutoRepository
    private lateinit var estoqueRepository: EstoqueRepository
    private lateinit var encomendaRepository: EncomendaRepository

    private val timestamp = 1_700_000_000_000L

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, BanavitaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        graph = TestRepositories(context, database)
        produtoRepository = ProdutoRepository(
            Provider { database.produtoDao() },
            Provider { database.grupoDao() },
        )
        estoqueRepository = EstoqueRepository(Provider { database.estoqueDao() })
        encomendaRepository = EncomendaRepository(
            Provider { database },
            Provider { database.encomendaDao() },
            Provider { database.clienteDao() },
            Provider { database.produtoDao() },
            graph.loteRepository,
            graph.vendaRepository,
            Provider { graph.eventoRepository },
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `compra de perecivel cria lote crua na fria e transferencia parte o lote`() = runBlocking {
        val produtoId = nanica()
        graph.compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 800.0, 200)),
        )
        val lotesAntes = database.loteDao().listByProduto(produtoId)
        assertEquals(1, lotesAntes.size)
        assertEquals(Camara.FRIA.name, lotesAntes.single().camara)
        assertEquals(MaturacaoEstado.CRUA.name, lotesAntes.single().estado)
        assertNull(lotesAntes.single().etapaInicioEpochDay)
        assertEquals(800.0, lotesAntes.single().quantidadeRestante, 0.0001)

        graph.loteRepository.transferirParaEtileno(lotesAntes.single().id, 200.0)
        val lotes = database.loteDao().listByProduto(produtoId)
        val fria = lotes.single { it.camara == Camara.FRIA.name }
        val etileno = lotes.single { it.camara == Camara.ETILENO.name }
        assertEquals(600.0, fria.quantidadeRestante, 0.0001)
        assertEquals(200.0, etileno.quantidadeRestante, 0.0001)
        assertEquals(lotesAntes.single().id, etileno.origemId)
        assertEquals(MaturacaoEstado.EM_MATURACAO.name, etileno.estado)
        assertEquals(RipeningClock.todayEpochDay(), etileno.etapaInicioEpochDay)
        assertEquals(800.0, estoqueRepository.getSaldo(produtoId), 0.0001)
    }

    @Test
    fun `relogio avanca so na etileno conforme a curva`() = runBlocking {
        val produtoId = nanica()
        graph.compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 100.0, 200)),
        )
        val origem = database.loteDao().listByProduto(produtoId).single()
        graph.loteRepository.transferirParaEtileno(origem.id, 100.0)
        val etileno = database.loteDao().listByProduto(produtoId)
            .single { it.camara == Camara.ETILENO.name }
        database.loteDao().update(
            etileno.copy(etapaInicioEpochDay = RipeningClock.todayEpochDay() - 4),
        )
        graph.loteRepository.avancarMaturacao()
        val atualizado = database.loteDao().getById(etileno.id)!!
        // Maturacao (1) + Verde (3) = 4 dias. No 4º dia vira Verdolenga.
        assertEquals(MaturacaoEstado.VERDOLENGA.name, atualizado.estado)
        assertEquals(100.0, atualizado.quantidadeRestante, 0.0001)
    }

    @Test
    fun `venda FIFO baixa o lote mais antigo do mesmo estado`() = runBlocking {
        val produtoId = nanica()
        graph.compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 100.0, 200)),
        )
        graph.compraRepository.create(
            data = timestamp + 1,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 50.0, 200)),
        )
        graph.vendaRepository.create(
            data = timestamp,
            clienteId = null,
            itens = listOf(
                DocumentoItemInput(produtoId, 120.0, estado = MaturacaoEstado.CRUA),
            ),
        )
        val lotes = database.loteDao().listByProduto(produtoId).sortedBy { it.dataEntrada }
        assertEquals(0.0, lotes[0].quantidadeRestante, 0.0001)
        assertEquals(30.0, lotes[1].quantidadeRestante, 0.0001)
        assertEquals(30.0, estoqueRepository.getSaldo(produtoId), 0.0001)
    }

    @Test(expected = InsufficientStockException::class)
    fun testVendaRecusaSaldoInsuficienteDaqueleEstado() {
        runBlocking {
            val produtoId = nanica()
            graph.compraRepository.create(
                data = timestamp,
                fornecedorId = null,
                itens = listOf(DocumentoItemInput(produtoId, 10.0, 200)),
            )
            graph.vendaRepository.create(
                data = timestamp,
                clienteId = null,
                itens = listOf(
                    DocumentoItemInput(produtoId, 1.0, estado = MaturacaoEstado.AMARELA),
                ),
            )
        }
    }

    @Test(expected = InvalidDocumentException::class)
    fun testVendaDePerecivelExigeEstado() {
        runBlocking {
            val produtoId = nanica()
            graph.compraRepository.create(
                data = timestamp,
                fornecedorId = null,
                itens = listOf(DocumentoItemInput(produtoId, 10.0, 200)),
            )
            graph.vendaRepository.create(
                data = timestamp,
                clienteId = null,
                itens = listOf(DocumentoItemInput(produtoId, 1.0)),
            )
        }
    }

    @Test
    fun `perda some do lote e nao conta como venda`() = runBlocking {
        val produtoId = nanica()
        graph.compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 100.0, 250)),
        )
        val loteId = database.loteDao().listByProduto(produtoId).single().id
        graph.loteRepository.registrarPerda(loteId, 10.0, PerdaMotivo.APODRECEU, timestamp)
        assertEquals(90.0, estoqueRepository.getSaldo(produtoId), 0.0001)
        assertEquals(0.0, database.estoqueDao().totalVendido(produtoId, null), 0.0001)
        val perdas = database.perdaDao().listDoProduto(produtoId)
        assertEquals(1, perdas.size)
        assertEquals(2500L, perdas.single().valorCentavos)
        assertEquals(10.0, perdas.single().quantidade, 0.0001)
    }

    @Test
    fun `queima vale so no dia local`() = runBlocking {
        val produtoId = nanica()
        graph.loteRepository.aplicarQueima(produtoId, MaturacaoEstado.PINTADA, 99)
        val now = System.currentTimeMillis()
        assertEquals(99L, graph.loteRepository.precoVenda(produtoId, MaturacaoEstado.PINTADA, now))
        val amanha = PeriodRanges.startOfDay(LocalDate.now(ZoneId.systemDefault()).plusDays(1))
        assertEquals(350L, graph.loteRepository.precoVenda(produtoId, MaturacaoEstado.PINTADA, amanha))
        assertEquals(350L, graph.loteRepository.precoVenda(produtoId, MaturacaoEstado.AMARELA, now))
    }

    @Test
    fun `previsao ignora lote na fria e entra so apos soltar`() = runBlocking {
        val produtoId = nanica()
        graph.compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 80.0, 200)),
        )
        assertTrue(graph.loteRepository.previsao().none { it.produtoId == produtoId && it.kgEm3Dias > 0 })
        val loteId = database.loteDao().listByProduto(produtoId).single().id
        graph.loteRepository.transferirParaEtileno(loteId, 80.0)
        val oferta = graph.loteRepository.previsao().filter { it.produtoId == produtoId }
        assertTrue(oferta.any { it.kgEm3Dias + it.kgEm7Dias > 0 })
    }

    @Test
    fun `encomenda atender gera venda se houver saldo do estado`() = runBlocking {
        val produtoId = nanica()
        val clienteId = br.com.banavita.app.data.repository.ClienteRepository(
            Provider { database.clienteDao() },
        ).create("Padaria", null)
        graph.compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 50.0, 200)),
        )
        val encomendaId = encomendaRepository.create(
            clienteId = clienteId,
            dataEntrega = timestamp,
            itens = listOf(DocumentoItemInput(produtoId, 20.0, estado = MaturacaoEstado.CRUA)),
        )
        val vendaId = encomendaRepository.atender(encomendaId, aPrazo = false, permitirParcial = false)
        val venda = graph.vendaRepository.getById(vendaId)!!
        assertEquals(1, venda.itens.size)
        assertEquals(20.0, venda.itens.single().quantidade, 0.0001)
        assertEquals(30.0, estoqueRepository.getSaldo(produtoId), 0.0001)
    }

    @Test
    fun `nao perecivel continua com saldo compras menos vendas`() = runBlocking {
        val produtoId = produtoRepository.create("Caixa", 100, null, "UN", perecivel = false)
        graph.compraRepository.create(
            data = timestamp,
            fornecedorId = null,
            itens = listOf(DocumentoItemInput(produtoId, 5.0, 100)),
        )
        assertTrue(database.loteDao().listByProduto(produtoId).isEmpty())
        graph.vendaRepository.create(
            data = timestamp,
            clienteId = null,
            itens = listOf(DocumentoItemInput(produtoId, 2.0)),
        )
        assertEquals(3.0, estoqueRepository.getSaldo(produtoId), 0.0001)
    }

    private suspend fun nanica() = produtoRepository.create(
        descricao = "Banana-nanica",
        precoCentavos = 350,
        grupoId = null,
        unidade = "KG",
        perecivel = true,
        curva = RipeningCurve.DEFAULT,
    )
}
