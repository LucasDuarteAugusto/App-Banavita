package br.com.banavita.app.data.local.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.banavita.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class DashboardAggregate(
    val id: String,
    val label: String,
    val value: Long,
    val quantity: Double,
    val operations: Int,
    val coveredRevenue: Long,
    val cost: Double,
)

data class FinanceDashboardRow(
    val id: UUID,
    val partnerId: UUID?,
    val partner: String,
    val date: Long,
    val due: Long?,
    val balance: Long,
    val incoming: Boolean,
)

data class CashMovement(val date: Long, val value: Long)

data class InventoryDashboardRow(val productId: UUID, val name: String, val unit: String,
    val state: String?, val chamber: String?, val quantity: Double)

@Dao
interface DashboardDao {
    @Transaction
    @Query("SELECT * FROM encomenda WHERE (ENC_DATA_ENTREGA >= :start AND ENC_DATA_ENTREGA < :end) OR (:backlog = 1 AND ENC_STATUS != 'ENTREGUE' AND ENC_DATA_ENTREGA < :start) ORDER BY ENC_DATA_ENTREGA")
    fun orders(start: Long, end: Long, backlog: Boolean): Flow<List<br.com.banavita.app.data.local.relation.EncomendaWithItems>>

    @Query("SELECT * FROM evento WHERE (EVE_DATAHORA >= :start AND EVE_DATAHORA < :end) OR (EVE_TIPO = 'TAREFA' AND EVE_STATUS != 'CONCLUIDO' AND EVE_DATAHORA < :start) ORDER BY EVE_DATAHORA")
    fun events(start: Long, end: Long): Flow<List<EventoEntity>>

    @Query("""SELECT l.LOT_PRODUTO AS productId, p.PRO_DESCRICAO AS name, COALESCE(l.LOT_UNIDADE, p.PRO_UNIDADE) AS unit,
        l.LOT_ESTADO AS state, l.LOT_CAMARA AS chamber, SUM(l.LOT_QTDE_RESTANTE) AS quantity
        FROM lote l JOIN produto p ON p.PRO_ID = l.LOT_PRODUTO WHERE l.LOT_QTDE_RESTANTE > 0.0000001
        GROUP BY l.LOT_PRODUTO, unit, l.LOT_ESTADO, l.LOT_CAMARA
        UNION ALL SELECT p.PRO_ID, p.PRO_DESCRICAO, m.unit, NULL, NULL, SUM(m.qty)
        FROM (SELECT COI_PRODUTO AS pid, COALESCE(COI_UNIDADE, p.PRO_UNIDADE) AS unit, COI_QTDE AS qty
              FROM compra_item JOIN produto p ON p.PRO_ID = COI_PRODUTO
              UNION ALL SELECT VEI_PRODUTO, COALESCE(VEI_UNIDADE, p.PRO_UNIDADE), -VEI_QTDE
              FROM venda_item JOIN produto p ON p.PRO_ID = VEI_PRODUTO) m
        JOIN produto p ON p.PRO_ID = m.pid WHERE p.PRO_PERECIVEL = 0 GROUP BY p.PRO_ID, m.unit""")
    fun inventory(): Flow<List<InventoryDashboardRow>>
    @RawQuery(observedEntities = [VendaEntity::class, VendaItemEntity::class, CompraEntity::class,
        CompraItemEntity::class, ProdutoEntity::class, ClienteEntity::class, FornecedorEntity::class])
    fun aggregates(query: SupportSQLiteQuery): Flow<List<DashboardAggregate>>

    @Query("""SELECT v.VEN_ID AS id, v.VEN_CLIENTE AS partnerId, COALESCE(c.CLI_NOME, 'Sem cliente') AS partner,
        v.VEN_DATA AS date, v.VEN_VENCIMENTO AS due,
        v.VEN_VLRTOT - COALESCE((SELECT SUM(REC_VALOR) FROM recebimento WHERE REC_VENDA = v.VEN_ID), 0) AS balance,
        1 AS incoming FROM venda v LEFT JOIN cliente c ON c.CLI_ID = v.VEN_CLIENTE
        WHERE v.VEN_PRAZO = 1 AND balance > 0
        UNION ALL SELECT v.COM_ID, v.COM_FORNECEDOR, COALESCE(c.FOR_NOME, 'Sem fornecedor'), v.COM_DATA,
        v.COM_VENCIMENTO, v.COM_VLRTOT - COALESCE((SELECT SUM(PAG_VALOR) FROM pagamento WHERE PAG_COMPRA = v.COM_ID), 0),
        0 FROM compra v LEFT JOIN fornecedor c ON c.FOR_ID = v.COM_FORNECEDOR
        WHERE v.COM_PRAZO = 1 AND v.COM_VLRTOT - COALESCE((SELECT SUM(PAG_VALOR) FROM pagamento WHERE PAG_COMPRA = v.COM_ID), 0) > 0""")
    fun finance(): Flow<List<FinanceDashboardRow>>

    @Query("SELECT REC_DATA AS date, REC_VALOR AS value FROM recebimento WHERE REC_DATA >= :start AND REC_DATA < :end UNION ALL SELECT PAG_DATA, -PAG_VALOR FROM pagamento WHERE PAG_DATA >= :start AND PAG_DATA < :end")
    fun cash(start: Long, end: Long): Flow<List<CashMovement>>

    @Query("SELECT * FROM caixa_base WHERE id = 1")
    fun cashBase(): Flow<CaixaBaseEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCashBase(base: CaixaBaseEntity)

    @Query("SELECT * FROM lote WHERE LOT_QTDE_RESTANTE > 0.0000001")
    fun lots(): Flow<List<LoteEntity>>

    @Query("SELECT SUM(PER_VALOR) FROM perda WHERE PER_DATA >= :start AND PER_DATA < :end")
    fun losses(start: Long, end: Long): Flow<Long?>

    @Query("SELECT * FROM estoque_snapshot WHERE dia >= :start AND dia < :end")
    fun snapshots(start: Long, end: Long): Flow<List<EstoqueSnapshotEntity>>
    @Query("DELETE FROM estoque_snapshot WHERE dia = :day")
    suspend fun deleteSnapshot(day: Long)
    @Insert
    suspend fun insertSnapshots(rows: List<EstoqueSnapshotEntity>)

    @Query("SELECT * FROM entrega_item WHERE requestId = :requestId")
    suspend fun delivery(requestId: UUID): List<EntregaItemEntity>
    @Insert
    suspend fun insertDelivery(rows: List<EntregaItemEntity>)
    @Query("SELECT * FROM entrega_item WHERE itemId IN (SELECT ENI_ID FROM encomenda_item WHERE ENI_ENCOMENDA = :id) ORDER BY data DESC")
    suspend fun deliveryHistory(id: UUID): List<EntregaItemEntity>

    @Query("UPDATE evento SET EVE_STATUS = :status, EVE_TIPO = 'ENTREGA', updated_at = :now WHERE EVE_ID = :id")
    suspend fun syncDeliveryEvent(id: UUID, status: String, now: Long)
    @Query("SELECT COUNT(*) FROM encomenda WHERE ENC_EVENTO = :id")
    suspend fun linkedEvent(id: UUID): Int
    @Query("SELECT COUNT(*) FROM entrega_item WHERE vendaId = :id")
    suspend fun linkedSale(id: UUID): Int
}
