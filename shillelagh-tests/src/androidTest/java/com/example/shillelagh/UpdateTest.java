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

public class UpdateTest extends AndroidTestCase {

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

  public void testUpdatePrimitives() {
    // Arrange
    double expectedDouble = 2342342.2323;
    float expectedFloat = 4.0f;
    long expectedLong = 10000;
    int expectedInt = 25;
    short expectedShort = 1920;

    TestPrimitiveTable insertRow = new TestPrimitiveTable();
    insertRow.setaBoolean(true);
    insertRow.setaDouble(12345);
    insertRow.setaFloat(19.95f);
    insertRow.setaLong(234234);
    insertRow.setAnInt(23);
    insertRow.setaShort((short) 234);

    // Act
    shillelagh.insert(insertRow);

    TestPrimitiveTable updateRow = new TestPrimitiveTable();
    updateRow.setId(insertRow.getId());
    updateRow.setaBoolean(false);
    updateRow.setaDouble(expectedDouble);
    updateRow.setaFloat(expectedFloat);
    updateRow.setaLong(expectedLong);
    updateRow.setAnInt(expectedInt);
    updateRow.setaShort(expectedShort);

    shillelagh.update(updateRow);

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
    assertThat(cursor.getInt(6)).isEqualTo(0); // aBoolean, false maps to 0

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testUpdateBoxedPrimitives() {
    // Arrange
    double expectedDouble = 10000.55;
    float expectedFloat = 2.0f;
    long expectedLong = 200000;
    int expectedInt = 42;
    short expectedShort = 2;

    TestBoxedPrimitivesTable insertRow = new TestBoxedPrimitivesTable();
    insertRow.setaBoolean(true);
    insertRow.setaDouble((double) 23422123);
    insertRow.setaFloat(2934f);
    insertRow.setAnInteger(12);
    insertRow.setaLong((long) 21342);
    insertRow.setaShort((short) 234);

    // Act
    shillelagh.insert(insertRow);

    TestBoxedPrimitivesTable updateRow = new TestBoxedPrimitivesTable();
    updateRow.setId(insertRow.getId());
    updateRow.setaBoolean(false);
    updateRow.setaDouble(expectedDouble);
    updateRow.setaFloat(expectedFloat);
    updateRow.setAnInteger(expectedInt);
    updateRow.setaLong(expectedLong);
    updateRow.setaShort(expectedShort);

    shillelagh.update(updateRow);

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

  public void testUpdateJavaObjects() {
    // Arrange
    final Date now = new Date();
    final String expected = "TestString";
    TestJavaObjectsTable insertRow = new TestJavaObjectsTable();
    insertRow.setaDate(new Date(2342342));
    insertRow.setaString("NotATestString");

    // Act
    shillelagh.insert(insertRow);

    TestJavaObjectsTable updateRow = new TestJavaObjectsTable();
    updateRow.setId(insertRow.getId());
    updateRow.setaDate(now);
    updateRow.setaString(expected);

    shillelagh.update(updateRow);

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
      shillelagh.update(row);
    } catch (RuntimeException e) {
      assertThat(e.getMessage()).isEqualTo("Unable to update com.example.shillelagh.model." +
          "TestNotTableObject. Are you missing @Table annotation?");
      return;
    }

    // Assert
    throw new AssertionError("Expected Exception Not Thrown");
  }
}
