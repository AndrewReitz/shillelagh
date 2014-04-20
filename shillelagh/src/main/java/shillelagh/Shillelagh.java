package shillelagh;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.CancellationSignal;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static shillelagh.internal.ShillelaghInjector.CREATE_TABLE_FUNCTION;
import static shillelagh.internal.ShillelaghInjector.DELETE_OBJECT_FUNCTION;
import static shillelagh.internal.ShillelaghInjector.DROP_TABLE_FUNCTION;
import static shillelagh.internal.ShillelaghInjector.INSERT_OBJECT_FUNCTION;
import static shillelagh.internal.ShillelaghInjector.MAP_OBJECT_FUNCTION;
import static shillelagh.internal.ShillelaghInjector.UPDATE_ID_FUNCTION;
import static shillelagh.internal.ShillelaghInjector.UPDATE_OBJECT_FUNCTION;
import static shillelagh.internal.ShillelaghProcessor.SUFFIX;

public final class Shillelagh {

  private Shillelagh() {
    // No instantiation
  }

  /**
   * SQL statement to select the id of the last inserted row. Does not end with ; in order to be
   * used with {@link android.database.sqlite.SQLiteDatabase#rawQuery(String, String[])}
   */
  private static final String GET_ID_OF_LAST_INSERTED_ROW_SQL = "SELECT ROWID FROM Book ORDER BY ROWID DESC LIMIT 1";

  private static final Map<Class<?>, Class<?>> CACHED_CLASSES = new LinkedHashMap<>();
  private static final Map<String, Method> CACHED_METHODS = new LinkedHashMap<>();

  private static final String TAG = "Shillelagh";
  private static boolean debug = false;

  private static SQLiteOpenHelper sqliteOpenHelper;

  public static void init(SQLiteOpenHelper soh) {
    sqliteOpenHelper = soh;
  }

  /** Turn on/off debug logging. */
  public static void setDebug(boolean debug) {
    Shillelagh.debug = debug;
  }

  /** Creates the table from the object. The DB must be passed in from the SqliteHelper otherwise an illegal state exception will occur */ // TODO Figure out better solution
  public static void createTable(SQLiteDatabase db, Class<?> tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(db, shillelagh, CREATE_TABLE_FUNCTION);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to create table for " + tableObject.getName(), e); //TODO tableOjbect.getName ! always = to the table name
    }
  }

  /** Drops the table created from the table object. The DB must be passed in from the SqliteHelper otherwise an illegal state exception will occur */
  public static void dropTable(SQLiteDatabase db, Class<?> tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(db, shillelagh, DROP_TABLE_FUNCTION);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to drop table for " + tableObject.getName(), e);
    }
  }

  public static void insert(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh, INSERT_OBJECT_FUNCTION, tableObject);
      final SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
      final Method method = findMethodForClass(shillelagh, UPDATE_ID_FUNCTION, tableObject, db);
      method.invoke(null, tableObject, db);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to insert into " + tableObject.getClass().getName(), e);
    }
  }

  public static void update(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh, UPDATE_OBJECT_FUNCTION, tableObject);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to update " + tableObject.getClass().getName(), e);
    }
  }

  public static void delete(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh, DELETE_OBJECT_FUNCTION, tableObject);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to update " + tableObject.getClass().getName(), e);
    }
  }

  public static void delete(Class<?> tableObject, long id) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh, DELETE_OBJECT_FUNCTION, id);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) { // TODO FIX ALL THESE EXCEPTIONS
      throw new UnableToCreateTableException("Unable to update " + tableObject.getName(), e);
    }
  }

  public static <T extends List> T map(Class<?> tableObject, Cursor cursor) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      final Method mapMethod = findMethodForClass(shillelagh, MAP_OBJECT_FUNCTION, new Class<?>[] {Cursor.class});
      return (T) mapMethod.invoke(null, cursor);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e); // TODO Actually create an exception type...
    }
  }

  public static Cursor query(boolean distinct, String table, String[] columns,
                             String selection, String[] selectionArgs, String groupBy,
                             String having, String orderBy, String limit) {
    return sqliteOpenHelper.getReadableDatabase()
            .query(distinct, table, columns, selection, selectionArgs,
                    groupBy, having, orderBy, limit);
  }

  public static Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
    return sqliteOpenHelper.getReadableDatabase().query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
  }

  public static Cursor query(String table, String[] columns, String selection,
                             String[] selectionArgs, String groupBy, String having,
                             String orderBy) {
    return sqliteOpenHelper.getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
  }

  public static Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
    return sqliteOpenHelper.getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
  }

  public static Cursor rawQuery(String sql) {
    return sqliteOpenHelper.getReadableDatabase().rawQuery(sql, null);
  }

  public static Cursor rawQuery(String sql, String[] selectionArgs) {
    return sqliteOpenHelper.getReadableDatabase().rawQuery(sql, selectionArgs);
  }

  public static Cursor rawQuery(String sql, String[] selectionArgs,
                                CancellationSignal cancellationSignal) {
    return sqliteOpenHelper.getReadableDatabase().rawQuery(sql, selectionArgs, cancellationSignal);
  }

  // TODO REMOVE FROM PROD
  public static SQLiteDatabase getReadableDatabase() {
    return sqliteOpenHelper.getReadableDatabase();
  }

  private static Class<?> findShillelaghForClass(Class<?> clazz) throws ClassNotFoundException {
    Class<?> shillelagh = CACHED_CLASSES.get(clazz);
    if (shillelagh != null) {
      log("Class Cash Hit!");
      return shillelagh;
    }

    log("Class Cash Miss");
    final String className = clazz.getName();
    shillelagh = Class.forName(className + SUFFIX);
    CACHED_CLASSES.put(clazz, shillelagh);
    return shillelagh;
  }

  private static void getAndExecuteSqlStatement(SQLiteDatabase database, Class<?> shillelagh, String methodName, Object... params)
          throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    final Method method = findMethodForClass(shillelagh, methodName, params);
    String sql = (String) method.invoke(null, params);
    executeSql(database, sql);
  }

  private static Class<?>[] getParamTypes(Object... params) {
    Class<?>[] paramTypes = new Class[params.length];
    for (int i = 0; i < params.length; i++) {
      paramTypes[i] = params[i].getClass();
    }
    return paramTypes;
  }

  private static Method findMethodForClass(Class<?> shillelagh, String methodName, Object... params) throws NoSuchMethodException {
    Class<?>[] paramTypes = getParamTypes(params);
    return findMethodForClass(shillelagh, methodName, paramTypes);
  }

  private static Method findMethodForClass(Class<?> shillelagh, String methodName, Class<?>[] paramTypes) throws NoSuchMethodException {
    String fqMethodName = shillelagh.getCanonicalName() + "#" + methodName;
    Method method = CACHED_METHODS.get(fqMethodName);
    if (method != null) {
      log("Method Cache Hit!");
      return method;
    }

    log("Method Cache Miss");
    method = shillelagh.getMethod(methodName, paramTypes);
    CACHED_METHODS.put(fqMethodName, method);
    return method;
  }

  private static void executeSql(SQLiteDatabase database, String query) {
    log("Running SQL: %s", query);
    database.execSQL(query);
  }

  private static void log(String format, Object... args) {
    if (debug) Log.d(TAG, String.format(format, args));
  }

  static final class UnableToCreateTableException extends RuntimeException {
    UnableToCreateTableException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
