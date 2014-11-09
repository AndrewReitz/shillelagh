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

package shillelagh;

import android.database.Cursor;
import java.util.List;
import rx.Observable;

import static shillelagh.Operator.AND;
import static shillelagh.Operator.BETWEEN;
import static shillelagh.Operator.EQUAL;
import static shillelagh.Operator.GREATER_THAN_EQUAL;
import static shillelagh.Operator.GREATHER_THAN;
import static shillelagh.Operator.IS;
import static shillelagh.Operator.LESS_THAN;
import static shillelagh.Operator.LESS_THAN_EQUAL;
import static shillelagh.Operator.LIKE;
import static shillelagh.Operator.NOT;
import static shillelagh.Operator.NOT_EQUAL;
import static shillelagh.Operator.NULL;
import static shillelagh.Operator.OR;
import static shillelagh.Operator.ORDER_BY;
import static shillelagh.Shillelagh.HAS_RX_JAVA;

/** Work in progress */
public final class QueryBuilder<T> {

  private final StringBuilder query;

  private final Class<? extends T> tableObject;
  private final Shillelagh shillelagh;

  private final String columnName;

  QueryBuilder(Class<? extends T> tableObject, Shillelagh shillelagh, String columnName,
      StringBuilder query) {
    this.tableObject = tableObject;
    this.shillelagh = shillelagh;
    this.columnName = columnName;
    this.query = query;
  }

  public QueryBuilder<T> isEqualTo(Object value) {
    query.append(EQUAL.operator).append(valueOf(columnName, value));
    return this;
  }

  public QueryBuilder<T> isNotEqualTo(Object value) {
    query.append(NOT_EQUAL.operator).append(valueOf(columnName, value));
    return this;
  }

  public QueryBuilder<T> isGreaterThanOrEqualTo(Object value) {
    query.append(GREATER_THAN_EQUAL.operator).append(valueOf(columnName, value));
    return this;
  }

  public QueryBuilder<T> isGreaterThan(Object value) {
    query.append(GREATHER_THAN.operator).append(valueOf(columnName, value));
    return this;
  }

  public QueryBuilder<T> isLessThanOrEqualTo(Object value) {
    query.append(LESS_THAN_EQUAL.operator).append(valueOf(columnName, value));
    return this;
  }

  public QueryBuilder<T> isLessThan(Object value) {
    query.append(LESS_THAN.operator).append(valueOf(columnName, value));
    return this;
  }

  public QueryBuilder<T> between(Object value1, Object value2) {
    query.append(BETWEEN.operator)
        .append(valueOf(columnName, value1))
        .append(AND.operator)
        .append(valueOf(columnName, value2));
    return this;
  }

  public QueryBuilder<T> and(String columnName) {
    checkColumnName(columnName);
    query.append(AND.operator).append(columnName);
    return this;
  }

  public QueryBuilder<T> or(String columnName) {
    checkColumnName(columnName);
    query.append(OR.operator).append(columnName);
    return this;
  }

  public QueryBuilder<T> like(Object value) {
    query.append(LIKE.operator).append("\'").append(value).append("\'");
    return this;
  }

  public QueryBuilder<T> isNotNull() {
    query.append(IS.operator).append(NOT.operator).append(NULL.operator);
    return this;
  }

  public QueryBuilder<T> isNull() {
    query.append(IS.operator).append(NULL.operator);
    return this;
  }

  public QueryBuilder<T> not() {
    query.append(NOT.operator);
    return this;
  }

  public QueryBuilder orderBy(String columnName) {
    checkColumnName(columnName);
    query.append(ORDER_BY.operator).append(columnName);
    return this;
  }

  public List<T> toList() {
    return shillelagh.rawQuery(tableObject, query.toString());
  }

  public Observable<T> toObservable() {
    if (!HAS_RX_JAVA) {
      throw new RuntimeException(
          "RxJava not available! Add RxJava to your build to use this feature");
    }

    return shillelagh.getObservable(tableObject, new CursorLoader() {
      @Override public Cursor getCursor() {
        return shillelagh.rawQuery(query.toString());
      }
    });
  }

  public Cursor toCursor() {
    return shillelagh.rawQuery(query.toString());
  }

  /** Returns the created query as a string */
  @Override public String toString() {
    return query.toString().trim();
  }

  /** Check to ensure that the column name provided isEqualTo valid */
  private void checkColumnName(String columnName) {
    try {
      tableObject.getDeclaredField(columnName);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(
          String.format("%s isEqualTo not a field found in %s", columnName, tableObject));
    }
  }

  /** Forces the value to be wrapped with ' if it isEqualTo for a string field. */
  private Object valueOf(String columnName, Object value) {
    java.lang.reflect.Field field;
    try {
      field = tableObject.getDeclaredField(columnName);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(
          String.format("%s isEqualTo not a field found in %s", columnName, tableObject));
    }
    return field.getType() == String.class ? String.format("\'%s\'", value) : value;
  }
}
