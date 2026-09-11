package br.com.banavita.app.data.local.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import java.util.UUID

@DatabaseView(
    viewName = "vw_produto_estoque",
    value = """
        SELECT
            p.PRO_ID AS produto_id,
            p.PRO_UNIDADE AS unidade,
            CASE
                WHEN p.PRO_PERECIVEL = 1 THEN COALESCE(l.qtde, 0)
                ELSE COALESCE(c.qtde, 0) - COALESCE(v.qtde, 0)
            END AS saldo
        FROM produto p
        LEFT JOIN (
            SELECT COI_PRODUTO AS id, SUM(COI_QTDE) AS qtde
            FROM compra_item
            GROUP BY COI_PRODUTO
        ) c ON c.id = p.PRO_ID
        LEFT JOIN (
            SELECT VEI_PRODUTO AS id, SUM(VEI_QTDE) AS qtde
            FROM venda_item
            GROUP BY VEI_PRODUTO
        ) v ON v.id = p.PRO_ID
        LEFT JOIN (
            SELECT LOT_PRODUTO AS id, SUM(LOT_QTDE_RESTANTE) AS qtde
            FROM lote
            GROUP BY LOT_PRODUTO
        ) l ON l.id = p.PRO_ID
    """,
)
data class ProdutoEstoqueView(
    @ColumnInfo(name = "produto_id")
    val produtoId: UUID,
    @ColumnInfo(name = "unidade")
    val unidade: String,
    @ColumnInfo(name = "saldo")
    val saldo: Double,
)

@DatabaseView(
    viewName = "vw_estoque_linha",
    value = """
        SELECT
            l.LOT_PRODUTO AS produto_id,
            p.PRO_DESCRICAO AS descricao,
            p.PRO_UNIDADE AS unidade,
            l.LOT_ESTADO AS estado,
            l.LOT_CAMARA AS camara,
            SUM(l.LOT_QTDE_RESTANTE) AS quantidade,
            1 AS perecivel
        FROM lote l
        INNER JOIN produto p ON p.PRO_ID = l.LOT_PRODUTO
        WHERE l.LOT_QTDE_RESTANTE > 0.0000001
        GROUP BY l.LOT_PRODUTO, p.PRO_DESCRICAO, p.PRO_UNIDADE, l.LOT_ESTADO, l.LOT_CAMARA
        UNION ALL
        SELECT
            p.PRO_ID AS produto_id,
            p.PRO_DESCRICAO AS descricao,
            p.PRO_UNIDADE AS unidade,
            NULL AS estado,
            NULL AS camara,
            COALESCE(c.qtde, 0) - COALESCE(v.qtde, 0) AS quantidade,
            0 AS perecivel
        FROM produto p
        LEFT JOIN (
            SELECT COI_PRODUTO AS id, SUM(COI_QTDE) AS qtde
            FROM compra_item
            GROUP BY COI_PRODUTO
        ) c ON c.id = p.PRO_ID
        LEFT JOIN (
            SELECT VEI_PRODUTO AS id, SUM(VEI_QTDE) AS qtde
            FROM venda_item
            GROUP BY VEI_PRODUTO
        ) v ON v.id = p.PRO_ID
        WHERE p.PRO_PERECIVEL = 0
    """,
)
data class EstoqueLinhaView(
    @ColumnInfo(name = "produto_id")
    val produtoId: UUID,
    @ColumnInfo(name = "descricao")
    val descricao: String,
    @ColumnInfo(name = "unidade")
    val unidade: String,
    @ColumnInfo(name = "estado")
    val estado: String?,
    @ColumnInfo(name = "camara")
    val camara: String?,
    @ColumnInfo(name = "quantidade")
    val quantidade: Double,
    @ColumnInfo(name = "perecivel")
    val perecivel: Int,
)
