package shillelagh.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

class SqliteInteger {
  static final List<TypeKind> INTEGER_KINDS = Arrays.asList(
          TypeKind.INT,
          TypeKind.SHORT,
          TypeKind.LONG,
          TypeKind.BYTE, // true false types
          TypeKind.BOOLEAN // true false types
  );

  static final List<String> INTEGER_OBJECTS = Arrays.asList(
          "java.lang.Integer",
          "java.lang.Short",
          "java.lang.Long",
          "java.lang.Boolean",
          "java.lang.Byte",
          "java.util.Date"
  );

  private HashSet<TypeKind> integerKinds = new HashSet<>(INTEGER_KINDS.size());
  private HashSet<String> integerObjects = new HashSet<>(INTEGER_OBJECTS.size());

  SqliteInteger() {
    integerKinds.addAll(INTEGER_KINDS);
    integerObjects.addAll(INTEGER_OBJECTS);
  }

  /**
   * Checks if a TypeMirror is mapped to Sqlite Integer Type
   *
   * @param typeMirror The TypeMirror to check if it's an Sqlite Integer Type
   * @return true if it maps to Sqlite Integer false otherwise
   */
  boolean isTypeOf(TypeMirror typeMirror) {
    if (integerKinds.contains(typeMirror.getKind())) {
      return true;
    }

    if (integerObjects.contains(typeMirror.toString())) {
      return true;
    }

    return false;
  }
}
