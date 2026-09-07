package br.com.banavita.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import br.com.banavita.app.data.local.converter.UuidConverters;
import br.com.banavita.app.data.local.entity.PagamentoEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PagamentoDao_Impl implements PagamentoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PagamentoEntity> __insertionAdapterOfPagamentoEntity;

  private final UuidConverters __uuidConverters = new UuidConverters();

  public PagamentoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPagamentoEntity = new EntityInsertionAdapter<PagamentoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `pagamento` (`PAG_ID`,`created_at`,`updated_at`,`PAG_COMPRA`,`PAG_DATA`,`PAG_VALOR`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PagamentoEntity entity) {
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
        statement.bindLong(5, entity.getData());
        statement.bindLong(6, entity.getValorCentavos());
      }
    };
  }

  @Override
  public Object insert(final PagamentoEntity pagamento,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPagamentoEntity.insert(pagamento);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PagamentoEntity>> observeAll() {
    final String _sql = "SELECT * FROM pagamento ORDER BY PAG_DATA DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pagamento"}, new Callable<List<PagamentoEntity>>() {
      @Override
      @NonNull
      public List<PagamentoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfCompraId = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_COMPRA");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_DATA");
          final int _cursorIndexOfValorCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_VALOR");
          final List<PagamentoEntity> _result = new ArrayList<PagamentoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PagamentoEntity _item;
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
            final long _tmpData;
            _tmpData = _cursor.getLong(_cursorIndexOfData);
            final long _tmpValorCentavos;
            _tmpValorCentavos = _cursor.getLong(_cursorIndexOfValorCentavos);
            _item = new PagamentoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpCompraId,_tmpData,_tmpValorCentavos);
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
  public Flow<List<PagamentoEntity>> observeByCompra(final UUID compraId) {
    final String _sql = "SELECT * FROM pagamento WHERE PAG_COMPRA = ? ORDER BY PAG_DATA ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(compraId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pagamento"}, new Callable<List<PagamentoEntity>>() {
      @Override
      @NonNull
      public List<PagamentoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfCompraId = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_COMPRA");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_DATA");
          final int _cursorIndexOfValorCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_VALOR");
          final List<PagamentoEntity> _result = new ArrayList<PagamentoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PagamentoEntity _item;
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
            final UUID _tmpCompraId;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCompraId)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfCompraId);
            }
            final UUID _tmp_4 = __uuidConverters.toUuid(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpCompraId = _tmp_4;
            }
            final long _tmpData;
            _tmpData = _cursor.getLong(_cursorIndexOfData);
            final long _tmpValorCentavos;
            _tmpValorCentavos = _cursor.getLong(_cursorIndexOfValorCentavos);
            _item = new PagamentoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpCompraId,_tmpData,_tmpValorCentavos);
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
  public Object getByCompra(final UUID compraId,
      final Continuation<? super List<PagamentoEntity>> $completion) {
    final String _sql = "SELECT * FROM pagamento WHERE PAG_COMPRA = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(compraId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PagamentoEntity>>() {
      @Override
      @NonNull
      public List<PagamentoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfCompraId = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_COMPRA");
          final int _cursorIndexOfData = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_DATA");
          final int _cursorIndexOfValorCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "PAG_VALOR");
          final List<PagamentoEntity> _result = new ArrayList<PagamentoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PagamentoEntity _item;
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
            final UUID _tmpCompraId;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfCompraId)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfCompraId);
            }
            final UUID _tmp_4 = __uuidConverters.toUuid(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpCompraId = _tmp_4;
            }
            final long _tmpData;
            _tmpData = _cursor.getLong(_cursorIndexOfData);
            final long _tmpValorCentavos;
            _tmpValorCentavos = _cursor.getLong(_cursorIndexOfValorCentavos);
            _item = new PagamentoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpCompraId,_tmpData,_tmpValorCentavos);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object totalByCompra(final UUID compraId, final Continuation<? super Long> $completion) {
    final String _sql = "SELECT COALESCE(SUM(PAG_VALOR), 0) FROM pagamento WHERE PAG_COMPRA = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(compraId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp_1;
            _tmp_1 = _cursor.getLong(0);
            _result = _tmp_1;
          } else {
            _result = 0L;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
