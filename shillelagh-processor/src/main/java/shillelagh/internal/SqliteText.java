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

import javax.lang.model.type.TypeMirror;

class SqliteText {
  /**
   * Checks if a TypeMirror is mapped to Sqlite Integer Type
   *
   * @param typeMirror The TypeMirror to check if it's an Sqlite Integer Type
   * @return true if it maps to Sqlite Integer false otherwise
   */
  boolean isTypeOf(TypeMirror typeMirror) {
    return "java.lang.String".equals(typeMirror.toString());
  }
}
