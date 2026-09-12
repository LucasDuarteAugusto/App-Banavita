package br.com.banavita.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.ViewInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import br.com.banavita.app.data.local.dao.ClienteDao;
import br.com.banavita.app.data.local.dao.ClienteDao_Impl;
import br.com.banavita.app.data.local.dao.CompraDao;
import br.com.banavita.app.data.local.dao.CompraDao_Impl;
import br.com.banavita.app.data.local.dao.EncomendaDao;
import br.com.banavita.app.data.local.dao.EncomendaDao_Impl;
import br.com.banavita.app.data.local.dao.EstoqueDao;
import br.com.banavita.app.data.local.dao.EstoqueDao_Impl;
import br.com.banavita.app.data.local.dao.EventoDao;
import br.com.banavita.app.data.local.dao.EventoDao_Impl;
import br.com.banavita.app.data.local.dao.FornecedorDao;
import br.com.banavita.app.data.local.dao.FornecedorDao_Impl;
import br.com.banavita.app.data.local.dao.GrupoDao;
import br.com.banavita.app.data.local.dao.GrupoDao_Impl;
import br.com.banavita.app.data.local.dao.LoteDao;
import br.com.banavita.app.data.local.dao.LoteDao_Impl;
import br.com.banavita.app.data.local.dao.PagamentoDao;
import br.com.banavita.app.data.local.dao.PagamentoDao_Impl;
import br.com.banavita.app.data.local.dao.PerdaDao;
import br.com.banavita.app.data.local.dao.PerdaDao_Impl;
import br.com.banavita.app.data.local.dao.PrecoQueimaDao;
import br.com.banavita.app.data.local.dao.PrecoQueimaDao_Impl;
import br.com.banavita.app.data.local.dao.ProdutoDao;
import br.com.banavita.app.data.local.dao.ProdutoDao_Impl;
import br.com.banavita.app.data.local.dao.RecebimentoDao;
import br.com.banavita.app.data.local.dao.RecebimentoDao_Impl;
import br.com.banavita.app.data.local.dao.VendaDao;
import br.com.banavita.app.data.local.dao.VendaDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BanavitaDatabase_Impl extends BanavitaDatabase {
  private volatile GrupoDao _grupoDao;

  private volatile ProdutoDao _produtoDao;

  private volatile ClienteDao _clienteDao;

  private volatile FornecedorDao _fornecedorDao;

  private volatile VendaDao _vendaDao;

  private volatile CompraDao _compraDao;

  private volatile EstoqueDao _estoqueDao;

  private volatile RecebimentoDao _recebimentoDao;

  private volatile PagamentoDao _pagamentoDao;

  private volatile EventoDao _eventoDao;

  private volatile LoteDao _loteDao;

  private volatile PerdaDao _perdaDao;

  private volatile PrecoQueimaDao _precoQueimaDao;

  private volatile EncomendaDao _encomendaDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `grupo` (`GRU_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `GRU_NOME` TEXT NOT NULL, `GRU_OBS` TEXT, PRIMARY KEY(`GRU_ID`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `produto` (`PRO_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `PRO_DESCRICAO` TEXT NOT NULL, `PRO_PRECO` INTEGER NOT NULL, `PRO_GRUPO` TEXT, `PRO_UNIDADE` TEXT NOT NULL DEFAULT 'UN', `PRO_PERECIVEL` INTEGER NOT NULL DEFAULT 0, `PRO_DIAS_VERDE` INTEGER NOT NULL DEFAULT 3, `PRO_DIAS_VERDOLENGA` INTEGER NOT NULL DEFAULT 2, `PRO_DIAS_AMARELA` INTEGER NOT NULL DEFAULT 2, `PRO_DIAS_PINTADA` INTEGER NOT NULL DEFAULT 2, `PRO_PRECO_VERDE` INTEGER NOT NULL DEFAULT 0, `PRO_PRECO_VERDOLENGA` INTEGER NOT NULL DEFAULT 0, `PRO_PRECO_AMARELA` INTEGER NOT NULL DEFAULT 0, `PRO_PRECO_PINTADA` INTEGER NOT NULL DEFAULT 0, `PRO_PRECO_PODRE` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(`PRO_ID`), FOREIGN KEY(`PRO_GRUPO`) REFERENCES `grupo`(`GRU_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_produto_PRO_GRUPO` ON `produto` (`PRO_GRUPO`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `cliente` (`CLI_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `CLI_NOME` TEXT NOT NULL, `CLI_TELEFONE` TEXT, `CLI_PONTO_PREF` TEXT, PRIMARY KEY(`CLI_ID`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `fornecedor` (`FOR_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `FOR_NOME` TEXT NOT NULL, `FOR_TELEFONE` TEXT, PRIMARY KEY(`FOR_ID`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `venda` (`VEN_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `VEN_DATA` INTEGER NOT NULL, `VEN_CLIENTE` TEXT, `VEN_VLRTOT` INTEGER NOT NULL, `VEN_PRAZO` INTEGER NOT NULL, PRIMARY KEY(`VEN_ID`), FOREIGN KEY(`VEN_CLIENTE`) REFERENCES `cliente`(`CLI_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_venda_VEN_DATA` ON `venda` (`VEN_DATA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_venda_VEN_CLIENTE` ON `venda` (`VEN_CLIENTE`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `venda_item` (`VEI_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `VEI_VENDA` TEXT NOT NULL, `VEI_PRODUTO` TEXT NOT NULL, `VEI_VLRUN` INTEGER NOT NULL, `VEI_QTDE` REAL NOT NULL, `VEI_VLRTOT` INTEGER NOT NULL, `VEI_ESTADO` TEXT, PRIMARY KEY(`VEI_ID`), FOREIGN KEY(`VEI_VENDA`) REFERENCES `venda`(`VEN_ID`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`VEI_PRODUTO`) REFERENCES `produto`(`PRO_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_venda_item_VEI_VENDA` ON `venda_item` (`VEI_VENDA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_venda_item_VEI_PRODUTO` ON `venda_item` (`VEI_PRODUTO`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `compra` (`COM_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `COM_DATA` INTEGER NOT NULL, `COM_FORNECEDOR` TEXT, `COM_VLRTOT` INTEGER NOT NULL, `COM_PRAZO` INTEGER NOT NULL, PRIMARY KEY(`COM_ID`), FOREIGN KEY(`COM_FORNECEDOR`) REFERENCES `fornecedor`(`FOR_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_compra_COM_DATA` ON `compra` (`COM_DATA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_compra_COM_FORNECEDOR` ON `compra` (`COM_FORNECEDOR`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `compra_item` (`COI_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `COI_COMPRA` TEXT NOT NULL, `COI_PRODUTO` TEXT NOT NULL, `COI_VLRUN` INTEGER NOT NULL, `COI_QTDE` REAL NOT NULL, `COI_VLRTOT` INTEGER NOT NULL, PRIMARY KEY(`COI_ID`), FOREIGN KEY(`COI_COMPRA`) REFERENCES `compra`(`COM_ID`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`COI_PRODUTO`) REFERENCES `produto`(`PRO_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_compra_item_COI_COMPRA` ON `compra_item` (`COI_COMPRA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_compra_item_COI_PRODUTO` ON `compra_item` (`COI_PRODUTO`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recebimento` (`REC_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `REC_VENDA` TEXT NOT NULL, `REC_DATA` INTEGER NOT NULL, `REC_VALOR` INTEGER NOT NULL, PRIMARY KEY(`REC_ID`), FOREIGN KEY(`REC_VENDA`) REFERENCES `venda`(`VEN_ID`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recebimento_REC_VENDA` ON `recebimento` (`REC_VENDA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recebimento_REC_DATA` ON `recebimento` (`REC_DATA`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pagamento` (`PAG_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `PAG_COMPRA` TEXT NOT NULL, `PAG_DATA` INTEGER NOT NULL, `PAG_VALOR` INTEGER NOT NULL, PRIMARY KEY(`PAG_ID`), FOREIGN KEY(`PAG_COMPRA`) REFERENCES `compra`(`COM_ID`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_pagamento_PAG_COMPRA` ON `pagamento` (`PAG_COMPRA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_pagamento_PAG_DATA` ON `pagamento` (`PAG_DATA`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `evento` (`EVE_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `EVE_TITULO` TEXT NOT NULL, `EVE_DATAHORA` INTEGER NOT NULL, `EVE_OBS` TEXT, `EVE_CLIENTE` TEXT, `EVE_FORNECEDOR` TEXT, `EVE_NOTIFICAR` INTEGER NOT NULL, `EVE_ANTECIPACAO_MIN` INTEGER, `EVE_NOTIF_TITULO` TEXT, `EVE_NOTIF_CORPO` TEXT, PRIMARY KEY(`EVE_ID`), FOREIGN KEY(`EVE_CLIENTE`) REFERENCES `cliente`(`CLI_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`EVE_FORNECEDOR`) REFERENCES `fornecedor`(`FOR_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_evento_EVE_DATAHORA` ON `evento` (`EVE_DATAHORA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_evento_EVE_CLIENTE` ON `evento` (`EVE_CLIENTE`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_evento_EVE_FORNECEDOR` ON `evento` (`EVE_FORNECEDOR`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `lote` (`LOT_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `LOT_PRODUTO` TEXT NOT NULL, `LOT_COMPRA` TEXT, `LOT_COMPRA_ITEM` TEXT, `LOT_ORIGEM` TEXT, `LOT_DATA` INTEGER NOT NULL, `LOT_QTDE_INICIAL` REAL NOT NULL, `LOT_QTDE_RESTANTE` REAL NOT NULL, `LOT_CAMARA` TEXT NOT NULL, `LOT_ESTADO` TEXT NOT NULL, `LOT_ETAPA_INICIO_DAY` INTEGER, `LOT_CUSTO_UN` INTEGER NOT NULL, `LOT_ROTULO` TEXT NOT NULL, PRIMARY KEY(`LOT_ID`), FOREIGN KEY(`LOT_PRODUTO`) REFERENCES `produto`(`PRO_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`LOT_COMPRA`) REFERENCES `compra`(`COM_ID`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_lote_LOT_PRODUTO` ON `lote` (`LOT_PRODUTO`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_lote_LOT_COMPRA` ON `lote` (`LOT_COMPRA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_lote_LOT_ESTADO` ON `lote` (`LOT_ESTADO`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_lote_LOT_CAMARA` ON `lote` (`LOT_CAMARA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_lote_LOT_ORIGEM` ON `lote` (`LOT_ORIGEM`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `lote_baixa` (`LBA_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `LBA_LOTE` TEXT NOT NULL, `LBA_QTDE` REAL NOT NULL, `LBA_VENDA` TEXT, `LBA_PERDA` TEXT, PRIMARY KEY(`LBA_ID`), FOREIGN KEY(`LBA_LOTE`) REFERENCES `lote`(`LOT_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_lote_baixa_LBA_LOTE` ON `lote_baixa` (`LBA_LOTE`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_lote_baixa_LBA_VENDA` ON `lote_baixa` (`LBA_VENDA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_lote_baixa_LBA_PERDA` ON `lote_baixa` (`LBA_PERDA`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `perda` (`PER_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `PER_DATA` INTEGER NOT NULL, `PER_PRODUTO` TEXT NOT NULL, `PER_LOTE` TEXT, `PER_ESTADO` TEXT NOT NULL, `PER_QTDE` REAL NOT NULL, `PER_MOTIVO` TEXT NOT NULL, `PER_VALOR` INTEGER NOT NULL, PRIMARY KEY(`PER_ID`), FOREIGN KEY(`PER_PRODUTO`) REFERENCES `produto`(`PRO_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT , FOREIGN KEY(`PER_LOTE`) REFERENCES `lote`(`LOT_ID`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_perda_PER_PRODUTO` ON `perda` (`PER_PRODUTO`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_perda_PER_LOTE` ON `perda` (`PER_LOTE`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_perda_PER_DATA` ON `perda` (`PER_DATA`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `preco_queima` (`QUE_ID` TEXT NOT NULL, `QUE_PRODUTO` TEXT NOT NULL, `QUE_ESTADO` TEXT NOT NULL, `QUE_PRECO` INTEGER NOT NULL, `QUE_INICIO` INTEGER NOT NULL, `QUE_FIM` INTEGER NOT NULL, PRIMARY KEY(`QUE_ID`), FOREIGN KEY(`QUE_PRODUTO`) REFERENCES `produto`(`PRO_ID`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_preco_queima_QUE_PRODUTO_QUE_ESTADO_QUE_INICIO` ON `preco_queima` (`QUE_PRODUTO`, `QUE_ESTADO`, `QUE_INICIO`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `encomenda` (`ENC_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `ENC_CLIENTE` TEXT NOT NULL, `ENC_DATA_ENTREGA` INTEGER NOT NULL, `ENC_STATUS` TEXT NOT NULL, `ENC_VENDA` TEXT, PRIMARY KEY(`ENC_ID`), FOREIGN KEY(`ENC_CLIENTE`) REFERENCES `cliente`(`CLI_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_encomenda_ENC_CLIENTE` ON `encomenda` (`ENC_CLIENTE`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_encomenda_ENC_DATA_ENTREGA` ON `encomenda` (`ENC_DATA_ENTREGA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_encomenda_ENC_STATUS` ON `encomenda` (`ENC_STATUS`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `encomenda_item` (`ENI_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `ENI_ENCOMENDA` TEXT NOT NULL, `ENI_PRODUTO` TEXT NOT NULL, `ENI_ESTADO` TEXT NOT NULL, `ENI_QTDE` REAL NOT NULL, PRIMARY KEY(`ENI_ID`), FOREIGN KEY(`ENI_ENCOMENDA`) REFERENCES `encomenda`(`ENC_ID`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`ENI_PRODUTO`) REFERENCES `produto`(`PRO_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_encomenda_item_ENI_ENCOMENDA` ON `encomenda_item` (`ENI_ENCOMENDA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_encomenda_item_ENI_PRODUTO` ON `encomenda_item` (`ENI_PRODUTO`)");
        db.execSQL("CREATE VIEW `vw_produto_estoque` AS SELECT\n"
                + "            p.PRO_ID AS produto_id,\n"
                + "            p.PRO_UNIDADE AS unidade,\n"
                + "            CASE\n"
                + "                WHEN p.PRO_PERECIVEL = 1 THEN COALESCE(l.qtde, 0)\n"
                + "                ELSE COALESCE(c.qtde, 0) - COALESCE(v.qtde, 0)\n"
                + "            END AS saldo\n"
                + "        FROM produto p\n"
                + "        LEFT JOIN (\n"
                + "            SELECT COI_PRODUTO AS id, SUM(COI_QTDE) AS qtde\n"
                + "            FROM compra_item\n"
                + "            GROUP BY COI_PRODUTO\n"
                + "        ) c ON c.id = p.PRO_ID\n"
                + "        LEFT JOIN (\n"
                + "            SELECT VEI_PRODUTO AS id, SUM(VEI_QTDE) AS qtde\n"
                + "            FROM venda_item\n"
                + "            GROUP BY VEI_PRODUTO\n"
                + "        ) v ON v.id = p.PRO_ID\n"
                + "        LEFT JOIN (\n"
                + "            SELECT LOT_PRODUTO AS id, SUM(LOT_QTDE_RESTANTE) AS qtde\n"
                + "            FROM lote\n"
                + "            GROUP BY LOT_PRODUTO\n"
                + "        ) l ON l.id = p.PRO_ID");
        db.execSQL("CREATE VIEW `vw_estoque_linha` AS SELECT\n"
                + "            l.LOT_PRODUTO AS produto_id,\n"
                + "            p.PRO_DESCRICAO AS descricao,\n"
                + "            p.PRO_UNIDADE AS unidade,\n"
                + "            l.LOT_ESTADO AS estado,\n"
                + "            l.LOT_CAMARA AS camara,\n"
                + "            SUM(l.LOT_QTDE_RESTANTE) AS quantidade,\n"
                + "            1 AS perecivel\n"
                + "        FROM lote l\n"
                + "        INNER JOIN produto p ON p.PRO_ID = l.LOT_PRODUTO\n"
                + "        WHERE l.LOT_QTDE_RESTANTE > 0.0000001\n"
                + "        GROUP BY l.LOT_PRODUTO, p.PRO_DESCRICAO, p.PRO_UNIDADE, l.LOT_ESTADO, l.LOT_CAMARA\n"
                + "        UNION ALL\n"
                + "        SELECT\n"
                + "            p.PRO_ID AS produto_id,\n"
                + "            p.PRO_DESCRICAO AS descricao,\n"
                + "            p.PRO_UNIDADE AS unidade,\n"
                + "            NULL AS estado,\n"
                + "            NULL AS camara,\n"
                + "            COALESCE(c.qtde, 0) - COALESCE(v.qtde, 0) AS quantidade,\n"
                + "            0 AS perecivel\n"
                + "        FROM produto p\n"
                + "        LEFT JOIN (\n"
                + "            SELECT COI_PRODUTO AS id, SUM(COI_QTDE) AS qtde\n"
                + "            FROM compra_item\n"
                + "            GROUP BY COI_PRODUTO\n"
                + "        ) c ON c.id = p.PRO_ID\n"
                + "        LEFT JOIN (\n"
                + "            SELECT VEI_PRODUTO AS id, SUM(VEI_QTDE) AS qtde\n"
                + "            FROM venda_item\n"
                + "            GROUP BY VEI_PRODUTO\n"
                + "        ) v ON v.id = p.PRO_ID\n"
                + "        WHERE p.PRO_PERECIVEL = 0");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6972427fd0320bccb955b02fdbc67915')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `grupo`");
        db.execSQL("DROP TABLE IF EXISTS `produto`");
        db.execSQL("DROP TABLE IF EXISTS `cliente`");
        db.execSQL("DROP TABLE IF EXISTS `fornecedor`");
        db.execSQL("DROP TABLE IF EXISTS `venda`");
        db.execSQL("DROP TABLE IF EXISTS `venda_item`");
        db.execSQL("DROP TABLE IF EXISTS `compra`");
        db.execSQL("DROP TABLE IF EXISTS `compra_item`");
        db.execSQL("DROP TABLE IF EXISTS `recebimento`");
        db.execSQL("DROP TABLE IF EXISTS `pagamento`");
        db.execSQL("DROP TABLE IF EXISTS `evento`");
        db.execSQL("DROP TABLE IF EXISTS `lote`");
        db.execSQL("DROP TABLE IF EXISTS `lote_baixa`");
        db.execSQL("DROP TABLE IF EXISTS `perda`");
        db.execSQL("DROP TABLE IF EXISTS `preco_queima`");
        db.execSQL("DROP TABLE IF EXISTS `encomenda`");
        db.execSQL("DROP TABLE IF EXISTS `encomenda_item`");
        db.execSQL("DROP VIEW IF EXISTS `vw_produto_estoque`");
        db.execSQL("DROP VIEW IF EXISTS `vw_estoque_linha`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsGrupo = new HashMap<String, TableInfo.Column>(5);
        _columnsGrupo.put("GRU_ID", new TableInfo.Column("GRU_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGrupo.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGrupo.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGrupo.put("GRU_NOME", new TableInfo.Column("GRU_NOME", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGrupo.put("GRU_OBS", new TableInfo.Column("GRU_OBS", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGrupo = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGrupo = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGrupo = new TableInfo("grupo", _columnsGrupo, _foreignKeysGrupo, _indicesGrupo);
        final TableInfo _existingGrupo = TableInfo.read(db, "grupo");
        if (!_infoGrupo.equals(_existingGrupo)) {
          return new RoomOpenHelper.ValidationResult(false, "grupo(br.com.banavita.app.data.local.entity.GrupoEntity).\n"
                  + " Expected:\n" + _infoGrupo + "\n"
                  + " Found:\n" + _existingGrupo);
        }
        final HashMap<String, TableInfo.Column> _columnsProduto = new HashMap<String, TableInfo.Column>(17);
        _columnsProduto.put("PRO_ID", new TableInfo.Column("PRO_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_DESCRICAO", new TableInfo.Column("PRO_DESCRICAO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_PRECO", new TableInfo.Column("PRO_PRECO", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_GRUPO", new TableInfo.Column("PRO_GRUPO", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_UNIDADE", new TableInfo.Column("PRO_UNIDADE", "TEXT", true, 0, "'UN'", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_PERECIVEL", new TableInfo.Column("PRO_PERECIVEL", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_DIAS_VERDE", new TableInfo.Column("PRO_DIAS_VERDE", "INTEGER", true, 0, "3", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_DIAS_VERDOLENGA", new TableInfo.Column("PRO_DIAS_VERDOLENGA", "INTEGER", true, 0, "2", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_DIAS_AMARELA", new TableInfo.Column("PRO_DIAS_AMARELA", "INTEGER", true, 0, "2", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_DIAS_PINTADA", new TableInfo.Column("PRO_DIAS_PINTADA", "INTEGER", true, 0, "2", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_PRECO_VERDE", new TableInfo.Column("PRO_PRECO_VERDE", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_PRECO_VERDOLENGA", new TableInfo.Column("PRO_PRECO_VERDOLENGA", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_PRECO_AMARELA", new TableInfo.Column("PRO_PRECO_AMARELA", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_PRECO_PINTADA", new TableInfo.Column("PRO_PRECO_PINTADA", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_PRECO_PODRE", new TableInfo.Column("PRO_PRECO_PODRE", "INTEGER", true, 0, "0", TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProduto = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysProduto.add(new TableInfo.ForeignKey("grupo", "RESTRICT", "NO ACTION", Arrays.asList("PRO_GRUPO"), Arrays.asList("GRU_ID")));
        final HashSet<TableInfo.Index> _indicesProduto = new HashSet<TableInfo.Index>(1);
        _indicesProduto.add(new TableInfo.Index("index_produto_PRO_GRUPO", false, Arrays.asList("PRO_GRUPO"), Arrays.asList("ASC")));
        final TableInfo _infoProduto = new TableInfo("produto", _columnsProduto, _foreignKeysProduto, _indicesProduto);
        final TableInfo _existingProduto = TableInfo.read(db, "produto");
        if (!_infoProduto.equals(_existingProduto)) {
          return new RoomOpenHelper.ValidationResult(false, "produto(br.com.banavita.app.data.local.entity.ProdutoEntity).\n"
                  + " Expected:\n" + _infoProduto + "\n"
                  + " Found:\n" + _existingProduto);
        }
        final HashMap<String, TableInfo.Column> _columnsCliente = new HashMap<String, TableInfo.Column>(6);
        _columnsCliente.put("CLI_ID", new TableInfo.Column("CLI_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCliente.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCliente.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCliente.put("CLI_NOME", new TableInfo.Column("CLI_NOME", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCliente.put("CLI_TELEFONE", new TableInfo.Column("CLI_TELEFONE", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCliente.put("CLI_PONTO_PREF", new TableInfo.Column("CLI_PONTO_PREF", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCliente = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCliente = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCliente = new TableInfo("cliente", _columnsCliente, _foreignKeysCliente, _indicesCliente);
        final TableInfo _existingCliente = TableInfo.read(db, "cliente");
        if (!_infoCliente.equals(_existingCliente)) {
          return new RoomOpenHelper.ValidationResult(false, "cliente(br.com.banavita.app.data.local.entity.ClienteEntity).\n"
                  + " Expected:\n" + _infoCliente + "\n"
                  + " Found:\n" + _existingCliente);
        }
        final HashMap<String, TableInfo.Column> _columnsFornecedor = new HashMap<String, TableInfo.Column>(5);
        _columnsFornecedor.put("FOR_ID", new TableInfo.Column("FOR_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFornecedor.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFornecedor.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFornecedor.put("FOR_NOME", new TableInfo.Column("FOR_NOME", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFornecedor.put("FOR_TELEFONE", new TableInfo.Column("FOR_TELEFONE", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFornecedor = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFornecedor = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFornecedor = new TableInfo("fornecedor", _columnsFornecedor, _foreignKeysFornecedor, _indicesFornecedor);
        final TableInfo _existingFornecedor = TableInfo.read(db, "fornecedor");
        if (!_infoFornecedor.equals(_existingFornecedor)) {
          return new RoomOpenHelper.ValidationResult(false, "fornecedor(br.com.banavita.app.data.local.entity.FornecedorEntity).\n"
                  + " Expected:\n" + _infoFornecedor + "\n"
                  + " Found:\n" + _existingFornecedor);
        }
        final HashMap<String, TableInfo.Column> _columnsVenda = new HashMap<String, TableInfo.Column>(7);
        _columnsVenda.put("VEN_ID", new TableInfo.Column("VEN_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenda.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenda.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenda.put("VEN_DATA", new TableInfo.Column("VEN_DATA", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenda.put("VEN_CLIENTE", new TableInfo.Column("VEN_CLIENTE", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenda.put("VEN_VLRTOT", new TableInfo.Column("VEN_VLRTOT", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVenda.put("VEN_PRAZO", new TableInfo.Column("VEN_PRAZO", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVenda = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVenda.add(new TableInfo.ForeignKey("cliente", "RESTRICT", "NO ACTION", Arrays.asList("VEN_CLIENTE"), Arrays.asList("CLI_ID")));
        final HashSet<TableInfo.Index> _indicesVenda = new HashSet<TableInfo.Index>(2);
        _indicesVenda.add(new TableInfo.Index("index_venda_VEN_DATA", false, Arrays.asList("VEN_DATA"), Arrays.asList("ASC")));
        _indicesVenda.add(new TableInfo.Index("index_venda_VEN_CLIENTE", false, Arrays.asList("VEN_CLIENTE"), Arrays.asList("ASC")));
        final TableInfo _infoVenda = new TableInfo("venda", _columnsVenda, _foreignKeysVenda, _indicesVenda);
        final TableInfo _existingVenda = TableInfo.read(db, "venda");
        if (!_infoVenda.equals(_existingVenda)) {
          return new RoomOpenHelper.ValidationResult(false, "venda(br.com.banavita.app.data.local.entity.VendaEntity).\n"
                  + " Expected:\n" + _infoVenda + "\n"
                  + " Found:\n" + _existingVenda);
        }
        final HashMap<String, TableInfo.Column> _columnsVendaItem = new HashMap<String, TableInfo.Column>(9);
        _columnsVendaItem.put("VEI_ID", new TableInfo.Column("VEI_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_VENDA", new TableInfo.Column("VEI_VENDA", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_PRODUTO", new TableInfo.Column("VEI_PRODUTO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_VLRUN", new TableInfo.Column("VEI_VLRUN", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_QTDE", new TableInfo.Column("VEI_QTDE", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_VLRTOT", new TableInfo.Column("VEI_VLRTOT", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_ESTADO", new TableInfo.Column("VEI_ESTADO", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVendaItem = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysVendaItem.add(new TableInfo.ForeignKey("venda", "CASCADE", "NO ACTION", Arrays.asList("VEI_VENDA"), Arrays.asList("VEN_ID")));
        _foreignKeysVendaItem.add(new TableInfo.ForeignKey("produto", "RESTRICT", "NO ACTION", Arrays.asList("VEI_PRODUTO"), Arrays.asList("PRO_ID")));
        final HashSet<TableInfo.Index> _indicesVendaItem = new HashSet<TableInfo.Index>(2);
        _indicesVendaItem.add(new TableInfo.Index("index_venda_item_VEI_VENDA", false, Arrays.asList("VEI_VENDA"), Arrays.asList("ASC")));
        _indicesVendaItem.add(new TableInfo.Index("index_venda_item_VEI_PRODUTO", false, Arrays.asList("VEI_PRODUTO"), Arrays.asList("ASC")));
        final TableInfo _infoVendaItem = new TableInfo("venda_item", _columnsVendaItem, _foreignKeysVendaItem, _indicesVendaItem);
        final TableInfo _existingVendaItem = TableInfo.read(db, "venda_item");
        if (!_infoVendaItem.equals(_existingVendaItem)) {
          return new RoomOpenHelper.ValidationResult(false, "venda_item(br.com.banavita.app.data.local.entity.VendaItemEntity).\n"
                  + " Expected:\n" + _infoVendaItem + "\n"
                  + " Found:\n" + _existingVendaItem);
        }
        final HashMap<String, TableInfo.Column> _columnsCompra = new HashMap<String, TableInfo.Column>(7);
        _columnsCompra.put("COM_ID", new TableInfo.Column("COM_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompra.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompra.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompra.put("COM_DATA", new TableInfo.Column("COM_DATA", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompra.put("COM_FORNECEDOR", new TableInfo.Column("COM_FORNECEDOR", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompra.put("COM_VLRTOT", new TableInfo.Column("COM_VLRTOT", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompra.put("COM_PRAZO", new TableInfo.Column("COM_PRAZO", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCompra = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysCompra.add(new TableInfo.ForeignKey("fornecedor", "RESTRICT", "NO ACTION", Arrays.asList("COM_FORNECEDOR"), Arrays.asList("FOR_ID")));
        final HashSet<TableInfo.Index> _indicesCompra = new HashSet<TableInfo.Index>(2);
        _indicesCompra.add(new TableInfo.Index("index_compra_COM_DATA", false, Arrays.asList("COM_DATA"), Arrays.asList("ASC")));
        _indicesCompra.add(new TableInfo.Index("index_compra_COM_FORNECEDOR", false, Arrays.asList("COM_FORNECEDOR"), Arrays.asList("ASC")));
        final TableInfo _infoCompra = new TableInfo("compra", _columnsCompra, _foreignKeysCompra, _indicesCompra);
        final TableInfo _existingCompra = TableInfo.read(db, "compra");
        if (!_infoCompra.equals(_existingCompra)) {
          return new RoomOpenHelper.ValidationResult(false, "compra(br.com.banavita.app.data.local.entity.CompraEntity).\n"
                  + " Expected:\n" + _infoCompra + "\n"
                  + " Found:\n" + _existingCompra);
        }
        final HashMap<String, TableInfo.Column> _columnsCompraItem = new HashMap<String, TableInfo.Column>(8);
        _columnsCompraItem.put("COI_ID", new TableInfo.Column("COI_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompraItem.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompraItem.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompraItem.put("COI_COMPRA", new TableInfo.Column("COI_COMPRA", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompraItem.put("COI_PRODUTO", new TableInfo.Column("COI_PRODUTO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompraItem.put("COI_VLRUN", new TableInfo.Column("COI_VLRUN", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompraItem.put("COI_QTDE", new TableInfo.Column("COI_QTDE", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCompraItem.put("COI_VLRTOT", new TableInfo.Column("COI_VLRTOT", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCompraItem = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysCompraItem.add(new TableInfo.ForeignKey("compra", "CASCADE", "NO ACTION", Arrays.asList("COI_COMPRA"), Arrays.asList("COM_ID")));
        _foreignKeysCompraItem.add(new TableInfo.ForeignKey("produto", "RESTRICT", "NO ACTION", Arrays.asList("COI_PRODUTO"), Arrays.asList("PRO_ID")));
        final HashSet<TableInfo.Index> _indicesCompraItem = new HashSet<TableInfo.Index>(2);
        _indicesCompraItem.add(new TableInfo.Index("index_compra_item_COI_COMPRA", false, Arrays.asList("COI_COMPRA"), Arrays.asList("ASC")));
        _indicesCompraItem.add(new TableInfo.Index("index_compra_item_COI_PRODUTO", false, Arrays.asList("COI_PRODUTO"), Arrays.asList("ASC")));
        final TableInfo _infoCompraItem = new TableInfo("compra_item", _columnsCompraItem, _foreignKeysCompraItem, _indicesCompraItem);
        final TableInfo _existingCompraItem = TableInfo.read(db, "compra_item");
        if (!_infoCompraItem.equals(_existingCompraItem)) {
          return new RoomOpenHelper.ValidationResult(false, "compra_item(br.com.banavita.app.data.local.entity.CompraItemEntity).\n"
                  + " Expected:\n" + _infoCompraItem + "\n"
                  + " Found:\n" + _existingCompraItem);
        }
        final HashMap<String, TableInfo.Column> _columnsRecebimento = new HashMap<String, TableInfo.Column>(6);
        _columnsRecebimento.put("REC_ID", new TableInfo.Column("REC_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecebimento.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecebimento.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecebimento.put("REC_VENDA", new TableInfo.Column("REC_VENDA", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecebimento.put("REC_DATA", new TableInfo.Column("REC_DATA", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecebimento.put("REC_VALOR", new TableInfo.Column("REC_VALOR", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecebimento = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysRecebimento.add(new TableInfo.ForeignKey("venda", "CASCADE", "NO ACTION", Arrays.asList("REC_VENDA"), Arrays.asList("VEN_ID")));
        final HashSet<TableInfo.Index> _indicesRecebimento = new HashSet<TableInfo.Index>(2);
        _indicesRecebimento.add(new TableInfo.Index("index_recebimento_REC_VENDA", false, Arrays.asList("REC_VENDA"), Arrays.asList("ASC")));
        _indicesRecebimento.add(new TableInfo.Index("index_recebimento_REC_DATA", false, Arrays.asList("REC_DATA"), Arrays.asList("ASC")));
        final TableInfo _infoRecebimento = new TableInfo("recebimento", _columnsRecebimento, _foreignKeysRecebimento, _indicesRecebimento);
        final TableInfo _existingRecebimento = TableInfo.read(db, "recebimento");
        if (!_infoRecebimento.equals(_existingRecebimento)) {
          return new RoomOpenHelper.ValidationResult(false, "recebimento(br.com.banavita.app.data.local.entity.RecebimentoEntity).\n"
                  + " Expected:\n" + _infoRecebimento + "\n"
                  + " Found:\n" + _existingRecebimento);
        }
        final HashMap<String, TableInfo.Column> _columnsPagamento = new HashMap<String, TableInfo.Column>(6);
        _columnsPagamento.put("PAG_ID", new TableInfo.Column("PAG_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPagamento.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPagamento.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPagamento.put("PAG_COMPRA", new TableInfo.Column("PAG_COMPRA", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPagamento.put("PAG_DATA", new TableInfo.Column("PAG_DATA", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPagamento.put("PAG_VALOR", new TableInfo.Column("PAG_VALOR", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPagamento = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPagamento.add(new TableInfo.ForeignKey("compra", "CASCADE", "NO ACTION", Arrays.asList("PAG_COMPRA"), Arrays.asList("COM_ID")));
        final HashSet<TableInfo.Index> _indicesPagamento = new HashSet<TableInfo.Index>(2);
        _indicesPagamento.add(new TableInfo.Index("index_pagamento_PAG_COMPRA", false, Arrays.asList("PAG_COMPRA"), Arrays.asList("ASC")));
        _indicesPagamento.add(new TableInfo.Index("index_pagamento_PAG_DATA", false, Arrays.asList("PAG_DATA"), Arrays.asList("ASC")));
        final TableInfo _infoPagamento = new TableInfo("pagamento", _columnsPagamento, _foreignKeysPagamento, _indicesPagamento);
        final TableInfo _existingPagamento = TableInfo.read(db, "pagamento");
        if (!_infoPagamento.equals(_existingPagamento)) {
          return new RoomOpenHelper.ValidationResult(false, "pagamento(br.com.banavita.app.data.local.entity.PagamentoEntity).\n"
                  + " Expected:\n" + _infoPagamento + "\n"
                  + " Found:\n" + _existingPagamento);
        }
        final HashMap<String, TableInfo.Column> _columnsEvento = new HashMap<String, TableInfo.Column>(12);
        _columnsEvento.put("EVE_ID", new TableInfo.Column("EVE_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("EVE_TITULO", new TableInfo.Column("EVE_TITULO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("EVE_DATAHORA", new TableInfo.Column("EVE_DATAHORA", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("EVE_OBS", new TableInfo.Column("EVE_OBS", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("EVE_CLIENTE", new TableInfo.Column("EVE_CLIENTE", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("EVE_FORNECEDOR", new TableInfo.Column("EVE_FORNECEDOR", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("EVE_NOTIFICAR", new TableInfo.Column("EVE_NOTIFICAR", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("EVE_ANTECIPACAO_MIN", new TableInfo.Column("EVE_ANTECIPACAO_MIN", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("EVE_NOTIF_TITULO", new TableInfo.Column("EVE_NOTIF_TITULO", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("EVE_NOTIF_CORPO", new TableInfo.Column("EVE_NOTIF_CORPO", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEvento = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysEvento.add(new TableInfo.ForeignKey("cliente", "RESTRICT", "NO ACTION", Arrays.asList("EVE_CLIENTE"), Arrays.asList("CLI_ID")));
        _foreignKeysEvento.add(new TableInfo.ForeignKey("fornecedor", "RESTRICT", "NO ACTION", Arrays.asList("EVE_FORNECEDOR"), Arrays.asList("FOR_ID")));
        final HashSet<TableInfo.Index> _indicesEvento = new HashSet<TableInfo.Index>(3);
        _indicesEvento.add(new TableInfo.Index("index_evento_EVE_DATAHORA", false, Arrays.asList("EVE_DATAHORA"), Arrays.asList("ASC")));
        _indicesEvento.add(new TableInfo.Index("index_evento_EVE_CLIENTE", false, Arrays.asList("EVE_CLIENTE"), Arrays.asList("ASC")));
        _indicesEvento.add(new TableInfo.Index("index_evento_EVE_FORNECEDOR", false, Arrays.asList("EVE_FORNECEDOR"), Arrays.asList("ASC")));
        final TableInfo _infoEvento = new TableInfo("evento", _columnsEvento, _foreignKeysEvento, _indicesEvento);
        final TableInfo _existingEvento = TableInfo.read(db, "evento");
        if (!_infoEvento.equals(_existingEvento)) {
          return new RoomOpenHelper.ValidationResult(false, "evento(br.com.banavita.app.data.local.entity.EventoEntity).\n"
                  + " Expected:\n" + _infoEvento + "\n"
                  + " Found:\n" + _existingEvento);
        }
        final HashMap<String, TableInfo.Column> _columnsLote = new HashMap<String, TableInfo.Column>(15);
        _columnsLote.put("LOT_ID", new TableInfo.Column("LOT_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_PRODUTO", new TableInfo.Column("LOT_PRODUTO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_COMPRA", new TableInfo.Column("LOT_COMPRA", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_COMPRA_ITEM", new TableInfo.Column("LOT_COMPRA_ITEM", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_ORIGEM", new TableInfo.Column("LOT_ORIGEM", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_DATA", new TableInfo.Column("LOT_DATA", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_QTDE_INICIAL", new TableInfo.Column("LOT_QTDE_INICIAL", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_QTDE_RESTANTE", new TableInfo.Column("LOT_QTDE_RESTANTE", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_CAMARA", new TableInfo.Column("LOT_CAMARA", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_ESTADO", new TableInfo.Column("LOT_ESTADO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_ETAPA_INICIO_DAY", new TableInfo.Column("LOT_ETAPA_INICIO_DAY", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_CUSTO_UN", new TableInfo.Column("LOT_CUSTO_UN", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLote.put("LOT_ROTULO", new TableInfo.Column("LOT_ROTULO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLote = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysLote.add(new TableInfo.ForeignKey("produto", "RESTRICT", "NO ACTION", Arrays.asList("LOT_PRODUTO"), Arrays.asList("PRO_ID")));
        _foreignKeysLote.add(new TableInfo.ForeignKey("compra", "SET NULL", "NO ACTION", Arrays.asList("LOT_COMPRA"), Arrays.asList("COM_ID")));
        final HashSet<TableInfo.Index> _indicesLote = new HashSet<TableInfo.Index>(5);
        _indicesLote.add(new TableInfo.Index("index_lote_LOT_PRODUTO", false, Arrays.asList("LOT_PRODUTO"), Arrays.asList("ASC")));
        _indicesLote.add(new TableInfo.Index("index_lote_LOT_COMPRA", false, Arrays.asList("LOT_COMPRA"), Arrays.asList("ASC")));
        _indicesLote.add(new TableInfo.Index("index_lote_LOT_ESTADO", false, Arrays.asList("LOT_ESTADO"), Arrays.asList("ASC")));
        _indicesLote.add(new TableInfo.Index("index_lote_LOT_CAMARA", false, Arrays.asList("LOT_CAMARA"), Arrays.asList("ASC")));
        _indicesLote.add(new TableInfo.Index("index_lote_LOT_ORIGEM", false, Arrays.asList("LOT_ORIGEM"), Arrays.asList("ASC")));
        final TableInfo _infoLote = new TableInfo("lote", _columnsLote, _foreignKeysLote, _indicesLote);
        final TableInfo _existingLote = TableInfo.read(db, "lote");
        if (!_infoLote.equals(_existingLote)) {
          return new RoomOpenHelper.ValidationResult(false, "lote(br.com.banavita.app.data.local.entity.LoteEntity).\n"
                  + " Expected:\n" + _infoLote + "\n"
                  + " Found:\n" + _existingLote);
        }
        final HashMap<String, TableInfo.Column> _columnsLoteBaixa = new HashMap<String, TableInfo.Column>(6);
        _columnsLoteBaixa.put("LBA_ID", new TableInfo.Column("LBA_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoteBaixa.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoteBaixa.put("LBA_LOTE", new TableInfo.Column("LBA_LOTE", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoteBaixa.put("LBA_QTDE", new TableInfo.Column("LBA_QTDE", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoteBaixa.put("LBA_VENDA", new TableInfo.Column("LBA_VENDA", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLoteBaixa.put("LBA_PERDA", new TableInfo.Column("LBA_PERDA", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLoteBaixa = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysLoteBaixa.add(new TableInfo.ForeignKey("lote", "RESTRICT", "NO ACTION", Arrays.asList("LBA_LOTE"), Arrays.asList("LOT_ID")));
        final HashSet<TableInfo.Index> _indicesLoteBaixa = new HashSet<TableInfo.Index>(3);
        _indicesLoteBaixa.add(new TableInfo.Index("index_lote_baixa_LBA_LOTE", false, Arrays.asList("LBA_LOTE"), Arrays.asList("ASC")));
        _indicesLoteBaixa.add(new TableInfo.Index("index_lote_baixa_LBA_VENDA", false, Arrays.asList("LBA_VENDA"), Arrays.asList("ASC")));
        _indicesLoteBaixa.add(new TableInfo.Index("index_lote_baixa_LBA_PERDA", false, Arrays.asList("LBA_PERDA"), Arrays.asList("ASC")));
        final TableInfo _infoLoteBaixa = new TableInfo("lote_baixa", _columnsLoteBaixa, _foreignKeysLoteBaixa, _indicesLoteBaixa);
        final TableInfo _existingLoteBaixa = TableInfo.read(db, "lote_baixa");
        if (!_infoLoteBaixa.equals(_existingLoteBaixa)) {
          return new RoomOpenHelper.ValidationResult(false, "lote_baixa(br.com.banavita.app.data.local.entity.LoteBaixaEntity).\n"
                  + " Expected:\n" + _infoLoteBaixa + "\n"
                  + " Found:\n" + _existingLoteBaixa);
        }
        final HashMap<String, TableInfo.Column> _columnsPerda = new HashMap<String, TableInfo.Column>(10);
        _columnsPerda.put("PER_ID", new TableInfo.Column("PER_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerda.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerda.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerda.put("PER_DATA", new TableInfo.Column("PER_DATA", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerda.put("PER_PRODUTO", new TableInfo.Column("PER_PRODUTO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerda.put("PER_LOTE", new TableInfo.Column("PER_LOTE", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerda.put("PER_ESTADO", new TableInfo.Column("PER_ESTADO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerda.put("PER_QTDE", new TableInfo.Column("PER_QTDE", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerda.put("PER_MOTIVO", new TableInfo.Column("PER_MOTIVO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPerda.put("PER_VALOR", new TableInfo.Column("PER_VALOR", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPerda = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysPerda.add(new TableInfo.ForeignKey("produto", "RESTRICT", "NO ACTION", Arrays.asList("PER_PRODUTO"), Arrays.asList("PRO_ID")));
        _foreignKeysPerda.add(new TableInfo.ForeignKey("lote", "SET NULL", "NO ACTION", Arrays.asList("PER_LOTE"), Arrays.asList("LOT_ID")));
        final HashSet<TableInfo.Index> _indicesPerda = new HashSet<TableInfo.Index>(3);
        _indicesPerda.add(new TableInfo.Index("index_perda_PER_PRODUTO", false, Arrays.asList("PER_PRODUTO"), Arrays.asList("ASC")));
        _indicesPerda.add(new TableInfo.Index("index_perda_PER_LOTE", false, Arrays.asList("PER_LOTE"), Arrays.asList("ASC")));
        _indicesPerda.add(new TableInfo.Index("index_perda_PER_DATA", false, Arrays.asList("PER_DATA"), Arrays.asList("ASC")));
        final TableInfo _infoPerda = new TableInfo("perda", _columnsPerda, _foreignKeysPerda, _indicesPerda);
        final TableInfo _existingPerda = TableInfo.read(db, "perda");
        if (!_infoPerda.equals(_existingPerda)) {
          return new RoomOpenHelper.ValidationResult(false, "perda(br.com.banavita.app.data.local.entity.PerdaEntity).\n"
                  + " Expected:\n" + _infoPerda + "\n"
                  + " Found:\n" + _existingPerda);
        }
        final HashMap<String, TableInfo.Column> _columnsPrecoQueima = new HashMap<String, TableInfo.Column>(6);
        _columnsPrecoQueima.put("QUE_ID", new TableInfo.Column("QUE_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPrecoQueima.put("QUE_PRODUTO", new TableInfo.Column("QUE_PRODUTO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPrecoQueima.put("QUE_ESTADO", new TableInfo.Column("QUE_ESTADO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPrecoQueima.put("QUE_PRECO", new TableInfo.Column("QUE_PRECO", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPrecoQueima.put("QUE_INICIO", new TableInfo.Column("QUE_INICIO", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPrecoQueima.put("QUE_FIM", new TableInfo.Column("QUE_FIM", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPrecoQueima = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPrecoQueima.add(new TableInfo.ForeignKey("produto", "CASCADE", "NO ACTION", Arrays.asList("QUE_PRODUTO"), Arrays.asList("PRO_ID")));
        final HashSet<TableInfo.Index> _indicesPrecoQueima = new HashSet<TableInfo.Index>(1);
        _indicesPrecoQueima.add(new TableInfo.Index("index_preco_queima_QUE_PRODUTO_QUE_ESTADO_QUE_INICIO", true, Arrays.asList("QUE_PRODUTO", "QUE_ESTADO", "QUE_INICIO"), Arrays.asList("ASC", "ASC", "ASC")));
        final TableInfo _infoPrecoQueima = new TableInfo("preco_queima", _columnsPrecoQueima, _foreignKeysPrecoQueima, _indicesPrecoQueima);
        final TableInfo _existingPrecoQueima = TableInfo.read(db, "preco_queima");
        if (!_infoPrecoQueima.equals(_existingPrecoQueima)) {
          return new RoomOpenHelper.ValidationResult(false, "preco_queima(br.com.banavita.app.data.local.entity.PrecoQueimaEntity).\n"
                  + " Expected:\n" + _infoPrecoQueima + "\n"
                  + " Found:\n" + _existingPrecoQueima);
        }
        final HashMap<String, TableInfo.Column> _columnsEncomenda = new HashMap<String, TableInfo.Column>(7);
        _columnsEncomenda.put("ENC_ID", new TableInfo.Column("ENC_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomenda.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomenda.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomenda.put("ENC_CLIENTE", new TableInfo.Column("ENC_CLIENTE", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomenda.put("ENC_DATA_ENTREGA", new TableInfo.Column("ENC_DATA_ENTREGA", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomenda.put("ENC_STATUS", new TableInfo.Column("ENC_STATUS", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomenda.put("ENC_VENDA", new TableInfo.Column("ENC_VENDA", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEncomenda = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysEncomenda.add(new TableInfo.ForeignKey("cliente", "RESTRICT", "NO ACTION", Arrays.asList("ENC_CLIENTE"), Arrays.asList("CLI_ID")));
        final HashSet<TableInfo.Index> _indicesEncomenda = new HashSet<TableInfo.Index>(3);
        _indicesEncomenda.add(new TableInfo.Index("index_encomenda_ENC_CLIENTE", false, Arrays.asList("ENC_CLIENTE"), Arrays.asList("ASC")));
        _indicesEncomenda.add(new TableInfo.Index("index_encomenda_ENC_DATA_ENTREGA", false, Arrays.asList("ENC_DATA_ENTREGA"), Arrays.asList("ASC")));
        _indicesEncomenda.add(new TableInfo.Index("index_encomenda_ENC_STATUS", false, Arrays.asList("ENC_STATUS"), Arrays.asList("ASC")));
        final TableInfo _infoEncomenda = new TableInfo("encomenda", _columnsEncomenda, _foreignKeysEncomenda, _indicesEncomenda);
        final TableInfo _existingEncomenda = TableInfo.read(db, "encomenda");
        if (!_infoEncomenda.equals(_existingEncomenda)) {
          return new RoomOpenHelper.ValidationResult(false, "encomenda(br.com.banavita.app.data.local.entity.EncomendaEntity).\n"
                  + " Expected:\n" + _infoEncomenda + "\n"
                  + " Found:\n" + _existingEncomenda);
        }
        final HashMap<String, TableInfo.Column> _columnsEncomendaItem = new HashMap<String, TableInfo.Column>(7);
        _columnsEncomendaItem.put("ENI_ID", new TableInfo.Column("ENI_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomendaItem.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomendaItem.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomendaItem.put("ENI_ENCOMENDA", new TableInfo.Column("ENI_ENCOMENDA", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomendaItem.put("ENI_PRODUTO", new TableInfo.Column("ENI_PRODUTO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomendaItem.put("ENI_ESTADO", new TableInfo.Column("ENI_ESTADO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEncomendaItem.put("ENI_QTDE", new TableInfo.Column("ENI_QTDE", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEncomendaItem = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysEncomendaItem.add(new TableInfo.ForeignKey("encomenda", "CASCADE", "NO ACTION", Arrays.asList("ENI_ENCOMENDA"), Arrays.asList("ENC_ID")));
        _foreignKeysEncomendaItem.add(new TableInfo.ForeignKey("produto", "RESTRICT", "NO ACTION", Arrays.asList("ENI_PRODUTO"), Arrays.asList("PRO_ID")));
        final HashSet<TableInfo.Index> _indicesEncomendaItem = new HashSet<TableInfo.Index>(2);
        _indicesEncomendaItem.add(new TableInfo.Index("index_encomenda_item_ENI_ENCOMENDA", false, Arrays.asList("ENI_ENCOMENDA"), Arrays.asList("ASC")));
        _indicesEncomendaItem.add(new TableInfo.Index("index_encomenda_item_ENI_PRODUTO", false, Arrays.asList("ENI_PRODUTO"), Arrays.asList("ASC")));
        final TableInfo _infoEncomendaItem = new TableInfo("encomenda_item", _columnsEncomendaItem, _foreignKeysEncomendaItem, _indicesEncomendaItem);
        final TableInfo _existingEncomendaItem = TableInfo.read(db, "encomenda_item");
        if (!_infoEncomendaItem.equals(_existingEncomendaItem)) {
          return new RoomOpenHelper.ValidationResult(false, "encomenda_item(br.com.banavita.app.data.local.entity.EncomendaItemEntity).\n"
                  + " Expected:\n" + _infoEncomendaItem + "\n"
                  + " Found:\n" + _existingEncomendaItem);
        }
        final ViewInfo _infoVwProdutoEstoque = new ViewInfo("vw_produto_estoque", "CREATE VIEW `vw_produto_estoque` AS SELECT\n"
                + "            p.PRO_ID AS produto_id,\n"
                + "            p.PRO_UNIDADE AS unidade,\n"
                + "            CASE\n"
                + "                WHEN p.PRO_PERECIVEL = 1 THEN COALESCE(l.qtde, 0)\n"
                + "                ELSE COALESCE(c.qtde, 0) - COALESCE(v.qtde, 0)\n"
                + "            END AS saldo\n"
                + "        FROM produto p\n"
                + "        LEFT JOIN (\n"
                + "            SELECT COI_PRODUTO AS id, SUM(COI_QTDE) AS qtde\n"
                + "            FROM compra_item\n"
                + "            GROUP BY COI_PRODUTO\n"
                + "        ) c ON c.id = p.PRO_ID\n"
                + "        LEFT JOIN (\n"
                + "            SELECT VEI_PRODUTO AS id, SUM(VEI_QTDE) AS qtde\n"
                + "            FROM venda_item\n"
                + "            GROUP BY VEI_PRODUTO\n"
                + "        ) v ON v.id = p.PRO_ID\n"
                + "        LEFT JOIN (\n"
                + "            SELECT LOT_PRODUTO AS id, SUM(LOT_QTDE_RESTANTE) AS qtde\n"
                + "            FROM lote\n"
                + "            GROUP BY LOT_PRODUTO\n"
                + "        ) l ON l.id = p.PRO_ID");
        final ViewInfo _existingVwProdutoEstoque = ViewInfo.read(db, "vw_produto_estoque");
        if (!_infoVwProdutoEstoque.equals(_existingVwProdutoEstoque)) {
          return new RoomOpenHelper.ValidationResult(false, "vw_produto_estoque(br.com.banavita.app.data.local.view.ProdutoEstoqueView).\n"
                  + " Expected:\n" + _infoVwProdutoEstoque + "\n"
                  + " Found:\n" + _existingVwProdutoEstoque);
        }
        final ViewInfo _infoVwEstoqueLinha = new ViewInfo("vw_estoque_linha", "CREATE VIEW `vw_estoque_linha` AS SELECT\n"
                + "            l.LOT_PRODUTO AS produto_id,\n"
                + "            p.PRO_DESCRICAO AS descricao,\n"
                + "            p.PRO_UNIDADE AS unidade,\n"
                + "            l.LOT_ESTADO AS estado,\n"
                + "            l.LOT_CAMARA AS camara,\n"
                + "            SUM(l.LOT_QTDE_RESTANTE) AS quantidade,\n"
                + "            1 AS perecivel\n"
                + "        FROM lote l\n"
                + "        INNER JOIN produto p ON p.PRO_ID = l.LOT_PRODUTO\n"
                + "        WHERE l.LOT_QTDE_RESTANTE > 0.0000001\n"
                + "        GROUP BY l.LOT_PRODUTO, p.PRO_DESCRICAO, p.PRO_UNIDADE, l.LOT_ESTADO, l.LOT_CAMARA\n"
                + "        UNION ALL\n"
                + "        SELECT\n"
                + "            p.PRO_ID AS produto_id,\n"
                + "            p.PRO_DESCRICAO AS descricao,\n"
                + "            p.PRO_UNIDADE AS unidade,\n"
                + "            NULL AS estado,\n"
                + "            NULL AS camara,\n"
                + "            COALESCE(c.qtde, 0) - COALESCE(v.qtde, 0) AS quantidade,\n"
                + "            0 AS perecivel\n"
                + "        FROM produto p\n"
                + "        LEFT JOIN (\n"
                + "            SELECT COI_PRODUTO AS id, SUM(COI_QTDE) AS qtde\n"
                + "            FROM compra_item\n"
                + "            GROUP BY COI_PRODUTO\n"
                + "        ) c ON c.id = p.PRO_ID\n"
                + "        LEFT JOIN (\n"
                + "            SELECT VEI_PRODUTO AS id, SUM(VEI_QTDE) AS qtde\n"
                + "            FROM venda_item\n"
                + "            GROUP BY VEI_PRODUTO\n"
                + "        ) v ON v.id = p.PRO_ID\n"
                + "        WHERE p.PRO_PERECIVEL = 0");
        final ViewInfo _existingVwEstoqueLinha = ViewInfo.read(db, "vw_estoque_linha");
        if (!_infoVwEstoqueLinha.equals(_existingVwEstoqueLinha)) {
          return new RoomOpenHelper.ValidationResult(false, "vw_estoque_linha(br.com.banavita.app.data.local.view.EstoqueLinhaView).\n"
                  + " Expected:\n" + _infoVwEstoqueLinha + "\n"
                  + " Found:\n" + _existingVwEstoqueLinha);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "6972427fd0320bccb955b02fdbc67915", "ad50c87d0524a55d8635d60aca58b194");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(2);
    final HashSet<String> _tables = new HashSet<String>(4);
    _tables.add("produto");
    _tables.add("compra_item");
    _tables.add("venda_item");
    _tables.add("lote");
    _viewTables.put("vw_produto_estoque", _tables);
    final HashSet<String> _tables_1 = new HashSet<String>(4);
    _tables_1.add("lote");
    _tables_1.add("produto");
    _tables_1.add("compra_item");
    _tables_1.add("venda_item");
    _viewTables.put("vw_estoque_linha", _tables_1);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "grupo","produto","cliente","fornecedor","venda","venda_item","compra","compra_item","recebimento","pagamento","evento","lote","lote_baixa","perda","preco_queima","encomenda","encomenda_item");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `produto`");
      _db.execSQL("DELETE FROM `grupo`");
      _db.execSQL("DELETE FROM `venda`");
      _db.execSQL("DELETE FROM `cliente`");
      _db.execSQL("DELETE FROM `fornecedor`");
      _db.execSQL("DELETE FROM `venda_item`");
      _db.execSQL("DELETE FROM `compra`");
      _db.execSQL("DELETE FROM `compra_item`");
      _db.execSQL("DELETE FROM `recebimento`");
      _db.execSQL("DELETE FROM `pagamento`");
      _db.execSQL("DELETE FROM `evento`");
      _db.execSQL("DELETE FROM `lote_baixa`");
      _db.execSQL("DELETE FROM `lote`");
      _db.execSQL("DELETE FROM `perda`");
      _db.execSQL("DELETE FROM `preco_queima`");
      _db.execSQL("DELETE FROM `encomenda`");
      _db.execSQL("DELETE FROM `encomenda_item`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(GrupoDao.class, GrupoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProdutoDao.class, ProdutoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ClienteDao.class, ClienteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FornecedorDao.class, FornecedorDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VendaDao.class, VendaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CompraDao.class, CompraDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EstoqueDao.class, EstoqueDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecebimentoDao.class, RecebimentoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PagamentoDao.class, PagamentoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EventoDao.class, EventoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LoteDao.class, LoteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PerdaDao.class, PerdaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PrecoQueimaDao.class, PrecoQueimaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(EncomendaDao.class, EncomendaDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public GrupoDao grupoDao() {
    if (_grupoDao != null) {
      return _grupoDao;
    } else {
      synchronized(this) {
        if(_grupoDao == null) {
          _grupoDao = new GrupoDao_Impl(this);
        }
        return _grupoDao;
      }
    }
  }

  @Override
  public ProdutoDao produtoDao() {
    if (_produtoDao != null) {
      return _produtoDao;
    } else {
      synchronized(this) {
        if(_produtoDao == null) {
          _produtoDao = new ProdutoDao_Impl(this);
        }
        return _produtoDao;
      }
    }
  }

  @Override
  public ClienteDao clienteDao() {
    if (_clienteDao != null) {
      return _clienteDao;
    } else {
      synchronized(this) {
        if(_clienteDao == null) {
          _clienteDao = new ClienteDao_Impl(this);
        }
        return _clienteDao;
      }
    }
  }

  @Override
  public FornecedorDao fornecedorDao() {
    if (_fornecedorDao != null) {
      return _fornecedorDao;
    } else {
      synchronized(this) {
        if(_fornecedorDao == null) {
          _fornecedorDao = new FornecedorDao_Impl(this);
        }
        return _fornecedorDao;
      }
    }
  }

  @Override
  public VendaDao vendaDao() {
    if (_vendaDao != null) {
      return _vendaDao;
    } else {
      synchronized(this) {
        if(_vendaDao == null) {
          _vendaDao = new VendaDao_Impl(this);
        }
        return _vendaDao;
      }
    }
  }

  @Override
  public CompraDao compraDao() {
    if (_compraDao != null) {
      return _compraDao;
    } else {
      synchronized(this) {
        if(_compraDao == null) {
          _compraDao = new CompraDao_Impl(this);
        }
        return _compraDao;
      }
    }
  }

  @Override
  public EstoqueDao estoqueDao() {
    if (_estoqueDao != null) {
      return _estoqueDao;
    } else {
      synchronized(this) {
        if(_estoqueDao == null) {
          _estoqueDao = new EstoqueDao_Impl(this);
        }
        return _estoqueDao;
      }
    }
  }

  @Override
  public RecebimentoDao recebimentoDao() {
    if (_recebimentoDao != null) {
      return _recebimentoDao;
    } else {
      synchronized(this) {
        if(_recebimentoDao == null) {
          _recebimentoDao = new RecebimentoDao_Impl(this);
        }
        return _recebimentoDao;
      }
    }
  }

  @Override
  public PagamentoDao pagamentoDao() {
    if (_pagamentoDao != null) {
      return _pagamentoDao;
    } else {
      synchronized(this) {
        if(_pagamentoDao == null) {
          _pagamentoDao = new PagamentoDao_Impl(this);
        }
        return _pagamentoDao;
      }
    }
  }

  @Override
  public EventoDao eventoDao() {
    if (_eventoDao != null) {
      return _eventoDao;
    } else {
      synchronized(this) {
        if(_eventoDao == null) {
          _eventoDao = new EventoDao_Impl(this);
        }
        return _eventoDao;
      }
    }
  }

  @Override
  public LoteDao loteDao() {
    if (_loteDao != null) {
      return _loteDao;
    } else {
      synchronized(this) {
        if(_loteDao == null) {
          _loteDao = new LoteDao_Impl(this);
        }
        return _loteDao;
      }
    }
  }

  @Override
  public PerdaDao perdaDao() {
    if (_perdaDao != null) {
      return _perdaDao;
    } else {
      synchronized(this) {
        if(_perdaDao == null) {
          _perdaDao = new PerdaDao_Impl(this);
        }
        return _perdaDao;
      }
    }
  }

  @Override
  public PrecoQueimaDao precoQueimaDao() {
    if (_precoQueimaDao != null) {
      return _precoQueimaDao;
    } else {
      synchronized(this) {
        if(_precoQueimaDao == null) {
          _precoQueimaDao = new PrecoQueimaDao_Impl(this);
        }
        return _precoQueimaDao;
      }
    }
  }

  @Override
  public EncomendaDao encomendaDao() {
    if (_encomendaDao != null) {
      return _encomendaDao;
    } else {
      synchronized(this) {
        if(_encomendaDao == null) {
          _encomendaDao = new EncomendaDao_Impl(this);
        }
        return _encomendaDao;
      }
    }
  }
}
