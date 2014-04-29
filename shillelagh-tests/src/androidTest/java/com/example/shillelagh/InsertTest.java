package com.example.shillelagh;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestPrimitiveTable;

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
    TestPrimitiveTable object = new TestPrimitiveTable();
    object.setaBoolean(true);
    object.setaDouble(10);
    object.setaFloat(15f);
    object.setaLong(4);
    object.setAnInt(9);
    object.setaShort((short) 6);

    // Act
    shillelagh.insert(object);

    // Assert
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM TestPrimitiveTable", null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getInt(0)).isEqualTo(1); // id column
    assertThat(cursor.getShort(1)).isEqualTo((short) 6); // aShort
    assertThat(cursor.getInt(2)).isEqualTo(9); // anInt
    assertThat(cursor.getLong(3)).isEqualTo(4); // aLong
    assertThat(cursor.getFloat(4)).isEqualTo(15f); // aFloat
    assertThat(cursor.getDouble(5)).isEqualTo(10); // aDouble
    assertThat(cursor.getInt(6)).isEqualTo(1); // aBoolean

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }
}
