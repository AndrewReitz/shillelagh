package shillelagh.internal;

import java.util.Iterator;

import shillelagh.SqliteType;

public class ShillelaghInjector {

  /** Internal class function to get the sql string */
  public static final String CREATE_TABLE_FUNCTION = "getCreateTableSql";
  public static final String DROP_TABLE_FUNCTION = "getDropTableSql";
  public static final String INSERT_OBJECT_FUNCTION = "getInsertSql";

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

  /** Create the java functions required */
  String brewJava() {
    StringBuilder builder = new StringBuilder();
    builder.append("// Generated code from Shillelagh. Do not modify!\n");
    builder.append("package ").append(classPackage).append(";\n\n");
    builder.append("public class ").append(className).append(" {\n");
    emmitCreateTableSql(builder);
    builder.append("\n\n");
    emmitDropTableSql(builder);
    builder.append("\n\n");
    emmitInsertObject(builder);
    builder.append("}\n");
    return builder.toString();
  }

  /** Creates the function for getting the create sql string */
  private void emmitCreateTableSql(StringBuilder builder) {
    builder.append("  public static String ").append(CREATE_TABLE_FUNCTION).append("() {");
    builder.append("    return \"").append(tableObject.toString()).append("\";");
    builder.append("  }");
  }

  /** Creates the function for getting the drop sql string */
  private void emmitDropTableSql(StringBuilder builder) {
    builder.append("  public static String ").append(DROP_TABLE_FUNCTION).append("() {");
    builder.append("    return \"DROP TABLE IF EXISTS ").append(tableObject.getTableName()).append("\";");
    builder.append("  }");
  }

  /** Creates the function for inserting a new value into the database */
  private void emmitInsertObject(StringBuilder builder) {
    builder.append("  public static String ").append(INSERT_OBJECT_FUNCTION).append("(").append(targetClass).append(" element) {");
    builder.append("    return \"INSERT INTO ").append(tableObject.getTableName());
    builder.append(" (");

    StringBuilder columns = new StringBuilder();
    StringBuilder values = new StringBuilder();
    Iterator<TableColumn> iterator = tableObject.getColumns().iterator();
    while (iterator.hasNext()) {
      TableColumn column = iterator.next();
      columns.append(column.getColumnName());
      if (column.getType() == SqliteType.TEXT) {
        // TODO Checks to make sure elements are accessible
        values.append("'\" + element.").append(column.getColumnName()).append(" + \"'");
      } else if (column.isDate()) {
        values.append("\" + element.").append(column.getColumnName()).append(".getTime() + \"");
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
    builder.append(values.toString()).append(");\";");
    builder.append("  }");
  }
}
