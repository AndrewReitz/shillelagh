/*
 * Copyright 2014 Andrew Reitz
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

import java.util.Date;

import javax.lang.model.element.Element;

import static shillelagh.internal.SqliteType.INTEGER;

/** Represents the data for a column in a database and mapping it back to its java counter part */
class TableColumn {
  private final SqliteType sqliteType;
  private final String columnName;

  /** The name of the member variable this column should read/write. */
  private final String memberName;

  private String type;

  /** Indicates if column is a one to one mapping */
  private boolean oneToOne = false;

  /**
   * Construct a table column from the element
   *
   * @param element element to construct this object from
   * @param type Type that created this column should map back to in java as a String.
   * Ex. "java.lang.Integer"
   * @param columnName The name of this column.
   */
  TableColumn(Element element, String type, String columnName) {
    this.columnName = Strings.isBlank(columnName) ? element.getSimpleName().toString() : columnName;
    this.memberName = element.getSimpleName().toString();
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
  TableColumn(String columnName, String memberName, String type, SqliteType sqliteType) {
    this.columnName = columnName;
    this.memberName = memberName;
    this.type = type;
    this.sqliteType = sqliteType;
  }

  String getColumnName() {
    return columnName;
  }

  String getMemberName() {
    return memberName;
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
