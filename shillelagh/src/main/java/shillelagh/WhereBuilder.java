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

import static shillelagh.Operator.ORDER_BY;
import static shillelagh.Operator.WHERE;
import static shillelagh.Shillelagh.getTableName;

/**
 * Builder for constructing where statements to query the database with RxJava.
 *
 * @param <T> The resulting database model object.
 */
public final class WhereBuilder<T> extends Builder<T> {
  WhereBuilder(Shillelagh shillelagh, Class<? extends T> tableObject) {
    super(shillelagh, tableObject,
        new StringBuilder("SELECT * FROM ").append(getTableName(tableObject)));
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
}
