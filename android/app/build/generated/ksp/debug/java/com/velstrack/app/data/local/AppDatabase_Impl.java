package com.velstrack.app.data.local;

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
import com.velstrack.app.data.local.dao.CallDao;
import com.velstrack.app.data.local.dao.CallDao_AppDatabase_Impl;
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
public final class AppDatabase_Impl extends AppDatabase {
  private volatile CallDao _callDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `calls` (`id` TEXT NOT NULL, `callFingerprint` TEXT NOT NULL, `clientPhoneHash` TEXT NOT NULL, `durationSeconds` INTEGER NOT NULL, `callType` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `isSynced` INTEGER NOT NULL, `connectedAtMillis` INTEGER, `disconnectedAtMillis` INTEGER, `sessionState` TEXT NOT NULL, `callVerified` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_calls_callFingerprint` ON `calls` (`callFingerprint`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'df13013278dac6eb24a78faa5d01688c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `calls`");
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
        final HashMap<String, TableInfo.Column> _columnsCalls = new HashMap<String, TableInfo.Column>(11);
        _columnsCalls.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("callFingerprint", new TableInfo.Column("callFingerprint", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("clientPhoneHash", new TableInfo.Column("clientPhoneHash", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("durationSeconds", new TableInfo.Column("durationSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("callType", new TableInfo.Column("callType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("isSynced", new TableInfo.Column("isSynced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("connectedAtMillis", new TableInfo.Column("connectedAtMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("disconnectedAtMillis", new TableInfo.Column("disconnectedAtMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("sessionState", new TableInfo.Column("sessionState", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCalls.put("callVerified", new TableInfo.Column("callVerified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCalls = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCalls = new HashSet<TableInfo.Index>(1);
        _indicesCalls.add(new TableInfo.Index("index_calls_callFingerprint", true, Arrays.asList("callFingerprint"), Arrays.asList("ASC")));
        final TableInfo _infoCalls = new TableInfo("calls", _columnsCalls, _foreignKeysCalls, _indicesCalls);
        final TableInfo _existingCalls = TableInfo.read(db, "calls");
        if (!_infoCalls.equals(_existingCalls)) {
          return new RoomOpenHelper.ValidationResult(false, "calls(com.velstrack.app.data.local.entity.CallEntity).\n"
                  + " Expected:\n" + _infoCalls + "\n"
                  + " Found:\n" + _existingCalls);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "df13013278dac6eb24a78faa5d01688c", "4847edb08d47fdafbc4b449cf05cc1c0");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "calls");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `calls`");
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
    _typeConvertersMap.put(CallDao.class, CallDao_AppDatabase_Impl.getRequiredConverters());
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
  public CallDao callDao() {
    if (_callDao != null) {
      return _callDao;
    } else {
      synchronized(this) {
        if(_callDao == null) {
          _callDao = new CallDao_AppDatabase_Impl(this);
        }
        return _callDao;
      }
    }
  }
}
