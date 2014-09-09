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

package shillelagh.internal;

import com.google.common.collect.Lists;
import com.squareup.javawriter.JavaWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;

import shillelagh.Shillelagh;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static shillelagh.Shillelagh.$$CREATE_TABLE_FUNCTION;
import static shillelagh.Shillelagh.$$DELETE_OBJECT_FUNCTION;
import static shillelagh.Shillelagh.$$DROP_TABLE_FUNCTION;
import static shillelagh.Shillelagh.$$GET_OBJECT_BY_ID;
import static shillelagh.Shillelagh.$$INSERT_OBJECT_FUNCTION;
import static shillelagh.Shillelagh.$$MAP_OBJECT_FUNCTION;
import static shillelagh.Shillelagh.$$MAP_SINGLE_FUNCTION;
import static shillelagh.Shillelagh.$$SUFFIX;
import static shillelagh.Shillelagh.$$UPDATE_ID_FUNCTION;
import static shillelagh.Shillelagh.$$UPDATE_OBJECT_FUNCTION;

class TableObject {

  private static final String SERIALIZE_FUNCTION = "serialize";
  private static final String DESERIALIZE_FUNCTION = "deserialize";
  private static final String GET_ID_FUNCTION = "getId";
  private static final String SELECT_ALL_FUNCTION = "selectAll";
  private static final String PARENT_INSERT_FUNCTION = "parentInsert";
  private static final String INSERT_ONE_TO_ONE = "insertOneToOne";

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
  private final String classPackage;
  private final String className;
  private final ShillelaghLogger logger;

  private boolean isChildTable = false;
  private String idColumnName;

  private final List<TableColumn> columns = Lists.newLinkedList();

  TableObject(Element element, String classPackage, String className, ShillelaghLogger logger) {
    this.element = element;
    this.classPackage = classPackage;
    this.className = className;
    this.logger = logger;
  }

  void setIdColumnName(String idColumnName) {
    this.idColumnName = idColumnName;
  }

  String getIdColumnName() {
    return idColumnName;
  }

  void setIsChildTable(boolean isChildTable) {
    this.isChildTable = isChildTable;
  }

  Element getOriginatingElement() {
    return element;
  }

  void addColumn(TableColumn column) {
    columns.add(column);
  }

  String getTableName() {
    return element.toString().replace(".", "_");
  }

  String getTargetClass() {
    return element.toString();
  }

  /** Get table schema */
  private String getSchema() {
    StringBuilder sb = new StringBuilder();
    Iterator<TableColumn> iterator = columns.iterator();
    while (iterator.hasNext()) {
      TableColumn column = iterator.next();
      if (column.isOneToMany()) {
        if (!iterator.hasNext()) {
          // remove the extra ", " after one to many
          int length = sb.length();
          sb.replace(length - 2, length, "");
        }
        continue;
      }
      sb.append(column);
      if (iterator.hasNext()) {
        sb.append(", ");
      }
    }

    return String.format(CREATE_TABLE_DEFAULT, getTableName(), idColumnName, sb.toString());
  }

  /** Get the fully qualified class name */
  String getFqcn() {
    return classPackage + "." + className;
  }

  /** Create the java functions required for the internal class */
  void brewJava(Writer writer) throws IOException {
    logger.d("brewJava");
    JavaWriter javaWriter = new JavaWriter(writer);
    javaWriter.setCompressingTypes(false);

    javaWriter.emitSingleLineComment("Generated code from Shillelagh. Do not modify!")
        .emitPackage(classPackage)
        /* Knows nothing of android types */
        .emitImports("android.content.ContentValues", "android.database.Cursor",
            "android.database.DatabaseUtils", "android.database.sqlite.SQLiteDatabase")
        .emitImports(ByteArrayInputStream.class, ByteArrayOutputStream.class, IOException.class,
            ObjectInputStream.class, ObjectOutputStream.class, LinkedList.class, Date.class,
            List.class)
        .beginType(className, "class", EnumSet.of(PUBLIC, FINAL));

    if (this.isChildTable) {
      emitParentInsert(javaWriter);
      emitSelectAll(javaWriter);
    }
    emitInsert(javaWriter);
    emitOneToOneInsert(javaWriter);
    emitGetId(javaWriter);
    emitCreateTable(javaWriter);
    emitDropTable(javaWriter);
    emitUpdate(javaWriter);
    emitUpdateColumnId(javaWriter);
    emitDeleteWithId(javaWriter);
    emitDeleteWithObject(javaWriter);
    emitMapCursorToObject(javaWriter);
    emitSingleMap(javaWriter);
    emitSelectById(javaWriter);
    emitByteArraySerialization(javaWriter);
    javaWriter.endType();
  }

  /** Create a way to get an id for foreign keys */
  private void emitGetId(JavaWriter javaWriter) throws IOException {
    logger.d("emitGetId");
    javaWriter.beginMethod(
        "long", GET_ID_FUNCTION, EnumSet.of(PUBLIC, STATIC), getTargetClass(), "value")
        .emitStatement("return value.%s", idColumnName)
        .endMethod();
  }

  /** Creates the function for creating the table */
  private void emitCreateTable(JavaWriter javaWriter) throws IOException {
    logger.d("emitCreateTable");
    javaWriter.beginMethod(
        "void", $$CREATE_TABLE_FUNCTION, EnumSet.of(PUBLIC, STATIC), "SQLiteDatabase", "db")
        .emitStatement("db.execSQL(\"%s\")", getSchema())
        .endMethod();
  }

  /** Creates the function dropping the table */
  private void emitDropTable(JavaWriter javaWriter) throws IOException {
    logger.d("emitDropTable");
    javaWriter.beginMethod(
        "void", $$DROP_TABLE_FUNCTION, EnumSet.of(PUBLIC, STATIC), "SQLiteDatabase", "db")
        .emitStatement("db.execSQL(\"DROP TABLE IF EXISTS %s\")", getTableName())
        .endMethod();
  }

  /** Creates the function for inserting a new value into the database */
  private void emitInsert(JavaWriter javaWriter) throws IOException {
    logger.d("emitInsert");
    String tableName = getTableName();
    javaWriter.beginMethod("void", $$INSERT_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        getTargetClass(), "element", "SQLiteDatabase", "db")
        .emitStatement("ContentValues values = new ContentValues()");
    List<TableColumn> childColumns = Lists.newLinkedList();
    for (TableColumn column : columns) {
      String columnName = column.getColumnName();
      if (column.isBlob() && !column.isByteArray()) {
        javaWriter.emitStatement("values.put(\"%s\", %s(element.%s))", columnName,
            SERIALIZE_FUNCTION, columnName);
      } else if (column.isOneToOne()) {
        javaWriter.emitStatement("%s%s.%s(element.%s, db)", column.getType(),
            $$SUFFIX, INSERT_ONE_TO_ONE, column.getColumnName())
            .emitStatement("values.put(\"%s\", %s%s.%s(element.%s))", columnName,
                column.getType(), $$SUFFIX, GET_ID_FUNCTION, columnName);
      } else if (column.isDate()) {
        javaWriter.emitStatement(
            "values.put(\"%s\", element.%s.getTime())", columnName, columnName);
      } else if (column.isOneToMany()) {
        childColumns.add(column);
      } else if (!column.isOneToManyChild()) {
        javaWriter.emitStatement("values.put(\"%s\", element.%s)", columnName, columnName);
      }
    }
    javaWriter.emitStatement("db.insert(\"%s\", null, values)", tableName);

    if (!childColumns.isEmpty()) {
      javaWriter.emitStatement("long id = DatabaseUtils.longForQuery(db, "
          + "\"SELECT ROWID FROM %s ORDER BY ROWID DESC LIMIT 1\", null)", tableName);
    }
    for (TableColumn childColumn : childColumns) {
      javaWriter.emitStatement("%s%s.%s(id, element.%s, db)", childColumn.getType(),
          $$SUFFIX, PARENT_INSERT_FUNCTION, childColumn.getColumnName());
    }

    javaWriter.endMethod();
  }

  /** Creates the function for updating an object */
  private void emitUpdate(JavaWriter javaWriter) throws IOException {
    logger.d("emitUpdate");
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
      } else if (column.isOneToMany()) {
        javaWriter.beginControlFlow("for (%s child : element.%s)",
            column.getType().replace("$", "."), column.getColumnName())
            .emitStatement("%s%s.%s(child, db)", column.getType(), $$SUFFIX,
                $$UPDATE_OBJECT_FUNCTION)
            .endControlFlow();
      } else if (column.isOneToManyChild()) {
        // TODO: actually no way of actually updating this value directly add a wrapper?
      } else {
        javaWriter.emitStatement("values.put(\"%s\", element.%s)", columnName, columnName);
      }
    }
    javaWriter.emitStatement("db.update(\"%s\", values, \"%s = \" + element.%s, null)",
        getTableName(), idColumnName, idColumnName);
    javaWriter.endMethod();
  }

  /** Updates the id of the object to the last insert */
  private void emitUpdateColumnId(JavaWriter javaWriter) throws IOException {
    logger.d("emitUpdateColumnId");
    // Updates the column id for the last inserted row
    javaWriter.beginMethod("void", $$UPDATE_ID_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        getTargetClass(), "element", "SQLiteDatabase", "db")
        .emitStatement("long id = DatabaseUtils.longForQuery(db, \"%s\", null)",
            String.format(GET_ID_OF_LAST_INSERTED_ROW_SQL, getTableName()))
        .emitStatement("element.%s = id", idColumnName)
        .endMethod();
  }

  /** Creates the function for deleting an object by id */
  private void emitDeleteWithId(JavaWriter javaWriter) throws IOException {
    logger.d("emitDeleteWithId");
    javaWriter.beginMethod("void", $$DELETE_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC), "Long",
        "id", "SQLiteDatabase", "db")
        .emitStatement("db.delete(\"%s\", \"%s = \" + id, null)", getTableName(), idColumnName)
        .endMethod();
  }

  /** Creates the function for deleting an object from the table */
  private void emitDeleteWithObject(JavaWriter javaWriter) throws IOException {
    logger.d("emitDeleteWithObject");
    javaWriter.beginMethod("void", $$DELETE_OBJECT_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        getTargetClass(), "element", "SQLiteDatabase", "db")
        .emitStatement(
            "%s(element.%s, db)", $$DELETE_OBJECT_FUNCTION, idColumnName)
        .endMethod();
  }

  /** Creates the function for mapping a cursor to the object after executing a sql statement */
  private void emitMapCursorToObject(JavaWriter javaWriter) throws IOException {
    logger.d("emitMapCursorToObject");

    final String targetClass = getTargetClass();

    javaWriter.beginMethod("List<" + targetClass + ">", $$MAP_OBJECT_FUNCTION,
        EnumSet.of(PUBLIC, STATIC), "Cursor", "cursor", "SQLiteDatabase", "db")
        .emitStatement("List<%s> tableObjects = new LinkedList<%s>()", targetClass, targetClass)
        .beginControlFlow("if (cursor.moveToFirst())")
        .beginControlFlow("while (!cursor.isAfterLast())")
        .emitStatement("%s tableObject = %s(cursor, db)", targetClass, $$MAP_SINGLE_FUNCTION)
        .emitStatement("tableObjects.add(tableObject)")
        .emitStatement("cursor.moveToNext()")
        .endControlFlow()
        .endControlFlow()
        .emitStatement("return tableObjects")
        .endMethod();
  }

  private void emitSingleMap(JavaWriter javaWriter) throws IOException {
    String targetClass = getTargetClass();
    javaWriter.beginMethod(targetClass, $$MAP_SINGLE_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        "Cursor", "cursor", "SQLiteDatabase", "db")
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
      } else if (column.isOneToMany()) {
        javaWriter.emitStatement("Cursor childCursor = %s%s.%s(db)", column.getType(),
            $$SUFFIX, SELECT_ALL_FUNCTION)
            .emitStatement("tableObject.%s = %s%s.%s(childCursor, db)",
                column.getColumnName(), column.getType(), $$SUFFIX, $$MAP_OBJECT_FUNCTION);
      } else if (column.isOneToManyChild()) {
        // TODO Skip and have custom mapping?
      } else {
        javaWriter.emitStatement("tableObject.%s = cursor.%s(cursor.getColumnIndex(\"%s\"))",
            columnName, CursorFunctions.get(column.getType()), columnName);
      }
    }

    javaWriter.emitStatement("return tableObject")
        .endMethod();
  }

  /** Creates function for getting an object by value */
  private void emitSelectById(JavaWriter javaWriter) throws IOException {
    logger.d("emitSelectById");
    javaWriter.beginMethod(getTargetClass(), $$GET_OBJECT_BY_ID, EnumSet.of(PUBLIC, STATIC), "long",
        "id", "SQLiteDatabase", "db")
        .emitStatement(
            "return %s(db.rawQuery(\"SELECT * FROM %s WHERE %s  = id\", null), db).get(0)",
            $$MAP_OBJECT_FUNCTION, getTableName(), idColumnName)
        .endMethod();
  }

  private void emitParentInsert(JavaWriter javaWriter) throws IOException {
    javaWriter.beginMethod("void", PARENT_INSERT_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        "long", "parentId", String.format("List<%s>", getTargetClass()), "children",
        "SQLiteDatabase", "db")
        .beginControlFlow("for(%s child : children)", getTargetClass())
        .emitStatement("ContentValues values = new ContentValues()");
    for (TableColumn column : columns) {
      String columnName = column.getColumnName();
      if (column.getSqlType() == SqliteType.BLOB && !column.isByteArray()) {
        javaWriter.emitStatement("values.put(\"%s\", %s(child.%s))", columnName,
            SERIALIZE_FUNCTION, columnName);
      } else if (column.isOneToOne()) {
        javaWriter.emitStatement("values.put(\"%s\", %s%s.%s(child.%s))", columnName,
            column.getType(), $$SUFFIX, GET_ID_FUNCTION, columnName);
      } else if (column.isDate()) {
        javaWriter.emitStatement(
            "values.put(\"%s\", child.%s.getTime())", columnName, columnName);
      } else if (column.getSqlType() == SqliteType.ONE_TO_MANY) {
        javaWriter.emitStatement("%s.%s.%s(%s)", column.getType(), $$SUFFIX,
            PARENT_INSERT_FUNCTION, columnName);
      } else if (column.getSqlType() == SqliteType.ONE_TO_MANY_CHILD) {
        javaWriter.emitStatement("values.put(\"%s\", %s)", columnName, "parentId");
      } else {
        javaWriter.emitStatement("values.put(\"%s\", child.%s)", columnName, columnName);
      }
    }
    javaWriter.emitStatement("db.insert(\"%s\", null, values)", getTableName())
        .emitStatement("long id = DatabaseUtils.longForQuery(db, \"SELECT ROWID FROM %s "
            + "ORDER BY ROWID DESC LIMIT 1\", null)", getTableName())
        .emitStatement("child.%s = id", idColumnName)
        .endControlFlow()
        .endMethod();
  }

  private void emitSelectAll(JavaWriter javaWriter) throws IOException {
    logger.d("emitSelectAll");
    javaWriter.beginMethod("Cursor", SELECT_ALL_FUNCTION, EnumSet.of(PUBLIC, STATIC),
        "SQLiteDatabase", "db")
        .emitStatement("return db.rawQuery(\"SELECT * FROM %s\", null)", getTableName())
        .endMethod();
  }

  private void emitOneToOneInsert(JavaWriter javaWriter) throws IOException {
    logger.d("emitOneToOneInsert");
    javaWriter.beginMethod("void", INSERT_ONE_TO_ONE, EnumSet.of(PUBLIC, STATIC), getTargetClass(),
        "element", "SQLiteDatabase", "db")
        .emitStatement("%s(element, db)", $$INSERT_OBJECT_FUNCTION)
        .emitStatement("long id = DatabaseUtils.longForQuery(db, \"%s\", null)",
            String.format(GET_ID_OF_LAST_INSERTED_ROW_SQL, getTableName()))
        .emitStatement("element.%s = id", idColumnName)
        .endMethod();
  }

  /** Creates functions for serialization to and from byte arrays */
  private void emitByteArraySerialization(JavaWriter javaWriter) throws IOException {
    logger.d("emitByteArraySerialization");
    javaWriter.beginMethod("<K> byte[]", SERIALIZE_FUNCTION, EnumSet.of(STATIC), "K", "object")
        .beginControlFlow("try")
        .emitStatement("ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()")
        .emitStatement(
            "ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)")
        .emitStatement("objectOutputStream.writeObject(object)")
        .emitStatement("return byteArrayOutputStream.toByteArray()")
        .nextControlFlow("catch (IOException e)")
        .emitStatement("throw new RuntimeException(e)")
        .endControlFlow()
        .endMethod()
        .beginMethod("<K> K", DESERIALIZE_FUNCTION, EnumSet.of(STATIC), "byte[]", "bytes")
        .beginControlFlow("try")
        .emitStatement(
            "ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)")
        .emitStatement(
            "ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)")
        .emitStatement("return (K) objectInputStream.readObject()")
        .nextControlFlow("catch (IOException e)")
        .emitStatement("throw new RuntimeException(e)")
        .nextControlFlow("catch (ClassNotFoundException e)")
        .emitStatement("throw new RuntimeException(e)")
        .endControlFlow()
        .endMethod();
  }

  @Override public String toString() {
    return getSchema();
  }
}
