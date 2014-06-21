package shillelagh.internal;

import java.util.HashMap;
import java.util.Iterator;

/** Class in charge of creating all the code injected into other classes */
public class ShillelaghInjector {

  // I'm not really happy with the code in here, but then again, I'm writing Java in Strings...

  /**
   * Internal class function names
   */
  public static final String CREATE_TABLE_FUNCTION = "getCreateTableSql";
  public static final String DROP_TABLE_FUNCTION = "getDropTableSql";
  public static final String INSERT_OBJECT_FUNCTION = "getInsertSql";
  public static final String UPDATE_OBJECT_FUNCTION = "getUpdateSql";
  public static final String UPDATE_ID_FUNCTION = "updateColumnId";
  public static final String DELETE_OBJECT_FUNCTION = "getDeleteSql";
  public static final String SELECT_OBJECT_FUNCTION = "select";
  public static final String MAP_OBJECT_FUNCTION = "map";

  /**
   * SQL statement to select the id of the last inserted row. Does not end with ; in order to be
   * used with {@link android.database.sqlite.SQLiteDatabase#rawQuery(String, String[])}
   */
  private static final String GET_ID_OF_LAST_INSERTED_ROW_SQL = "SELECT ROWID FROM %s ORDER BY ROWID DESC LIMIT 1";
  private static final HashMap<String, String> SUPPORTED_CURSOR_METHODS = new HashMap<String, String>();

  static {
    final String cursorFunctionInt = "getInt";
    final String cursorFunctionDouble = "getDouble";
    final String cursorFunctionFloat = "getFloat";
    final String cursorFunctionLong = "getLong";
    final String cursorFunctionShort = "getShort";
    final String cursorFunctionString = "getString";

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
  }

  private final String classPackage;
  private final String className;
  private final String targetClass;
  private final ShillelaghLogger logger;
  private TableObject tableObject;

  ShillelaghInjector(String classPackage, String className, String targetClass, ShillelaghLogger logger) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
    this.logger = logger;
  }

  public void setTable(TableObject tableObject) {
    this.tableObject = tableObject;
  }

  /** Get the fully qualified class name */
  String getFqcn() {
    return classPackage + "." + className;
  }

  /** Create the java functions required for the internal class */
  String brewJava() {
    StringBuilder builder = new StringBuilder();
    builder.append("// Generated code from Shillelagh. Do not modify!\n");
    builder.append("package ").append(classPackage).append(";\n\n");
    builder.append("import android.database.Cursor;\n");
    builder.append("import android.database.DatabaseUtils;\n");
    builder.append("import android.util.Log;\n");
    builder.append("import android.database.sqlite.SQLiteDatabase;\n\n");
    builder.append("import java.util.ArrayList;\n");
    builder.append("import java.util.Date;\n");
    builder.append("import java.util.List;\n\n");
    builder.append("public class ").append(className).append(" {\n");
    emmitCreateTableSql(builder);
    builder.append("\n");
    emmitDropTableSql(builder);
    builder.append("\n");
    emmitInsertSql(builder);
    builder.append("\n");
    emmitUpdateSql(builder);
    builder.append("\n");
    emmitUpdateColumnId(builder);
    builder.append("\n");
    emmitDeleteSqlWithId(builder);
    builder.append("\n");
    emmitDeleteSqlWithObject(builder);
    builder.append("\n");
    emmitMapCursorToObject(builder);
    builder.append("}\n");
    return builder.toString();
  }

  /** Creates the function for getting the create sql string */
  private void emmitCreateTableSql(StringBuilder builder) {
    builder.append("  public static String ").append(CREATE_TABLE_FUNCTION).append("() {\n");
    builder.append("    return \"").append(tableObject.getSchema()).append("\";\n");
    builder.append("  }\n");
  }

  /** Creates the function for getting the drop sql string */
  private void emmitDropTableSql(StringBuilder builder) {
    builder.append("  public static String ").append(DROP_TABLE_FUNCTION).append("() {\n");
    builder.append("    return \"DROP TABLE IF EXISTS ").append(tableObject.getTableName()).append("\";\n");
    builder.append("  }\n");
  }

  /** Creates the function for getting the insert sql string to insert a new value into the database */
  private void emmitInsertSql(StringBuilder builder) {
    builder.append("  public static String ").append(INSERT_OBJECT_FUNCTION).append("(").append(targetClass).append(" element) {\n");
    builder.append("    return \"INSERT INTO ").append(tableObject.getTableName());
    builder.append(" (");

    StringBuilder columns = new StringBuilder();
    StringBuilder values = new StringBuilder();
    Iterator<TableColumn> iterator = tableObject.getColumns().iterator();
    while (iterator.hasNext()) {
      TableColumn column = iterator.next();
      columns.append(column.getColumnName());
      if (column.getSqlType() == SqliteType.TEXT) {
        values.append("'\" + element.").append(column.getColumnName()).append(" + \"'");
      } else if (column.isDate()) {
        values.append("\" + element.").append(column.getColumnName()).append(".getTime() + \"");
      }  else if (column.isBoolean()) {
        values.append("\" + (element.").append(column.getColumnName()).append(" ? \"1\" : \"0\") + \"");
      } else {
        values.append("\" + element.").append(column.getColumnName()).append(" + \"");
      }
      if (iterator.hasNext()) {
        columns.append(", ");
        values.append(", ");
      }
    }

    builder.append(columns.toString()).append(")");
    builder.append(" VALUES (");
    builder.append(values.toString()).append(");\";\n");
    builder.append("  }\n");
  }

  /** Updates the id of the object to the last insert */
  private void emmitUpdateColumnId(StringBuilder builder) {
    // Updates the column id for the last inserted row
    builder.append("  public static void ").append(UPDATE_ID_FUNCTION).append("(").append(targetClass).append(" element, SQLiteDatabase db) {\n");
    builder.append("    long id = DatabaseUtils.longForQuery(db, \"").append(String.format(GET_ID_OF_LAST_INSERTED_ROW_SQL, tableObject.getTableName())).append("\", null);\n");
    builder.append("    element.").append(tableObject.getIdColumnName()).append(" = id;\n");
    builder.append("  }\n");
  }

  /** Creates the function for getting the update sql statement */
  private void emmitUpdateSql(StringBuilder builder) {
    builder.append("  public static String ").append(UPDATE_OBJECT_FUNCTION).append("(").append(targetClass).append(" element) {\n");
    builder.append("    return \"UPDATE ").append(tableObject.getTableName()).append(" SET ");

    StringBuilder columnUpdates = new StringBuilder();
    Iterator<TableColumn> iterator = tableObject.getColumns().iterator();
    while (iterator.hasNext()) {
      TableColumn column = iterator.next();
      if (column.getSqlType() == SqliteType.TEXT) {
        columnUpdates.append(column.getColumnName()).append(" = \'\" + element.").append(column.getColumnName()).append(" + \"\'");
      } else if (column.isDate()) {
        columnUpdates.append(column.getColumnName()).append(" = \" + element.").append(column.getColumnName()).append(".getTime() + \"");
      } else if (column.isBoolean()) {
        columnUpdates.append(column.getColumnName()).append(" = \" + (element.").append(column.getColumnName()).append(" ? \"1\" : \"0\") + \"");
      } else {
        columnUpdates.append(column.getColumnName()).append(" = \" + element.").append(column.getColumnName()).append(" + \"");
      }
      if (iterator.hasNext()) {
        columnUpdates.append(", ");
      }
    }

    builder.append(columnUpdates.toString());
    builder.append(" WHERE ").append(tableObject.getIdColumnName()).append(" = \" + element.").append(tableObject.getIdColumnName()).append(" + \"");
    builder.append("\";\n");
    builder.append("  }\n");
  }

  /** Creates the function for getting the delete sql statement */
  private void emmitDeleteSqlWithObject(StringBuilder builder) {
    builder.append("  public static String ").append(DELETE_OBJECT_FUNCTION).append("(").append(targetClass).append(" element) {\n");
    builder.append("    return ").append(DELETE_OBJECT_FUNCTION).append("(element.").append(tableObject.getIdColumnName()).append(");\n");
    builder.append("  }\n");
  }

  /** Creates the function for getting the delete sql statement */
  private void emmitDeleteSqlWithId(StringBuilder builder) {
    builder.append("  public static String ").append(DELETE_OBJECT_FUNCTION).append("(Long id) {\n");
    builder.append("    return \"DELETE FROM ").append(tableObject.getTableName()).append(" WHERE id = \" + id;");
    builder.append("  }\n");
  }

  /** Creates the function for mapping a cursor to the object after executing a sql statement */
  private void emmitMapCursorToObject(StringBuilder builder) {
    final String idColumnName = tableObject.getIdColumnName();

    builder.append("  public static List<").append(targetClass).append(">").append(MAP_OBJECT_FUNCTION).append("(Cursor cursor) {\n");
    builder.append("    List<").append(targetClass).append("> tableObjects = new ArrayList<>();\n");
    builder.append("    if (cursor.moveToFirst()) {\n"); // can't assume the cursor is already at the front
    builder.append("       while (!cursor.isAfterLast()) {\n");
    builder.append("        ").append(targetClass).append(" tableObject = new ").append(targetClass).append("();\n");
    builder.append("        tableObject.").append(idColumnName).append(" = cursor.getLong(cursor.getColumnIndex(\"").append(idColumnName).append("\"));\n");
    for (TableColumn column : tableObject.getColumns()) {
      String columnName = column.getColumnName();
      if (column.isDate()) {
        builder.append("        tableObject.").append(columnName).append(" = new Date(cursor.").append(getCursorCommand(long.class.getName())).append("(cursor.getColumnIndex(\"").append(columnName).append("\")));\n");
      } else if (column.isBoolean()) {
        builder.append("        tableObject.").append(columnName).append(" = cursor.").append(getCursorCommand(column.getType())).append("(cursor.getColumnIndex(\"").append(columnName).append("\")) == 1;\n");
      } else {
        builder.append("        tableObject.").append(columnName).append(" = cursor.").append(getCursorCommand(column.getType())).append("(cursor.getColumnIndex(\"").append(columnName).append("\"));\n");
      }
    }
    builder.append("        tableObjects.add(tableObject);\n");
    builder.append("        cursor.moveToNext();\n");
    builder.append("      }\n");
    builder.append("    }\n");
    builder.append("  return tableObjects;");
    builder.append("  }\n");
  }

  /**
   * Maps a type to the corresponding Cursor get function. For mapping objects between the database
   * and java.
   */
  private String getCursorCommand(String type) {
    logger.d("getCursorCommand: type = " + type);

    final String returnValue = SUPPORTED_CURSOR_METHODS.get(type);
    return returnValue != null ? returnValue : "getBlob"; // all others are blobs
  }
}
