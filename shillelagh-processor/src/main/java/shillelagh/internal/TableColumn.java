package shillelagh.internal;

import java.util.Date;

import javax.lang.model.element.Element;

import static shillelagh.internal.SqliteType.INTEGER;

/** Represents the data for a column in a database and mapping it back to its java counter part */
class TableColumn {
  private final SqliteType sqliteType;
  private final String columnName;
  private String type;

  /** Indicates if column is a one to one mapping */
  private boolean oneToOne = false;

  /**
   * Construct a table column from the element
   *
   * @param element element to construct this object from
   * @param type Type that created this column should map back to in java as a String.
   *             Ex. "java.lang.Integer"
   */
  TableColumn(Element element, String type) {
    this.columnName = element.getSimpleName().toString();
    this.sqliteType = SqliteType.from(element);
    this.type = type;
  }

  /**
   * Construct a table column directly
   *
   * @param columnName the name of this column
   * @param type the type this column should map back from sql to in java
   * @param sqliteType the sqlite type this column will be
   */
  TableColumn(String columnName, String type, SqliteType sqliteType) {
    this.columnName = columnName;
    this.type = type;
    this.sqliteType = sqliteType;
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

  /** Allow setting the generic type for one to manys */
  void setType(String type) {
    this.type = type;
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

  boolean isBlob() {
    return sqliteType == SqliteType.BLOB;
  }

  boolean isOneToMany() {
    return sqliteType == SqliteType.ONE_TO_MANY;
  }

  boolean isOneToManyChild() {
    return sqliteType == SqliteType.ONE_TO_MANY_CHILD;
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
