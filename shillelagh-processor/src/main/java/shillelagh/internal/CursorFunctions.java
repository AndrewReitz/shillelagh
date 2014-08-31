package shillelagh.internal;

import java.util.HashMap;

final class CursorFunctions {
  private static final String BLOB = "blob";
  private static final HashMap<String, String> SUPPORTED_CURSOR_METHODS = new HashMap<String, String>();

  static {
    final String cursorFunctionInt = "getInt";
    final String cursorFunctionDouble = "getDouble";
    final String cursorFunctionFloat = "getFloat";
    final String cursorFunctionLong = "getLong";
    final String cursorFunctionShort = "getShort";
    final String cursorFunctionString = "getString";
    final String cursorFunctionBlob = "getBlob";

    SUPPORTED_CURSOR_METHODS.put(int.class.getName(), cursorFunctionInt);
    SUPPORTED_CURSOR_METHODS.put(Integer.class.getName(), cursorFunctionInt);
    SUPPORTED_CURSOR_METHODS.put(boolean.class.getName(), cursorFunctionInt);
    SUPPORTED_CURSOR_METHODS.put(Boolean.class.getName(), cursorFunctionInt);
    SUPPORTED_CURSOR_METHODS.put(double.class.getName(), cursorFunctionDouble);
    SUPPORTED_CURSOR_METHODS.put(Double.class.getName(), cursorFunctionDouble);
    SUPPORTED_CURSOR_METHODS.put(float.class.getName(), cursorFunctionFloat);
    SUPPORTED_CURSOR_METHODS.put(Float.class.getName(), cursorFunctionFloat);
    SUPPORTED_CURSOR_METHODS.put(long.class.getName(), cursorFunctionLong);
    SUPPORTED_CURSOR_METHODS.put(Long.class.getName(), cursorFunctionLong);
    SUPPORTED_CURSOR_METHODS.put(short.class.getName(), cursorFunctionShort);
    SUPPORTED_CURSOR_METHODS.put(Short.class.getName(), cursorFunctionShort);
    SUPPORTED_CURSOR_METHODS.put(String.class.getName(), cursorFunctionString);
    SUPPORTED_CURSOR_METHODS.put(BLOB, cursorFunctionBlob);
  }

  /**
   * Maps a type to the corresponding Cursor get function. For mapping objects between the database
   * and java. If a type is not found getBlob is returned
   */
  public static String get(String type) {
    final String returnValue = SUPPORTED_CURSOR_METHODS.get(type);
    return returnValue != null ? returnValue : SUPPORTED_CURSOR_METHODS.get(BLOB);
  }

  private CursorFunctions() {
    throw new UnsupportedOperationException();
  }
}
