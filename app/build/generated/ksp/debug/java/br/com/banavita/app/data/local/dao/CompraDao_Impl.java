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
import br.com.banavita.app.data.local.entity.CompraEntity;
import br.com.banavita.app.data.local.entity.CompraItemEntity;
import br.com.banavita.app.data.local.relation.CompraWithItems;
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
public final class CompraDao_Impl implements CompraDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CompraEntity> __insertionAdapterOfCompraEntity;

  private final UuidConverters __uuidConverters = new UuidConverters();

  private final EntityInsertionAdapter<CompraItemEntity> __insertionAdapterOfCompraItemEntity;

  private final EntityDeletionOrUpdateAdapter<CompraEntity> __updateAdapterOfCompraEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItensByCompra;

  private final SharedSQLiteStatement __preparedStmtOfDeleteCompra;

  public CompraDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCompraEntity = new EntityInsertionAdapter<CompraEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `compra` (`COM_ID`,`created_at`,`updated_at`,`COM_DATA`,`COM_FORNECEDOR`,`COM_VLRTOT`,`COM_PRAZO`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CompraEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        statement.bindLong(4, entity.getData());
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getFornecedorId());
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
    this.__insertionAdapterOfCompraItemEntity = new EntityInsertionAdapter<CompraItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `compra_item` (`COI_ID`,`created_at`,`updated_at`,`COI_COMPRA`,`COI_PRODUTO`,`COI_VLRUN`,`COI_QTDE`,`COI_VLRTOT`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CompraItemEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getCompraId());
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
    this.__updateAdapterOfCompraEntity = new EntityDeletionOrUpdateAdapter<CompraEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `compra` SET `COM_ID` = ?,`created_at` = ?,`updated_at` = ?,`COM_DATA` = ?,`COM_FORNECEDOR` = ?,`COM_VLRTOT` = ?,`COM_PRAZO` = ? WHERE `COM_ID` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CompraEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        statement.bindLong(4, entity.getData());
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getFornecedorId());
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
    this.__preparedStmtOfDeleteItensByCompra = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM compra_item WHERE COI_COMPRA = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteCompra = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM compra WHERE COM_ID = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertCompra(final CompraEntity compra,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCompraEntity.insert(compra);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItens(final List<CompraItemEntity> itens,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCompraItemEntity.insert(itens);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCompra(final CompraEntity compra,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCompraEntity.handle(compra);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object replaceItens(final UUID compraId, final List<CompraItemEntity> itens,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> CompraDao.DefaultImpls.replaceItens(CompraDao_Impl.this, compraId, itens, __cont), $completion);
  }

  @Override
  public Object insertCompraComItens(final CompraEntity compra, final List<CompraItemEntity> itens,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> CompraDao.DefaultImpls.insertCompraComItens(CompraDao_Impl.this, compra, itens, __cont), $completion);
  }

  @Override
  public Object updateCompraComItens(final CompraEntity compra, final List<CompraItemEntity> itens,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> CompraDao.DefaultImpls.updateCompraComItens(CompraDao_Impl.this, compra, itens, __cont), $completion);
  }

  @Override
  public Object deleteCompraComItens(final UUID id, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> CompraDao.DefaultImpls.deleteCompraComItens(CompraDao_Impl.this, id, __cont), $completion);
  }

  @Override
  public Object deleteItensByCompra(final UUID compraId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteItensByCompra.acquire();
        int _argIndex = 1;
        final String _tmp = __uuidConverters.fromUuid(compraId);
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
          __preparedStmtOfDeleteItensByCompra.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCompra(final UUID id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteCompra.acquire();
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
          __preparedStmtOfDeleteCompra.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CompraEntity>> observeAll() {
    final String _sql = "SELECT * FROM compra ORDER BY COM_DATA DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"compra"}, new Callable<List<CompraEntity>>() {
      @Override
      @NonNull
      public List<CompraEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_DATA");
          final int _cursorIndexOfFornecedorId = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_FORNECEDOR");
          final int _cursorIndexOfValorTotalCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_VLRTOT");
          final int _cursorIndexOfAPrazo = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_PRAZO");
          final List<CompraEntity> _result = new ArrayList<CompraEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CompraEntity _item;
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
            final UUID _tmpFornecedorId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfFornecedorId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfFornecedorId);
            }
            _tmpFornecedorId = __uuidConverters.toUuid(_tmp_2);
            final long _tmpValorTotalCentavos;
            _tmpValorTotalCentavos = _cursor.getLong(_cursorIndexOfValorTotalCentavos);
            final boolean _tmpAPrazo;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfAPrazo);
            _tmpAPrazo = _tmp_3 != 0;
            _item = new CompraEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpData,_tmpFornecedorId,_tmpValorTotalCentavos,_tmpAPrazo);
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
      final Continuation<? super CompraWithItems> $completion) {
    final String _sql = "SELECT * FROM compra WHERE COM_ID = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<CompraWithItems>() {
      @Override
      @Nullable
      public CompraWithItems call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_ID");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_DATA");
            final int _cursorIndexOfFornecedorId = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_FORNECEDOR");
            final int _cursorIndexOfValorTotalCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_VLRTOT");
            final int _cursorIndexOfAPrazo = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_PRAZO");
            final ArrayMap<String, ArrayList<CompraItemEntity>> _collectionItens = new ArrayMap<String, ArrayList<CompraItemEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfId);
              if (!_collectionItens.containsKey(_tmpKey)) {
                _collectionItens.put(_tmpKey, new ArrayList<CompraItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcompraItemAsbrComBanavitaAppDataLocalEntityCompraItemEntity(_collectionItens);
            final CompraWithItems _result;
            if (_cursor.moveToFirst()) {
              final CompraEntity _tmpCompra;
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
              final UUID _tmpFornecedorId;
              final String _tmp_3;
              if (_cursor.isNull(_cursorIndexOfFornecedorId)) {
                _tmp_3 = null;
              } else {
                _tmp_3 = _cursor.getString(_cursorIndexOfFornecedorId);
              }
              _tmpFornecedorId = __uuidConverters.toUuid(_tmp_3);
              final long _tmpValorTotalCentavos;
              _tmpValorTotalCentavos = _cursor.getLong(_cursorIndexOfValorTotalCentavos);
              final boolean _tmpAPrazo;
              final int _tmp_4;
              _tmp_4 = _cursor.getInt(_cursorIndexOfAPrazo);
              _tmpAPrazo = _tmp_4 != 0;
              _tmpCompra = new CompraEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpData,_tmpFornecedorId,_tmpValorTotalCentavos,_tmpAPrazo);
              final ArrayList<CompraItemEntity> _tmpItensCollection;
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              _tmpItensCollection = _collectionItens.get(_tmpKey_1);
              _result = new CompraWithItems(_tmpCompra,_tmpItensCollection);
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
  public Flow<List<CompraWithItems>> observeAllWithItems() {
    final String _sql = "SELECT * FROM compra ORDER BY COM_DATA DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"compra_item",
        "compra"}, new Callable<List<CompraWithItems>>() {
      @Override
      @NonNull
      public List<CompraWithItems> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_ID");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
            final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
            final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_DATA");
            final int _cursorIndexOfFornecedorId = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_FORNECEDOR");
            final int _cursorIndexOfValorTotalCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_VLRTOT");
            final int _cursorIndexOfAPrazo = CursorUtil.getColumnIndexOrThrow(_cursor, "COM_PRAZO");
            final ArrayMap<String, ArrayList<CompraItemEntity>> _collectionItens = new ArrayMap<String, ArrayList<CompraItemEntity>>();
            while (_cursor.moveToNext()) {
              final String _tmpKey;
              _tmpKey = _cursor.getString(_cursorIndexOfId);
              if (!_collectionItens.containsKey(_tmpKey)) {
                _collectionItens.put(_tmpKey, new ArrayList<CompraItemEntity>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcompraItemAsbrComBanavitaAppDataLocalEntityCompraItemEntity(_collectionItens);
            final List<CompraWithItems> _result = new ArrayList<CompraWithItems>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final CompraWithItems _item;
              final CompraEntity _tmpCompra;
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
              final UUID _tmpFornecedorId;
              final String _tmp_2;
              if (_cursor.isNull(_cursorIndexOfFornecedorId)) {
                _tmp_2 = null;
              } else {
                _tmp_2 = _cursor.getString(_cursorIndexOfFornecedorId);
              }
              _tmpFornecedorId = __uuidConverters.toUuid(_tmp_2);
              final long _tmpValorTotalCentavos;
              _tmpValorTotalCentavos = _cursor.getLong(_cursorIndexOfValorTotalCentavos);
              final boolean _tmpAPrazo;
              final int _tmp_3;
              _tmp_3 = _cursor.getInt(_cursorIndexOfAPrazo);
              _tmpAPrazo = _tmp_3 != 0;
              _tmpCompra = new CompraEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpData,_tmpFornecedorId,_tmpValorTotalCentavos,_tmpAPrazo);
              final ArrayList<CompraItemEntity> _tmpItensCollection;
              final String _tmpKey_1;
              _tmpKey_1 = _cursor.getString(_cursorIndexOfId);
              _tmpItensCollection = _collectionItens.get(_tmpKey_1);
              _item = new CompraWithItems(_tmpCompra,_tmpItensCollection);
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

  private void __fetchRelationshipcompraItemAsbrComBanavitaAppDataLocalEntityCompraItemEntity(
      @NonNull final ArrayMap<String, ArrayList<CompraItemEntity>> _map) {
    final Set<String> __mapKeySet = _map.keySet();
    if (__mapKeySet.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchArrayMap(_map, true, (map) -> {
        __fetchRelationshipcompraItemAsbrComBanavitaAppDataLocalEntityCompraItemEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `COI_ID`,`created_at`,`updated_at`,`COI_COMPRA`,`COI_PRODUTO`,`COI_VLRUN`,`COI_QTDE`,`COI_VLRTOT` FROM `compra_item` WHERE `COI_COMPRA` IN (");
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
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "COI_COMPRA");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfCreatedAt = 1;
      final int _cursorIndexOfUpdatedAt = 2;
      final int _cursorIndexOfCompraId = 3;
      final int _cursorIndexOfProdutoId = 4;
      final int _cursorIndexOfValorUnitarioCentavos = 5;
      final int _cursorIndexOfQuantidade = 6;
      final int _cursorIndexOfValorTotalCentavos = 7;
      while (_cursor.moveToNext()) {
        final String _tmpKey;
        _tmpKey = _cursor.getString(_itemKeyIndex);
        final ArrayList<CompraItemEntity> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final CompraItemEntity _item_1;
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
          final UUID _tmpCompraId;
          final String _tmp_2;
          if (_cursor.isNull(_cursorIndexOfCompraId)) {
            _tmp_2 = null;
          } else {
            _tmp_2 = _cursor.getString(_cursorIndexOfCompraId);
          }
          final UUID _tmp_3 = __uuidConverters.toUuid(_tmp_2);
          if (_tmp_3 == null) {
            throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
          } else {
            _tmpCompraId = _tmp_3;
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
          _item_1 = new CompraItemEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpCompraId,_tmpProdutoId,_tmpValorUnitarioCentavos,_tmpQuantidade,_tmpValorTotalCentavos);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
