package shillelagh.internal;

import java.util.Date;

import javax.lang.model.element.Element;

/** Represents the data for a column in a database and mapping it back to its java counter part */
class TableColumn {
  private final SqliteType type;
  private final String columnName;
  private final Element element;

  TableColumn(Element element) {
    this.columnName = element.getSimpleName().toString();
    this.element = element;
    this.type = SqliteType.from(element);
  }

  String getColumnName() {
    return columnName;
  }

  SqliteType getSqlType() {
    return type;
  }

  String getType() {
    return element.asType().toString();
  }

  boolean isDate() {
    return getType().equals(Date.class.getName());
  }

  boolean isBoolean() {
    final String typeString = getType();
    return typeString.equals(boolean.class.getName()) || typeString.equals(Boolean.class.getName());
  }

  boolean isByteArray() {
    final String typeString = getType();
    return typeString.equals("byte[]");
  }

  @Override public String toString() {
    return columnName + " " + type.toString();
  }
}
