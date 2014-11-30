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

/** Builder for turning sql statements into lists, observables or cursors */
public class Builder<T> {
  final StringBuilder query;

  final Class<? extends T> tableObject;
  final Shillelagh shillelagh;

  Builder(Shillelagh shillelagh, Class<? extends T> tableObject, StringBuilder query) {
    this.shillelagh = shillelagh;
    this.tableObject = tableObject;
    this.query = query;
  }

  /** Executes a query and returns the results as a list */
  public final List<T> toList() {
    return shillelagh.rawQuery(tableObject, query.toString());
  }

  /** Executes a query and returns the results wrapped in an observable */
  public final Observable<T> toObservable() {
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

  /** Executes a query and returns the results in a cursor */
  public final Cursor toCursor() {
    return shillelagh.rawQuery(query.toString());
  }

  /** Returns the created query as a string */
  @Override public final String toString() {
    return query.toString().trim();
  }

  /** Check to ensure that the column name provided is valid */
  final void checkColumnName(String columnName) {
    try {
      tableObject.getDeclaredField(columnName);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(
          String.format("%s isEqualTo not a field found in %s", columnName, tableObject));
    }
  }
}
