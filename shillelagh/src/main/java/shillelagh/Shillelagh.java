package shillelagh;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import shillelagh.internal.ShillelaghInjector;
import shillelagh.internal.ShillelaghProcessor;

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
            final Method method = shillelagh.getMethod(ShillelaghInjector.CREATE_TABLE_FUNCTION);
            database.execSQL((String) method.invoke(null));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UnableToCreateTableException("Unable to create table for " + tableObject.getName(), e);
        }
    }

    private static Class<?> findShillelaghForClass(Class<?> clazz) throws ClassNotFoundException {
        Class<?> shillelagh = CACHED_CLASSES.get(clazz);
        if (shillelagh != null) {
            return shillelagh;
        }
        final String className = clazz.getName();
        shillelagh = Class.forName(className + ShillelaghProcessor.SUFFIX);
        CACHED_CLASSES.put(clazz, shillelagh);
        return shillelagh;
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
