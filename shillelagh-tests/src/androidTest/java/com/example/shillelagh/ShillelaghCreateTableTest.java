package com.example.shillelagh;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.io.File;

import shillelagh.Shillelagh;

import static org.fest.assertions.api.Assertions.assertThat;

public class ShillelaghCreateTableTest extends AndroidTestCase {

  private static final int CURRENT_DATABASE_VERSION = 1;
  private SQLiteDatabase database;
  private File databaseFile;

  private static final int TABLE_INFO_NAME_COLUMN = 1;
  private static final int TABLE_INFO_TYPE_COLUMN = 2;
  private static final int TABLE_INFO_NULLABLE_COLUMN = 3;
  private static final int TABLE_INFO_DEFAULT_VALUE_COLUMN = 4;
  private static final int TABLE_INFO_PRIMARAY_KEY_COLUMN = 5;

  private static final String SQL_INTEGER = "INTEGER";
  private static final String SQL_REAL = "REAL";
  private static final String SQL_TEXT = "TEXT";

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    File dbDir = getContext().getDir("tests", Context.MODE_PRIVATE);
    databaseFile = new File(dbDir, "database_test.db");

    if (databaseFile.exists()) {
      databaseFile.delete();
    }
    database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getPath(), null);
    assertNotNull(database);
    database.setVersion(CURRENT_DATABASE_VERSION);
  }

  @Override
  protected void tearDown() throws Exception {
    database.close();
    databaseFile.delete();
    super.tearDown();
  }

  public void testShouldCreatePrimitiveDatabase() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestPrimitiveTable.class);
    final Cursor cursor = database.rawQuery("PRAGMA table_info(" +
        TestPrimitiveTable.class.getSimpleName() + ")", null);

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
    final Cursor cursor = database.rawQuery("PRAGMA table_info(" +
        TestBoxedPrimitivesTable.class.getSimpleName() + ")", null);

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
    final Cursor cursor = database.rawQuery("PRAGMA table_info(" +
        TestJavaObjectsTable.class.getSimpleName() + ")", null);

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
}
