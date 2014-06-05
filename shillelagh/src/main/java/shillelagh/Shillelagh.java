package shillelagh;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
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

  private static final Map<Class<?>, Class<?>> CACHED_CLASSES = new LinkedHashMap<>();
  private static final Map<String, Method> CACHED_METHODS = new LinkedHashMap<>();

  private static final String TAG = Shillelagh.class.getSimpleName();
  private static boolean debug = false;

  private final SQLiteOpenHelper sqliteOpenHelper;

  public Shillelagh(SQLiteOpenHelper sqliteOpenHelper) {
    this.sqliteOpenHelper = sqliteOpenHelper;
  }

  /** Turn on/off debug logging. */
  public static void setDebug(boolean debug) {
    Shillelagh.debug = debug;
  }

  /**
   * Creates the table from the object. The DB must be passed in from the SQLiteOpenHelper
   * otherwise an illegal state exception will occur
   */
  public static void createTable(SQLiteDatabase db, Class<?> tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(db, shillelagh, CREATE_TABLE_FUNCTION);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to create table for " + tableObject +
          ". Are you missing @Table annotation?", e);
    }
  }

  /**
   * Drops the table created from the table object. The DB must be passed in from
   * the SQLiteOpenHelper otherwise an illegal state exception will occur
   */
  public static void dropTable(SQLiteDatabase db, Class<?> tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(db, shillelagh, DROP_TABLE_FUNCTION);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to drop table for " + tableObject +
          ". Are you missing @Table annotation?", e);
    }
  }

  public static <T extends List<M>, M> T map(Class<? extends M> tableObject, Cursor cursor) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      final Method mapMethod = findMethodForClass(shillelagh, MAP_OBJECT_FUNCTION,
          new Class<?>[]{Cursor.class});
      //noinspection unchecked
      return (T) mapMethod.invoke(null, cursor);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to map cursor to " + tableObject, e);
    }
  }

  /** Insert the object into the table */
  public void insert(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh,
          INSERT_OBJECT_FUNCTION, tableObject);
      final SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
      final Method method = findMethodForClass(shillelagh, UPDATE_ID_FUNCTION, tableObject, db);
      method.invoke(null, tableObject, db);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to insert into " + tableObject.getClass().getName() +
          ". Are you missing @Table annotation?", e);
    }
  }

  public void update(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh,
          UPDATE_OBJECT_FUNCTION, tableObject);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to update " + tableObject.getClass().getName()
          + ". Are you missing @Table annotation?", e);
    }
  }

  public void delete(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh,
          DELETE_OBJECT_FUNCTION, tableObject);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to update " + tableObject.getClass().getName(), e);
    }
  }

  public void delete(final Class<?> tableObject, final long id) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh,
          DELETE_OBJECT_FUNCTION, id);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to delete from " + tableObject + " with id = " + id, e);
    }
  }

  public Cursor query(boolean distinct, String table, String[] columns,
                      String selection, String[] selectionArgs, String groupBy,
                      String having, String orderBy, String limit) {
    return sqliteOpenHelper.getReadableDatabase()
        .query(distinct, table, columns, selection, selectionArgs,
            groupBy, having, orderBy, limit);
  }

  public <T extends List<M>, M> T query(Class<? extends M> tableObject, boolean distinct, String table, String[] columns,
                                        String selection, String[] selectionArgs, String groupBy,
                                        String having, String orderBy, String limit) {
    Cursor results = sqliteOpenHelper.getReadableDatabase()
        .query(distinct, table, columns, selection, selectionArgs,
            groupBy, having, orderBy, limit);

    return map(tableObject, results);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public Cursor query(boolean distinct, String table, String[] columns,
                      String selection, String[] selectionArgs, String groupBy,
                      String having, String orderBy, String limit,
                      CancellationSignal cancellationSignal) {
    return sqliteOpenHelper.getReadableDatabase()
        .query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy,
            limit, cancellationSignal);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public <T extends List<M>, M> T query(Class<? extends M> tableObject, boolean distinct, String table, String[] columns,
                                        String selection, String[] selectionArgs, String groupBy,
                                        String having, String orderBy, String limit,
                                        CancellationSignal cancellationSignal) {
    Cursor results = sqliteOpenHelper.getReadableDatabase()
        .query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy,
            limit, cancellationSignal);
    return map(tableObject, results);
  }

  public Cursor query(String table, String[] columns, String selection,
                      String[] selectionArgs, String groupBy, String having,
                      String orderBy) {
    return sqliteOpenHelper.getReadableDatabase().query(table, columns, selection,
        selectionArgs, groupBy, having, orderBy);
  }

  public <T extends List<M>, M> T query(Class<? extends M> tableObject, String table, String[] columns, String selection,
                                        String[] selectionArgs, String groupBy, String having,
                                        String orderBy) {
    final Cursor results = sqliteOpenHelper.getReadableDatabase().query(table, columns, selection,
        selectionArgs, groupBy, having, orderBy);
    return map(tableObject, results);
  }

  public Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
                      String groupBy, String having, String orderBy, String limit) {
    return sqliteOpenHelper.getReadableDatabase().query(table, columns, selection, selectionArgs,
        groupBy, having, orderBy, limit);
  }

  public <T extends List<M>, M> T query(Class<? extends M> tableObject, String table, String[] columns, String selection, String[] selectionArgs,
                                        String groupBy, String having, String orderBy, String limit) {
    final Cursor results = sqliteOpenHelper.getReadableDatabase().query(table, columns, selection, selectionArgs,
        groupBy, having, orderBy, limit);

    return map(tableObject, results);
  }

  public Cursor rawQuery(String sql) {
    return sqliteOpenHelper.getReadableDatabase().rawQuery(sql, null);
  }

  public <T extends List<M>, M> T rawQuery(Class<? extends M> tableObject, String sql) {
    return this.rawQuery(tableObject, sql, null);
  }

  public Cursor rawQuery(String sql, String[] selectionArgs) {
    return sqliteOpenHelper.getReadableDatabase().rawQuery(sql, selectionArgs);
  }

  public <T extends List<M>, M> T rawQuery(Class<? extends M> tableObject, String sql, String[] selectionArgs) {
    final Cursor result = sqliteOpenHelper.getReadableDatabase().rawQuery(sql, selectionArgs);
    return map(tableObject, result);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public Cursor rawQuery(String sql, String[] selectionArgs,
                         CancellationSignal cancellationSignal) {
    return sqliteOpenHelper.getReadableDatabase().rawQuery(sql, selectionArgs, cancellationSignal);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public <T extends List<M>, M> T rawQuery(Class<? extends M> tableObject, String sql, String[] selectionArgs,
                                           CancellationSignal cancellationSignal) {
    final Cursor results = sqliteOpenHelper.getReadableDatabase().rawQuery(sql, selectionArgs, cancellationSignal);
    return map(tableObject, results);
  }

  public SQLiteDatabase getReadableDatabase() {
    return sqliteOpenHelper.getReadableDatabase();
  }

  public SQLiteDatabase getWritableDatabase() {
    return sqliteOpenHelper.getWritableDatabase();
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

  private static void getAndExecuteSqlStatement(SQLiteDatabase database,
                                                Class<?> shillelagh,
                                                String methodName,
                                                Object... params
  ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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

  private static Method findMethodForClass(Class<?> shillelagh,
                                           String methodName, Object... params
  ) throws NoSuchMethodException {
    Class<?>[] paramTypes = getParamTypes(params);
    return findMethodForClass(shillelagh, methodName, paramTypes);
  }

  private static Method findMethodForClass(Class<?> shillelagh, String methodName,
                                           Class<?>[] paramTypes) throws NoSuchMethodException {
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
}
