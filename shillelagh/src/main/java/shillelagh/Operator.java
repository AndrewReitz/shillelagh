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

/** Operators used for building queries */
enum Operator {
  BETWEEN(" BETWEEN "),
  ORDER_BY(" ORDER BY "),
  WHERE(" WHERE "),
  OR(" OR "),
  AND(" AND "),
  EQUAL(" = "),
  NOT_EQUAL(" != "),
  GREATER_THAN_EQUAL(" >= "),
  GREATHER_THAN(" > "),
  LESS_THAN_EQUAL(" <= "),
  LESS_THAN(" < "),
  IS(" IS "),
  NOT(" NOT "),
  LIKE(" LIKE "),
  NULL(" NULL ");

  final String operator;

  private Operator(String operator) {
    this.operator = operator;
  }
}
