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

/** Builder for queries after the order by statement has been called */
public final class OrderByBuilder<T> extends Builder<T> {
  OrderByBuilder(Shillelagh shillelagh, Class<? extends T> tableObject, StringBuilder query) {
    super(shillelagh, tableObject, query);
  }

  /** Order the results in ascending order */
  public Builder<T> ascending() {
    this.query.append(" ASC");
    return this;
  }

  /** Order the results in descending order */
  public Builder<T> descending() {
    this.query.append(" DESC");
    return new Builder<T>(shillelagh, tableObject, query);
  }
}
