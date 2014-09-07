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
