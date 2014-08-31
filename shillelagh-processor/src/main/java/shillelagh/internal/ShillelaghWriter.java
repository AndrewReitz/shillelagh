package shillelagh.internal;

import com.squareup.javawriter.JavaWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Generated;

import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.FINAL;
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

  private final ShillelaghLogger logger;

  private TableObject tableObject;

  ShillelaghWriter(String classPackage, String className, String targetClass,
                   ShillelaghLogger logger
  ) {
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
  void brewJava(Writer writer) throws IOException {
    logger.d("brewJava");
    JavaWriter javaWriter = new JavaWriter(writer);

    javaWriter.emitSingleLineComment("Generated code from Shillelagh. Do not modify!")
        .emitPackage(classPackage)
        .emitImports("android.content.ContentValues", "android.database.Cursor", // Knows nothing of android types
            "android.database.DatabaseUtils", "android.database.sqlite.SQLiteDatabase")
        .emitImports(ByteArrayInputStream.class, ByteArrayOutputStream.class, IOException.class,
            ObjectInputStream.class, ObjectOutputStream.class, LinkedList.class, Date.class,
            List.class)
        .emitAnnotation(Generated.class, "Shillelagh")
        .beginType(className, "class", EnumSet.of(PUBLIC, FINAL));

    emitGetId(javaWriter);
    emitCreateTable(javaWriter);
    emitDropTable(javaWriter);
    emitInsert(javaWriter);
    emitUpdate(javaWriter);
    emitUpdateColumnId(javaWriter);
    emitDeleteWithId(javaWriter);
    emitDeleteWithObject(javaWriter);
    emitMapCursorToObject(javaWriter);
    emitSelectById(javaWriter);
    emitByteArraySerialization(javaWriter);
    javaWriter.endType();
  }

  /** Create a way to get an id for foreign keys */
  private void emitGetId(JavaWriter javaWriter) throws IOException {
    logger.d("emitGetId");
    javaWriter.beginMethod("long", GET_ID_FUNCTION, EnumSet.of(PUBLIC, STATIC), targetClass, "value")
        .emitStatement("return value.%s", tableObject.getIdColumnName())
        .endMethod();
  }

  /** Creates the function for creating the table */
  private void emitCreateTable(JavaWriter javaWriter) throws IOException {
    logger.d("emitCreateTable");
    javaWriter.beginMethod("void", $$CREATE_TABLE_FUNCTION, EnumSet.of(PUBLIC, STATIC), "SQLiteDatabase", "db")
        .emitStatement("db.execSQL(\"%s\")", tableObject.getSchema())
        .endMethod();
  }

  /** Creates the function dropping the table */
  private void emitDropTable(JavaWriter javaWriter) throws IOException {
    logger.d("emitDropTable");
    javaWriter.beginMethod("void", $$DROP_TABLE_FUNCTION, EnumSet.of(PUBLIC, STATIC), "SQLiteDatabase", "db")
        .emitStatement("db.execSQL(\"DROP TABLE IF EXISTS %s\"", tableObject.getTableName())
        .endMethod();
  }

  /** Creates the function for inserting a new value into the database */
  private void emitInsert(JavaWriter javaWriter) throws IOException {
    logger.d("emitInsert");
    javaWriter.beginMethod("void", $$INSERT_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC), targetClass, "element", "SQLiteDatabase", "db")
        .emitStatement("ContentValues values = new ContentValues()");
    for (TableColumn column : tableObject.getColumns()) {
      String columnName = column.getColumnName();
      if (column.getSqlType() == SqliteType.BLOB && !column.isByteArray()) {
        javaWriter.emitStatement("values.put(\"%s\", %s(element.%s))", columnName, SERIALIZE_FUNCTION, columnName);
      } else if (column.isOneToOne()) {
        javaWriter.emitStatement("values.put(\"%s\", %s%s.%s(element.%s))", columnName, column.getType(), $$SUFFIX, GET_ID_FUNCTION, columnName);
      } else if (column.isDate()) {
        javaWriter.emitStatement("values.put(\"%s\", element.%s.getTime())", columnName, columnName);
      } else {
        javaWriter.emitStatement("values.put(\"%s\", element.%s)", columnName, columnName);
      }
    }
    javaWriter.emitStatement("db.insert(\"%s\", null, values)", tableObject.getTableName())
        .endMethod();
  }

  /** Updates the id of the object to the last insert */
  private void emitUpdateColumnId(JavaWriter javaWriter) throws IOException {
    logger.d("emitUpdateColumnId");
    // Updates the column id for the last inserted row
    javaWriter.beginMethod("void", $$UPDATE_ID_FUNCTION, EnumSet.of(PUBLIC, STATIC), targetClass, "element", "SQLiteDatabase", "db")
        .emitStatement("long id = DatabaseUtils.longForQuery(db, \"%s\", null)", String.format(GET_ID_OF_LAST_INSERTED_ROW_SQL, tableObject.getTableName()))
        .emitStatement("element.%s = id", tableObject.getIdColumnName())
        .endMethod();
  }

  /** Creates the function for updating an object */
  private void emitUpdate(JavaWriter javaWriter) throws IOException {
    logger.d("emitUpdate");
    javaWriter.beginMethod("void", $$UPDATE_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC), targetClass, "element", "SQLiteDatabase", "db")
        .emitStatement("ContentValues values = new ContentValues()");
    for (TableColumn column : tableObject.getColumns()) {
      String columnName = column.getColumnName();
      if (column.getSqlType() == SqliteType.BLOB && !column.isByteArray()) {
        javaWriter.emitStatement("values.put(\"%s\", %s(element.%s))", columnName, SERIALIZE_FUNCTION, columnName);
      } else if (column.isOneToOne()) {
        javaWriter.emitStatement("values.put(\"%\", %s%s.%s(element.%s))", columnName, column.getType(), $$SUFFIX, GET_ID_FUNCTION, columnName);
      } else if (column.isDate()) {
        javaWriter.emitStatement("values.put(\"%s\", element.%s.getTime())");
      } else {
        javaWriter.emitStatement("values.put(\"%s\", element.%s)", columnName, columnName);
      }
    }
    final String idColumnName = tableObject.getIdColumnName();
    javaWriter.emitStatement("db.update(\"%s\", values, \"%s = \" + element.%s, null)", tableObject.getTableName(), idColumnName, idColumnName);
    javaWriter.endMethod();
  }

  /** Creates the function for deleting an object from the table */
  private void emitDeleteWithObject(JavaWriter javaWriter) throws IOException {
    logger.d("emitDeleteWithObject");
    javaWriter.beginMethod("void", $$DELETE_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC), targetClass, "element", "SQLiteDatabase", "db")
        .emitStatement("%s(element.%s, db)", $$DELETE_OBJECT_FUNCTION, targetClass)
        .endMethod();
  }

  /** Creates the function for deleting an object by id */
  private void emitDeleteWithId(JavaWriter javaWriter) throws IOException {
    logger.d("emitDeleteWithId");
    javaWriter.beginMethod("void", $$DELETE_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC), "Long id", "SQLiteDatabase db")
        .emitStatement("db.delete(\"%s\", \"%s = \" + id, null)", tableObject.getTableName(), tableObject.getIdColumnName())
        .endMethod();
  }

  /** Creates function for getting an object by value */
  private void emitSelectById(JavaWriter javaWriter) throws IOException {
    logger.d("emitSelectById");
    javaWriter.beginMethod(targetClass, $$GET_OBJECT_BY_ID, EnumSet.of(PUBLIC, STATIC), "long id", "SQLiteDatabase db")
        .emitStatement("return %s(db.rawQuery(\"SELECT * FROM %s WHERE %s  = id\", null), db).get(0)", $$MAP_OBJECT_FUNCTION, tableObject.getTableName(), tableObject.getIdColumnName())
        .endMethod();
  }

  // TODO fix this. This is just a mess...

  /** Creates the function for mapping a cursor to the object after executing a sql statement */
  private void emitMapCursorToObject(JavaWriter javaWriter) throws IOException {
    logger.d("emitMapCursorToObject");
    final String tableName = targetClass;

    javaWriter.beginMethod("List<" + tableName + ">", $$MAP_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC), "Cursor", "cursor", "SQLiteDatabase", "db")
        .emitStatement("List<%s> tableObjects = new LinkedList<%s>()", tableName, tableName)
        .beginControlFlow("if (cursor.moveToFirst())") // can't assume the cursor is already at the front
        .beginControlFlow("while (!cursor.isAfterLast())")
        .emitStatement("%s tableObject = new %s()", tableName, targetClass);

    for (TableColumn column : tableObject.getColumns()) {
      String columnName = column.getColumnName();
      if (column.isDate()) {
        javaWriter.emitStatement("tableObject.%s = new Date(cursor.%s(cursor.getColumnIndex(\"%s\")))", columnName, CursorFunctions.get(long.class.getName()), columnName);
      } else if (column.isOneToOne()) {
        javaWriter.emitStatement("tableObject.%s = %s%s.%s(cursor.%s(cursor.getColumnIndex(\"%s)), db)", columnName, column.getType(), $$SUFFIX, $$GET_OBJECT_BY_ID, CursorFunctions.get(Long.class.getName()), columnName);
      } else if (column.isBoolean()) {
        javaWriter.emitStatement("tableObject.%s = cursor.%s(cursor.getColumnIndex(\"%s\")) == 1");
      } else if (column.getSqlType() == SqliteType.BLOB) {
        if (column.isByteArray()) {
          javaWriter.emitStatement("tableObject.%s = cursor.%s(cursor.getColumnIndex(\"%s\"))", columnName, CursorFunctions.get(column.getType()), columnName);
        } else {
          javaWriter.emitStatement("tableObject.%s = %s(cursor.%s(cursor.getColumnIndex(\"%s\")));", columnName, DESERIALIZE_FUNCTION, CursorFunctions.get(column.getType()), columnName);
        }
      } else {
        javaWriter.emitStatement("tableObject.%s = cursor.%s(cursor.getColumnIndex(\"%s\"))", columnName, CursorFunctions.get(column.getType()), columnName);
      }
    }
    javaWriter.emitStatement("tableObjects.add(tableObject)")
        .emitStatement("cursor.moveToNext()")
        .endControlFlow()
        .endControlFlow()
        .emitStatement("return tableObjects")
        .endMethod();
  }

  /** Creates functions for serialization to and from byte arrays */
  private void emitByteArraySerialization(JavaWriter javaWriter) throws IOException {
    logger.d("emitByteArraySerialization");
    javaWriter.beginMethod("<K> byte[]", SERIALIZE_FUNCTION, EnumSet.of(DEFAULT, STATIC), "K object")
        .beginControlFlow("try")
        .emitStatement("ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()")
        .emitStatement("ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)")
        .emitStatement("objectOutputStream.writeObject(object)")
        .emitStatement("return byteArrayOutputStream.toByteArray()")
        .nextControlFlow("catch (IOException e)")
        .emitStatement("throw new RuntimeException(e)")
        .endControlFlow()
        .endMethod()
        .beginControlFlow("<K> K", DESERIALIZE_FUNCTION, EnumSet.of(DEFAULT, STATIC), "byte[] bytes")
        .beginControlFlow("try")
        .emitStatement("ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)")
        .emitStatement("ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)")
        .emitStatement("return (K) objectInputStream.readObject()")
        .nextControlFlow("catch (IOException e)")
        .emitStatement("throw new RuntimeException(e)")
        .nextControlFlow("catch (ClassNotFoundException e)")
        .emitStatement("throw new RuntimeException(e)")
        .endControlFlow()
        .endMethod();
    logger.d("exit emitByteArraySerialization");
  }
}
