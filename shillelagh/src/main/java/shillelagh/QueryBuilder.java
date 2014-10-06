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

import static shillelagh.Shillelagh.HAS_RX_JAVA;
import static shillelagh.Shillelagh.getTableName;

/** Work in progress */
public final class QueryBuilder<T> {
  private final StringBuilder query;

  private final Class<? extends T> tableObject;
  private final Shillelagh shillelagh;

  QueryBuilder(Class<? extends T> tableObject, Shillelagh shillelagh) {
    this.tableObject = tableObject;
    this.shillelagh = shillelagh;

    query = new StringBuilder("SELECT * FROM ")
        .append(getTableName(tableObject));
  }

  public QueryBuilder where(String columnName, String value) {
    query.append(" ")
        .append(columnName)
        .append(" = ")
        .append(value);
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
}
