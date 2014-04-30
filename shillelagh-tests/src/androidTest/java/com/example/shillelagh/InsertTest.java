package com.example.shillelagh;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.util.Calendar;
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

  public void testInsertPrimitives() {
    // Arrange
    TestPrimitiveTable row = new TestPrimitiveTable();
    row.setaBoolean(true);
    row.setaDouble(10);
    row.setaFloat(15f);
    row.setaLong(4);
    row.setAnInt(9);
    row.setaShort((short) 6);

    // Act
    shillelagh.insert(row);

    // Assert
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestPrimitiveTable.class.getSimpleName(), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1); // id column
    assertThat(cursor.getShort(1)).isEqualTo((short) 6); // aShort
    assertThat(cursor.getInt(2)).isEqualTo(9); // anInt
    assertThat(cursor.getLong(3)).isEqualTo(4); // aLong
    assertThat(cursor.getFloat(4)).isEqualTo(15f); // aFloat
    assertThat(cursor.getDouble(5)).isEqualTo(10); // aDouble
    assertThat(cursor.getInt(6)).isEqualTo(1); // aBoolean

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testInsertBoxedPrimitives() {
    // Arrange
    TestBoxedPrimitivesTable row = new TestBoxedPrimitivesTable();
    row.setaBoolean(false);
    row.setaDouble(Double.MAX_VALUE);
    row.setaFloat(Float.MAX_VALUE);
    row.setAnInteger(Integer.MAX_VALUE);
    row.setaLong(Long.MAX_VALUE);
    row.setaShort(Short.MAX_VALUE);

    // Act
    shillelagh.insert(row);

    // Assert
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestBoxedPrimitivesTable.class.getSimpleName(), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1); // id column
    assertThat(cursor.getInt(1)).isEqualTo(0); // aBoolean
    assertThat(cursor.getDouble(2)).isEqualTo(Double.MAX_VALUE); // aDouble
    assertThat(cursor.getFloat(3)).isEqualTo(Float.MAX_VALUE); // aFloat
    assertThat(cursor.getInt(4)).isEqualTo(Integer.MAX_VALUE); // anInteger
    assertThat(cursor.getLong(5)).isEqualTo(Long.MAX_VALUE); // aLong
    assertThat(cursor.getShort(6)).isEqualTo(Short.MAX_VALUE); // aShort

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
        "SELECT * FROM " + TestJavaObjectsTable.class.getName(), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1);
    assertThat(cursor.getString(1)).isEqualTo(expected);
    assertThat(cursor.getLong(2)).isEqualTo(now.getTime());

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }
}
