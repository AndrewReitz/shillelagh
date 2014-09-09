/*
 * Copyright ${year} Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import rx.Observable;
import rx.Subscriber;

public final class Shillelagh {

  /**
   * Internal class function names
   */
  public static final String $$SUFFIX = "$$Shillelagh";
  public static final String $$CREATE_TABLE_FUNCTION = "createTable";
  public static final String $$DROP_TABLE_FUNCTION = "dropTable";
  public static final String $$INSERT_OBJECT_FUNCTION = "insertObject";
  public static final String $$UPDATE_OBJECT_FUNCTION = "updateObject";
  public static final String $$UPDATE_ID_FUNCTION = "updateColumnId";
  public static final String $$DELETE_OBJECT_FUNCTION = "deleteObject";
  public static final String $$GET_OBJECT_BY_ID = "getById";
  public static final String $$MAP_OBJECT_FUNCTION = "map";
  public static final String $$MAP_SINGLE_FUNCTION = "singleMap";

  static final boolean HAS_RX_JAVA = hasRxJavaOnClasspath();

  private static final Map<Class<?>, Class<?>> CACHED_CLASSES
      = new LinkedHashMap<Class<?>, Class<?>>();
  private static final Map<String, Method> CACHED_METHODS = new LinkedHashMap<String, Method>();

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
  public static void createTable(SQLiteDatabase db, Class<?> tableClass) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableClass);
      executeMethod(shillelagh, $$CREATE_TABLE_FUNCTION, db);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to create table for " + tableClass
          + ". Are you missing @Table annotation?", e);
    }
  }

  /**
   * Drops the table created from the table object. The DB must be passed in from
   * the SQLiteOpenHelper otherwise an illegal state exception will occur
   */
  public static void dropTable(SQLiteDatabase db, Class<?> tableClass) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableClass);
      executeMethod(shillelagh, $$DROP_TABLE_FUNCTION, db);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to drop table for " + tableClass
          + ". Are you missing @Table annotation?", e);
    }
  }

  /** Insert the object into the table */
  public void insert(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      executeMethod(shillelagh, $$INSERT_OBJECT_FUNCTION, tableObject,
          sqliteOpenHelper.getWritableDatabase());
      final SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
      final Method method = findMethodForClass(shillelagh, $$UPDATE_ID_FUNCTION, tableObject, db);
      method.invoke(null, tableObject, db);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to insert into " + tableObject.getClass().getName()
          + ". Did you forget to call Shillelagh.createTable "
          + "or are you missing @Table annotation?", e);
    }
  }

  /**
   * Updates an Object. This object MUST have it's id field populated
   * with the id of the row you are trying to update.
   *
   * @param tableObject The object that will update a row in the table
   */
  public void update(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      executeMethod(shillelagh, $$UPDATE_OBJECT_FUNCTION, tableObject,
          sqliteOpenHelper.getWritableDatabase());
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to update " + tableObject.getClass().getName()
          + ". Are you missing @Table annotation?", e);
    }
  }

  /**
   * Deletes the object from the table in which it resides. This does the look up based off of
   * the ID of the object. Passing in an object with out and ID will not delete other rows.
   *
   * @param tableObject table object with ID field populated.
   */
  public void delete(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      executeMethod(shillelagh, $$DELETE_OBJECT_FUNCTION, tableObject,
          sqliteOpenHelper.getWritableDatabase());
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to update " + tableObject.getClass().getName(), e);
    }
  }

  /**
   * Deletes a row in the table with the corresponding ID.
   *
   * @param tableClass The class which was used to generate the table that
   *                   you want to delete the corresponding row out of.
   * @param id         the id of the row you want to match
   */
  public void delete(final Class<?> tableClass, final long id) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableClass);
      executeMethod(
          shillelagh, $$DELETE_OBJECT_FUNCTION, id,
          sqliteOpenHelper.getWritableDatabase());
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to delete from " + tableClass + " with id = " + id, e);
    }
  }

  /**
   * Maps data from the cursor to it's corresponding model object.
   *
   * @param tableClass Class the data from the cursor should be mapped to. This class must have the
   * @param cursor     Cursor of data pulled from the tableClass's generated table.
   * @return List of tableClass objects mapped from the cursor.
   * @see shillelagh.Table annotation on it
   */
  @SuppressWarnings("unchecked")
  public <T extends List<M>, M> T map(Class<? extends M> tableClass, Cursor cursor) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableClass);
      final Method mapMethod = findMethodForClass(shillelagh, $$MAP_OBJECT_FUNCTION,
          /* cursor is interface so can't resolve automatically */
          new Class<?>[]{Cursor.class, SQLiteDatabase.class});
      return (T) mapMethod.invoke(null, cursor, getReadableDatabase());
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to map cursor to " + tableClass, e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T singleMap(Class<? extends T> tableClass, Cursor cursor) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableClass);
      final Method mapMethod = findMethodForClass(shillelagh, $$MAP_SINGLE_FUNCTION,
          /* cursor is interface so can't resolve automatically */
          new Class<?>[]{Cursor.class, SQLiteDatabase.class});
      return (T) mapMethod.invoke(null, cursor, getReadableDatabase());
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException("Unable to map cursor to " + tableClass, e);
    }
  }

  /** Get the table name of the class */
  public static String getTableName(Class<?> clazz) {
    return clazz.getCanonicalName().replace(".", "_");
  }

  // Shillelagh Selectors

  public <T> QueryBuilder<T> createQuery(Class<? extends T> tableObject) {
    return new QueryBuilder<T>(tableObject, this);
  }

  // End Shillelagh Selectors

  // Android Wrappers

  /**
   * Equivalent to calling {@link SQLiteDatabase#query(boolean, String, String[], String, String[],
   * String, String, String, String)}
   */
  public Cursor query(boolean distinct, String table, String[] columns,
                      String selection, String[] selectionArgs, String groupBy,
                      String having, String orderBy, String limit) {
    return sqliteOpenHelper.getReadableDatabase()
        .query(distinct, table, columns, selection, selectionArgs,
            groupBy, having, orderBy, limit);
  }

  /**
   * Equivalent to calling
   * {@link SQLiteDatabase#query(boolean, String, String[], String, String[], String, String,
   * String, String)} then passing the result to
   * {@link Shillelagh#map(Class, android.database.Cursor)}
   */
  public <T extends List<M>, M> T query(Class<? extends M> tableClass, boolean distinct,
                                        String[] columns, String selection, String[] selectionArgs,
                                        String groupBy, String having, String orderBy, String limit
  ) {
    Cursor results = sqliteOpenHelper.getReadableDatabase()
        .query(distinct, getTableName(tableClass), columns, selection, selectionArgs,
            groupBy, having, orderBy, limit);

    return map(tableClass, results);
  }

  public <T> Observable<T> createQuery(final Class<? extends T> tableClass, final boolean distinct,
                                 final String[] columns, final String selection,
                                 final String[] selectionArgs, final String groupBy,
                                 final String having, final String orderBy,
                                 final String limit
  ) {
    if (!HAS_RX_JAVA) {
      throw new RuntimeException(
          "RxJava not available! Add RxJava to your build to use this feature");
    }

    final Shillelagh shillelagh = this;
    return getObservable(tableClass, new QueryBuilder.CursorLoader() {
      @Override public Cursor getCursor() {
        return shillelagh.query(distinct, getTableName(tableClass), columns, selection,
            selectionArgs, groupBy, having, orderBy, limit);
      }
    });
  }

  /**
   * The equivalent of calling
   * {@link SQLiteDatabase#query(boolean, String, String[], String, String[], String, String,
   * String, String, android.os.CancellationSignal)}
   * <p/>
   * Only available for API 16+
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public Cursor query(boolean distinct, String table, String[] columns,
                      String selection, String[] selectionArgs, String groupBy,
                      String having, String orderBy, String limit,
                      CancellationSignal cancellationSignal) {
    return sqliteOpenHelper.getReadableDatabase()
        .query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy,
            limit, cancellationSignal);
  }

  /**
   * The equivalent of calling
   * {@link SQLiteDatabase#query(boolean, String, String[], String, String[], String, String,
   * String, String, android.os.CancellationSignal)}
   * and then calling {@link Shillelagh#map(Class, android.database.Cursor)} on the result
   * <p/>
   * Only available for API 16+
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public <T extends List<M>, M> T query(Class<? extends M> tableObject, boolean distinct,
                                        String[] columns, String selection, String[] selectionArgs,
                                        String groupBy, String having, String orderBy, String limit,
                                        CancellationSignal cancellationSignal) {
    Cursor results = sqliteOpenHelper.getReadableDatabase()
        .query(distinct, getTableName(tableObject), columns, selection, selectionArgs, groupBy,
            having, orderBy, limit, cancellationSignal);
    return map(tableObject, results);
  }

  /**
   * Equivalent to calling {@link SQLiteDatabase#query(String, String[], String, String[], String,
   * String, String)}
   */
  public Cursor query(String table, String[] columns, String selection,
                      String[] selectionArgs, String groupBy, String having,
                      String orderBy) {
    return sqliteOpenHelper.getReadableDatabase().query(table, columns, selection,
        selectionArgs, groupBy, having, orderBy);
  }

  /**
   * Equivalent to calling {@link SQLiteDatabase#query(String, String[], String, String[], String,
   * String, String)} and then calling {@link Shillelagh#map(Class, android.database.Cursor)}
   * on the result.
   */
  public <T extends List<M>, M> T query(Class<? extends M> tableObject, String[] columns,
                                        String selection, String[] selectionArgs, String groupBy,
                                        String having, String orderBy) {
    final Cursor results = query(getTableName(tableObject), columns, selection,
        selectionArgs, groupBy, having, orderBy);
    return map(tableObject, results);
  }

  public <T> Observable<T> createQuery(final Class<? extends T> tableObject, final String[] columns,
                                 final String selection, final String[] selectionArgs, final String groupBy,
                                 final String having, final String orderBy) {
    if (!HAS_RX_JAVA) {
      throw new RuntimeException(
          "RxJava not available! Add RxJava to your build to use this feature");
    }

    final Shillelagh shillelagh = this;
    return getObservable(tableObject, new QueryBuilder.CursorLoader() {
      @Override public Cursor getCursor() {
        return shillelagh.query(getTableName(tableObject), columns, selection,
            selectionArgs, groupBy, having, orderBy);
      }
    });
  }

  /**
   * Equivalent to calling {@link SQLiteDatabase#query(String, String[], String, String[], String,
   * String, String, String)}
   */
  public Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
                      String groupBy, String having, String orderBy, String limit) {
    return sqliteOpenHelper.getReadableDatabase().query(table, columns, selection, selectionArgs,
        groupBy, having, orderBy, limit);
  }

  /**
   * Equivalent to calling {@link SQLiteDatabase#query(String, String[], String, String[], String,
   * String, String, String)}
   * and then calling {@link Shillelagh#map(Class, android.database.Cursor)} on the result.
   */
  public <T extends List<M>, M> T query(Class<? extends M> tableObject, String[] columns,
                                        String selection, String[] selectionArgs, String groupBy,
                                        String having, String orderBy, String limit
  ) {
    final Cursor results = query(getTableName(tableObject), columns, selection,
        selectionArgs, groupBy, having, orderBy, limit);

    return map(tableObject, results);
  }

  public <T> Observable<T> createQuery(final Class<? extends T> tableObject, final String[] columns,
                                 final String selection, final String[] selectionArgs,
                                 final String groupBy, final String having,
                                 final String orderBy, final String limit
  ) {
    if (!HAS_RX_JAVA) {
      throw new RuntimeException(
          "RxJava not available! Add RxJava to your build to use this feature");
    }
    final Shillelagh shillelagh = this;
    return getObservable(tableObject, new QueryBuilder.CursorLoader() {
      @Override public Cursor getCursor() {
        return shillelagh.query(getTableName(tableObject), columns, selection,
            selectionArgs, groupBy, having, orderBy, limit);
      }
    });
  }

  /**
   * Equivalent to calling {@link SQLiteDatabase#rawQuery(String, String[])} where the selection
   * args are null.
   */
  public Cursor rawQuery(String sql, Object... sqlArgs) {
    return sqliteOpenHelper.getReadableDatabase()
        .rawQuery(formatString(sql, sqlArgs), null);
  }

  /**
   * Equivalent to calling {@link SQLiteDatabase#rawQuery(String, String[])} where the selection
   * args are null and then passing the result to
   * {@link Shillelagh#map(Class, android.database.Cursor)}
   */
  public <T extends List<M>, M> T rawQuery(Class<? extends M> tableObject, String sql,
                                           Object... sqlArgs) {
    return this.rawQuery(tableObject, sql, null, sqlArgs);
  }

  public <T> Observable<T> createQuery(Class<? extends T> tableObject, final String sql,
                                       final Object... sqlArgs) {
    if (!HAS_RX_JAVA) {
      throw new RuntimeException(
          "RxJava not available! Add RxJava to your build to use this feature");
    }

    final Shillelagh shillelagh = this;
    return getObservable(tableObject, new QueryBuilder.CursorLoader() {
      @Override public Cursor getCursor() {
        return shillelagh.rawQuery(sql, sqlArgs);
      }
    });
  }

  /** Equivalent to calling {@link SQLiteDatabase#rawQuery(String, String[])} */
  public Cursor rawQuery(String sql, String[] selectionArgs, Object... sqlArgs) {
    return sqliteOpenHelper.getReadableDatabase()
        .rawQuery(formatString(sql, sqlArgs), selectionArgs);
  }

  /**
   * Equivalent to calling {@link SQLiteDatabase#rawQuery(String, String[])}
   * and then passing the result to {@link Shillelagh#map(Class, android.database.Cursor)}
   */
  public <T extends List<M>, M> T rawQuery(Class<? extends M> tableObject, String sql,
                                           String[] selectionArgs, Object... sqlArgs
  ) {
    final Cursor result = sqliteOpenHelper.getReadableDatabase()
        .rawQuery(formatString(sql, sqlArgs), selectionArgs);
    return map(tableObject, result);
  }

  public <T> Observable<T> createQuery(Class<? extends T> tableObject, final String sql,
                                    final String[] selectionArgs, final Object... sqlArgs) {
    if (!HAS_RX_JAVA) {
      throw new RuntimeException(
          "RxJava not available! Add RxJava to your build to use this feature");
    }

    final Shillelagh shillelagh = this;
    return getObservable(tableObject, new QueryBuilder.CursorLoader() {
      @Override public Cursor getCursor() {
        return shillelagh.rawQuery(sql, selectionArgs, sqlArgs);
      }
    });
  }

  /**
   * Equivalent to calling {@link SQLiteDatabase#rawQuery(String, String[], CancellationSignal)}
   * <p/>
   * Only available for API 16+
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public Cursor rawQuery(String sql, String[] selectionArgs,
                         CancellationSignal cancellationSignal, Object... sqlArgs) {
    return sqliteOpenHelper.getReadableDatabase()
        .rawQuery(formatString(sql, sqlArgs), selectionArgs, cancellationSignal);
  }

  /**
   * Equivalent to calling {@link SQLiteDatabase#rawQuery(String, String[], CancellationSignal)}
   * and then passing the result to {@link Shillelagh#map(Class, android.database.Cursor)}
   * <p/>
   * Only available for API 16+
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
  public <T extends List<M>, M> T rawQuery(Class<? extends M> tableObject, String sql,
                                           String[] selectionArgs,
                                           CancellationSignal cancellationSignal, Object... sqlArgs
  ) {
    final Cursor results = sqliteOpenHelper.getReadableDatabase()
        .rawQuery(formatString(sql, sqlArgs), selectionArgs, cancellationSignal);
    return map(tableObject, results);
  }

  public SQLiteDatabase getReadableDatabase() {
    return sqliteOpenHelper.getReadableDatabase();
  }

  public SQLiteDatabase getWritableDatabase() {
    return sqliteOpenHelper.getWritableDatabase();
  }

  // End Android Wrappers

  /** Finds the internal Shillelagh class written to the clazz object by ShillelaghInjector */
  private static Class<?> findShillelaghForClass(Class<?> clazz) throws ClassNotFoundException {
    Class<?> shillelagh = CACHED_CLASSES.get(clazz);
    if (shillelagh != null) {
      log("Class Cache Hit!");
      return shillelagh;
    }

    log("Class Cache Miss");
    final String className = clazz.getName();
    shillelagh = Class.forName(className + $$SUFFIX);
    CACHED_CLASSES.put(clazz, shillelagh);
    return shillelagh;
  }

  /** Gets internal method and then executes it */
  private static void executeMethod(Class<?> shillelagh, String methodName, Object... params)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    final Method method = findMethodForClass(shillelagh, methodName, params);
    method.invoke(null, params);
  }

  /**
   * Gets the Class objects of a list of parameters, this is used for figuring out the parameters
   * types of a method
   */
  private static Class<?>[] getParamTypes(Object... params) {
    Class<?>[] paramTypes = new Class[params.length];
    for (int i = 0; i < params.length; i++) {
      paramTypes[i] = params[i].getClass();
    }
    return paramTypes;
  }

  /** Finds a method in a class using reflection */
  private static Method findMethodForClass(Class<?> shillelagh,
                                           String methodName, Object... params
  ) throws NoSuchMethodException {
    Class<?>[] paramTypes = getParamTypes(params);
    return findMethodForClass(shillelagh, methodName, paramTypes);
  }

  /** Finds a method in a class using reflection */
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
    log("Running SQL Statement: %s", query);
    database.execSQL(query);
  }

  private static void log(String format, Object... args) {
    if (debug) Log.d(TAG, String.format(format, args));
  }

  private static String formatString(String message, Object... args) {
    // If no varargs are supplied, treat it as a request to log the string without formatting.
    return args.length == 0 ? message : String.format(message, args);
  }

  private static boolean hasRxJavaOnClasspath() {
    try {
      Class.forName("rx.Observable");
      return true;
    } catch (ClassNotFoundException ignored) {
    }
    return false;
  }

  <T> Observable<T> getObservable(final Class<? extends T> tableObject,
                                  final QueryBuilder.CursorLoader cursorLoader) {
    return Observable.create(new Observable.OnSubscribe<T>() {
      @Override public void call(Subscriber<? super T> subscriber) {
        final Cursor cursor = cursorLoader.getCursor();
        if (cursor.moveToFirst()) {
          while (!cursor.isAfterLast()) {
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(singleMap(tableObject, cursor));
              cursor.moveToNext();
            }
          }
        }
      }
    });
  }
}
