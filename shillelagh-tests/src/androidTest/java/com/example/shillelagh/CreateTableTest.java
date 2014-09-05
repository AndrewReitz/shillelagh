package com.example.shillelagh;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestBlobs;
import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestNotTableObject;
import com.example.shillelagh.model.TestOneToMany;
import com.example.shillelagh.model.TestOneToOne;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.io.File;

import shillelagh.Shillelagh;

import static org.fest.assertions.api.Assertions.assertThat;

public class CreateTableTest extends AndroidTestCase {

  private static final int CURRENT_DATABASE_VERSION = 1;
  private static final String TABLE_INFO_QUERY = "PRAGMA table_info(%S)";
  private static final int TABLE_INFO_NAME_COLUMN = 1;
  private static final int TABLE_INFO_TYPE_COLUMN = 2;
  private static final int TABLE_INFO_NULLABLE_COLUMN = 3;
  private static final int TABLE_INFO_DEFAULT_VALUE_COLUMN = 4;
  private static final int TABLE_INFO_PRIMARAY_KEY_COLUMN = 5;
  private static final String SQL_INTEGER = "INTEGER";
  private static final String SQL_REAL = "REAL";
  private static final String SQL_TEXT = "TEXT";
  private static final String SQL_BLOB = "BLOB";
  private SQLiteDatabase database;
  private File databaseFile;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    File dbDir = getContext().getDir("tests", Context.MODE_PRIVATE);
    databaseFile = new File(dbDir, "database_test.db");

    if (databaseFile.exists()) {
      //noinspection ResultOfMethodCallIgnored
      databaseFile.delete();
    }
    database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getPath(), null);
    assertNotNull(database);
    database.setVersion(CURRENT_DATABASE_VERSION);
  }

  @Override
  protected void tearDown() throws Exception {
    database.close();
    //noinspection ResultOfMethodCallIgnored
    databaseFile.delete();
    super.tearDown();
  }

  public void testShouldCreatePrimitiveDatabase() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestPrimitiveTable.class);
    final Cursor cursor = database.rawQuery(String.format(TABLE_INFO_QUERY,
        TestPrimitiveTable.class.getSimpleName()), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(7);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aShort");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("anInt");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aLong");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aFloat");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_REAL);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aDouble");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_REAL);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aBoolean");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateBoxedPrimitivesTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestBoxedPrimitivesTable.class);
    final Cursor cursor = database.rawQuery(String.format(TABLE_INFO_QUERY,
        TestBoxedPrimitivesTable.class.getSimpleName()), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(7);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("_id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aBoolean");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aDouble");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_REAL);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aFloat");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_REAL);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("anInteger");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aLong");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aShort");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateJavaObjectTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestJavaObjectsTable.class);
    final Cursor cursor = database.rawQuery(String.format(TABLE_INFO_QUERY,
        TestJavaObjectsTable.class.getSimpleName()), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(3);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aString");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_TEXT);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aDate");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateBlobObjectsTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestBlobs.class);
    final Cursor cursor = database.rawQuery(String.format(TABLE_INFO_QUERY,
        TestBlobs.class.getSimpleName()), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(4);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aByteArray");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_BLOB);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("anotherByteArray");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_BLOB);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aTestBlobObject");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_BLOB);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateOneToOneTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestOneToOne.class);
    final Cursor cursor = database.rawQuery(String.format(TABLE_INFO_QUERY,
        TestOneToOne.class.getSimpleName()), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(2);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("child");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateOneToManyTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestOneToMany.class);
    final Cursor cursor = database.rawQuery(String.format(TABLE_INFO_QUERY,
        TestOneToMany.class.getSimpleName()), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(2);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("someValue");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_TEXT);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  /** Test for checking that a internal class can be created */
  public void testShouldCreateInnerObjectTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestOneToOne.Child.class);
    final Cursor cursor = database.rawQuery(String.format(TABLE_INFO_QUERY,
        TestOneToOne.Child.class.getSimpleName()), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(2);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("childName");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_TEXT);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldFailOnNonAnnotatedClass() {
    try {
      Shillelagh.createTable(database, TestNotTableObject.class);
    } catch (RuntimeException e) {
      assertThat(e.getMessage()).isEqualTo("Unable to create table for class com.example.shillelagh" +
          ".model.TestNotTableObject. Are you missing @Table annotation?");
      return;
    }

    throw new AssertionError("Expected Exception Not Thrown");
  }
}
