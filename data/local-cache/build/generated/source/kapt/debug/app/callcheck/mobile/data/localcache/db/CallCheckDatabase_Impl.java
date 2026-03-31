package app.callcheck.mobile.data.localcache.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao;
import app.callcheck.mobile.data.localcache.dao.PreJudgeCacheDao_Impl;
import app.callcheck.mobile.data.localcache.dao.UserCallRecordDao;
import app.callcheck.mobile.data.localcache.dao.UserCallRecordDao_Impl;
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
public final class CallCheckDatabase_Impl extends CallCheckDatabase {
  private volatile UserCallRecordDao _userCallRecordDao;

  private volatile PreJudgeCacheDao _preJudgeCacheDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_call_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `canonical_number` TEXT NOT NULL, `display_number` TEXT NOT NULL, `tag` TEXT, `memo` TEXT, `last_action` TEXT, `ai_risk_level` TEXT, `ai_category` TEXT, `call_count` INTEGER NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_user_call_records_canonical_number` ON `user_call_records` (`canonical_number`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_call_records_tag` ON `user_call_records` (`tag`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_user_call_records_updated_at` ON `user_call_records` (`updated_at`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pre_judge_cache` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `canonical_number` TEXT NOT NULL, `action` TEXT NOT NULL, `risk_score` REAL NOT NULL, `category` TEXT NOT NULL, `confidence` REAL NOT NULL, `summary` TEXT NOT NULL, `hit_count` INTEGER NOT NULL, `last_user_action` TEXT, `last_judged_at` INTEGER NOT NULL, `created_at` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_pre_judge_cache_canonical_number` ON `pre_judge_cache` (`canonical_number`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_pre_judge_cache_last_judged_at` ON `pre_judge_cache` (`last_judged_at`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_pre_judge_cache_hit_count` ON `pre_judge_cache` (`hit_count`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '3a102a72ddc522b5ff1aab5a4f2f7387')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `user_call_records`");
        db.execSQL("DROP TABLE IF EXISTS `pre_judge_cache`");
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
        final HashMap<String, TableInfo.Column> _columnsUserCallRecords = new HashMap<String, TableInfo.Column>(11);
        _columnsUserCallRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("canonical_number", new TableInfo.Column("canonical_number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("display_number", new TableInfo.Column("display_number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("tag", new TableInfo.Column("tag", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("memo", new TableInfo.Column("memo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("last_action", new TableInfo.Column("last_action", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("ai_risk_level", new TableInfo.Column("ai_risk_level", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("ai_category", new TableInfo.Column("ai_category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("call_count", new TableInfo.Column("call_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCallRecords.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserCallRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserCallRecords = new HashSet<TableInfo.Index>(3);
        _indicesUserCallRecords.add(new TableInfo.Index("index_user_call_records_canonical_number", true, Arrays.asList("canonical_number"), Arrays.asList("ASC")));
        _indicesUserCallRecords.add(new TableInfo.Index("index_user_call_records_tag", false, Arrays.asList("tag"), Arrays.asList("ASC")));
        _indicesUserCallRecords.add(new TableInfo.Index("index_user_call_records_updated_at", false, Arrays.asList("updated_at"), Arrays.asList("ASC")));
        final TableInfo _infoUserCallRecords = new TableInfo("user_call_records", _columnsUserCallRecords, _foreignKeysUserCallRecords, _indicesUserCallRecords);
        final TableInfo _existingUserCallRecords = TableInfo.read(db, "user_call_records");
        if (!_infoUserCallRecords.equals(_existingUserCallRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "user_call_records(app.callcheck.mobile.data.localcache.entity.UserCallRecord).\n"
                  + " Expected:\n" + _infoUserCallRecords + "\n"
                  + " Found:\n" + _existingUserCallRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsPreJudgeCache = new HashMap<String, TableInfo.Column>(11);
        _columnsPreJudgeCache.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("canonical_number", new TableInfo.Column("canonical_number", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("action", new TableInfo.Column("action", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("risk_score", new TableInfo.Column("risk_score", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("summary", new TableInfo.Column("summary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("hit_count", new TableInfo.Column("hit_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("last_user_action", new TableInfo.Column("last_user_action", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("last_judged_at", new TableInfo.Column("last_judged_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPreJudgeCache.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPreJudgeCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPreJudgeCache = new HashSet<TableInfo.Index>(3);
        _indicesPreJudgeCache.add(new TableInfo.Index("index_pre_judge_cache_canonical_number", true, Arrays.asList("canonical_number"), Arrays.asList("ASC")));
        _indicesPreJudgeCache.add(new TableInfo.Index("index_pre_judge_cache_last_judged_at", false, Arrays.asList("last_judged_at"), Arrays.asList("ASC")));
        _indicesPreJudgeCache.add(new TableInfo.Index("index_pre_judge_cache_hit_count", false, Arrays.asList("hit_count"), Arrays.asList("ASC")));
        final TableInfo _infoPreJudgeCache = new TableInfo("pre_judge_cache", _columnsPreJudgeCache, _foreignKeysPreJudgeCache, _indicesPreJudgeCache);
        final TableInfo _existingPreJudgeCache = TableInfo.read(db, "pre_judge_cache");
        if (!_infoPreJudgeCache.equals(_existingPreJudgeCache)) {
          return new RoomOpenHelper.ValidationResult(false, "pre_judge_cache(app.callcheck.mobile.data.localcache.entity.PreJudgeCacheEntry).\n"
                  + " Expected:\n" + _infoPreJudgeCache + "\n"
                  + " Found:\n" + _existingPreJudgeCache);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "3a102a72ddc522b5ff1aab5a4f2f7387", "03a9e521f4530aa76ea03643e7844d0a");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "user_call_records","pre_judge_cache");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `user_call_records`");
      _db.execSQL("DELETE FROM `pre_judge_cache`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
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
    _typeConvertersMap.put(UserCallRecordDao.class, UserCallRecordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PreJudgeCacheDao.class, PreJudgeCacheDao_Impl.getRequiredConverters());
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
  public UserCallRecordDao userCallRecordDao() {
    if (_userCallRecordDao != null) {
      return _userCallRecordDao;
    } else {
      synchronized(this) {
        if(_userCallRecordDao == null) {
          _userCallRecordDao = new UserCallRecordDao_Impl(this);
        }
        return _userCallRecordDao;
      }
    }
  }

  @Override
  public PreJudgeCacheDao preJudgeCacheDao() {
    if (_preJudgeCacheDao != null) {
      return _preJudgeCacheDao;
    } else {
      synchronized(this) {
        if(_preJudgeCacheDao == null) {
          _preJudgeCacheDao = new PreJudgeCacheDao_Impl(this);
        }
        return _preJudgeCacheDao;
      }
    }
  }
}
