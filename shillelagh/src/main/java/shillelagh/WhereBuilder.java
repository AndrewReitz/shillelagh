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

import static shillelagh.Operator.ORDER_BY;
import static shillelagh.Operator.WHERE;
import static shillelagh.Shillelagh.HAS_RX_JAVA;
import static shillelagh.Shillelagh.getTableName;

public final class WhereBuilder<T> {

  private final StringBuilder query;

  private final Class<? extends T> tableObject;
  private final Shillelagh shillelagh;

  WhereBuilder(Class<? extends T> tableObject, Shillelagh shillelagh) {
    this.tableObject = tableObject;
    this.shillelagh = shillelagh;

    query = new StringBuilder("SELECT * FROM ") //
        .append(getTableName(tableObject)); //
  }

  public QueryBuilder<T> where(String columnName) {
    checkColumnName(columnName);
    query.append(WHERE.operator).append(columnName);
    return new QueryBuilder<T>(tableObject, shillelagh, columnName, query);
  }

  public OrderByBuilder<T> orderBy(String columnName) {
    checkColumnName(columnName);
    query.append(ORDER_BY.operator).append(columnName);
    return new OrderByBuilder<T>(shillelagh, tableObject, query);
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
}
