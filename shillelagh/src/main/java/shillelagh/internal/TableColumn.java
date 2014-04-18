package shillelagh.internal;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.TypeMirror;

import shillelagh.SqliteType;

/** Represents the data for a column in a database and mapping it back to its java counter part */
class TableColumn {

  private final ShillelaghLogger logger;
  private final SqliteType type;
  private final String columnName;
  private final Element element;

  TableColumn(SqliteTypeUtils sqliteTypeUtils, Element element, ShillelaghLogger logger) {
    this.columnName = element.getSimpleName().toString();
    this.logger = logger;
    this.element = element;

    TypeMirror typeMirror = element.asType();
    this.type = sqliteTypeUtils.getSqliteType(typeMirror);
    logger.d("Element " + element + " Type " + typeMirror.toString());
  }

  String getColumnName() {
    return columnName;
  }

  SqliteType getSqlType() {
    return type;
  }

  String getType()  {
    return element.asType().toString();
  }

  boolean isDate() {
    return element.asType().toString().equals("java.util.Date");
  }

  @Override public String toString() {
    return columnName + " " + type.toString();
  }
}
