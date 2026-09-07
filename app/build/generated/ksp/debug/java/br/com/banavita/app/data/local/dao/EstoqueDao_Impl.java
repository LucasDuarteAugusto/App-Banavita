package br.com.banavita.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import br.com.banavita.app.data.local.converter.UuidConverters;
import br.com.banavita.app.data.local.view.ProdutoEstoqueView;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.IllegalStateException;
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
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class EstoqueDao_Impl implements EstoqueDao {
  private final RoomDatabase __db;

  private final UuidConverters __uuidConverters = new UuidConverters();

  public EstoqueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
  }

  @Override
  public Flow<List<ProdutoEstoqueView>> observeAll() {
    final String _sql = "SELECT * FROM vw_produto_estoque ORDER BY produto_id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vw_produto_estoque"}, new Callable<List<ProdutoEstoqueView>>() {
      @Override
      @NonNull
      public List<ProdutoEstoqueView> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfProdutoId = CursorUtil.getColumnIndexOrThrow(_cursor, "produto_id");
          final int _cursorIndexOfUnidade = CursorUtil.getColumnIndexOrThrow(_cursor, "unidade");
          final int _cursorIndexOfSaldo = CursorUtil.getColumnIndexOrThrow(_cursor, "saldo");
          final List<ProdutoEstoqueView> _result = new ArrayList<ProdutoEstoqueView>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProdutoEstoqueView _item;
            final UUID _tmpProdutoId;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfProdutoId)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfProdutoId);
            }
            final UUID _tmp_1 = __uuidConverters.toUuid(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpProdutoId = _tmp_1;
            }
            final String _tmpUnidade;
            _tmpUnidade = _cursor.getString(_cursorIndexOfUnidade);
            final double _tmpSaldo;
            _tmpSaldo = _cursor.getDouble(_cursorIndexOfSaldo);
            _item = new ProdutoEstoqueView(_tmpProdutoId,_tmpUnidade,_tmpSaldo);
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
  public Object getByProdutoId(final UUID produtoId,
      final Continuation<? super ProdutoEstoqueView> $completion) {
    final String _sql = "SELECT * FROM vw_produto_estoque WHERE produto_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(produtoId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProdutoEstoqueView>() {
      @Override
      @Nullable
      public ProdutoEstoqueView call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfProdutoId = CursorUtil.getColumnIndexOrThrow(_cursor, "produto_id");
          final int _cursorIndexOfUnidade = CursorUtil.getColumnIndexOrThrow(_cursor, "unidade");
          final int _cursorIndexOfSaldo = CursorUtil.getColumnIndexOrThrow(_cursor, "saldo");
          final ProdutoEstoqueView _result;
          if (_cursor.moveToFirst()) {
            final UUID _tmpProdutoId;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfProdutoId)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfProdutoId);
            }
            final UUID _tmp_2 = __uuidConverters.toUuid(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpProdutoId = _tmp_2;
            }
            final String _tmpUnidade;
            _tmpUnidade = _cursor.getString(_cursorIndexOfUnidade);
            final double _tmpSaldo;
            _tmpSaldo = _cursor.getDouble(_cursorIndexOfSaldo);
            _result = new ProdutoEstoqueView(_tmpProdutoId,_tmpUnidade,_tmpSaldo);
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
  public Object totalComprado(final UUID produtoId,
      final Continuation<? super Double> $completion) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(COI_QTDE), 0)\n"
            + "        FROM compra_item\n"
            + "        WHERE COI_PRODUTO = ?\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(produtoId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @NonNull
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final double _tmp_1;
            _tmp_1 = _cursor.getDouble(0);
            _result = _tmp_1;
          } else {
            _result = 0.0;
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
  public Object totalVendido(final UUID produtoId, final UUID excludeVendaId,
      final Continuation<? super Double> $completion) {
    final String _sql = "\n"
            + "        SELECT COALESCE(SUM(VEI_QTDE), 0)\n"
            + "        FROM venda_item\n"
            + "        WHERE VEI_PRODUTO = ?\n"
            + "        AND (? IS NULL OR VEI_VENDA != ?)\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(produtoId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    _argIndex = 2;
    final String _tmp_1 = __uuidConverters.fromUuid(excludeVendaId);
    if (_tmp_1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_1);
    }
    _argIndex = 3;
    final String _tmp_2 = __uuidConverters.fromUuid(excludeVendaId);
    if (_tmp_2 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp_2);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @NonNull
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final double _tmp_3;
            _tmp_3 = _cursor.getDouble(0);
            _result = _tmp_3;
          } else {
            _result = 0.0;
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
  public Object entradasDoProduto(final UUID produtoId,
      final Continuation<? super List<EstoqueMovimentoRow>> $completion) {
    final String _sql = "\n"
            + "        SELECT compra.COM_DATA AS data, compra_item.COI_QTDE AS quantidade, compra.COM_ID AS documentoId\n"
            + "        FROM compra_item\n"
            + "        INNER JOIN compra ON compra.COM_ID = compra_item.COI_COMPRA\n"
            + "        WHERE compra_item.COI_PRODUTO = ?\n"
            + "        ORDER BY compra.COM_DATA DESC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(produtoId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EstoqueMovimentoRow>>() {
      @Override
      @NonNull
      public List<EstoqueMovimentoRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfData = 0;
          final int _cursorIndexOfQuantidade = 1;
          final int _cursorIndexOfDocumentoId = 2;
          final List<EstoqueMovimentoRow> _result = new ArrayList<EstoqueMovimentoRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EstoqueMovimentoRow _item;
            final long _tmpData;
            _tmpData = _cursor.getLong(_cursorIndexOfData);
            final double _tmpQuantidade;
            _tmpQuantidade = _cursor.getDouble(_cursorIndexOfQuantidade);
            final UUID _tmpDocumentoId;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDocumentoId)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfDocumentoId);
            }
            final UUID _tmp_2 = __uuidConverters.toUuid(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpDocumentoId = _tmp_2;
            }
            _item = new EstoqueMovimentoRow(_tmpData,_tmpQuantidade,_tmpDocumentoId);
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
  public Object saidasDoProduto(final UUID produtoId,
      final Continuation<? super List<EstoqueMovimentoRow>> $completion) {
    final String _sql = "\n"
            + "        SELECT venda.VEN_DATA AS data, venda_item.VEI_QTDE AS quantidade, venda.VEN_ID AS documentoId\n"
            + "        FROM venda_item\n"
            + "        INNER JOIN venda ON venda.VEN_ID = venda_item.VEI_VENDA\n"
            + "        WHERE venda_item.VEI_PRODUTO = ?\n"
            + "        ORDER BY venda.VEN_DATA DESC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __uuidConverters.fromUuid(produtoId);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EstoqueMovimentoRow>>() {
      @Override
      @NonNull
      public List<EstoqueMovimentoRow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfData = 0;
          final int _cursorIndexOfQuantidade = 1;
          final int _cursorIndexOfDocumentoId = 2;
          final List<EstoqueMovimentoRow> _result = new ArrayList<EstoqueMovimentoRow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EstoqueMovimentoRow _item;
            final long _tmpData;
            _tmpData = _cursor.getLong(_cursorIndexOfData);
            final double _tmpQuantidade;
            _tmpQuantidade = _cursor.getDouble(_cursorIndexOfQuantidade);
            final UUID _tmpDocumentoId;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfDocumentoId)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfDocumentoId);
            }
            final UUID _tmp_2 = __uuidConverters.toUuid(_tmp_1);
            if (_tmp_2 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.UUID', but it was NULL.");
            } else {
              _tmpDocumentoId = _tmp_2;
            }
            _item = new EstoqueMovimentoRow(_tmpData,_tmpQuantidade,_tmpDocumentoId);
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
