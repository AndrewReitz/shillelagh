package com.example.shillelagh;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.util.Date;
import java.util.Random;

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
    Random rand = new Random();
    double expectedDouble = rand.nextDouble();
    float expectedFloat = rand.nextFloat();
    long expectedlong = rand.nextLong();
    int expectedInt = rand.nextInt();
    short expectedShort = (short) rand.nextInt(Short.MAX_VALUE);

    TestPrimitiveTable row = new TestPrimitiveTable();
    row.setaBoolean(true);
    row.setaDouble(expectedDouble);
    row.setaFloat(expectedFloat);
    row.setaLong(expectedlong);
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
    assertThat(cursor.getLong(3)).isEqualTo(expectedlong); // aLong
    assertThat(cursor.getFloat(4)).isEqualTo(expectedFloat); // aFloat
    assertThat(cursor.getDouble(5)).isEqualTo(expectedDouble); // aDouble
    assertThat(cursor.getInt(6)).isEqualTo(1); // aBoolean, true maps to 1

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testInsertBoxedPrimitives() {
    // Arrange
    Random rand = new Random();
    double expectedDouble = rand.nextDouble();
    float expectedFloat = rand.nextFloat();
    long expectedlong = rand.nextLong();
    int expectedInt = rand.nextInt();
    short expectedShort = (short) rand.nextInt(Short.MAX_VALUE);

    TestBoxedPrimitivesTable row = new TestBoxedPrimitivesTable();
    row.setaBoolean(false);
    row.setaDouble(expectedDouble);
    row.setaFloat(expectedFloat);
    row.setAnInteger(expectedInt);
    row.setaLong(expectedlong);
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
    assertThat(cursor.getLong(5)).isEqualTo(expectedlong); // aLong
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

  // TODO Make Insert Error Tests
}
