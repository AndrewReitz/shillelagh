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

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static shillelagh.internal.TableObject.DESERIALIZE_FUNCTION;
import static shillelagh.internal.TableObject.SERIALIZE_FUNCTION;

/** Class in charge of creating all the code injected into other classes */
public final class ShillelaghWriter {

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
    tableObject.emitGetId(javaWriter);
  }

  /** Creates the function for creating the table */
  private void emitCreateTable(JavaWriter javaWriter) throws IOException {
    logger.d("emitCreateTable");
    tableObject.emitCreateTable(javaWriter);
  }

  /** Creates the function dropping the table */
  private void emitDropTable(JavaWriter javaWriter) throws IOException {
    logger.d("emitDropTable");
    tableObject.emitDropTable(javaWriter);
  }

  /** Creates the function for inserting a new value into the database */
  private void emitInsert(JavaWriter javaWriter) throws IOException {
    logger.d("emitInsert");
    tableObject.emitInsert(javaWriter);
  }

  /** Updates the id of the object to the last insert */
  private void emitUpdateColumnId(JavaWriter javaWriter) throws IOException {
    logger.d("emitUpdateColumnId");
    tableObject.emitUpdateColumnId(javaWriter);
  }

  /** Creates the function for updating an object */
  private void emitUpdate(JavaWriter javaWriter) throws IOException {
    logger.d("emitUpdate");
    tableObject.emitUpdate(javaWriter);
  }

  /** Creates the function for deleting an object from the table */
  private void emitDeleteWithObject(JavaWriter javaWriter) throws IOException {
    logger.d("emitDeleteWithObject");
    tableObject.emitDeleteWithObject(javaWriter);
  }

  /** Creates the function for deleting an object by id */
  private void emitDeleteWithId(JavaWriter javaWriter) throws IOException {
    logger.d("emitDeleteWithId");
    tableObject.emitDeleteWithId(javaWriter);
  }

  /** Creates function for getting an object by value */
  private void emitSelectById(JavaWriter javaWriter) throws IOException {
    logger.d("emitSelectById");
    tableObject.emitSelectById(javaWriter);
  }

  /** Creates the function for mapping a cursor to the object after executing a sql statement */
  private void emitMapCursorToObject(JavaWriter javaWriter) throws IOException {
    logger.d("emitMapCursorToObject");
    tableObject.emitMapCursorToObject(javaWriter);
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
    logger.d("exit emitByteArraySerialization");
  }
}
