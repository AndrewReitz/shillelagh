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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

class SqliteReal {
  static final List<TypeKind> REAL_KINDS = Arrays.asList(
          TypeKind.DOUBLE,
          TypeKind.FLOAT
  );

  static final List<String> REAL_OBJECTS = Arrays.asList(
          "java.lang.Double",
          "java.lang.Float",
          "java.lang.Number"
  );

  private HashSet<TypeKind> realKinds = new HashSet<TypeKind>(REAL_KINDS);
  private HashSet<String> realObjects = new HashSet<String>(REAL_OBJECTS);

  SqliteReal() {
    realKinds.addAll(REAL_KINDS);
    realObjects.addAll(REAL_OBJECTS);
  }

  /**
   * Checks if a TypeMirror is mapped to Sqlite Type
   *
   * @param typeMirror The TypeMirror to check if it's an Sqlite Type
   * @return true if it maps to the Sqlite Object false otherwise
   */
  boolean isTypeOf(TypeMirror typeMirror) {
    if (realKinds.contains(typeMirror.getKind())) {
      return true;
    }

    if (realObjects.contains(typeMirror.toString())) {
      return true;
    }

    return false;
  }
}
