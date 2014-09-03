package shillelagh.internal;

import com.google.common.collect.Lists;
import com.squareup.javawriter.JavaWriter;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.lang.model.element.Element;

import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static shillelagh.Shillelagh.$$CREATE_TABLE_FUNCTION;
import static shillelagh.Shillelagh.$$DELETE_OBJECT_FUNCTION;
import static shillelagh.Shillelagh.$$DROP_TABLE_FUNCTION;
import static shillelagh.Shillelagh.$$GET_OBJECT_BY_ID;
import static shillelagh.Shillelagh.$$INSERT_OBJECT_FUNCTION;
import static shillelagh.Shillelagh.$$MAP_OBJECT_FUNCTION;
import static shillelagh.Shillelagh.$$SUFFIX;
import static shillelagh.Shillelagh.$$UPDATE_ID_FUNCTION;
import static shillelagh.Shillelagh.$$UPDATE_OBJECT_FUNCTION;

class TableObject {

  protected static final String SERIALIZE_FUNCTION = "serialize";
  protected static final String DESERIALIZE_FUNCTION = "deserialize";
  private static final String GET_ID_FUNCTION = "getId";

  /** Used as a template to create a new table */
  private static final String CREATE_TABLE_DEFAULT = "CREATE TABLE %s "
      + "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s);";

  /**
   * SQL statement to select the id of the last inserted row. Does not end with ; in order to be
   * used with SQLiteDatabase#rawQuery(String, String[])
   */
  private static final String GET_ID_OF_LAST_INSERTED_ROW_SQL
      = "SELECT ROWID FROM %s ORDER BY ROWID DESC LIMIT 1";

  private final Element element;
  private String idColumnName;


  private final List<TableColumn> columns = Lists.newLinkedList();

  TableObject(Element element) {
    this.element = element;
  }

  void setIdColumnName(String idColumnName) {
    this.idColumnName = idColumnName;
  }

  String getIdColumnName() {
    return idColumnName;
  }

  void addColumn(TableColumn column) {
    columns.add(column);
  }

  String getTableName() {
    return element.getSimpleName().toString();
  }

  String getTargetClass() {
    return element.toString();
  }

  /** Get table schema */
  String getSchema() {
    StringBuilder sb = new StringBuilder();
    Iterator<TableColumn> iterator = columns.iterator();
    while (iterator.hasNext()) {
      sb.append(iterator.next());
      if (iterator.hasNext()) {
        sb.append(", ");
      }
    }

    return String.format(
            CREATE_TABLE_DEFAULT,
            getTableName(),
            idColumnName,
            sb.toString()
    );
  }

  void emitGetId(JavaWriter javaWriter) throws IOException {
    javaWriter.beginMethod(
        "long", GET_ID_FUNCTION, EnumSet.of(PUBLIC, STATIC), getTargetClass(), "value")
        .emitStatement("return value.%s", idColumnName)
        .endMethod();
  }

  void emitCreateTable(JavaWriter javaWriter) throws IOException {
    javaWriter.beginMethod(
        "void", $$CREATE_TABLE_FUNCTION, EnumSet.of(PUBLIC, STATIC), "SQLiteDatabase", "db")
        .emitStatement("db.execSQL(\"%s\")", getSchema())
        .endMethod();
  }

  void emitDropTable(JavaWriter javaWriter) throws IOException {
    javaWriter.beginMethod(
        "void", $$DROP_TABLE_FUNCTION, EnumSet.of(PUBLIC, STATIC), "SQLiteDatabase", "db")
        .emitStatement("db.execSQL(\"DROP TABLE IF EXISTS %s\")", getTableName())
        .endMethod();
  }

  void emitInsert(JavaWriter javaWriter) throws IOException {
    javaWriter.beginMethod("void", $$INSERT_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        getTargetClass(), "element", "SQLiteDatabase", "db")
        .emitStatement("ContentValues values = new ContentValues()");
    for (TableColumn column : columns) {
      String columnName = column.getColumnName();
      if (column.getSqlType() == SqliteType.BLOB && !column.isByteArray()) {
        javaWriter.emitStatement("values.put(\"%s\", %s(element.%s))", columnName,
            SERIALIZE_FUNCTION, columnName);
      } else if (column.isOneToOne()) {
        javaWriter.emitStatement("values.put(\"%s\", %s%s.%s(element.%s))", columnName,
            column.getType(), $$SUFFIX, GET_ID_FUNCTION, columnName);
      } else if (column.isDate()) {
        javaWriter.emitStatement(
            "values.put(\"%s\", element.%s.getTime())", columnName, columnName);
      } else {
        javaWriter.emitStatement("values.put(\"%s\", element.%s)", columnName, columnName);
      }
    }
    javaWriter.emitStatement("db.insert(\"%s\", null, values)", getTableName())
        .endMethod();
  }

  void emitUpdate(JavaWriter javaWriter) throws IOException {
    javaWriter.beginMethod("void", $$UPDATE_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        getTargetClass(), "element", "SQLiteDatabase", "db")
        .emitStatement("ContentValues values = new ContentValues()");
    for (TableColumn column : columns) {
      String columnName = column.getColumnName();
      if (column.getSqlType() == SqliteType.BLOB && !column.isByteArray()) {
        javaWriter.emitStatement("values.put(\"%s\", %s(element.%s))", columnName,
            SERIALIZE_FUNCTION, columnName);
      } else if (column.isOneToOne()) {
        javaWriter.emitStatement("values.put(\"%s\", %s%s.%s(element.%s))", columnName,
            column.getType(), $$SUFFIX, GET_ID_FUNCTION, columnName);
      } else if (column.isDate()) {
        javaWriter.emitStatement("values.put(\"%s\", element.%s.getTime())", columnName,
            columnName);
      } else {
        javaWriter.emitStatement("values.put(\"%s\", element.%s)", columnName, columnName);
      }
    }
    javaWriter.emitStatement("db.update(\"%s\", values, \"%s = \" + element.%s, null)",
        getTableName(), idColumnName, idColumnName);
    javaWriter.endMethod();
  }

  void emitUpdateColumnId(JavaWriter javaWriter) throws IOException {
    // Updates the column id for the last inserted row
    javaWriter.beginMethod("void", $$UPDATE_ID_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        getTargetClass(), "element", "SQLiteDatabase", "db")
        .emitStatement("long id = DatabaseUtils.longForQuery(db, \"%s\", null)",
            String.format(GET_ID_OF_LAST_INSERTED_ROW_SQL, getTableName()))
        .emitStatement("element.%s = id", idColumnName)
        .endMethod();
  }

  void emitDeleteWithId(JavaWriter javaWriter) throws IOException {
    javaWriter.beginMethod("void", $$DELETE_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC), "Long",
        "id", "SQLiteDatabase", "db")
        .emitStatement("db.delete(\"%s\", \"%s = \" + id, null)", getTableName(), idColumnName)
        .endMethod();
  }

  void emitDeleteWithObject(JavaWriter javaWriter) throws IOException {
    javaWriter.beginMethod("void", $$DELETE_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        getTargetClass(), "element", "SQLiteDatabase", "db")
        .emitStatement(
            "%s(element.%s, db)", $$DELETE_OBJECT_FUNCTION, idColumnName)
        .endMethod();
  }

  void emitMapCursorToObject(JavaWriter javaWriter) throws IOException {
    final String targetClass = getTargetClass();

    javaWriter.beginMethod("List<" + targetClass + ">", $$MAP_OBJECT_FUNCTION,
        EnumSet.of(PUBLIC, STATIC), "Cursor", "cursor", "SQLiteDatabase", "db")
        .emitStatement("List<%s> tableObjects = new LinkedList<%s>()", targetClass, targetClass)
        .beginControlFlow("if (cursor.moveToFirst())")
        .beginControlFlow("while (!cursor.isAfterLast())")
        .emitStatement("%s tableObject = new %s()", targetClass, getTargetClass())
        .emitStatement("tableObject.%s = cursor.getLong(cursor.getColumnIndex(\"%s\"))",
            idColumnName, idColumnName);

    for (TableColumn column : columns) {
      String columnName = column.getColumnName();
      if (column.isDate()) {
        javaWriter.emitStatement(
            "tableObject.%s = new Date(cursor.%s(cursor.getColumnIndex(\"%s\")))", columnName,
            CursorFunctions.get(long.class.getName()), columnName);
      } else if (column.isOneToOne()) {
        javaWriter.emitStatement(
            "tableObject.%s = %s%s.%s(cursor.%s(cursor.getColumnIndex(\"%s\")), db)",
            columnName, column.getType(), $$SUFFIX, $$GET_OBJECT_BY_ID,
            CursorFunctions.get(Long.class.getName()), columnName);
      } else if (column.isBoolean()) {
        javaWriter.emitStatement("tableObject.%s = cursor.%s(cursor.getColumnIndex(\"%s\")) == 1",
            columnName, CursorFunctions.get(column.getType()), columnName);
      } else if (column.getSqlType() == SqliteType.BLOB) {
        if (column.isByteArray()) {
          javaWriter.emitStatement("tableObject.%s = cursor.%s(cursor.getColumnIndex(\"%s\"))",
              columnName, CursorFunctions.get(column.getType()), columnName);
        } else {
          javaWriter.emitStatement(
              "tableObject.%s = %s(cursor.%s(cursor.getColumnIndex(\"%s\")));", columnName,
              DESERIALIZE_FUNCTION, CursorFunctions.get(column.getType()), columnName);
        }
      } else {
        javaWriter.emitStatement("tableObject.%s = cursor.%s(cursor.getColumnIndex(\"%s\"))",
            columnName, CursorFunctions.get(column.getType()), columnName);
      }
    }
    javaWriter.emitStatement("tableObjects.add(tableObject)")
        .emitStatement("cursor.moveToNext()")
        .endControlFlow()
        .endControlFlow()
        .emitStatement("return tableObjects")
        .endMethod();
  }

  void emitSelectById(JavaWriter javaWriter) throws IOException {
    javaWriter.beginMethod(getTargetClass(), $$GET_OBJECT_BY_ID, EnumSet.of(PUBLIC, STATIC), "long",
        "id", "SQLiteDatabase", "db")
        .emitStatement(
            "return %s(db.rawQuery(\"SELECT * FROM %s WHERE %s  = id\", null), db).get(0)",
            $$MAP_OBJECT_FUNCTION, getTableName(), idColumnName)
        .endMethod();
  }

  @Override public String toString() {
    return getSchema();
  }
}
