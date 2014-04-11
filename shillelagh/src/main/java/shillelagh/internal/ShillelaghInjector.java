package shillelagh.internal;

public class ShillelaghInjector {

  /** Internal class function to get the sql string */
  public static final String CREATE_TABLE_FUNCTION = "getCreateTableSql";
  public static final String DROP_TABLE_FUNCTION = "getDropTableSql";

  private final String classPackage;
  private final String className;
  private final String targetClass;

  private TableObject tableObject;

  ShillelaghInjector(String classPackage, String className, String targetClass) {
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

  /** Create the java functions required */
  String brewJava() {
    StringBuilder builder = new StringBuilder();
    builder.append("// Generated code from Shillelagh. Do not modify!\n");
    builder.append("package ").append(classPackage).append(";\n\n");
    builder.append("public class ").append(className).append(" {\n");
    emmitCreateTableSql(builder);
    builder.append("\n\n");
    emmitDropTableSql(builder);
    builder.append("}\n");
    return builder.toString();
  }

  /** Creates the function for getting the sql string */
  private void emmitCreateTableSql(StringBuilder builder) {
    builder.append("  public static String ").append(CREATE_TABLE_FUNCTION).append("() {");
    builder.append("    return \"").append(tableObject.toString()).append("\";");
    builder.append("  }");
  }

  private void emmitDropTableSql(StringBuilder builder) {
    builder.append("  public static String ").append(DROP_TABLE_FUNCTION).append("() {");
    builder.append("    return \"DROP TABLE IF EXISTS ").append(tableObject.getTableName()).append("\";");
    builder.append("  }");
  }
}
