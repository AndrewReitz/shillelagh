package shillelagh;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import static shillelagh.internal.ShillelaghInjector.CREATE_TABLE_FUNCTION;
import static shillelagh.internal.ShillelaghInjector.DROP_TABLE_FUNCTION;
import static shillelagh.internal.ShillelaghProcessor.SUFFIX;

public final class Shillelagh {

  private Shillelagh() {
    // No instantiation
  }

  private static final Map<Class<?>, Class<?>> CACHED_CLASSES = new LinkedHashMap<>();

  private static final String TAG = "Shillelagh";
  private static boolean debug = false;

  /** Turn on/off debug logging. */
  public static void setDebug(boolean debug) {
    Shillelagh.debug = debug;
  }

  public static void createTable(SQLiteDatabase database, Class<?> tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(database, shillelagh, CREATE_TABLE_FUNCTION);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to create table for " + tableObject.getName(), e);
    }
  }

  public static void dropTable(SQLiteDatabase database, Class<?> tableObject) {
    try {
      final Class<?> shillelagh = findShillelaghForClass(tableObject);
      getAndExecuteSqlStatement(database, shillelagh, DROP_TABLE_FUNCTION);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new UnableToCreateTableException("Unable to drop table for " + tableObject.getName(), e);
    }
  }

  private static Class<?> findShillelaghForClass(Class<?> clazz) throws ClassNotFoundException {
    Class<?> shillelagh = CACHED_CLASSES.get(clazz);
    if (shillelagh != null) {
      log("Cash Hit!");
      return shillelagh;
    }

    log("Cash Miss");
    final String className = clazz.getName();
    shillelagh = Class.forName(className + SUFFIX);
    CACHED_CLASSES.put(clazz, shillelagh);
    return shillelagh;
  }

  private static void getAndExecuteSqlStatement(SQLiteDatabase database, Class<?> shillelagh, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    final Method method = shillelagh.getMethod(methodName);
    String query = (String) method.invoke(null);
    log("Running Query: %s", query);
    database.execSQL(query);
  }

  private static void log(String format, Object... args) {
    log(String.format(format, args));
  }

  private static void log(String message) {
    if (debug) Log.d(TAG, message);
  }

  public static final class UnableToCreateTableException extends RuntimeException {
    UnableToCreateTableException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
