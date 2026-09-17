package br.com.banavita.app.data.repository

import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.local.dao.GrupoDao
import br.com.banavita.app.data.local.dao.ProdutoDao
import br.com.banavita.app.data.local.entity.ProdutoEntity
import br.com.banavita.app.data.mapper.toDomain
import br.com.banavita.app.domain.exception.InvalidDocumentException
import br.com.banavita.app.domain.exception.ReferencedEntityException
import br.com.banavita.app.domain.model.PrecosPorEstado
import br.com.banavita.app.domain.model.Produto
import br.com.banavita.app.domain.model.RipeningCurve
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ProdutoRepository @Inject constructor(
    private val produtoDao: Provider<ProdutoDao>,
    private val grupoDao: Provider<GrupoDao>,
) {
    fun observeAll(): Flow<List<Produto>> =
        produtoDao.get().observeAll().map { produtos -> produtos.map { it.toDomain() } }

    suspend fun getById(id: UUID): Produto? =
        produtoDao.get().getById(id)?.toDomain()

    suspend fun create(
        descricao: String,
        precoCentavos: Long,
        grupoId: UUID?,
        unidade: String = "UN",
        perecivel: Boolean = false,
        curva: RipeningCurve = RipeningCurve.DEFAULT,
        precosEstado: PrecosPorEstado? = null,
    ): UUID {
        grupoId?.let {
            if (grupoDao.get().getById(it) == null) {
                throw ReferencedEntityException("Grupo $it não encontrado.")
            }
        }
        if (perecivel && !curva.isValid()) {
            throw InvalidDocumentException("Informe dias maiores que zero em cada etapa da curva.")
        }
        val timestamp = nowEpochMillis()
        val id = UUID.randomUUID()
        val precos = precosEstado ?: PrecosPorEstado.fromTabela(precoCentavos)
        produtoDao.get().insert(
            ProdutoEntity(
                id = id,
                createdAt = timestamp,
                updatedAt = timestamp,
                descricao = descricao,
                precoCentavos = precoCentavos,
                grupoId = grupoId,
                unidade = unidade,
                perecivel = perecivel,
                diasEmMaturacao = curva.diasEmMaturacao,
                diasVerde = curva.diasVerde,
                diasVerdolenga = curva.diasVerdolenga,
                diasAmarela = curva.diasAmarela,
                diasPintada = curva.diasPintada,
                precoCruaCentavos = precos.cruaCentavos,
                precoEmMaturacaoCentavos = precos.emMaturacaoCentavos,
                precoVerdeCentavos = precos.verdeCentavos,
                precoVerdolengaCentavos = precos.verdolengaCentavos,
                precoAmarelaCentavos = precos.amarelaCentavos,
                precoPintadaCentavos = precos.pintadaCentavos,
                precoPodreCentavos = precos.podreCentavos,
            ),
        )
        return id
    }

    suspend fun update(
        id: UUID,
        descricao: String,
        precoCentavos: Long,
        grupoId: UUID?,
        unidade: String,
        perecivel: Boolean = false,
        curva: RipeningCurve = RipeningCurve.DEFAULT,
        precosEstado: PrecosPorEstado? = null,
    ) {
        grupoId?.let {
            if (grupoDao.get().getById(it) == null) {
                throw ReferencedEntityException("Grupo $it não encontrado.")
            }
        }
        if (perecivel && !curva.isValid()) {
            throw InvalidDocumentException("Informe dias maiores que zero em cada etapa da curva.")
        }
        val existing = produtoDao.get().getById(id)
            ?: throw ReferencedEntityException("Produto $id não encontrado.")
        val precos = precosEstado ?: PrecosPorEstado.fromTabela(precoCentavos)
        produtoDao.get().update(
            existing.copy(
                descricao = descricao,
                precoCentavos = precoCentavos,
                grupoId = grupoId,
                unidade = unidade,
                perecivel = perecivel,
                diasEmMaturacao = curva.diasEmMaturacao,
                diasVerde = curva.diasVerde,
                diasVerdolenga = curva.diasVerdolenga,
                diasAmarela = curva.diasAmarela,
                diasPintada = curva.diasPintada,
                precoCruaCentavos = precos.cruaCentavos,
                precoEmMaturacaoCentavos = precos.emMaturacaoCentavos,
                precoVerdeCentavos = precos.verdeCentavos,
                precoVerdolengaCentavos = precos.verdolengaCentavos,
                precoAmarelaCentavos = precos.amarelaCentavos,
                precoPintadaCentavos = precos.pintadaCentavos,
                precoPodreCentavos = precos.podreCentavos,
                updatedAt = nowEpochMillis(),
            ),
        )
    }

    suspend fun delete(id: UUID) {
        val vendas = produtoDao.get().countVendasByProduto(id)
        val compras = produtoDao.get().countComprasByProduto(id)
        if (vendas > 0 || compras > 0) {
            throw ReferencedEntityException(
                "Produto possui movimentações (vendas: $vendas, compras: $compras).",
            )
        }
        val existing = produtoDao.get().getById(id) ?: return
        produtoDao.get().delete(existing)
    }
}
