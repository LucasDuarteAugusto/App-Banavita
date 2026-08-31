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
            COALESCE(c.qtde, 0) - COALESCE(v.qtde, 0) AS saldo
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
