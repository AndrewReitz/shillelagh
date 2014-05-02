package com.example.shillelagh;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestPrimitiveTable;

import java.util.List;
import java.util.Random;

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
    String tmp = "INSERT INTO TestBoxedPrimitivesTable (aBoolean, aDouble, aFloat, anInteger, aLong, aShort) VALUES (0, 0.7305609574109958, 0.27616096, 159640438, -768017058020027579, 25805);";
    String tmp2 = "INSERT INTO TestJavaObjectsTable (aString, aDate) VALUES ('TestString', 1398996896871);";
    String tmp3 = "INSERT INTO TestPrimitiveTable (aShort, anInt, aLong, aFloat, aDouble, aBoolean) VALUES (29909, -1402799165, 336640275481577743, 0.44336712, 0.28459932805244603, 1);";
    // Arrange
    Random rand = new Random();
    double expectedDouble = rand.nextDouble();
    float expectedFloat = rand.nextFloat();
    long expectedLong = rand.nextLong();
    int expectedInt = rand.nextInt();
    short expectedShort = (short) rand.nextInt(Short.MAX_VALUE);

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

    List<TestPrimitiveTable> result = Shillelagh.map(TestPrimitiveTable.class, cursor);

    assertThat(result.size()).isEqualTo(1);
    TestPrimitiveTable testPrimitiveTable = result.get(1);
    assertThat(testPrimitiveTable.getId()).isEqualTo(1);
    assertThat(testPrimitiveTable.getaShort()).isEqualTo(expectedShort);
    assertThat(testPrimitiveTable.getAnInt()).isEqualTo(expectedInt);
    assertThat(testPrimitiveTable.getaLong()).isEqualTo(expectedLong);
    assertThat(testPrimitiveTable.getaDouble()).isEqualTo(expectedDouble);
    assertThat(testPrimitiveTable.isaBoolean()).isTrue();
  }
}
