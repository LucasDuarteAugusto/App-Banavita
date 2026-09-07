package br.com.banavita.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import br.com.banavita.app.data.local.converter.UuidConverters;
import br.com.banavita.app.data.local.entity.ProdutoEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Integer;
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
public final class ProdutoDao_Impl implements ProdutoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProdutoEntity> __insertionAdapterOfProdutoEntity;

  private final UuidConverters __uuidConverters = new UuidConverters();

  private final EntityDeletionOrUpdateAdapter<ProdutoEntity> __deletionAdapterOfProdutoEntity;

  private final EntityDeletionOrUpdateAdapter<ProdutoEntity> __updateAdapterOfProdutoEntity;

  public ProdutoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProdutoEntity = new EntityInsertionAdapter<ProdutoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `produto` (`PRO_ID`,`created_at`,`updated_at`,`PRO_DESCRICAO`,`PRO_PRECO`,`PRO_GRUPO`,`PRO_UNIDADE`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProdutoEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        statement.bindString(4, entity.getDescricao());
        statement.bindLong(5, entity.getPrecoCentavos());
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getGrupoId());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_1);
        }
        statement.bindString(7, entity.getUnidade());
      }
    };
    this.__deletionAdapterOfProdutoEntity = new EntityDeletionOrUpdateAdapter<ProdutoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `produto` WHERE `PRO_ID` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProdutoEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
      }
    };
    this.__updateAdapterOfProdutoEntity = new EntityDeletionOrUpdateAdapter<ProdutoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `produto` SET `PRO_ID` = ?,`created_at` = ?,`updated_at` = ?,`PRO_DESCRICAO` = ?,`PRO_PRECO` = ?,`PRO_GRUPO` = ?,`PRO_UNIDADE` = ? WHERE `PRO_ID` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProdutoEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        statement.bindString(4, entity.getDescricao());
        statement.bindLong(5, entity.getPrecoCentavos());
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getGrupoId());
        if (_tmp_1 == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, _tmp_1);
        }
        statement.bindString(7, entity.getUnidade());
        final String _tmp_2 = __uuidConverters.fromUuid(entity.getId());
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_2);
        }
      }
    };
  }

  @Override
  public Object insert(final ProdutoEntity produto, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProdutoEntity.insert(produto);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ProdutoEntity produto, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfProdutoEntity.handle(produto);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ProdutoEntity produto, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfProdutoEntity.handle(produto);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ProdutoEntity>> observeAll() {
    final String _sql = "SELECT * FROM produto ORDER BY PRO_DESCRICAO ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"produto"}, new Callable<List<ProdutoEntity>>() {
      @Override
      @NonNull
      public List<ProdutoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfDescricao = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_DESCRICAO");
          final int _cursorIndexOfPrecoCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_PRECO");
          final int _cursorIndexOfGrupoId = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_GRUPO");
          final int _cursorIndexOfUnidade = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_UNIDADE");
          final List<ProdutoEntity> _result = new ArrayList<ProdutoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProdutoEntity _item;
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
            final String _tmpDescricao;
            _tmpDescricao = _cursor.getString(_cursorIndexOfDescricao);
            final long _tmpPrecoCentavos;
            _tmpPrecoCentavos = _cursor.getLong(_cursorIndexOfPrecoCentavos);
            final UUID _tmpGrupoId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfGrupoId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfGrupoId);
            }
            _tmpGrupoId = __uuidConverters.toUuid(_tmp_2);
            final String _tmpUnidade;
            _tmpUnidade = _cursor.getString(_cursorIndexOfUnidade);
            _item = new ProdutoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDescricao,_tmpPrecoCentavos,_tmpGrupoId,_tmpUnidade);
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
  public Object getAll(final Continuation<? super List<ProdutoEntity>> $completion) {
    final String _sql = "SELECT * FROM produto ORDER BY PRO_DESCRICAO ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ProdutoEntity>>() {
      @Override
      @NonNull
      public List<ProdutoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfDescricao = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_DESCRICAO");
          final int _cursorIndexOfPrecoCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_PRECO");
          final int _cursorIndexOfGrupoId = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_GRUPO");
          final int _cursorIndexOfUnidade = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_UNIDADE");
          final List<ProdutoEntity> _result = new ArrayList<ProdutoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProdutoEntity _item;
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
            final String _tmpDescricao;
            _tmpDescricao = _cursor.getString(_cursorIndexOfDescricao);
            final long _tmpPrecoCentavos;
            _tmpPrecoCentavos = _cursor.getLong(_cursorIndexOfPrecoCentavos);
            final UUID _tmpGrupoId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfGrupoId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfGrupoId);
            }
            _tmpGrupoId = __uuidConverters.toUuid(_tmp_2);
            final String _tmpUnidade;
            _tmpUnidade = _cursor.getString(_cursorIndexOfUnidade);
            _item = new ProdutoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDescricao,_tmpPrecoCentavos,_tmpGrupoId,_tmpUnidade);
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
  public Object getById(final UUID id, final Continuation<? super ProdutoEntity> $completion) {
    final String _sql = "SELECT * FROM produto WHERE PRO_ID = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProdutoEntity>() {
      @Override
      @Nullable
      public ProdutoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfDescricao = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_DESCRICAO");
          final int _cursorIndexOfPrecoCentavos = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_PRECO");
          final int _cursorIndexOfGrupoId = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_GRUPO");
          final int _cursorIndexOfUnidade = CursorUtil.getColumnIndexOrThrow(_cursor, "PRO_UNIDADE");
          final ProdutoEntity _result;
          if (_cursor.moveToFirst()) {
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
            final String _tmpDescricao;
            _tmpDescricao = _cursor.getString(_cursorIndexOfDescricao);
            final long _tmpPrecoCentavos;
            _tmpPrecoCentavos = _cursor.getLong(_cursorIndexOfPrecoCentavos);
            final UUID _tmpGrupoId;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfGrupoId)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfGrupoId);
            }
            _tmpGrupoId = __uuidConverters.toUuid(_tmp_3);
            final String _tmpUnidade;
            _tmpUnidade = _cursor.getString(_cursorIndexOfUnidade);
            _result = new ProdutoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpDescricao,_tmpPrecoCentavos,_tmpGrupoId,_tmpUnidade);
          } else {
            _result = null;
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
  public Object countVendasByProduto(final UUID produtoId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM venda_item WHERE VEI_PRODUTO = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(produtoId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(0);
            _result = _tmp_1;
          } else {
            _result = 0;
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
  public Object countComprasByProduto(final UUID produtoId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM compra_item WHERE COI_PRODUTO = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(produtoId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(0);
            _result = _tmp_1;
          } else {
            _result = 0;
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
