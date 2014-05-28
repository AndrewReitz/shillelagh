package com.example.shillelagh;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestNotTableObject;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.util.Date;

import shillelagh.Shillelagh;

import static org.fest.assertions.api.Assertions.assertThat;

public class InsertTest extends AndroidTestCase {

  private SQLiteOpenHelper sqliteOpenHelper;
  private Shillelagh shillelagh;

  @Override protected void setUp() throws Exception {
    super.setUp();

    sqliteOpenHelper = new TestSQLiteOpenHelper(getContext());
    shillelagh = new Shillelagh(sqliteOpenHelper);
  }

  @Override protected void tearDown() throws Exception {
    getContext().deleteDatabase(sqliteOpenHelper.getDatabaseName());
    super.tearDown();
  }

  public void testInsertPrimitives() {
    // Arrange
    double expectedDouble = 2342342.2323;
    float expectedFloat = 4.0f;
    long expectedLong = 10000;
    int expectedInt = 23;
    short expectedShort = 234;

    TestPrimitiveTable row = new TestPrimitiveTable();
    row.setaBoolean(true);
    row.setaDouble(expectedDouble);
    row.setaFloat(expectedFloat);
    row.setaLong(expectedLong);
    row.setAnInt(expectedInt);
    row.setaShort(expectedShort);

    // Act
    shillelagh.insert(row);

    // Assert
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestPrimitiveTable.class.getSimpleName(), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1); // id column
    assertThat(cursor.getShort(1)).isEqualTo(expectedShort); // aShort
    assertThat(cursor.getInt(2)).isEqualTo(expectedInt); // anInt
    assertThat(cursor.getLong(3)).isEqualTo(expectedLong); // aLong
    assertThat(cursor.getFloat(4)).isEqualTo(expectedFloat); // aFloat
    assertThat(cursor.getDouble(5)).isEqualTo(expectedDouble); // aDouble
    assertThat(cursor.getInt(6)).isEqualTo(1); // aBoolean, true maps to 1

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testInsertBoxedPrimitives() {
    // Arrange
    double expectedDouble = 10000.55;
    float expectedFloat = 2.0f;
    long expectedLong = 200000;
    int expectedInt = 42;
    short expectedShort = 2;

    TestBoxedPrimitivesTable row = new TestBoxedPrimitivesTable();
    row.setaBoolean(false);
    row.setaDouble(expectedDouble);
    row.setaFloat(expectedFloat);
    row.setAnInteger(expectedInt);
    row.setaLong(expectedLong);
    row.setaShort(expectedShort);

    // Act
    shillelagh.insert(row);

    // Assert
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestBoxedPrimitivesTable.class.getSimpleName(), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1); // id column
    assertThat(cursor.getInt(1)).isEqualTo(0); // aBoolean
    assertThat(cursor.getDouble(2)).isEqualTo(expectedDouble); // aDouble
    assertThat(cursor.getFloat(3)).isEqualTo(expectedFloat); // aFloat
    assertThat(cursor.getInt(4)).isEqualTo(expectedInt); // anInteger
    assertThat(cursor.getLong(5)).isEqualTo(expectedLong); // aLong
    assertThat(cursor.getShort(6)).isEqualTo(expectedShort); // aShort

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testInsertJavaObjects() {
    // Arrange
    final Date now = new Date();
    final String expected = "TestString";
    TestJavaObjectsTable row = new TestJavaObjectsTable();
    row.setaDate(now);
    row.setaString(expected);

    // Act
    shillelagh.insert(row);

    // Assert
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestJavaObjectsTable.class.getSimpleName(), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1);
    assertThat(cursor.getString(1)).isEqualTo(expected);
    assertThat(cursor.getLong(2)).isEqualTo(now.getTime());

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testInsertShouldFailWhenNotAnnotated() {
    // Arrange
    TestNotTableObject row = new TestNotTableObject();
    row.setName("some text");
    row.setValue(6);

    // Act
    try {
      shillelagh.insert(row);
    } catch (RuntimeException e) {
      assertThat(e.getMessage()).isEqualTo("Unable to insert into com.example.shillelagh.model." +
          "TestNotTableObject. Are you missing @Table annotation?");
    }

    // Assert
    throw new AssertionError("Expected Exception Not Thrown");
  }

  // TODO Tests for null values
}
