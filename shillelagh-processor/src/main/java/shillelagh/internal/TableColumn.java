package shillelagh.internal;

import java.util.Date;

import javax.lang.model.element.Element;

import static shillelagh.internal.SqliteType.INTEGER;

/** Represents the data for a column in a database and mapping it back to its java counter part */
class TableColumn {
  private final SqliteType sqliteType;
  private final String columnName;
  private final String type;

  /** Indicates if column is a one to one mapping */
  private boolean oneToOne = false;

  TableColumn(Element element, String type) {
    this.columnName = element.getSimpleName().toString();
    this.sqliteType = SqliteType.from(element);
    this.type = type;
  }

  String getColumnName() {
    return columnName;
  }

  SqliteType getSqlType() {
    return isOneToOne() ? INTEGER : sqliteType;
  }

  String getType() {
    return type;
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

  void setOneToOne(boolean oneToOne) {
    this.oneToOne = oneToOne;
  }

  boolean isOneToOne() {
    return oneToOne;
  }

  @Override public String toString() {
    return columnName + " " + getSqlType().toString();
  }
}
