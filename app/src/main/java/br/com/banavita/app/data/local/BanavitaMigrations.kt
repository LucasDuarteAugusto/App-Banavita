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
