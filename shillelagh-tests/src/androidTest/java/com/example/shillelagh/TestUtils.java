package com.example.shillelagh;

final class TestUtils {
  private TestUtils() {
    // no instances
  }

  static String getTableName(Class<?> clazz) {
    return clazz.getCanonicalName().replace(".", "_");
  }
}
