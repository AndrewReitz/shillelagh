package com.example.shillelagh;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.util.List;

import shillelagh.Shillelagh;

import static org.fest.assertions.api.Assertions.assertThat;

/** Tests for mapping db objects to jave objects in Shillelagh */
public class MapTest extends AndroidTestCase {

  private SQLiteOpenHelper sqliteOpenHelper;
  private Shillelagh shillelagh;

  @Override protected void setUp() throws Exception {
    super.setUp();

    sqliteOpenHelper = new TestSQLiteOpenHelper(getContext());
    shillelagh = new Shillelagh(sqliteOpenHelper);
  }

  public void testMapCursorToPrimitives() {


    // Arrange
    int expectedBoolean = 0;
    double expectedDouble = 100000;
    float expectedFloat = 4.5f;
    long expectedLong = 300000;
    int expectedInt = 100;
    short expectedShort = 1;

    String testBoxedPrimitivesTest = String.format("INSERT INTO %s (aBoolean, aDouble, aFloat, anInteger, " +
        "aLong, aShort) VALUES (%s, %s, %s, %s, %s, %s);", TestBoxedPrimitivesTable.class.getSimpleName(),
        expectedBoolean, expectedDouble, expectedFloat, expectedInt, expectedLong, expectedShort);

    String tmp2 = "INSERT INTO TestJavaObjectsTable (aString, aDate) VALUES ('TestString', 1398996896871);";
    String tmp3 = "INSERT INTO TestPrimitiveTable (aShort, anInt, aLong, aFloat, aDouble, aBoolean) VALUES (29909, -1402799165, 336640275481577743, 0.44336712, 0.28459932805244603, 1);";

    // Act
    sqliteOpenHelper.getWritableDatabase().execSQL(testBoxedPrimitivesTest);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestBoxedPrimitivesTable.class.getSimpleName(), null);

    assertThat(cursor.getCount()).isGreaterThanOrEqualTo(1);

    List<TestBoxedPrimitivesTable> result = Shillelagh.map(TestBoxedPrimitivesTable.class, cursor);

    // Assert
    assertThat(result.size()).isEqualTo(1);
    TestBoxedPrimitivesTable testBoxedPrimitivesTable = result.get(0);
    assertThat(testBoxedPrimitivesTable.getId()).isEqualTo(1);
    assertThat(testBoxedPrimitivesTable.getaShort()).isEqualTo(expectedShort);
    assertThat(testBoxedPrimitivesTable.getAnInteger()).isEqualTo(expectedInt);
    assertThat(testBoxedPrimitivesTable.getaLong()).isEqualTo(expectedLong);
    assertThat(testBoxedPrimitivesTable.getaDouble()).isEqualTo(expectedDouble);
    assertThat(testBoxedPrimitivesTable.getaBoolean()).isFalse();
  }
}
