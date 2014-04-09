package shillelagh.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.lang.model.type.TypeKind;
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
