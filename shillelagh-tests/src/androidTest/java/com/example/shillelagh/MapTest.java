package com.example.shillelagh;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestBlobs;
import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;

import shillelagh.Field;
import shillelagh.Id;
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

  public void testMapCursorToBoxedPrimitives() {
    // Arrange
    int expectedBoolean = 0;
    double expectedDouble = 100000;
    float expectedFloat = 4.5f;
    long expectedLong = 300000;
    int expectedInt = 100;
    short expectedShort = 1;

    String testBoxedPrimitivesInsert = String.format("INSERT INTO %s (aBoolean, aDouble, aFloat, anInteger, " +
        "aLong, aShort) VALUES (%s, %s, %s, %s, %s, %s);", TestBoxedPrimitivesTable.class.getSimpleName(),
        expectedBoolean, expectedDouble, expectedFloat, expectedInt, expectedLong, expectedShort);

    // Act
    sqliteOpenHelper.getWritableDatabase().execSQL(testBoxedPrimitivesInsert);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestBoxedPrimitivesTable.class.getSimpleName(), null);

    List<TestBoxedPrimitivesTable> result = shillelagh.map(TestBoxedPrimitivesTable.class, cursor);

    // Assert
    assertThat(result.size()).isEqualTo(1);
    TestBoxedPrimitivesTable resultRow = result.get(0);
    assertThat(resultRow.getId()).isEqualTo(1);
    assertThat(resultRow.getaShort()).isEqualTo(expectedShort);
    assertThat(resultRow.getAnInteger()).isEqualTo(expectedInt);
    assertThat(resultRow.getaLong()).isEqualTo(expectedLong);
    assertThat(resultRow.getaDouble()).isEqualTo(expectedDouble);
    assertThat(resultRow.getaBoolean()).isFalse();
  }

  public void testMapToPrimitives() {
    // Arrange
    int expectedBoolean = 1;
    double expectedDouble = 2900;
    float expectedFloat = -3.5f;
    long expectedLong = 3000000;
    int expectedInt = -100;
    short expectedShort = 23;

    String testPrimitiveInsert = String.format("INSERT INTO %s (aShort, anInt," +
        "aLong, aFloat, aDouble, aBoolean) VALUES (%s, %s, %s, %s, %s, %s);",
        TestPrimitiveTable.class.getSimpleName(), expectedShort, expectedInt,
        expectedLong, expectedFloat, expectedDouble, expectedBoolean);

    // Act
    sqliteOpenHelper.getWritableDatabase().execSQL(testPrimitiveInsert);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestPrimitiveTable.class.getSimpleName(), null);
    List<TestPrimitiveTable> result = shillelagh.map(TestPrimitiveTable.class, cursor);

    // Assert
    assertThat(result.size()).isEqualTo(1);
    TestPrimitiveTable resultRow = result.get(0);
    assertThat(resultRow.getId()).isEqualTo(1);
    assertThat(resultRow.getaDouble()).isEqualTo(expectedDouble);
    assertThat(resultRow.getaFloat()).isEqualTo(expectedFloat);
    assertThat(resultRow.getaLong()).isEqualTo(expectedLong);
    assertThat(resultRow.getAnInt()).isEqualTo(expectedInt);
    assertThat(resultRow.getaShort()).isEqualTo(expectedShort);
    assertThat(resultRow.isaBoolean()).isTrue();

  }

  public void testMapToJavaObjects() {
    // Arrange
    String expectedString = "TestString";
    Date expectedDate = new Date();
    String testJavaObjectsInsert = String.format("INSERT INTO %s (aString, aDate)" +
        " VALUES ('%s', %s);", TestJavaObjectsTable.class.getSimpleName(), expectedString, expectedDate.getTime());

    // Act
    sqliteOpenHelper.getWritableDatabase().execSQL(testJavaObjectsInsert);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestJavaObjectsTable.class.getSimpleName(), null);

    List<TestJavaObjectsTable> result = shillelagh.map(TestJavaObjectsTable.class, cursor);

    // Assert
    assertThat(result.size()).isEqualTo(1);
    TestJavaObjectsTable resultRow = result.get(0);
    assertThat(resultRow.getId()).isEqualTo(1);
    assertThat(resultRow.getaDate()).isEqualTo(expectedDate);
    assertThat(resultRow.getaString()).isEqualTo(expectedString);
  }

  public void testMapToBlobs() throws IOException {
    // Arrange
    Byte[] expectedByteArray = new Byte[5];
    for (int i = 0; i < expectedByteArray.length; i++) {
      expectedByteArray[i] = (byte) (expectedByteArray.length - i);
    }

    byte[] expectedOtherByteArray = new byte[100];
    for (byte i = 0; i < expectedOtherByteArray.length; i++) {
      expectedOtherByteArray[i] = i;
    }

    TestBlobs.TestBlobObject expectedTestBlobObject = new TestBlobs.TestBlobObject();
    expectedTestBlobObject.testString = "hello world!!";

    ContentValues values = new ContentValues();
    values.put("aByteArray", serialize(expectedByteArray));
    values.put("anotherByteArray", expectedOtherByteArray);
    values.put("aTestBlobObject", serialize(expectedTestBlobObject));

    // Act
    sqliteOpenHelper.getWritableDatabase().insert(TestBlobs.class.getSimpleName(), null, values);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + TestBlobs.class.getSimpleName(), null);

    List<TestBlobs> result = shillelagh.map(TestBlobs.class, cursor);

    // Assert
    assertThat(result.size()).isEqualTo(1);
    TestBlobs resultRow = result.get(0);
    assertThat(resultRow.getId()).isEqualTo(1);
    assertThat(resultRow.getaByteArray()).isEqualTo(expectedByteArray);
    assertThat(resultRow.getAnotherByteArray()).isEqualTo(expectedOtherByteArray);
    assertThat(resultRow.getaTestBlobObject()).isEqualsToByComparingFields(expectedTestBlobObject);
  }

  public <K> byte[] serialize(K object) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    objectOutputStream.writeObject(object);
    return byteArrayOutputStream.toByteArray();
  }
}
