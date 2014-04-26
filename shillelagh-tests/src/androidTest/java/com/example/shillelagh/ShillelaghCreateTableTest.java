package com.example.shillelagh;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestPrimitiveTable;

import java.io.File;

import shillelagh.Shillelagh;

import static org.fest.assertions.api.Assertions.assertThat;

public class ShillelaghCreateTableTest extends AndroidTestCase {

  private static final int CURRENT_DATABASE_VERSION = 1;
  private SQLiteDatabase database;
  private File databaseFile;

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
    final String expected = "CREATE TABLE TestBoxedPrimitivesTable (" +
        "_id INTEGER PRIMARY KEY AUTOINCREMENT, aBoolean INTEGER, aByte INTEGER, " +
        "aCharacter NULL, aDouble REAL, aFloat REAL, anInteger INTEGER, aLong INTEGER, " +
        "aShort INTEGER, aString TEXT);";

    // Act
    Shillelagh.createTable(database, TestPrimitiveTable.class);
    final String actual = DatabaseUtils.stringForQuery(database,
        ".schema TestBoxedPrimitivesTable", null);

    // Assert
    assertThat(actual).isEqualToIgnoringCase(expected);
  }
}
