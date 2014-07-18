package com.example.shillelagh;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestNotTableObject;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.io.File;

import shillelagh.Shillelagh;

import static org.fest.assertions.api.Assertions.assertThat;

public class DropTableTest extends AndroidTestCase {

  private static final int CURRENT_DATABASE_VERSION = 1;
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

  public void testShouldDropTable() {
    // Arrange
    String tableCheckQuery = String.format("SELECT DISTINCT tbl_name FROM " +
        "sqlite_master WHERE tbl_name = \'%s\'", TestPrimitiveTable.class.getSimpleName());
    String create = String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY AUTOINCREMENT, aShort " +
        "INTEGER, anInt INTEGER, aLong INTEGER, aFloat REAL, aDouble REAL, aBoolean INTEGER);",
        TestPrimitiveTable.class.getSimpleName());
    database.execSQL(create);

    // Act
    Shillelagh.dropTable(database, TestPrimitiveTable.class);

    // Assert
    Cursor cursor = database.rawQuery(tableCheckQuery, null);

    boolean exists = false;
    if (cursor != null) {
      if (cursor.getCount() > 0) {
        cursor.close();
        exists = true;
      }
    }

    assertThat(exists).isFalse();
  }

  public void testShouldThrowErrorWhenNotAnnotated() {
    // Arrange
    String create = String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, name " +
            "TEXT, value INTEGER);", TestNotTableObject.class.getSimpleName());
    database.execSQL(create);

    // Act
    try {
      Shillelagh.dropTable(database, TestNotTableObject.class);
    } catch (RuntimeException e) {
      assertThat(e.getMessage()).isEqualTo("Unable to drop table for class com.example." +
          "shillelagh.model.TestNotTableObject. Are you missing @Table annotation?");
      return;
    }

    // Assert
    throw new AssertionError("Expected Exception Not Thrown");
  }
}
