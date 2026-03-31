package app.callcheck.mobile.data.localcache.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import app.callcheck.mobile.data.localcache.entity.PreJudgeCacheEntry;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PreJudgeCacheDao_Impl implements PreJudgeCacheDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PreJudgeCacheEntry> __insertionAdapterOfPreJudgeCacheEntry;

  private final SharedSQLiteStatement __preparedStmtOfEvictOldest;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByNumber;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final SharedSQLiteStatement __preparedStmtOfIncrementHit;

  private final SharedSQLiteStatement __preparedStmtOfUpdateUserAction;

  public PreJudgeCacheDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPreJudgeCacheEntry = new EntityInsertionAdapter<PreJudgeCacheEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pre_judge_cache` (`id`,`canonical_number`,`action`,`risk_score`,`category`,`confidence`,`summary`,`hit_count`,`last_user_action`,`last_judged_at`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PreJudgeCacheEntry entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getCanonicalNumber() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getCanonicalNumber());
        }
        if (entity.getAction() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getAction());
        }
        statement.bindDouble(4, entity.getRiskScore());
        if (entity.getCategory() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getCategory());
        }
        statement.bindDouble(6, entity.getConfidence());
        if (entity.getSummary() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getSummary());
        }
        statement.bindLong(8, entity.getHitCount());
        if (entity.getLastUserAction() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getLastUserAction());
        }
        statement.bindLong(10, entity.getLastJudgedAt());
        statement.bindLong(11, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfEvictOldest = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        DELETE FROM pre_judge_cache WHERE id IN (\n"
                + "            SELECT id FROM pre_judge_cache\n"
                + "            ORDER BY hit_count ASC, last_judged_at ASC\n"
                + "            LIMIT ?\n"
                + "        )\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByNumber = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pre_judge_cache WHERE canonical_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pre_judge_cache";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementHit = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE pre_judge_cache\n"
                + "        SET hit_count = hit_count + 1,\n"
                + "            last_judged_at = ?\n"
                + "        WHERE canonical_number = ?\n"
                + "    ";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateUserAction = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "\n"
                + "        UPDATE pre_judge_cache\n"
                + "        SET last_user_action = ?,\n"
                + "            last_judged_at = ?\n"
                + "        WHERE canonical_number = ?\n"
                + "    ";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final PreJudgeCacheEntry entry,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPreJudgeCacheEntry.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object evictOldest(final int count, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEvictOldest.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, count);
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
          __preparedStmtOfEvictOldest.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByNumber(final String canonicalNumber,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByNumber.acquire();
        int _argIndex = 1;
        if (canonicalNumber == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, canonicalNumber);
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
          __preparedStmtOfDeleteByNumber.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementHit(final String canonicalNumber, final long now,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementHit.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 2;
        if (canonicalNumber == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, canonicalNumber);
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
          __preparedStmtOfIncrementHit.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateUserAction(final String canonicalNumber, final String actionKey,
      final long now, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateUserAction.acquire();
        int _argIndex = 1;
        if (actionKey == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, actionKey);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, now);
        _argIndex = 3;
        if (canonicalNumber == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, canonicalNumber);
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
          __preparedStmtOfUpdateUserAction.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object lookup(final String canonicalNumber,
      final Continuation<? super PreJudgeCacheEntry> $completion) {
    final String _sql = "SELECT * FROM pre_judge_cache WHERE canonical_number = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (canonicalNumber == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, canonicalNumber);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PreJudgeCacheEntry>() {
      @Override
      @Nullable
      public PreJudgeCacheEntry call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCanonicalNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "canonical_number");
          final int _cursorIndexOfAction = CursorUtil.getColumnIndexOrThrow(_cursor, "action");
          final int _cursorIndexOfRiskScore = CursorUtil.getColumnIndexOrThrow(_cursor, "risk_score");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfHitCount = CursorUtil.getColumnIndexOrThrow(_cursor, "hit_count");
          final int _cursorIndexOfLastUserAction = CursorUtil.getColumnIndexOrThrow(_cursor, "last_user_action");
          final int _cursorIndexOfLastJudgedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "last_judged_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final PreJudgeCacheEntry _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCanonicalNumber;
            if (_cursor.isNull(_cursorIndexOfCanonicalNumber)) {
              _tmpCanonicalNumber = null;
            } else {
              _tmpCanonicalNumber = _cursor.getString(_cursorIndexOfCanonicalNumber);
            }
            final String _tmpAction;
            if (_cursor.isNull(_cursorIndexOfAction)) {
              _tmpAction = null;
            } else {
              _tmpAction = _cursor.getString(_cursorIndexOfAction);
            }
            final float _tmpRiskScore;
            _tmpRiskScore = _cursor.getFloat(_cursorIndexOfRiskScore);
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final String _tmpSummary;
            if (_cursor.isNull(_cursorIndexOfSummary)) {
              _tmpSummary = null;
            } else {
              _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            }
            final int _tmpHitCount;
            _tmpHitCount = _cursor.getInt(_cursorIndexOfHitCount);
            final String _tmpLastUserAction;
            if (_cursor.isNull(_cursorIndexOfLastUserAction)) {
              _tmpLastUserAction = null;
            } else {
              _tmpLastUserAction = _cursor.getString(_cursorIndexOfLastUserAction);
            }
            final long _tmpLastJudgedAt;
            _tmpLastJudgedAt = _cursor.getLong(_cursorIndexOfLastJudgedAt);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new PreJudgeCacheEntry(_tmpId,_tmpCanonicalNumber,_tmpAction,_tmpRiskScore,_tmpCategory,_tmpConfidence,_tmpSummary,_tmpHitCount,_tmpLastUserAction,_tmpLastJudgedAt,_tmpCreatedAt);
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
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM pre_judge_cache";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
