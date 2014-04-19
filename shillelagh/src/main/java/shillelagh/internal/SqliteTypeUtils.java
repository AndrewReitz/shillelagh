package shillelagh.internal;

import javax.lang.model.type.TypeMirror;

class SqliteTypeUtils {

  private ShillelaghLogger logger;

  private SqliteInteger integer;
  private SqliteReal real;
  private SqliteText text;

  SqliteTypeUtils(ShillelaghLogger logger) {
    this.logger = logger;
    this.integer = new SqliteInteger();
    this.real = new SqliteReal();
    this.text = new SqliteText();
  }

  SqliteType getSqliteType(TypeMirror typeMirror) {
    if (integer.isTypeOf(typeMirror)) {
      return SqliteType.INTEGER;
    } else if (real.isTypeOf(typeMirror)) {
      return SqliteType.REAL;
    } else if (text.isTypeOf(typeMirror)) {
      return SqliteType.TEXT;
    }

    logger.e("Unknown Type: " + typeMirror.toString());

    return SqliteType.NULL;
  }
}
