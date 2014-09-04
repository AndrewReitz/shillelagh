package shillelagh.internal;

import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import shillelagh.Field;

/** Sqlite3 Types */
enum SqliteType {
  NULL(Sets.newHashSet(Arrays.asList(TypeKind.NULL)), Collections.<String>emptySet()),
  INTEGER(Sets.newHashSet(Arrays.asList(
      TypeKind.INT,
      TypeKind.SHORT,
      TypeKind.LONG,
      TypeKind.BOOLEAN // true false types
  )), Sets.newHashSet(Arrays.asList(
      Integer.class.getName(),
      Short.class.getName(),
      Long.class.getName(),
      Boolean.class.getName(), // true false types
      Date.class.getName()
  ))),
  REAL(Sets.newHashSet(Arrays.asList(
      TypeKind.DOUBLE,
      TypeKind.FLOAT
  )), Sets.newHashSet(Arrays.asList(
      Double.class.getName(),
      Float.class.getName(),
      Number.class.getName()
  ))),
  TEXT(Collections.<TypeKind>emptySet(), Sets.newHashSet(Arrays.asList(
      String.class.getName()
  ))),
  BLOB(Collections.<TypeKind>emptySet(), Collections.<String>emptySet()),
  ONE_TO_MANY(Collections.<TypeKind>emptySet(), Collections.<String>emptySet()),
  ONE_TO_MANY_CHILD(Collections.<TypeKind>emptySet(), Collections.<String>emptySet()),
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

  static SqliteType from(Element element) {
    if (element == null) {
      throw new NullPointerException("element must not be null");
    }

    if (element.getAnnotation(Field.class).isBlob()) {
      return BLOB;
    }

    String typeString = element.asType().toString();
    // TODO Arrays?
    if (typeString.contains("java.util.List")) {
      return ONE_TO_MANY;
    }

    final TypeMirror typeMirror = element.asType();
    for (SqliteType sqliteType : values()) {
      if (sqliteType.kinds.contains(typeMirror.getKind())
          || sqliteType.objects.contains(typeMirror.toString())) {
        return sqliteType;
      }
    }

    return UNKNOWN;
  }
}
