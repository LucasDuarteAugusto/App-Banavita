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
import br.com.banavita.app.data.local.dao.EstoqueDao;
import br.com.banavita.app.data.local.dao.EstoqueDao_Impl;
import br.com.banavita.app.data.local.dao.EventoDao;
import br.com.banavita.app.data.local.dao.EventoDao_Impl;
import br.com.banavita.app.data.local.dao.FornecedorDao;
import br.com.banavita.app.data.local.dao.FornecedorDao_Impl;
import br.com.banavita.app.data.local.dao.GrupoDao;
import br.com.banavita.app.data.local.dao.GrupoDao_Impl;
import br.com.banavita.app.data.local.dao.PagamentoDao;
import br.com.banavita.app.data.local.dao.PagamentoDao_Impl;
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

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `grupo` (`GRU_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `GRU_NOME` TEXT NOT NULL, `GRU_OBS` TEXT, PRIMARY KEY(`GRU_ID`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `produto` (`PRO_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `PRO_DESCRICAO` TEXT NOT NULL, `PRO_PRECO` INTEGER NOT NULL, `PRO_GRUPO` TEXT, `PRO_UNIDADE` TEXT NOT NULL DEFAULT 'UN', PRIMARY KEY(`PRO_ID`), FOREIGN KEY(`PRO_GRUPO`) REFERENCES `grupo`(`GRU_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_produto_PRO_GRUPO` ON `produto` (`PRO_GRUPO`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `cliente` (`CLI_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `CLI_NOME` TEXT NOT NULL, `CLI_TELEFONE` TEXT, PRIMARY KEY(`CLI_ID`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `fornecedor` (`FOR_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `FOR_NOME` TEXT NOT NULL, `FOR_TELEFONE` TEXT, PRIMARY KEY(`FOR_ID`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `venda` (`VEN_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `VEN_DATA` INTEGER NOT NULL, `VEN_CLIENTE` TEXT, `VEN_VLRTOT` INTEGER NOT NULL, `VEN_PRAZO` INTEGER NOT NULL, PRIMARY KEY(`VEN_ID`), FOREIGN KEY(`VEN_CLIENTE`) REFERENCES `cliente`(`CLI_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_venda_VEN_DATA` ON `venda` (`VEN_DATA`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_venda_VEN_CLIENTE` ON `venda` (`VEN_CLIENTE`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `venda_item` (`VEI_ID` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `VEI_VENDA` TEXT NOT NULL, `VEI_PRODUTO` TEXT NOT NULL, `VEI_VLRUN` INTEGER NOT NULL, `VEI_QTDE` REAL NOT NULL, `VEI_VLRTOT` INTEGER NOT NULL, PRIMARY KEY(`VEI_ID`), FOREIGN KEY(`VEI_VENDA`) REFERENCES `venda`(`VEN_ID`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`VEI_PRODUTO`) REFERENCES `produto`(`PRO_ID`) ON UPDATE NO ACTION ON DELETE RESTRICT )");
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
        db.execSQL("CREATE VIEW `vw_produto_estoque` AS SELECT\n"
                + "            p.PRO_ID AS produto_id,\n"
                + "            p.PRO_UNIDADE AS unidade,\n"
                + "            COALESCE(c.qtde, 0) - COALESCE(v.qtde, 0) AS saldo\n"
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
                + "        ) v ON v.id = p.PRO_ID");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '7529cecac09bd81483b7f7ac5348445b')");
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
        db.execSQL("DROP VIEW IF EXISTS `vw_produto_estoque`");
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
        final HashMap<String, TableInfo.Column> _columnsProduto = new HashMap<String, TableInfo.Column>(7);
        _columnsProduto.put("PRO_ID", new TableInfo.Column("PRO_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_DESCRICAO", new TableInfo.Column("PRO_DESCRICAO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_PRECO", new TableInfo.Column("PRO_PRECO", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_GRUPO", new TableInfo.Column("PRO_GRUPO", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProduto.put("PRO_UNIDADE", new TableInfo.Column("PRO_UNIDADE", "TEXT", true, 0, "'UN'", TableInfo.CREATED_FROM_ENTITY));
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
        final HashMap<String, TableInfo.Column> _columnsCliente = new HashMap<String, TableInfo.Column>(5);
        _columnsCliente.put("CLI_ID", new TableInfo.Column("CLI_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCliente.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCliente.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCliente.put("CLI_NOME", new TableInfo.Column("CLI_NOME", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCliente.put("CLI_TELEFONE", new TableInfo.Column("CLI_TELEFONE", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
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
        final HashMap<String, TableInfo.Column> _columnsVendaItem = new HashMap<String, TableInfo.Column>(8);
        _columnsVendaItem.put("VEI_ID", new TableInfo.Column("VEI_ID", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_VENDA", new TableInfo.Column("VEI_VENDA", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_PRODUTO", new TableInfo.Column("VEI_PRODUTO", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_VLRUN", new TableInfo.Column("VEI_VLRUN", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_QTDE", new TableInfo.Column("VEI_QTDE", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVendaItem.put("VEI_VLRTOT", new TableInfo.Column("VEI_VLRTOT", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
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
        final ViewInfo _infoVwProdutoEstoque = new ViewInfo("vw_produto_estoque", "CREATE VIEW `vw_produto_estoque` AS SELECT\n"
                + "            p.PRO_ID AS produto_id,\n"
                + "            p.PRO_UNIDADE AS unidade,\n"
                + "            COALESCE(c.qtde, 0) - COALESCE(v.qtde, 0) AS saldo\n"
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
                + "        ) v ON v.id = p.PRO_ID");
        final ViewInfo _existingVwProdutoEstoque = ViewInfo.read(db, "vw_produto_estoque");
        if (!_infoVwProdutoEstoque.equals(_existingVwProdutoEstoque)) {
          return new RoomOpenHelper.ValidationResult(false, "vw_produto_estoque(br.com.banavita.app.data.local.view.ProdutoEstoqueView).\n"
                  + " Expected:\n" + _infoVwProdutoEstoque + "\n"
                  + " Found:\n" + _existingVwProdutoEstoque);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "7529cecac09bd81483b7f7ac5348445b", "0ba2e31b13a10f235d108ced821ea9eb");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(1);
    final HashSet<String> _tables = new HashSet<String>(3);
    _tables.add("produto");
    _tables.add("compra_item");
    _tables.add("venda_item");
    _viewTables.put("vw_produto_estoque", _tables);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "grupo","produto","cliente","fornecedor","venda","venda_item","compra","compra_item","recebimento","pagamento","evento");
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
}
