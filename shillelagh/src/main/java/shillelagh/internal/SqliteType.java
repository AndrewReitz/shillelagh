package shillelagh.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/** Sqlite3 Types */
enum SqliteType {
  NULL(new HashSet<TypeKind>(Arrays.asList(TypeKind.NULL)), Collections.<String>emptySet()),
  INTEGER(new HashSet<TypeKind>(Arrays.asList(
      TypeKind.INT,
      TypeKind.SHORT,
      TypeKind.LONG,
      TypeKind.BOOLEAN // true false types
  )), new HashSet<String>(Arrays.asList(
      Integer.class.getName(),
      Short.class.getName(),
      Long.class.getName(),
      Boolean.class.getName(), // true false types
      Date.class.getName()
  ))),
  REAL(new HashSet<TypeKind>(Arrays.asList(
      TypeKind.DOUBLE,
      TypeKind.FLOAT
  )), new HashSet<String>(Arrays.asList(
      Double.class.getName(),
      Float.class.getName(),
      Number.class.getName()
  ))),
  TEXT(new HashSet<TypeKind>(Arrays.asList(
      TypeKind.CHAR
  )),new HashSet<String>(Arrays.asList(
      String.class.getName(),
      Character.class.getName()
  ))),
  BLOB(Collections.<TypeKind>emptySet(), Collections.<String>emptySet()), // Saved for a later date
  // signals an unknown type probably should be a key into another table
  UNKNOWN(Collections.<TypeKind>emptySet(), Collections.<String>emptySet());

  private final Set<TypeKind> kinds;
  private final Set<String> objects;

  /**
   * @param kinds TypeKinds that the SqliteType is associated with
   * @param objects Strings of the objects this SqliteType is associated with
   */
  SqliteType(Set<TypeKind> kinds, Set<String> objects) {
    this.kinds = kinds;
    this.objects = objects;
  }

  static SqliteType from(TypeMirror typeMirror) {
    if (typeMirror == null) {
      throw new NullPointerException("typeMirror must not be null");
    }

    for (SqliteType sqliteType : values()) {
      if (sqliteType.kinds.contains(typeMirror.getKind()) ||
          sqliteType.objects.contains(typeMirror.toString())) {
        return sqliteType;
      }
    }

    return UNKNOWN;
  }
}
