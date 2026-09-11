package br.com.banavita.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import br.com.banavita.app.data.local.converter.UuidConverters;
import br.com.banavita.app.data.local.entity.VendaEntity;
import br.com.banavita.app.data.local.entity.VendaItemEntity;
import br.com.banavita.app.data.local.relation.VendaWithItems;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VendaDao_Impl implements VendaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VendaEntity> __insertionAdapterOfVendaEntity;

  private final UuidConverters __uuidConverters = new UuidConverters();

  private final EntityInsertionAdapter<VendaItemEntity> __insertionAdapterOfVendaItemEntity;

  private final EntityDeletionOrUpdateAdapter<VendaEntity> __updateAdapterOfVendaEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItensByVenda;

  private final SharedSQLiteStatement __preparedStmtOfDeleteVenda;

  public VendaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVendaEntity = new EntityInsertionAdapter<VendaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `venda` (`VEN_ID`,`created_at`,`updated_at`,`VEN_DATA`,`VEN_CLIENTE`,`VEN_VLRTOT`,`VEN_PRAZO`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VendaEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        statement.bindLong(4, entity.getData());
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getClienteId());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        statement.bindLong(6, entity.getValorTotalCentavos());
        final int _tmp_2 = entity.getAPrazo() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
      }
    };
    this.__insertionAdapterOfVendaItemEntity = new EntityInsertionAdapter<VendaItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `venda_item` (`VEI_ID`,`created_at`,`updated_at`,`VEI_VENDA`,`VEI_PRODUTO`,`VEI_VLRUN`,`VEI_QTDE`,`VEI_VLRTOT`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VendaItemEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getVendaId());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_1);
        }
        final String _tmp_2 = __uuidConverters.fromUuid(entity.getProdutoId());
        if (_tmp_2 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_2);
        }
        statement.bindLong(6, entity.getValorUnitarioCentavos());
        statement.bindDouble(7, entity.getQuantidade());
        statement.bindLong(8, entity.getValorTotalCentavos());
      }
    };
    this.__updateAdapterOfVendaEntity = new EntityDeletionOrUpdateAdapter<VendaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `venda` SET `VEN_ID` = ?,`created_at` = ?,`updated_at` = ?,`VEN_DATA` = ?,`VEN_CLIENTE` = ?,`VEN_VLRTOT` = ?,`VEN_PRAZO` = ? WHERE `VEN_ID` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VendaEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        statement.bindLong(4, entity.getData());
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getClienteId());
        if (_tmp_1 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_1);
        }
        statement.bindLong(6, entity.getValorTotalCentavos());
        final int _tmp_2 = entity.getAPrazo() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        final String _tmp_3 = __uuidConverters.fromUuid(entity.getId());
        if (_tmp_3 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_3);
        }
      }
    };
    this.__preparedStmtOfDeleteItensByVenda = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM venda_item WHERE VEI_VENDA = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteVenda = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM venda WHERE VEN_ID = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertVenda(final VendaEntity venda, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVendaEntity.insert(venda);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItens(final List<VendaItemEntity> itens,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVendaItemEntity.insert(itens);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateVenda(final VendaEntity venda, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVendaEntity.handle(venda);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object replaceItens(final UUID vendaId, final List<VendaItemEntity> itens,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> VendaDao.DefaultImpls.replaceItens(VendaDao_Impl.this, vendaId, itens, __cont), $completion);
  }

  @Override
  public Object insertVendaComItens(final VendaEntity venda, final List<VendaItemEntity> itens,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> VendaDao.DefaultImpls.insertVendaComItens(VendaDao_Impl.this, venda, itens, __cont), $completion);
  }

  @Override
  public Object updateVendaComItens(final VendaEntity venda, final List<VendaItemEntity> itens,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> VendaDao.DefaultImpls.updateVendaComItens(VendaDao_Impl.this, venda, itens, __cont), $completion);
  }

  @Override
  public Object deleteVendaComItens(final UUID id, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> VendaDao.DefaultImpls.deleteVendaComItens(VendaDao_Impl.this, id, __cont), $completion);
  }

  @Override
  public Object deleteItensByVenda(final UUID vendaId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteItensByVenda.acquire();
        int _argIndex = 1;
        final String _tmp = __uuidConverters.fromUuid(vendaId);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteItensByVenda.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteVenda(final UUID id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteVenda.acquire();
        int _argIndex = 1;
        final String _tmp = __uuidConverters.fromUuid(id);
        if (_tmp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, _tmp);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteVenda.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VendaEntity>> observeAll() {
    final String _sql = "SELECT * FROM venda ORDER BY VEN_DATA DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"venda"}, new Callable<List<VendaEntity>>() {
      @Override
      @NonNull
      public List<VendaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_DATA");
          final int _cursorIndexOfClienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_CLIENTE");
          final int _cursorIndexOfValorTotalCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_VLRTOT");
          final int _cursorIndexOfAPrazo = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_PRAZO");
          final List<VendaEntity> _result = new ArrayList<VendaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VendaEntity _item;
            final UUID _tmpId;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfId);
            }
            final UUID _tmp_1 = __uuidConverters.toUuid(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpId = _tmp_1;
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final long _tmpData;
            _tmpData = _cursor.getLong(_cursorIndexOfData);
            final UUID _tmpClienteId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfClienteId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfClienteId);
            }
            _tmpClienteId = __uuidConverters.toUuid(_tmp_2);
            final long _tmpValorTotalCentavos;
            _tmpValorTotalCentavos = _cursor.getLong(_cursorIndexOfValorTotalCentavos);
            final boolean _tmpAPrazo;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfAPrazo);
            _tmpAPrazo = _tmp_3 != 0;
            _item = new VendaEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpData,_tmpClienteId,_tmpValorTotalCentavos,_tmpAPrazo);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getWithItems(final UUID id,
      final Continuation<? super VendaWithItems> $completion) {
    final String _sql = "SELECT * FROM venda WHERE VEN_ID = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<VendaWithItems>() {
      @Override
      @Nullable
      public VendaWithItems call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_ID");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_DATA");
            final int _cursorIndexOfClienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_CLIENTE");
            final int _cursorIndexOfValorTotalCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_VLRTOT");
            final int _cursorIndexOfAPrazo = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_PRAZO");
            final ArrayMap<String, ArrayList<VendaItemEntity>> _collectionItens = new ArrayMap<String, ArrayList<VendaItemEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfId);
              if (!_collectionItens.containsKey(_tmpKey)) {
                _collectionItens.put(_tmpKey, new ArrayList<VendaItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipvendaItemAsbrComBanavitaAppDataLocalEntityVendaItemEntity(_collectionItens);
            final VendaWithItems _result;
            if (_cursor.moveToFirst()) {
              final VendaEntity _tmpVenda;
              final UUID _tmpId;
              final String _tmp_1;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmp_1 = null;
              } else {
                _tmp_1 = _cursor.getString(_cursorIndexOfId);
              }
              final UUID _tmp_2 = __uuidConverters.toUuid(_tmp_1);
              if (_tmp_2 == null) {
                throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
              } else {
                _tmpId = _tmp_2;
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              final long _tmpData;
              _tmpData = _cursor.getLong(_cursorIndexOfData);
              final UUID _tmpClienteId;
              final String _tmp_3;
              if (_cursor.isNull(_cursorIndexOfClienteId)) {
                _tmp_3 = null;
              } else {
                _tmp_3 = _cursor.getString(_cursorIndexOfClienteId);
              }
              _tmpClienteId = __uuidConverters.toUuid(_tmp_3);
              final long _tmpValorTotalCentavos;
              _tmpValorTotalCentavos = _cursor.getLong(_cursorIndexOfValorTotalCentavos);
              final boolean _tmpAPrazo;
              final int _tmp_4;
              _tmp_4 = _cursor.getInt(_cursorIndexOfAPrazo);
              _tmpAPrazo = _tmp_4 != 0;
              _tmpVenda = new VendaEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpData,_tmpClienteId,_tmpValorTotalCentavos,_tmpAPrazo);
              final ArrayList<VendaItemEntity> _tmpItensCollection;
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              _tmpItensCollection = _collectionItens.get(_tmpKey_1);
              _result = new VendaWithItems(_tmpVenda,_tmpItensCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VendaWithItems>> observeAllWithItems() {
    final String _sql = "SELECT * FROM venda ORDER BY VEN_DATA DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"venda_item",
        "venda"}, new Callable<List<VendaWithItems>>() {
      @Override
      @NonNull
      public List<VendaWithItems> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_ID");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_DATA");
            final int _cursorIndexOfClienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_CLIENTE");
            final int _cursorIndexOfValorTotalCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_VLRTOT");
            final int _cursorIndexOfAPrazo = CursorUtil.getColumnIndexOrThrow(_cursor, "VEN_PRAZO");
            final ArrayMap<String, ArrayList<VendaItemEntity>> _collectionItens = new ArrayMap<String, ArrayList<VendaItemEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfId);
              if (!_collectionItens.containsKey(_tmpKey)) {
                _collectionItens.put(_tmpKey, new ArrayList<VendaItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipvendaItemAsbrComBanavitaAppDataLocalEntityVendaItemEntity(_collectionItens);
            final List<VendaWithItems> _result = new ArrayList<VendaWithItems>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final VendaWithItems _item;
              final VendaEntity _tmpVenda;
              final UUID _tmpId;
              final String _tmp;
              if (_cursor.isNull(_cursorIndexOfId)) {
                _tmp = null;
              } else {
                _tmp = _cursor.getString(_cursorIndexOfId);
              }
              final UUID _tmp_1 = __uuidConverters.toUuid(_tmp);
              if (_tmp_1 == null) {
                throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
              } else {
                _tmpId = _tmp_1;
              }
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final long _tmpUpdatedAt;
              _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
              final long _tmpData;
              _tmpData = _cursor.getLong(_cursorIndexOfData);
              final UUID _tmpClienteId;
              final String _tmp_2;
              if (_cursor.isNull(_cursorIndexOfClienteId)) {
                _tmp_2 = null;
              } else {
                _tmp_2 = _cursor.getString(_cursorIndexOfClienteId);
              }
              _tmpClienteId = __uuidConverters.toUuid(_tmp_2);
              final long _tmpValorTotalCentavos;
              _tmpValorTotalCentavos = _cursor.getLong(_cursorIndexOfValorTotalCentavos);
              final boolean _tmpAPrazo;
              final int _tmp_3;
              _tmp_3 = _cursor.getInt(_cursorIndexOfAPrazo);
              _tmpAPrazo = _tmp_3 != 0;
              _tmpVenda = new VendaEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpData,_tmpClienteId,_tmpValorTotalCentavos,_tmpAPrazo);
              final ArrayList<VendaItemEntity> _tmpItensCollection;
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              _tmpItensCollection = _collectionItens.get(_tmpKey_1);
              _item = new VendaWithItems(_tmpVenda,_tmpItensCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipvendaItemAsbrComBanavitaAppDataLocalEntityVendaItemEntity(
      @NonNull final ArrayMap<String, ArrayList<VendaItemEntity>> _map) {
    final Set<String> __mapKeySet = _map.keySet();
    if (__mapKeySet.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchArrayMap(_map, true, (map) -> {
        __fetchRelationshipvendaItemAsbrComBanavitaAppDataLocalEntityVendaItemEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `VEI_ID`,`created_at`,`updated_at`,`VEI_VENDA`,`VEI_PRODUTO`,`VEI_VLRUN`,`VEI_QTDE`,`VEI_VLRTOT` FROM `venda_item` WHERE `VEI_VENDA` IN (");
    final int _inputSize = __mapKeySet.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : __mapKeySet) {
      _stmt.bindString(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "VEI_VENDA");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfCreatedAt = 1;
      final int _cursorIndexOfUpdatedAt = 2;
      final int _cursorIndexOfVendaId = 3;
      final int _cursorIndexOfProdutoId = 4;
      final int _cursorIndexOfValorUnitarioCentavos = 5;
      final int _cursorIndexOfQuantidade = 6;
      final int _cursorIndexOfValorTotalCentavos = 7;
      while (_cursor.moveToNext()) {
        final String _tmpKey;
        _tmpKey = _cursor.getString(_itemKeyIndex);
        final ArrayList<VendaItemEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final VendaItemEntity _item_1;
          final UUID _tmpId;
          final String _tmp;
          if (_cursor.isNull(_cursorIndexOfId)) {
            _tmp = null;
          } else {
            _tmp = _cursor.getString(_cursorIndexOfId);
          }
          final UUID _tmp_1 = __uuidConverters.toUuid(_tmp);
          if (_tmp_1 == null) {
            throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
          } else {
            _tmpId = _tmp_1;
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
          final long _tmpUpdatedAt;
          _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
          final UUID _tmpVendaId;
          final String _tmp_2;
          if (_cursor.isNull(_cursorIndexOfVendaId)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _cursor.getString(_cursorIndexOfVendaId);
          }
          final UUID _tmp_3 = __uuidConverters.toUuid(_tmp_2);
          if (_tmp_3 == null) {
            throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
          } else {
            _tmpVendaId = _tmp_3;
          }
          final UUID _tmpProdutoId;
          final String _tmp_4;
          if (_cursor.isNull(_cursorIndexOfProdutoId)) {
            _tmp_4 = null;
          } else {
            _tmp_4 = _cursor.getString(_cursorIndexOfProdutoId);
          }
          final UUID _tmp_5 = __uuidConverters.toUuid(_tmp_4);
          if (_tmp_5 == null) {
            throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
          } else {
            _tmpProdutoId = _tmp_5;
          }
          final long _tmpValorUnitarioCentavos;
          _tmpValorUnitarioCentavos = _cursor.getLong(_cursorIndexOfValorUnitarioCentavos);
          final double _tmpQuantidade;
          _tmpQuantidade = _cursor.getDouble(_cursorIndexOfQuantidade);
          final long _tmpValorTotalCentavos;
          _tmpValorTotalCentavos = _cursor.getLong(_cursorIndexOfValorTotalCentavos);
          _item_1 = new VendaItemEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpVendaId,_tmpProdutoId,_tmpValorUnitarioCentavos,_tmpQuantidade,_tmpValorTotalCentavos);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
