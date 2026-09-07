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
import br.com.banavita.app.data.local.entity.EventoEntity;
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
public final class EventoDao_Impl implements EventoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EventoEntity> __insertionAdapterOfEventoEntity;

  private final UuidConverters __uuidConverters = new UuidConverters();

  private final EntityDeletionOrUpdateAdapter<EventoEntity> __deletionAdapterOfEventoEntity;

  private final EntityDeletionOrUpdateAdapter<EventoEntity> __updateAdapterOfEventoEntity;

  public EventoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEventoEntity = new EntityInsertionAdapter<EventoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `evento` (`EVE_ID`,`created_at`,`updated_at`,`EVE_TITULO`,`EVE_DATAHORA`,`EVE_OBS`,`EVE_CLIENTE`,`EVE_FORNECEDOR`,`EVE_NOTIFICAR`,`EVE_ANTECIPACAO_MIN`,`EVE_NOTIF_TITULO`,`EVE_NOTIF_CORPO`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventoEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        statement.bindString(4, entity.getTitulo());
        statement.bindLong(5, entity.getDataHora());
        if (entity.getObservacao() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getObservacao());
        }
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getClienteId());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
        final String _tmp_2 = __uuidConverters.fromUuid(entity.getFornecedorId());
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_2);
        }
        final int _tmp_3 = entity.getNotificar() ? 1 : 0;
        statement.bindLong(9, _tmp_3);
        if (entity.getAntecipacaoMinutos() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getAntecipacaoMinutos());
        }
        if (entity.getNotifTitulo() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getNotifTitulo());
        }
        if (entity.getNotifCorpo() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotifCorpo());
        }
      }
    };
    this.__deletionAdapterOfEventoEntity = new EntityDeletionOrUpdateAdapter<EventoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `evento` WHERE `EVE_ID` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventoEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
      }
    };
    this.__updateAdapterOfEventoEntity = new EntityDeletionOrUpdateAdapter<EventoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `evento` SET `EVE_ID` = ?,`created_at` = ?,`updated_at` = ?,`EVE_TITULO` = ?,`EVE_DATAHORA` = ?,`EVE_OBS` = ?,`EVE_CLIENTE` = ?,`EVE_FORNECEDOR` = ?,`EVE_NOTIFICAR` = ?,`EVE_ANTECIPACAO_MIN` = ?,`EVE_NOTIF_TITULO` = ?,`EVE_NOTIF_CORPO` = ? WHERE `EVE_ID` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventoEntity entity) {
        final String _tmp = __uuidConverters.fromUuid(entity.getId());
        if (_tmp == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, _tmp);
        }
        statement.bindLong(2, entity.getCreatedAt());
        statement.bindLong(3, entity.getUpdatedAt());
        statement.bindString(4, entity.getTitulo());
        statement.bindLong(5, entity.getDataHora());
        if (entity.getObservacao() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getObservacao());
        }
        final String _tmp_1 = __uuidConverters.fromUuid(entity.getClienteId());
        if (_tmp_1 == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, _tmp_1);
        }
        final String _tmp_2 = __uuidConverters.fromUuid(entity.getFornecedorId());
        if (_tmp_2 == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, _tmp_2);
        }
        final int _tmp_3 = entity.getNotificar() ? 1 : 0;
        statement.bindLong(9, _tmp_3);
        if (entity.getAntecipacaoMinutos() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getAntecipacaoMinutos());
        }
        if (entity.getNotifTitulo() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getNotifTitulo());
        }
        if (entity.getNotifCorpo() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotifCorpo());
        }
        final String _tmp_4 = __uuidConverters.fromUuid(entity.getId());
        if (_tmp_4 == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, _tmp_4);
        }
      }
    };
  }

  @Override
  public Object insert(final EventoEntity evento, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEventoEntity.insert(evento);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final EventoEntity evento, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEventoEntity.handle(evento);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final EventoEntity evento, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEventoEntity.handle(evento);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EventoEntity>> observeAll() {
    final String _sql = "SELECT * FROM evento ORDER BY EVE_DATAHORA ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"evento"}, new Callable<List<EventoEntity>>() {
      @Override
      @NonNull
      public List<EventoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_TITULO");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_DATAHORA");
          final int _cursorIndexOfObservacao = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_OBS");
          final int _cursorIndexOfClienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_CLIENTE");
          final int _cursorIndexOfFornecedorId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_FORNECEDOR");
          final int _cursorIndexOfNotificar = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIFICAR");
          final int _cursorIndexOfAntecipacaoMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_ANTECIPACAO_MIN");
          final int _cursorIndexOfNotifTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIF_TITULO");
          final int _cursorIndexOfNotifCorpo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIF_CORPO");
          final List<EventoEntity> _result = new ArrayList<EventoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventoEntity _item;
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
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final String _tmpObservacao;
            if (_cursor.isNull(_cursorIndexOfObservacao)) {
              _tmpObservacao = null;
            } else {
              _tmpObservacao = _cursor.getString(_cursorIndexOfObservacao);
            }
            final UUID _tmpClienteId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfClienteId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfClienteId);
            }
            _tmpClienteId = __uuidConverters.toUuid(_tmp_2);
            final UUID _tmpFornecedorId;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFornecedorId)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFornecedorId);
            }
            _tmpFornecedorId = __uuidConverters.toUuid(_tmp_3);
            final boolean _tmpNotificar;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfNotificar);
            _tmpNotificar = _tmp_4 != 0;
            final Integer _tmpAntecipacaoMinutos;
            if (_cursor.isNull(_cursorIndexOfAntecipacaoMinutos)) {
              _tmpAntecipacaoMinutos = null;
            } else {
              _tmpAntecipacaoMinutos = _cursor.getInt(_cursorIndexOfAntecipacaoMinutos);
            }
            final String _tmpNotifTitulo;
            if (_cursor.isNull(_cursorIndexOfNotifTitulo)) {
              _tmpNotifTitulo = null;
            } else {
              _tmpNotifTitulo = _cursor.getString(_cursorIndexOfNotifTitulo);
            }
            final String _tmpNotifCorpo;
            if (_cursor.isNull(_cursorIndexOfNotifCorpo)) {
              _tmpNotifCorpo = null;
            } else {
              _tmpNotifCorpo = _cursor.getString(_cursorIndexOfNotifCorpo);
            }
            _item = new EventoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpTitulo,_tmpDataHora,_tmpObservacao,_tmpClienteId,_tmpFornecedorId,_tmpNotificar,_tmpAntecipacaoMinutos,_tmpNotifTitulo,_tmpNotifCorpo);
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
  public Flow<List<EventoEntity>> observeFrom(final long fromMillis) {
    final String _sql = "SELECT * FROM evento WHERE EVE_DATAHORA >= ? ORDER BY EVE_DATAHORA ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fromMillis);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"evento"}, new Callable<List<EventoEntity>>() {
      @Override
      @NonNull
      public List<EventoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_TITULO");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_DATAHORA");
          final int _cursorIndexOfObservacao = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_OBS");
          final int _cursorIndexOfClienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_CLIENTE");
          final int _cursorIndexOfFornecedorId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_FORNECEDOR");
          final int _cursorIndexOfNotificar = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIFICAR");
          final int _cursorIndexOfAntecipacaoMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_ANTECIPACAO_MIN");
          final int _cursorIndexOfNotifTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIF_TITULO");
          final int _cursorIndexOfNotifCorpo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIF_CORPO");
          final List<EventoEntity> _result = new ArrayList<EventoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventoEntity _item;
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
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final String _tmpObservacao;
            if (_cursor.isNull(_cursorIndexOfObservacao)) {
              _tmpObservacao = null;
            } else {
              _tmpObservacao = _cursor.getString(_cursorIndexOfObservacao);
            }
            final UUID _tmpClienteId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfClienteId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfClienteId);
            }
            _tmpClienteId = __uuidConverters.toUuid(_tmp_2);
            final UUID _tmpFornecedorId;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFornecedorId)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFornecedorId);
            }
            _tmpFornecedorId = __uuidConverters.toUuid(_tmp_3);
            final boolean _tmpNotificar;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfNotificar);
            _tmpNotificar = _tmp_4 != 0;
            final Integer _tmpAntecipacaoMinutos;
            if (_cursor.isNull(_cursorIndexOfAntecipacaoMinutos)) {
              _tmpAntecipacaoMinutos = null;
            } else {
              _tmpAntecipacaoMinutos = _cursor.getInt(_cursorIndexOfAntecipacaoMinutos);
            }
            final String _tmpNotifTitulo;
            if (_cursor.isNull(_cursorIndexOfNotifTitulo)) {
              _tmpNotifTitulo = null;
            } else {
              _tmpNotifTitulo = _cursor.getString(_cursorIndexOfNotifTitulo);
            }
            final String _tmpNotifCorpo;
            if (_cursor.isNull(_cursorIndexOfNotifCorpo)) {
              _tmpNotifCorpo = null;
            } else {
              _tmpNotifCorpo = _cursor.getString(_cursorIndexOfNotifCorpo);
            }
            _item = new EventoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpTitulo,_tmpDataHora,_tmpObservacao,_tmpClienteId,_tmpFornecedorId,_tmpNotificar,_tmpAntecipacaoMinutos,_tmpNotifTitulo,_tmpNotifCorpo);
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
  public Object getById(final UUID id, final Continuation<? super EventoEntity> $completion) {
    final String _sql = "SELECT * FROM evento WHERE EVE_ID = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(id);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EventoEntity>() {
      @Override
      @Nullable
      public EventoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_TITULO");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_DATAHORA");
          final int _cursorIndexOfObservacao = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_OBS");
          final int _cursorIndexOfClienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_CLIENTE");
          final int _cursorIndexOfFornecedorId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_FORNECEDOR");
          final int _cursorIndexOfNotificar = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIFICAR");
          final int _cursorIndexOfAntecipacaoMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_ANTECIPACAO_MIN");
          final int _cursorIndexOfNotifTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIF_TITULO");
          final int _cursorIndexOfNotifCorpo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIF_CORPO");
          final EventoEntity _result;
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
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final String _tmpObservacao;
            if (_cursor.isNull(_cursorIndexOfObservacao)) {
              _tmpObservacao = null;
            } else {
              _tmpObservacao = _cursor.getString(_cursorIndexOfObservacao);
            }
            final UUID _tmpClienteId;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfClienteId)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfClienteId);
            }
            _tmpClienteId = __uuidConverters.toUuid(_tmp_3);
            final UUID _tmpFornecedorId;
            final String _tmp_4;
            if (_cursor.isNull(_cursorIndexOfFornecedorId)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getString(_cursorIndexOfFornecedorId);
            }
            _tmpFornecedorId = __uuidConverters.toUuid(_tmp_4);
            final boolean _tmpNotificar;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfNotificar);
            _tmpNotificar = _tmp_5 != 0;
            final Integer _tmpAntecipacaoMinutos;
            if (_cursor.isNull(_cursorIndexOfAntecipacaoMinutos)) {
              _tmpAntecipacaoMinutos = null;
            } else {
              _tmpAntecipacaoMinutos = _cursor.getInt(_cursorIndexOfAntecipacaoMinutos);
            }
            final String _tmpNotifTitulo;
            if (_cursor.isNull(_cursorIndexOfNotifTitulo)) {
              _tmpNotifTitulo = null;
            } else {
              _tmpNotifTitulo = _cursor.getString(_cursorIndexOfNotifTitulo);
            }
            final String _tmpNotifCorpo;
            if (_cursor.isNull(_cursorIndexOfNotifCorpo)) {
              _tmpNotifCorpo = null;
            } else {
              _tmpNotifCorpo = _cursor.getString(_cursorIndexOfNotifCorpo);
            }
            _result = new EventoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpTitulo,_tmpDataHora,_tmpObservacao,_tmpClienteId,_tmpFornecedorId,_tmpNotificar,_tmpAntecipacaoMinutos,_tmpNotifTitulo,_tmpNotifCorpo);
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
  public Object getPendingReminders(final long fromMillis,
      final Continuation<? super List<EventoEntity>> $completion) {
    final String _sql = "SELECT * FROM evento WHERE EVE_NOTIFICAR = 1 AND EVE_DATAHORA >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fromMillis);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EventoEntity>>() {
      @Override
      @NonNull
      public List<EventoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_ID");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final int _cursorIndexOfTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_TITULO");
          final int _cursorIndexOfDataHora = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_DATAHORA");
          final int _cursorIndexOfObservacao = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_OBS");
          final int _cursorIndexOfClienteId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_CLIENTE");
          final int _cursorIndexOfFornecedorId = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_FORNECEDOR");
          final int _cursorIndexOfNotificar = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIFICAR");
          final int _cursorIndexOfAntecipacaoMinutos = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_ANTECIPACAO_MIN");
          final int _cursorIndexOfNotifTitulo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIF_TITULO");
          final int _cursorIndexOfNotifCorpo = CursorUtil.getColumnIndexOrThrow(_cursor, "EVE_NOTIF_CORPO");
          final List<EventoEntity> _result = new ArrayList<EventoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventoEntity _item;
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
            final String _tmpTitulo;
            _tmpTitulo = _cursor.getString(_cursorIndexOfTitulo);
            final long _tmpDataHora;
            _tmpDataHora = _cursor.getLong(_cursorIndexOfDataHora);
            final String _tmpObservacao;
            if (_cursor.isNull(_cursorIndexOfObservacao)) {
              _tmpObservacao = null;
            } else {
              _tmpObservacao = _cursor.getString(_cursorIndexOfObservacao);
            }
            final UUID _tmpClienteId;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfClienteId)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfClienteId);
            }
            _tmpClienteId = __uuidConverters.toUuid(_tmp_2);
            final UUID _tmpFornecedorId;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfFornecedorId)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfFornecedorId);
            }
            _tmpFornecedorId = __uuidConverters.toUuid(_tmp_3);
            final boolean _tmpNotificar;
            final int _tmp_4;
            _tmp_4 = _cursor.getInt(_cursorIndexOfNotificar);
            _tmpNotificar = _tmp_4 != 0;
            final Integer _tmpAntecipacaoMinutos;
            if (_cursor.isNull(_cursorIndexOfAntecipacaoMinutos)) {
              _tmpAntecipacaoMinutos = null;
            } else {
              _tmpAntecipacaoMinutos = _cursor.getInt(_cursorIndexOfAntecipacaoMinutos);
            }
            final String _tmpNotifTitulo;
            if (_cursor.isNull(_cursorIndexOfNotifTitulo)) {
              _tmpNotifTitulo = null;
            } else {
              _tmpNotifTitulo = _cursor.getString(_cursorIndexOfNotifTitulo);
            }
            final String _tmpNotifCorpo;
            if (_cursor.isNull(_cursorIndexOfNotifCorpo)) {
              _tmpNotifCorpo = null;
            } else {
              _tmpNotifCorpo = _cursor.getString(_cursorIndexOfNotifCorpo);
            }
            _item = new EventoEntity(_tmpId,_tmpCreatedAt,_tmpUpdatedAt,_tmpTitulo,_tmpDataHora,_tmpObservacao,_tmpClienteId,_tmpFornecedorId,_tmpNotificar,_tmpAntecipacaoMinutos,_tmpNotifTitulo,_tmpNotifCorpo);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
