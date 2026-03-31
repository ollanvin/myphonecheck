package app.callcheck.mobile.data.localcache.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import app.callcheck.mobile.data.localcache.entity.UserCallRecord;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserCallRecordDao_Impl implements UserCallRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserCallRecord> __insertionAdapterOfUserCallRecord;

  private final EntityDeletionOrUpdateAdapter<UserCallRecord> __updateAdapterOfUserCallRecord;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMemo;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTag;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAction;

  private final SharedSQLiteStatement __preparedStmtOfIncrementCallCount;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByNumber;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public UserCallRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserCallRecord = new EntityInsertionAdapter<UserCallRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_call_records` (`id`,`canonical_number`,`display_number`,`tag`,`memo`,`last_action`,`ai_risk_level`,`ai_category`,`call_count`,`created_at`,`updated_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserCallRecord entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getCanonicalNumber() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getCanonicalNumber());
        }
        if (entity.getDisplayNumber() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDisplayNumber());
        }
        if (entity.getTag() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getTag());
        }
        if (entity.getMemo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMemo());
        }
        if (entity.getLastAction() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLastAction());
        }
        if (entity.getAiRiskLevel() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAiRiskLevel());
        }
        if (entity.getAiCategory() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getAiCategory());
        }
        statement.bindLong(9, entity.getCallCount());
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfUserCallRecord = new EntityDeletionOrUpdateAdapter<UserCallRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_call_records` SET `id` = ?,`canonical_number` = ?,`display_number` = ?,`tag` = ?,`memo` = ?,`last_action` = ?,`ai_risk_level` = ?,`ai_category` = ?,`call_count` = ?,`created_at` = ?,`updated_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserCallRecord entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getCanonicalNumber() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getCanonicalNumber());
        }
        if (entity.getDisplayNumber() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDisplayNumber());
        }
        if (entity.getTag() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getTag());
        }
        if (entity.getMemo() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMemo());
        }
        if (entity.getLastAction() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLastAction());
        }
        if (entity.getAiRiskLevel() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAiRiskLevel());
        }
        if (entity.getAiCategory() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getAiCategory());
        }
        statement.bindLong(9, entity.getCallCount());
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getUpdatedAt());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateMemo = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_call_records SET memo = ?, updated_at = ? WHERE canonical_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTag = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_call_records SET tag = ?, updated_at = ? WHERE canonical_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateAction = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_call_records SET last_action = ?, updated_at = ? WHERE canonical_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementCallCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_call_records SET call_count = call_count + 1, updated_at = ? WHERE canonical_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByNumber = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user_call_records WHERE canonical_number = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user_call_records";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final UserCallRecord record, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfUserCallRecord.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final UserCallRecord record, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserCallRecord.handle(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMemo(final String canonicalNumber, final String memo, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMemo.acquire();
        int _argIndex = 1;
        if (memo == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, memo);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
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
          __preparedStmtOfUpdateMemo.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTag(final String canonicalNumber, final String tag, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTag.acquire();
        int _argIndex = 1;
        if (tag == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, tag);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
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
          __preparedStmtOfUpdateTag.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAction(final String canonicalNumber, final String action,
      final long updatedAt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAction.acquire();
        int _argIndex = 1;
        if (action == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, action);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
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
          __preparedStmtOfUpdateAction.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementCallCount(final String canonicalNumber, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementCallCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, updatedAt);
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
          __preparedStmtOfIncrementCallCount.release(_stmt);
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
  public Object findByNumber(final String canonicalNumber,
      final Continuation<? super UserCallRecord> $completion) {
    final String _sql = "SELECT * FROM user_call_records WHERE canonical_number = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (canonicalNumber == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, canonicalNumber);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserCallRecord>() {
      @Override
      @Nullable
      public UserCallRecord call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCanonicalNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "canonical_number");
          final int _cursorIndexOfDisplayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "display_number");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfLastAction = CursorUtil.getColumnIndexOrThrow(_cursor, "last_action");
          final int _cursorIndexOfAiRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_risk_level");
          final int _cursorIndexOfAiCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_category");
          final int _cursorIndexOfCallCount = CursorUtil.getColumnIndexOrThrow(_cursor, "call_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final UserCallRecord _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCanonicalNumber;
            if (_cursor.isNull(_cursorIndexOfCanonicalNumber)) {
              _tmpCanonicalNumber = null;
            } else {
              _tmpCanonicalNumber = _cursor.getString(_cursorIndexOfCanonicalNumber);
            }
            final String _tmpDisplayNumber;
            if (_cursor.isNull(_cursorIndexOfDisplayNumber)) {
              _tmpDisplayNumber = null;
            } else {
              _tmpDisplayNumber = _cursor.getString(_cursorIndexOfDisplayNumber);
            }
            final String _tmpTag;
            if (_cursor.isNull(_cursorIndexOfTag)) {
              _tmpTag = null;
            } else {
              _tmpTag = _cursor.getString(_cursorIndexOfTag);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final String _tmpLastAction;
            if (_cursor.isNull(_cursorIndexOfLastAction)) {
              _tmpLastAction = null;
            } else {
              _tmpLastAction = _cursor.getString(_cursorIndexOfLastAction);
            }
            final String _tmpAiRiskLevel;
            if (_cursor.isNull(_cursorIndexOfAiRiskLevel)) {
              _tmpAiRiskLevel = null;
            } else {
              _tmpAiRiskLevel = _cursor.getString(_cursorIndexOfAiRiskLevel);
            }
            final String _tmpAiCategory;
            if (_cursor.isNull(_cursorIndexOfAiCategory)) {
              _tmpAiCategory = null;
            } else {
              _tmpAiCategory = _cursor.getString(_cursorIndexOfAiCategory);
            }
            final int _tmpCallCount;
            _tmpCallCount = _cursor.getInt(_cursorIndexOfCallCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserCallRecord(_tmpId,_tmpCanonicalNumber,_tmpDisplayNumber,_tmpTag,_tmpMemo,_tmpLastAction,_tmpAiRiskLevel,_tmpAiCategory,_tmpCallCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<UserCallRecord> observeByNumber(final String canonicalNumber) {
    final String _sql = "SELECT * FROM user_call_records WHERE canonical_number = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (canonicalNumber == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, canonicalNumber);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_call_records"}, new Callable<UserCallRecord>() {
      @Override
      @Nullable
      public UserCallRecord call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCanonicalNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "canonical_number");
          final int _cursorIndexOfDisplayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "display_number");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfLastAction = CursorUtil.getColumnIndexOrThrow(_cursor, "last_action");
          final int _cursorIndexOfAiRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_risk_level");
          final int _cursorIndexOfAiCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_category");
          final int _cursorIndexOfCallCount = CursorUtil.getColumnIndexOrThrow(_cursor, "call_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final UserCallRecord _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCanonicalNumber;
            if (_cursor.isNull(_cursorIndexOfCanonicalNumber)) {
              _tmpCanonicalNumber = null;
            } else {
              _tmpCanonicalNumber = _cursor.getString(_cursorIndexOfCanonicalNumber);
            }
            final String _tmpDisplayNumber;
            if (_cursor.isNull(_cursorIndexOfDisplayNumber)) {
              _tmpDisplayNumber = null;
            } else {
              _tmpDisplayNumber = _cursor.getString(_cursorIndexOfDisplayNumber);
            }
            final String _tmpTag;
            if (_cursor.isNull(_cursorIndexOfTag)) {
              _tmpTag = null;
            } else {
              _tmpTag = _cursor.getString(_cursorIndexOfTag);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final String _tmpLastAction;
            if (_cursor.isNull(_cursorIndexOfLastAction)) {
              _tmpLastAction = null;
            } else {
              _tmpLastAction = _cursor.getString(_cursorIndexOfLastAction);
            }
            final String _tmpAiRiskLevel;
            if (_cursor.isNull(_cursorIndexOfAiRiskLevel)) {
              _tmpAiRiskLevel = null;
            } else {
              _tmpAiRiskLevel = _cursor.getString(_cursorIndexOfAiRiskLevel);
            }
            final String _tmpAiCategory;
            if (_cursor.isNull(_cursorIndexOfAiCategory)) {
              _tmpAiCategory = null;
            } else {
              _tmpAiCategory = _cursor.getString(_cursorIndexOfAiCategory);
            }
            final int _tmpCallCount;
            _tmpCallCount = _cursor.getInt(_cursorIndexOfCallCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserCallRecord(_tmpId,_tmpCanonicalNumber,_tmpDisplayNumber,_tmpTag,_tmpMemo,_tmpLastAction,_tmpAiRiskLevel,_tmpAiCategory,_tmpCallCount,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Flow<List<UserCallRecord>> observeAll() {
    final String _sql = "SELECT * FROM user_call_records ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_call_records"}, new Callable<List<UserCallRecord>>() {
      @Override
      @NonNull
      public List<UserCallRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCanonicalNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "canonical_number");
          final int _cursorIndexOfDisplayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "display_number");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfLastAction = CursorUtil.getColumnIndexOrThrow(_cursor, "last_action");
          final int _cursorIndexOfAiRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_risk_level");
          final int _cursorIndexOfAiCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_category");
          final int _cursorIndexOfCallCount = CursorUtil.getColumnIndexOrThrow(_cursor, "call_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<UserCallRecord> _result = new ArrayList<UserCallRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UserCallRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCanonicalNumber;
            if (_cursor.isNull(_cursorIndexOfCanonicalNumber)) {
              _tmpCanonicalNumber = null;
            } else {
              _tmpCanonicalNumber = _cursor.getString(_cursorIndexOfCanonicalNumber);
            }
            final String _tmpDisplayNumber;
            if (_cursor.isNull(_cursorIndexOfDisplayNumber)) {
              _tmpDisplayNumber = null;
            } else {
              _tmpDisplayNumber = _cursor.getString(_cursorIndexOfDisplayNumber);
            }
            final String _tmpTag;
            if (_cursor.isNull(_cursorIndexOfTag)) {
              _tmpTag = null;
            } else {
              _tmpTag = _cursor.getString(_cursorIndexOfTag);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final String _tmpLastAction;
            if (_cursor.isNull(_cursorIndexOfLastAction)) {
              _tmpLastAction = null;
            } else {
              _tmpLastAction = _cursor.getString(_cursorIndexOfLastAction);
            }
            final String _tmpAiRiskLevel;
            if (_cursor.isNull(_cursorIndexOfAiRiskLevel)) {
              _tmpAiRiskLevel = null;
            } else {
              _tmpAiRiskLevel = _cursor.getString(_cursorIndexOfAiRiskLevel);
            }
            final String _tmpAiCategory;
            if (_cursor.isNull(_cursorIndexOfAiCategory)) {
              _tmpAiCategory = null;
            } else {
              _tmpAiCategory = _cursor.getString(_cursorIndexOfAiCategory);
            }
            final int _tmpCallCount;
            _tmpCallCount = _cursor.getInt(_cursorIndexOfCallCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new UserCallRecord(_tmpId,_tmpCanonicalNumber,_tmpDisplayNumber,_tmpTag,_tmpMemo,_tmpLastAction,_tmpAiRiskLevel,_tmpAiCategory,_tmpCallCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<UserCallRecord>> observeByTag(final String tag) {
    final String _sql = "SELECT * FROM user_call_records WHERE tag = ? ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (tag == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, tag);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_call_records"}, new Callable<List<UserCallRecord>>() {
      @Override
      @NonNull
      public List<UserCallRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCanonicalNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "canonical_number");
          final int _cursorIndexOfDisplayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "display_number");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfLastAction = CursorUtil.getColumnIndexOrThrow(_cursor, "last_action");
          final int _cursorIndexOfAiRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_risk_level");
          final int _cursorIndexOfAiCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_category");
          final int _cursorIndexOfCallCount = CursorUtil.getColumnIndexOrThrow(_cursor, "call_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<UserCallRecord> _result = new ArrayList<UserCallRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UserCallRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCanonicalNumber;
            if (_cursor.isNull(_cursorIndexOfCanonicalNumber)) {
              _tmpCanonicalNumber = null;
            } else {
              _tmpCanonicalNumber = _cursor.getString(_cursorIndexOfCanonicalNumber);
            }
            final String _tmpDisplayNumber;
            if (_cursor.isNull(_cursorIndexOfDisplayNumber)) {
              _tmpDisplayNumber = null;
            } else {
              _tmpDisplayNumber = _cursor.getString(_cursorIndexOfDisplayNumber);
            }
            final String _tmpTag;
            if (_cursor.isNull(_cursorIndexOfTag)) {
              _tmpTag = null;
            } else {
              _tmpTag = _cursor.getString(_cursorIndexOfTag);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final String _tmpLastAction;
            if (_cursor.isNull(_cursorIndexOfLastAction)) {
              _tmpLastAction = null;
            } else {
              _tmpLastAction = _cursor.getString(_cursorIndexOfLastAction);
            }
            final String _tmpAiRiskLevel;
            if (_cursor.isNull(_cursorIndexOfAiRiskLevel)) {
              _tmpAiRiskLevel = null;
            } else {
              _tmpAiRiskLevel = _cursor.getString(_cursorIndexOfAiRiskLevel);
            }
            final String _tmpAiCategory;
            if (_cursor.isNull(_cursorIndexOfAiCategory)) {
              _tmpAiCategory = null;
            } else {
              _tmpAiCategory = _cursor.getString(_cursorIndexOfAiCategory);
            }
            final int _tmpCallCount;
            _tmpCallCount = _cursor.getInt(_cursorIndexOfCallCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new UserCallRecord(_tmpId,_tmpCanonicalNumber,_tmpDisplayNumber,_tmpTag,_tmpMemo,_tmpLastAction,_tmpAiRiskLevel,_tmpAiCategory,_tmpCallCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<UserCallRecord>> observeBlockedNumbers() {
    final String _sql = "SELECT * FROM user_call_records WHERE last_action = 'blocked' ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_call_records"}, new Callable<List<UserCallRecord>>() {
      @Override
      @NonNull
      public List<UserCallRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCanonicalNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "canonical_number");
          final int _cursorIndexOfDisplayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "display_number");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfLastAction = CursorUtil.getColumnIndexOrThrow(_cursor, "last_action");
          final int _cursorIndexOfAiRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_risk_level");
          final int _cursorIndexOfAiCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_category");
          final int _cursorIndexOfCallCount = CursorUtil.getColumnIndexOrThrow(_cursor, "call_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<UserCallRecord> _result = new ArrayList<UserCallRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UserCallRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCanonicalNumber;
            if (_cursor.isNull(_cursorIndexOfCanonicalNumber)) {
              _tmpCanonicalNumber = null;
            } else {
              _tmpCanonicalNumber = _cursor.getString(_cursorIndexOfCanonicalNumber);
            }
            final String _tmpDisplayNumber;
            if (_cursor.isNull(_cursorIndexOfDisplayNumber)) {
              _tmpDisplayNumber = null;
            } else {
              _tmpDisplayNumber = _cursor.getString(_cursorIndexOfDisplayNumber);
            }
            final String _tmpTag;
            if (_cursor.isNull(_cursorIndexOfTag)) {
              _tmpTag = null;
            } else {
              _tmpTag = _cursor.getString(_cursorIndexOfTag);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final String _tmpLastAction;
            if (_cursor.isNull(_cursorIndexOfLastAction)) {
              _tmpLastAction = null;
            } else {
              _tmpLastAction = _cursor.getString(_cursorIndexOfLastAction);
            }
            final String _tmpAiRiskLevel;
            if (_cursor.isNull(_cursorIndexOfAiRiskLevel)) {
              _tmpAiRiskLevel = null;
            } else {
              _tmpAiRiskLevel = _cursor.getString(_cursorIndexOfAiRiskLevel);
            }
            final String _tmpAiCategory;
            if (_cursor.isNull(_cursorIndexOfAiCategory)) {
              _tmpAiCategory = null;
            } else {
              _tmpAiCategory = _cursor.getString(_cursorIndexOfAiCategory);
            }
            final int _tmpCallCount;
            _tmpCallCount = _cursor.getInt(_cursorIndexOfCallCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new UserCallRecord(_tmpId,_tmpCanonicalNumber,_tmpDisplayNumber,_tmpTag,_tmpMemo,_tmpLastAction,_tmpAiRiskLevel,_tmpAiCategory,_tmpCallCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getRecordCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM user_call_records";
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

  @Override
  public Flow<List<UserCallRecord>> observeWithMemos() {
    final String _sql = "SELECT * FROM user_call_records WHERE memo IS NOT NULL AND memo != '' ORDER BY updated_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_call_records"}, new Callable<List<UserCallRecord>>() {
      @Override
      @NonNull
      public List<UserCallRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCanonicalNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "canonical_number");
          final int _cursorIndexOfDisplayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "display_number");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfLastAction = CursorUtil.getColumnIndexOrThrow(_cursor, "last_action");
          final int _cursorIndexOfAiRiskLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_risk_level");
          final int _cursorIndexOfAiCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "ai_category");
          final int _cursorIndexOfCallCount = CursorUtil.getColumnIndexOrThrow(_cursor, "call_count");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updated_at");
          final List<UserCallRecord> _result = new ArrayList<UserCallRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UserCallRecord _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCanonicalNumber;
            if (_cursor.isNull(_cursorIndexOfCanonicalNumber)) {
              _tmpCanonicalNumber = null;
            } else {
              _tmpCanonicalNumber = _cursor.getString(_cursorIndexOfCanonicalNumber);
            }
            final String _tmpDisplayNumber;
            if (_cursor.isNull(_cursorIndexOfDisplayNumber)) {
              _tmpDisplayNumber = null;
            } else {
              _tmpDisplayNumber = _cursor.getString(_cursorIndexOfDisplayNumber);
            }
            final String _tmpTag;
            if (_cursor.isNull(_cursorIndexOfTag)) {
              _tmpTag = null;
            } else {
              _tmpTag = _cursor.getString(_cursorIndexOfTag);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final String _tmpLastAction;
            if (_cursor.isNull(_cursorIndexOfLastAction)) {
              _tmpLastAction = null;
            } else {
              _tmpLastAction = _cursor.getString(_cursorIndexOfLastAction);
            }
            final String _tmpAiRiskLevel;
            if (_cursor.isNull(_cursorIndexOfAiRiskLevel)) {
              _tmpAiRiskLevel = null;
            } else {
              _tmpAiRiskLevel = _cursor.getString(_cursorIndexOfAiRiskLevel);
            }
            final String _tmpAiCategory;
            if (_cursor.isNull(_cursorIndexOfAiCategory)) {
              _tmpAiCategory = null;
            } else {
              _tmpAiCategory = _cursor.getString(_cursorIndexOfAiCategory);
            }
            final int _tmpCallCount;
            _tmpCallCount = _cursor.getInt(_cursorIndexOfCallCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new UserCallRecord(_tmpId,_tmpCanonicalNumber,_tmpDisplayNumber,_tmpTag,_tmpMemo,_tmpLastAction,_tmpAiRiskLevel,_tmpAiCategory,_tmpCallCount,_tmpCreatedAt,_tmpUpdatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
