package br.com.banavita.app.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE venda ADD COLUMN VEN_PRAZO INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE compra ADD COLUMN COM_PRAZO INTEGER NOT NULL DEFAULT 0")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS recebimento (
                REC_ID TEXT NOT NULL PRIMARY KEY,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                REC_VENDA TEXT NOT NULL,
                REC_DATA INTEGER NOT NULL,
                REC_VALOR INTEGER NOT NULL,
                FOREIGN KEY(REC_VENDA) REFERENCES venda(VEN_ID) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_recebimento_REC_VENDA ON recebimento (REC_VENDA)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_recebimento_REC_DATA ON recebimento (REC_DATA)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS pagamento (
                PAG_ID TEXT NOT NULL PRIMARY KEY,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                PAG_COMPRA TEXT NOT NULL,
                PAG_DATA INTEGER NOT NULL,
                PAG_VALOR INTEGER NOT NULL,
                FOREIGN KEY(PAG_COMPRA) REFERENCES compra(COM_ID) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_pagamento_PAG_COMPRA ON pagamento (PAG_COMPRA)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_pagamento_PAG_DATA ON pagamento (PAG_DATA)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS evento (
                EVE_ID TEXT NOT NULL PRIMARY KEY,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                EVE_TITULO TEXT NOT NULL,
                EVE_DATAHORA INTEGER NOT NULL,
                EVE_OBS TEXT,
                EVE_CLIENTE TEXT,
                EVE_FORNECEDOR TEXT,
                EVE_NOTIFICAR INTEGER NOT NULL DEFAULT 1,
                EVE_ANTECIPACAO_MIN INTEGER,
                EVE_NOTIF_TITULO TEXT,
                EVE_NOTIF_CORPO TEXT,
                FOREIGN KEY(EVE_CLIENTE) REFERENCES cliente(CLI_ID) ON UPDATE NO ACTION ON DELETE RESTRICT,
                FOREIGN KEY(EVE_FORNECEDOR) REFERENCES fornecedor(FOR_ID) ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_evento_EVE_DATAHORA ON evento (EVE_DATAHORA)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_evento_EVE_CLIENTE ON evento (EVE_CLIENTE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_evento_EVE_FORNECEDOR ON evento (EVE_FORNECEDOR)")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_PERECIVEL INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_DIAS_VERDE INTEGER NOT NULL DEFAULT 3")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_DIAS_VERDOLENGA INTEGER NOT NULL DEFAULT 2")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_DIAS_AMARELA INTEGER NOT NULL DEFAULT 2")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_DIAS_PINTADA INTEGER NOT NULL DEFAULT 2")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_PRECO_VERDE INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_PRECO_VERDOLENGA INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_PRECO_AMARELA INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_PRECO_PINTADA INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_PRECO_PODRE INTEGER NOT NULL DEFAULT 0")
        db.execSQL(
            """
            UPDATE produto SET
                PRO_PRECO_VERDE = PRO_PRECO,
                PRO_PRECO_VERDOLENGA = PRO_PRECO,
                PRO_PRECO_AMARELA = PRO_PRECO,
                PRO_PRECO_PINTADA = PRO_PRECO,
                PRO_PRECO_PODRE = 0
            """.trimIndent(),
        )
        db.execSQL("ALTER TABLE cliente ADD COLUMN CLI_PONTO_PREF TEXT")
        db.execSQL("ALTER TABLE venda_item ADD COLUMN VEI_ESTADO TEXT")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS lote (
                LOT_ID TEXT NOT NULL PRIMARY KEY,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                LOT_PRODUTO TEXT NOT NULL,
                LOT_COMPRA TEXT,
                LOT_COMPRA_ITEM TEXT,
                LOT_ORIGEM TEXT,
                LOT_DATA INTEGER NOT NULL,
                LOT_QTDE_INICIAL REAL NOT NULL,
                LOT_QTDE_RESTANTE REAL NOT NULL,
                LOT_CAMARA TEXT NOT NULL,
                LOT_ESTADO TEXT NOT NULL,
                LOT_ETAPA_INICIO_DAY INTEGER,
                LOT_CUSTO_UN INTEGER NOT NULL,
                LOT_ROTULO TEXT NOT NULL,
                FOREIGN KEY(LOT_PRODUTO) REFERENCES produto(PRO_ID) ON UPDATE NO ACTION ON DELETE RESTRICT,
                FOREIGN KEY(LOT_COMPRA) REFERENCES compra(COM_ID) ON UPDATE NO ACTION ON DELETE SET NULL
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_lote_LOT_PRODUTO ON lote (LOT_PRODUTO)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_lote_LOT_COMPRA ON lote (LOT_COMPRA)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_lote_LOT_ESTADO ON lote (LOT_ESTADO)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_lote_LOT_CAMARA ON lote (LOT_CAMARA)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_lote_LOT_ORIGEM ON lote (LOT_ORIGEM)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS lote_baixa (
                LBA_ID TEXT NOT NULL PRIMARY KEY,
                created_at INTEGER NOT NULL,
                LBA_LOTE TEXT NOT NULL,
                LBA_QTDE REAL NOT NULL,
                LBA_VENDA TEXT,
                LBA_PERDA TEXT,
                FOREIGN KEY(LBA_LOTE) REFERENCES lote(LOT_ID) ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_lote_baixa_LBA_LOTE ON lote_baixa (LBA_LOTE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_lote_baixa_LBA_VENDA ON lote_baixa (LBA_VENDA)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_lote_baixa_LBA_PERDA ON lote_baixa (LBA_PERDA)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS perda (
                PER_ID TEXT NOT NULL PRIMARY KEY,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                PER_DATA INTEGER NOT NULL,
                PER_PRODUTO TEXT NOT NULL,
                PER_LOTE TEXT,
                PER_ESTADO TEXT NOT NULL,
                PER_QTDE REAL NOT NULL,
                PER_MOTIVO TEXT NOT NULL,
                PER_VALOR INTEGER NOT NULL,
                FOREIGN KEY(PER_PRODUTO) REFERENCES produto(PRO_ID) ON UPDATE NO ACTION ON DELETE RESTRICT,
                FOREIGN KEY(PER_LOTE) REFERENCES lote(LOT_ID) ON UPDATE NO ACTION ON DELETE SET NULL
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_perda_PER_PRODUTO ON perda (PER_PRODUTO)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_perda_PER_LOTE ON perda (PER_LOTE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_perda_PER_DATA ON perda (PER_DATA)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS preco_queima (
                QUE_ID TEXT NOT NULL PRIMARY KEY,
                QUE_PRODUTO TEXT NOT NULL,
                QUE_ESTADO TEXT NOT NULL,
                QUE_PRECO INTEGER NOT NULL,
                QUE_INICIO INTEGER NOT NULL,
                QUE_FIM INTEGER NOT NULL,
                FOREIGN KEY(QUE_PRODUTO) REFERENCES produto(PRO_ID) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_preco_queima_QUE_PRODUTO_QUE_ESTADO_QUE_INICIO ON preco_queima (QUE_PRODUTO, QUE_ESTADO, QUE_INICIO)",
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS encomenda (
                ENC_ID TEXT NOT NULL PRIMARY KEY,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                ENC_CLIENTE TEXT NOT NULL,
                ENC_DATA_ENTREGA INTEGER NOT NULL,
                ENC_STATUS TEXT NOT NULL,
                ENC_VENDA TEXT,
                FOREIGN KEY(ENC_CLIENTE) REFERENCES cliente(CLI_ID) ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_encomenda_ENC_CLIENTE ON encomenda (ENC_CLIENTE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_encomenda_ENC_DATA_ENTREGA ON encomenda (ENC_DATA_ENTREGA)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_encomenda_ENC_STATUS ON encomenda (ENC_STATUS)")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS encomenda_item (
                ENI_ID TEXT NOT NULL PRIMARY KEY,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                ENI_ENCOMENDA TEXT NOT NULL,
                ENI_PRODUTO TEXT NOT NULL,
                ENI_ESTADO TEXT NOT NULL,
                ENI_QTDE REAL NOT NULL,
                FOREIGN KEY(ENI_ENCOMENDA) REFERENCES encomenda(ENC_ID) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(ENI_PRODUTO) REFERENCES produto(PRO_ID) ON UPDATE NO ACTION ON DELETE RESTRICT
            )
            """.trimIndent(),
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_encomenda_item_ENI_ENCOMENDA ON encomenda_item (ENI_ENCOMENDA)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_encomenda_item_ENI_PRODUTO ON encomenda_item (ENI_PRODUTO)")

        db.execSQL("DROP VIEW IF EXISTS `vw_produto_estoque`")
        db.execSQL(
            """
            CREATE VIEW `vw_produto_estoque` AS SELECT
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
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE VIEW `vw_estoque_linha` AS SELECT
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
            """.trimIndent(),
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_DIAS_EM_MATURACAO INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_PRECO_CRUA INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE produto ADD COLUMN PRO_PRECO_EM_MATURACAO INTEGER NOT NULL DEFAULT 0")
        db.execSQL(
            """
            UPDATE produto SET
                PRO_PRECO_CRUA = PRO_PRECO,
                PRO_PRECO_EM_MATURACAO = PRO_PRECO
            """.trimIndent(),
        )
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE venda_item ADD COLUMN VEI_UNIDADE TEXT")
        db.execSQL("ALTER TABLE compra_item ADD COLUMN COI_UNIDADE TEXT")
        db.execSQL("ALTER TABLE lote ADD COLUMN LOT_UNIDADE TEXT")
        db.execSQL("ALTER TABLE perda ADD COLUMN PER_UNIDADE TEXT")
        db.execSQL(
            """
            UPDATE venda_item SET VEI_UNIDADE = (SELECT PRO_UNIDADE FROM produto WHERE PRO_ID = VEI_PRODUTO)
            """.trimIndent(),
        )
        db.execSQL(
            """
            UPDATE compra_item SET COI_UNIDADE = (SELECT PRO_UNIDADE FROM produto WHERE PRO_ID = COI_PRODUTO)
            """.trimIndent(),
        )
        db.execSQL(
            """
            UPDATE lote SET LOT_UNIDADE = (SELECT PRO_UNIDADE FROM produto WHERE PRO_ID = LOT_PRODUTO)
            """.trimIndent(),
        )
        db.execSQL(
            """
            UPDATE perda SET PER_UNIDADE = (SELECT PRO_UNIDADE FROM produto WHERE PRO_ID = PER_PRODUTO)
            """.trimIndent(),
        )
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE encomenda_item ADD COLUMN ENI_VALOR_UNITARIO INTEGER")
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE encomenda ADD COLUMN ENC_EVENTO TEXT")
    }
}
