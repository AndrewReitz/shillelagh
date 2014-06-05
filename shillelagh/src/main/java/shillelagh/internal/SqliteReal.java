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

  private HashSet<TypeKind> realKinds = new HashSet<>(REAL_KINDS);
  private HashSet<String> realObjects = new HashSet<>(REAL_OBJECTS);

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
