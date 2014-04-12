package shillelagh;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static shillelagh.internal.ShillelaghInjector.CREATE_TABLE_FUNCTION;
import static shillelagh.internal.ShillelaghInjector.DROP_TABLE_FUNCTION;
import static shillelagh.internal.ShillelaghInjector.INSERT_OBJECT_FUNCTION;
import static shillelagh.internal.ShillelaghProcessor.SUFFIX;

public final class Shillelagh {

  private Shillelagh() {
    // No instantiation
  }

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

  public static void createTable(Class<?> tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh, CREATE_TABLE_FUNCTION);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to create table for " + tableObject.getName(), e); //TODO tableOjbect.getName ! always = to the table name
    }
  }

  public static void dropTable(Class<?> tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh, DROP_TABLE_FUNCTION);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to drop table for " + tableObject.getName(), e);
    }
  }

  public static void insert(Object tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject.getClass());
      final Class<?>[] paramTypes = getParamTypes(tableObject);
      final Method method = findMethodForClass(shillelagh, INSERT_OBJECT_FUNCTION, paramTypes);
      String sql = (String) method.invoke(null, tableObject);
      new SQLiteStatement(sqliteOpenHelper.getWritableDatabase(), sql, null);
      getAndExecuteSqlStatement(sqliteOpenHelper.getWritableDatabase(), shillelagh, INSERT_OBJECT_FUNCTION, tableObject);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to insert into " + tableObject.getClass().getName(), e);
    }
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
    Class<?>[] paramTypes = getParamTypes(params);
    final Method method = findMethodForClass(shillelagh, methodName, paramTypes);
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

  public static final class UnableToCreateTableException extends RuntimeException {
    UnableToCreateTableException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
