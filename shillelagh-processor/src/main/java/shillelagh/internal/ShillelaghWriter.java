package shillelagh.internal;

import shillelagh.Shillelagh;

import static shillelagh.Shillelagh.$$CREATE_TABLE_FUNCTION;
import static shillelagh.Shillelagh.$$DELETE_OBJECT_FUNCTION;
import static shillelagh.Shillelagh.$$DROP_TABLE_FUNCTION;
import static shillelagh.Shillelagh.$$GET_OBJECT_BY_ID;
import static shillelagh.Shillelagh.$$INSERT_OBJECT_FUNCTION;
import static shillelagh.Shillelagh.$$MAP_OBJECT_FUNCTION;
import static shillelagh.Shillelagh.$$SUFFIX;
import static shillelagh.Shillelagh.$$UPDATE_ID_FUNCTION;
import static shillelagh.Shillelagh.$$UPDATE_OBJECT_FUNCTION;

/** Class in charge of creating all the code injected into other classes */
public final class ShillelaghWriter {

  // I'm not really happy with the code in here, but then again, I'm writing Java in Strings...

  private static final String SERIALIZE_FUNCTION = "serialize";
  private static final String DESERIALIZE_FUNCTION = "deserialize";
  private static final String GET_ID_FUNCTION = "getId";

  /**
   * SQL statement to select the id of the last inserted row. Does not end with ; in order to be
   * used with SQLiteDatabase#rawQuery(String, String[])
   */
  private static final String GET_ID_OF_LAST_INSERTED_ROW_SQL = "SELECT ROWID FROM %s ORDER BY ROWID DESC LIMIT 1";

  private final String classPackage;
  private final String className;
  private final String targetClass;
  private TableObject tableObject;

  ShillelaghWriter(String classPackage, String className, String targetClass) {
    this.classPackage = classPackage;
    this.className = className;
    this.targetClass = targetClass;
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
    builder.append("import android.content.ContentValues;\n");
    builder.append("import android.database.Cursor;\n");
    builder.append("import android.database.DatabaseUtils;\n");
    builder.append("import android.database.sqlite.SQLiteDatabase;\n\n");
    builder.append("import java.io.ByteArrayInputStream;\n");
    builder.append("import java.io.ByteArrayOutputStream;\n");
    builder.append("import java.io.IOException;\n");
    builder.append("import java.io.ObjectInputStream;\n");
    builder.append("import java.io.ObjectOutputStream;\n\n");
    builder.append("import java.util.LinkedList;\n");
    builder.append("import java.util.Date;\n");
    builder.append("import java.util.List;\n\n");
    builder.append("public final class ").append(className).append(" {\n");
    emitGetId(builder);
    builder.append("\n");
    emmitCreateTable(builder);
    builder.append("\n");
    emmitDropTable(builder);
    builder.append("\n");
    emmitInsert(builder);
    builder.append("\n");
    emmitUpdate(builder);
    builder.append("\n");
    emmitUpdateColumnId(builder);
    builder.append("\n");
    emmitDeleteWithId(builder);
    builder.append("\n");
    emmitDeleteWithObject(builder);
    builder.append("\n");
    emmitMapCursorToObject(builder);
    builder.append("\n");
    emmitSelectById(builder);
    builder.append("\n");
    emmitByteArraySerialization(builder);
    builder.append("}\n");
    return builder.toString();
  }

  /** Create a way to get an id for foreign keys */
  private void emitGetId(StringBuilder builder) {
    builder.append("  public static long ").append(GET_ID_FUNCTION).append("(").append(targetClass).append(" value) {\n");
    builder.append("    return value.").append(tableObject.getIdColumnName()).append(";\n");
    builder.append("  }\n");
  }

  /** Creates the function for creating the table*/
  private void emmitCreateTable(StringBuilder builder) {
    builder.append("  public static void ").append($$CREATE_TABLE_FUNCTION).append("(SQLiteDatabase db) {\n");
    builder.append("    db.execSQL(\"").append(tableObject.getSchema()).append("\");\n");
    builder.append("  }\n");
  }

  /** Creates the function dropping the table */
  private void emmitDropTable(StringBuilder builder) {
    builder.append("  public static void ").append($$DROP_TABLE_FUNCTION).append("(SQLiteDatabase db) {\n");
    builder.append("    db.execSQL(\"DROP TABLE IF EXISTS ").append(tableObject.getTableName()).append("\");\n");
    builder.append("  }\n");
  }

  /** Creates the function for inserting a new value into the database */
  private void emmitInsert(StringBuilder builder) {
    builder.append("  public static void ").append($$INSERT_OBJECT_FUNCTION).append("(").append(targetClass).append(" element, SQLiteDatabase db) {\n");
    builder.append("    ContentValues values = new ContentValues();\n");
    for (TableColumn column : tableObject.getColumns()) {
      String columnName = column.getColumnName();
      if (column.getSqlType() == SqliteType.BLOB && !column.isByteArray()) {
        builder.append("    values.put(\"").append(columnName).append("\", ").append(SERIALIZE_FUNCTION).append("(element.").append(columnName).append("));\n");
      } else if (column.isOneToOne()) {
        builder.append("    values.put(\"").append(columnName).append("\", ").append(column.getType()).append($$SUFFIX).append(".").append(GET_ID_FUNCTION).append("(element.").append(columnName).append("));\n");
      } else if (column.isDate()) {
        builder.append("    values.put(\"").append(columnName).append("\", element.").append(columnName).append(".getTime());\n");
      } else {
        builder.append("    values.put(\"").append(columnName).append("\", element.").append(columnName).append(");\n");
      }
    }
    builder.append("    db.insert(\"").append(tableObject.getTableName()).append("\", null, values);\n");
    builder.append("  }\n");
  }

  /** Updates the id of the object to the last insert */
  private void emmitUpdateColumnId(StringBuilder builder) {
    // Updates the column id for the last inserted row
    builder.append("  public static void ").append($$UPDATE_ID_FUNCTION).append("(").append(targetClass).append(" element, SQLiteDatabase db) {\n");
    builder.append("    long id = DatabaseUtils.longForQuery(db, \"").append(String.format(GET_ID_OF_LAST_INSERTED_ROW_SQL, tableObject.getTableName())).append("\", null);\n");
    builder.append("    element.").append(tableObject.getIdColumnName()).append(" = id;\n");
    builder.append("  }\n");
  }

  /** Creates the function for updating an object */
  private void emmitUpdate(StringBuilder builder) {
    builder.append("  public static void ").append($$UPDATE_OBJECT_FUNCTION).append("(").append(targetClass).append(" element, SQLiteDatabase db) {\n");
    builder.append("    ContentValues values = new ContentValues();\n");
    for (TableColumn column : tableObject.getColumns()) {
      String columnName = column.getColumnName();
      if (column.getSqlType() == SqliteType.BLOB && !column.isByteArray()) {
        builder.append("    values.put(\"").append(columnName).append("\", ").append(SERIALIZE_FUNCTION).append("(element.").append(columnName).append("));\n");
      } else if (column.isOneToOne()) {
        builder.append("    values.put(\"").append(columnName).append("\", ").append(column.getType()).append($$SUFFIX).append(".").append(GET_ID_FUNCTION).append("(element.").append(columnName).append("));\n");
      } else if (column.isDate()) {
        builder.append("    values.put(\"").append(columnName).append("\", element.").append(columnName).append(".getTime());\n");
      } else {
        builder.append("    values.put(\"").append(columnName).append("\", element.").append(columnName).append(");\n");
      }
    }
    final String idColumnName = tableObject.getIdColumnName();
    builder.append("    db.update(\"").append(tableObject.getTableName()).append("\", values, \"").append(idColumnName).append(" = \" + element.").append(idColumnName).append(", null);\n");
    builder.append("  }\n");
  }

  /** Creates the function for deleting an object from the table */
  private void emmitDeleteWithObject(StringBuilder builder) {
    builder.append("  public static void ").append($$DELETE_OBJECT_FUNCTION).append("(").append(targetClass).append(" element, SQLiteDatabase db) {\n");
    builder.append("    ").append($$DELETE_OBJECT_FUNCTION).append("(element.").append(tableObject.getIdColumnName()).append(", db);\n");
    builder.append("  }\n");
  }

  /** Creates the function for deleting an object by id */
  private void emmitDeleteWithId(StringBuilder builder) {
    builder.append("  public static void ").append($$DELETE_OBJECT_FUNCTION).append("(Long id, SQLiteDatabase db) {\n");
    builder.append("    db.delete(\"").append(tableObject.getTableName()).append("\", \"").append(tableObject.getIdColumnName()).append(" = \" + id, null);\n");
    builder.append("  }\n");
  }

  /** Creates function for getting an object by value */
  private void emmitSelectById(StringBuilder builder) {
    builder.append("  public static ").append(targetClass).append(" ").append($$GET_OBJECT_BY_ID).append("(long id, SQLiteDatabase db) {\n");
    builder.append("    return ").append($$MAP_OBJECT_FUNCTION).append("(db.rawQuery(\"SELECT * FROM ").append(tableObject.getTableName()).append(" WHERE ").append(tableObject.getIdColumnName()).append(" = id\", null), db).get(0);\n");
    builder.append("  }\n");
  }

  // TODO fix this. This is just a mess...

  /** Creates the function for mapping a cursor to the object after executing a sql statement */
  private void emmitMapCursorToObject(StringBuilder builder) {
    final String idColumnName = tableObject.getIdColumnName();
    final String tableName = targetClass;

    builder.append("  public static List<").append(tableName).append("> ").append($$MAP_OBJECT_FUNCTION).append("(Cursor cursor, SQLiteDatabase db) {\n");
    builder.append("    List<").append(tableName).append("> tableObjects = new LinkedList<").append(tableName).append(">();\n");
    builder.append("    if (cursor.moveToFirst()) {\n"); // can't assume the cursor is already at the front
    builder.append("       while (!cursor.isAfterLast()) {\n");
    builder.append("        ").append(tableName).append(" tableObject = new ").append(targetClass).append("();\n");
    builder.append("        tableObject.").append(idColumnName).append(" = cursor.getLong(cursor.getColumnIndex(\"").append(idColumnName).append("\"));\n");
    for (TableColumn column : tableObject.getColumns()) {
      String columnName = column.getColumnName();
      if (column.isDate()) {
        builder.append("        tableObject.").append(columnName).append(" = new Date(cursor.").append(CursorFunctions.get(long.class.getName())).append("(cursor.getColumnIndex(\"").append(columnName).append("\")));\n");
      } else if (column.isOneToOne()) {
        builder.append("        tableObject.").append(columnName).append(" = ").append(column.getType()).append($$SUFFIX).append(".").append($$GET_OBJECT_BY_ID).append("(").append("cursor.").append(CursorFunctions.get(Long.class.getName())).append("(cursor.getColumnIndex(\"").append(columnName).append("\")), db);\n");
      } else if (column.isBoolean()) {
        builder.append("        tableObject.").append(columnName).append(" = cursor.").append(CursorFunctions.get(column.getType())).append("(cursor.getColumnIndex(\"").append(columnName).append("\")) == 1;\n");
      } else if (column.getSqlType() == SqliteType.BLOB) {
        if (column.isByteArray()) {
          builder.append("        tableObject.").append(columnName).append(" = cursor.").append(CursorFunctions.get(column.getType())).append("(cursor.getColumnIndex(\"").append(columnName).append("\"));\n");
        } else {
          builder.append("        tableObject.").append(columnName).append(" = ").append(DESERIALIZE_FUNCTION).append("(cursor.").append(CursorFunctions.get(column.getType())).append("(cursor.getColumnIndex(\"").append(columnName).append("\")));\n");
        }
      } else {
        builder.append("        tableObject.").append(columnName).append(" = cursor.").append(CursorFunctions.get(column.getType())).append("(cursor.getColumnIndex(\"").append(columnName).append("\"));\n");
      }
    }
    builder.append("        tableObjects.add(tableObject);\n");
    builder.append("        cursor.moveToNext();\n");
    builder.append("      }\n");
    builder.append("    }\n");
    builder.append("  return tableObjects;");
    builder.append("  }\n");
  }

  /** Creates functions for serialization to and from byte arrays */
  private void emmitByteArraySerialization(StringBuilder builder) {
    builder.append("  public static <K> byte[] ").append(SERIALIZE_FUNCTION).append("(K object) {\n");
    builder.append("    try {\n");
    builder.append("      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();\n");
    builder.append("      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);\n");
    builder.append("      objectOutputStream.writeObject(object);\n");
    builder.append("      return byteArrayOutputStream.toByteArray();\n");
    builder.append("    } catch (IOException e) {\n");
    builder.append("      throw new RuntimeException(e);\n");
    builder.append("    }\n");
    builder.append("  }\n\n");
    builder.append("  public static <K> K ").append(DESERIALIZE_FUNCTION).append("(byte[] bytes) {\n");
    builder.append("    try {\n");
    builder.append("      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);\n");
    builder.append("      ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);\n");
    builder.append("      return (K) objectInputStream.readObject();\n");
    builder.append("    } catch (IOException e) {\n");
    builder.append("      throw new RuntimeException(e);\n");
    builder.append("    } catch (ClassNotFoundException e) {\n");
    builder.append("      throw new RuntimeException(e);\n");
    builder.append("    }\n");
    builder.append("  }\n");
  }
}
