package br.com.banavita.app.data.repository

import androidx.room.withTransaction
import br.com.banavita.app.core.time.PeriodRanges
import br.com.banavita.app.core.time.RipeningClock
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.dao.LoteDao
import br.com.banavita.app.data.local.dao.PerdaDao
import br.com.banavita.app.data.local.dao.PrecoQueimaDao
import br.com.banavita.app.data.local.dao.ProdutoDao
import br.com.banavita.app.data.local.entity.CompraItemEntity
import br.com.banavita.app.data.local.entity.LoteBaixaEntity
import br.com.banavita.app.data.local.entity.LoteEntity
import br.com.banavita.app.data.local.entity.PerdaEntity
import br.com.banavita.app.data.local.entity.PrecoQueimaEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.InsufficientStockException
import br.com.banavita.app.domain.exception.InvalidDocumentException
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.Camara
import br.com.banavita.app.domain.model.EtilenoCapacidadeSnapshot
import br.com.banavita.app.domain.model.EstoqueLinha
import br.com.banavita.app.domain.model.Lote
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.MoverAdvice
import br.com.banavita.app.domain.model.OfertaPrevista
import br.com.banavita.app.domain.model.Perda
import br.com.banavita.app.domain.model.PerdaMotivo
import br.com.banavita.app.domain.model.PerecivelCriticoResumo
import br.com.banavita.app.domain.model.PeriodoPreset
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.round

@Singleton
class LoteRepository @Inject constructor(
    private val database: Provider<BanavitaDatabase>,
    private val loteDao: Provider<LoteDao>,
    private val produtoDao: Provider<ProdutoDao>,
    private val perdaDao: Provider<PerdaDao>,
    private val queimaDao: Provider<PrecoQueimaDao>,
    private val settings: AppSettingsRepository,
) {
    fun observeLinhas(): Flow<List<EstoqueLinha>> =
        loteDao.get().observeLinhas().map { rows -> rows.map { it.toDomain() } }

    fun observeLotesDoProduto(produtoId: UUID): Flow<List<Lote>> =
        loteDao.get().observeByProduto(produtoId).map { list -> list.map { it.toDomain() } }

    fun observeFilaFria(): Flow<List<Lote>> =
        loteDao.get().observeFilaFria().map { list -> list.map { it.toDomain() } }

    fun observePerdas(): Flow<List<Perda>> =
        perdaDao.get().observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun getById(id: UUID): Lote? = loteDao.get().getById(id)?.toDomain()

    suspend fun saldoPorEstado(produtoId: UUID, estado: MaturacaoEstado): Double =
        loteDao.get().saldoPorEstado(produtoId, estado.name)

    suspend fun criarLotesDaCompra(
        compraId: UUID,
        data: Long,
        itens: List<CompraItemEntity>,
        timestamp: Long,
    ) {
        val porProduto = mutableMapOf<UUID, Int>()
        val lotes = mutableListOf<LoteEntity>()
        for (item in itens) {
            val produto = produtoDao.get().getById(item.produtoId) ?: continue
            if (!produto.perecivel) continue
            val seq = (porProduto[item.produtoId] ?: 0) + 1
            porProduto[item.produtoId] = seq
            lotes += LoteEntity(
                id = UUID.randomUUID(),
                createdAt = timestamp,
                updatedAt = timestamp,
                produtoId = item.produtoId,
                compraId = compraId,
                compraItemId = item.id,
                origemId = null,
                dataEntrada = data,
                quantidadeInicial = item.quantidade,
                quantidadeRestante = item.quantidade,
                camara = Camara.FRIA.name,
                estado = MaturacaoEstado.CRUA.name,
                etapaInicioEpochDay = null,
                custoUnitarioCentavos = item.valorUnitarioCentavos,
                rotulo = rotuloLote(produto.descricao, data, seq),
                unidade = item.unidade ?: produto.unidade,
            )
        }
        if (lotes.isNotEmpty()) {
            loteDao.get().insertAll(lotes)
        }
    }

    suspend fun removerLotesDaCompraSeIntocados(compraId: UUID) {
        val lotes = loteDao.get().listByCompra(compraId)
        lotes.forEach { lote ->
            val tocado = lote.quantidadeRestante + 1e-9 < lote.quantidadeInicial ||
                lote.camara != Camara.FRIA.name
            if (tocado) {
                throw ReferencedEntityException(
                    "A compra já movimentou lotes perecíveis e não pode ser alterada/excluída.",
                )
            }
        }
        val filhos = lotes.any { parent ->
            loteDao.get().listByProduto(parent.produtoId).any { it.origemId == parent.id }
        }
        if (filhos) {
            throw ReferencedEntityException(
                "A compra já movimentou lotes perecíveis e não pode ser alterada/excluída.",
            )
        }
        if (lotes.isNotEmpty()) {
            loteDao.get().deleteByCompra(compraId)
        }
    }

    suspend fun transferirParaEtileno(loteId: UUID, quantidade: Double): UUID {
        if (quantidade <= 0) throw InvalidDocumentException("Informe a quantidade a soltar.")
        val lote = loteDao.get().getById(loteId)
            ?: throw ReferencedEntityException("Lote não encontrado.")
        if (lote.camara != Camara.FRIA.name) {
            throw InvalidDocumentException("Só é possível soltar lotes que estão na câmara fria.")
        }
        if (lote.quantidadeRestante + 1e-9 < quantidade) {
            throw InvalidDocumentException("Quantidade maior que o restante do lote.")
        }
        val folga = folgaEtileno()
        if (folga != null && quantidade > folga + 1e-9) {
            val unidade = lote.unidade ?: "unidade"
            throw InvalidDocumentException(
                "A etileno não comporta essa quantidade. Espaço restante: ${"%.1f".format(folga)} $unidade.",
            )
        }
        val timestamp = nowEpochMillis()
        val today = RipeningClock.todayEpochDay(timestamp)
        val novoId = UUID.randomUUID()
        val produto = produtoDao.get().getById(lote.produtoId)
        val rotulo = rotuloLote(produto?.descricao ?: lote.rotulo, timestamp, 1) + " · etileno"
        database.get().withTransaction {
            loteDao.get().update(
                lote.copy(
                    quantidadeRestante = lote.quantidadeRestante - quantidade,
                    updatedAt = timestamp,
                ),
            )
            loteDao.get().insert(
                LoteEntity(
                    id = novoId,
                    createdAt = timestamp,
                    updatedAt = timestamp,
                    produtoId = lote.produtoId,
                    compraId = lote.compraId,
                    compraItemId = lote.compraItemId,
                    origemId = lote.id,
                    dataEntrada = lote.dataEntrada,
                    quantidadeInicial = quantidade,
                    quantidadeRestante = quantidade,
                    camara = Camara.ETILENO.name,
                    estado = MaturacaoEstado.EM_MATURACAO.name,
                    etapaInicioEpochDay = today,
                    custoUnitarioCentavos = lote.custoUnitarioCentavos,
                    rotulo = rotulo,
                    unidade = lote.unidade,
                ),
            )
        }
        return novoId
    }

    /**
     * Folga para soltar: capacidade − total de itens na etileno.
     * Null = sem teto (capacidade em 0 nas Configurações).
     */
    suspend fun folgaEtileno(): Double? {
        val capacidade = settings.getEtilenoCapacidadeKg()
        if (capacidade <= 0.0) return null
        val naCamara = loteDao.get().qtdeNaEtileno()
        return (capacidade - naCamara).coerceAtLeast(0.0)
    }

    suspend fun kgNaEtileno(): Double = loteDao.get().qtdeNaEtileno()

    suspend fun kgNaFria(): Double = loteDao.get().qtdeNaFria()

    suspend fun capacidadeSnapshot(): EtilenoCapacidadeSnapshot {
        return EtilenoCapacidadeSnapshot(
            capacidade = settings.getEtilenoCapacidadeKg(),
            qtdeNaEtileno = loteDao.get().qtdeNaEtileno(),
            qtdeNaFria = loteDao.get().qtdeNaFria(),
            folga = folgaEtileno(),
        )
    }

    suspend fun avancarMaturacao(nowMillis: Long = nowEpochMillis()) {
        val today = RipeningClock.todayEpochDay(nowMillis)
        loteDao.get().listEtilenoAbertos().forEach { lote ->
            val produto = produtoDao.get().getById(lote.produtoId) ?: return@forEach
            val snap = RipeningClock.snapshot(
                camara = Camara.ETILENO,
                estadoRegistrado = MaturacaoEstado.valueOf(lote.estado),
                etapaInicioEpochDay = lote.etapaInicioEpochDay,
                curve = produto.toDomain().curva,
                todayEpochDay = today,
            )
            if (snap.estado.name != lote.estado || snap.etapaInicioEpochDay != lote.etapaInicioEpochDay) {
                loteDao.get().update(
                    lote.copy(
                        estado = snap.estado.name,
                        etapaInicioEpochDay = snap.etapaInicioEpochDay,
                        updatedAt = nowMillis,
                    ),
                )
            }
        }
        queimaDao.get().apagarExpiradas(nowMillis)
    }

    suspend fun corrigirEstado(loteId: UUID, estado: MaturacaoEstado) {
        val lote = loteDao.get().getById(loteId)
            ?: throw ReferencedEntityException("Lote não encontrado.")
        val today = RipeningClock.todayEpochDay()
        loteDao.get().update(
            lote.copy(
                estado = estado.name,
                etapaInicioEpochDay = if (lote.camara == Camara.ETILENO.name) today else lote.etapaInicioEpochDay,
                updatedAt = nowEpochMillis(),
            ),
        )
    }

    suspend fun consumirFifo(
        produtoId: UUID,
        estado: MaturacaoEstado,
        quantidade: Double,
        vendaId: UUID,
        timestamp: Long,
    ) {
        var restante = quantidade
        val lotes = loteDao.get().fifoDisponiveis(produtoId, estado.name)
        val disponivel = lotes.sumOf { it.quantidadeRestante }
        if (disponivel + 1e-9 < quantidade) {
            throw InsufficientStockException(produtoId.toString(), disponivel, quantidade)
        }
        for (lote in lotes) {
            if (restante <= 1e-12) break
            val baixa = min(lote.quantidadeRestante, restante)
            loteDao.get().update(
                lote.copy(
                    quantidadeRestante = lote.quantidadeRestante - baixa,
                    updatedAt = timestamp,
                ),
            )
            loteDao.get().insertBaixa(
                LoteBaixaEntity(
                    id = UUID.randomUUID(),
                    createdAt = timestamp,
                    loteId = lote.id,
                    quantidade = baixa,
                    vendaId = vendaId,
                    perdaId = null,
                ),
            )
            restante -= baixa
        }
    }

    suspend fun restaurarVenda(vendaId: UUID) {
        val baixas = loteDao.get().baixasDaVenda(vendaId)
        val timestamp = nowEpochMillis()
        baixas.forEach { baixa ->
            val lote = loteDao.get().getById(baixa.loteId) ?: return@forEach
            loteDao.get().update(
                lote.copy(
                    quantidadeRestante = lote.quantidadeRestante + baixa.quantidade,
                    updatedAt = timestamp,
                ),
            )
        }
        loteDao.get().deleteBaixasDaVenda(vendaId)
    }

    suspend fun registrarPerda(
        loteId: UUID,
        quantidade: Double,
        motivo: PerdaMotivo,
        data: Long = nowEpochMillis(),
    ): UUID {
        if (quantidade <= 0) throw InvalidDocumentException("Informe a quantidade da perda.")
        val lote = loteDao.get().getById(loteId)
            ?: throw ReferencedEntityException("Lote não encontrado.")
        if (lote.quantidadeRestante + 1e-9 < quantidade) {
            throw InvalidDocumentException("Quantidade maior que o restante do lote.")
        }
        val id = UUID.randomUUID()
        val timestamp = nowEpochMillis()
        val valor = round(quantidade * lote.custoUnitarioCentavos).toLong()
        database.get().withTransaction {
            loteDao.get().update(
                lote.copy(
                    quantidadeRestante = lote.quantidadeRestante - quantidade,
                    updatedAt = timestamp,
                ),
            )
            perdaDao.get().insert(
                PerdaEntity(
                    id = id,
                    createdAt = timestamp,
                    updatedAt = timestamp,
                    data = data,
                    produtoId = lote.produtoId,
                    loteId = lote.id,
                    estado = lote.estado,
                    quantidade = quantidade,
                    motivo = motivo.name,
                    valorCentavos = valor,
                    unidade = lote.unidade,
                ),
            )
            loteDao.get().insertBaixa(
                LoteBaixaEntity(
                    id = UUID.randomUUID(),
                    createdAt = timestamp,
                    loteId = lote.id,
                    quantidade = quantidade,
                    vendaId = null,
                    perdaId = id,
                ),
            )
        }
        return id
    }

    suspend fun aplicarQueima(produtoId: UUID, estado: MaturacaoEstado, precoCentavos: Long) {
        val zone = ZoneId.systemDefault()
        val today = Instant.ofEpochMilli(nowEpochMillis()).atZone(zone).toLocalDate()
        val inicio = PeriodRanges.startOfDay(today, zone)
        val fim = PeriodRanges.startOfDay(today.plusDays(1), zone)
        queimaDao.get().insert(
            PrecoQueimaEntity(
                id = UUID.randomUUID(),
                produtoId = produtoId,
                estado = estado.name,
                precoCentavos = precoCentavos,
                inicioDiaMillis = inicio,
                fimExclusivoMillis = fim,
            ),
        )
    }

    suspend fun precoVenda(produtoId: UUID, estado: MaturacaoEstado?, nowMillis: Long = nowEpochMillis()): Long {
        val produto = produtoDao.get().getById(produtoId)?.toDomain()
            ?: throw ReferencedEntityException("Produto não encontrado.")
        if (!produto.perecivel || estado == null) return produto.precoCentavos
        val queima = queimaDao.get().vigente(produtoId, estado.name, nowMillis)
        if (queima != null) return queima.precoCentavos
        return produto.precoSugerido(estado)
    }

    suspend fun previsao(horizonteDias: Int = 7): List<OfertaPrevista> {
        val today = RipeningClock.todayEpochDay()
        data class Key(val produtoId: UUID, val estado: MaturacaoEstado)
        val acc3 = mutableMapOf<Key, Double>()
        val acc7 = mutableMapOf<Key, Double>()
        val nomes = mutableMapOf<UUID, Pair<String, String>>()
        loteDao.get().listEtilenoAbertos().forEach { lote ->
            val produto = produtoDao.get().getById(lote.produtoId)?.toDomain() ?: return@forEach
            nomes[produto.id] = produto.descricao to produto.unidade
            val e3 = RipeningClock.estadoEm(
                Camara.ETILENO,
                MaturacaoEstado.valueOf(lote.estado),
                lote.etapaInicioEpochDay,
                produto.curva,
                today + 3,
            )
            val e7 = RipeningClock.estadoEm(
                Camara.ETILENO,
                MaturacaoEstado.valueOf(lote.estado),
                lote.etapaInicioEpochDay,
                produto.curva,
                today + horizonteDias.toLong(),
            )
            acc3.merge(Key(produto.id, e3), lote.quantidadeRestante, Double::plus)
            acc7.merge(Key(produto.id, e7), lote.quantidadeRestante, Double::plus)
        }
        return (acc3.keys + acc7.keys).map { key ->
            val (desc, un) = nomes[key.produtoId] ?: ("" to "KG")
            OfertaPrevista(
                produtoId = key.produtoId,
                descricao = desc,
                unidade = un,
                estado = key.estado,
                kgEm3Dias = acc3[key] ?: 0.0,
                kgEm7Dias = acc7[key] ?: 0.0,
            )
        }.sortedWith(compareBy({ it.descricao.lowercase() }, { it.estado.ordinal }))
    }

    suspend fun preverSaldoNaData(produtoId: UUID, estado: MaturacaoEstado, targetMillis: Long): Double {
        val targetEpochDay = RipeningClock.todayEpochDay(targetMillis)
        val today = RipeningClock.todayEpochDay()
        if (targetEpochDay <= today) {
            return saldoPorEstado(produtoId, estado)
        }
        var total = 0.0
        loteDao.get().listEtilenoAbertos().forEach { lote ->
            if (lote.produtoId == produtoId) {
                val produto = produtoDao.get().getById(lote.produtoId)?.toDomain() ?: return@forEach
                val estadoFuturo = RipeningClock.estadoEm(
                    Camara.ETILENO,
                    MaturacaoEstado.valueOf(lote.estado),
                    lote.etapaInicioEpochDay,
                    produto.curva,
                    targetEpochDay,
                )
                if (estadoFuturo == estado) {
                    total += lote.quantidadeRestante
                }
            }
        }
        return total
    }

    suspend fun resumoCriticos(): PerecivelCriticoResumo {
        avancarMaturacao()
        val linhas = loteDao.get().listLinhas().map { it.toDomain() }
        val pintada = linhas.filter { it.estado == MaturacaoEstado.PINTADA && it.quantidade > 1e-9 }
        val podre = linhas.filter { it.estado == MaturacaoEstado.PODRE && it.quantidade > 1e-9 }
        val today = RipeningClock.todayEpochDay()
        var kgAmanha = 0.0
        var descAmanha: String? = null
        var produtoIdAmanha: UUID? = null

        val moverMap = mutableMapOf<UUID, MoverAdvice>()

        loteDao.get().listEtilenoAbertos().forEach { lote ->
            val produto = produtoDao.get().getById(lote.produtoId)?.toDomain() ?: return@forEach
            val estadoAtual = MaturacaoEstado.valueOf(lote.estado)
            val amanha = RipeningClock.estadoEm(
                Camara.ETILENO,
                estadoAtual,
                lote.etapaInicioEpochDay,
                produto.curva,
                today + 1,
            )
            if (estadoAtual != MaturacaoEstado.PODRE && amanha == MaturacaoEstado.PODRE) {
                kgAmanha += lote.quantidadeRestante
                descAmanha = produto.descricao
                produtoIdAmanha = produto.id
            }

            // Aconselhável mover se estiver AMARELA ou PINTADA na etileno
            if (estadoAtual == MaturacaoEstado.AMARELA || estadoAtual == MaturacaoEstado.PINTADA) {
                val existing = moverMap[produto.id]
                moverMap[produto.id] = if (existing == null) {
                    MoverAdvice(produto.id, produto.descricao, lote.quantidadeRestante, lote.unidade ?: produto.unidade)
                } else {
                    existing.copy(quantidade = existing.quantidade + lote.quantidadeRestante)
                }
            }
        }
        val maisPintada = pintada.maxByOrNull { it.quantidade }
        val maisPodre = podre.maxByOrNull { it.quantidade }

        return PerecivelCriticoResumo(
            kgPintada = pintada.sumOf { it.quantidade },
            descricaoPintada = maisPintada?.descricao,
            unidadePintada = maisPintada?.unidade,
            produtoIdPintada = maisPintada?.produtoId,
            kgPodreOuAmanha = podre.sumOf { it.quantidade } + kgAmanha,
            descricaoPodre = maisPodre?.descricao ?: descAmanha,
            unidadePodre = maisPodre?.unidade ?: (produtoIdAmanha?.let { id -> database.get().produtoDao().getById(id)?.unidade }),
            produtoIdPodre = maisPodre?.produtoId ?: produtoIdAmanha,
            moverParaArmazenagem = moverMap.values.toList().sortedBy { it.descricao.lowercase() },
        )
    }

    private fun rotuloLote(descricao: String, dataMillis: Long, seq: Int): String {
        val data = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(dataMillis))
        val base = "$descricao $data"
        return if (seq > 1) "$base #$seq" else base
    }
}
