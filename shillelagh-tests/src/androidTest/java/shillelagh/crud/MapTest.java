/*
 * Copyright 2014 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shillelagh.crud;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.example.shillelagh.TestSQLiteOpenHelper;
import com.example.shillelagh.model.TestBlobs;
import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestOneToMany;
import com.example.shillelagh.model.TestOneToOne;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import shillelagh.Shillelagh;

import static org.assertj.core.api.Assertions.assertThat;
import static shillelagh.Shillelagh.getTableName;

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
    final int expectedBoolean = 0;
    final double expectedDouble = 100000;
    final float expectedFloat = 4.5f;
    final long expectedLong = 300000;
    final int expectedInt = 100;
    final short expectedShort = 1;

    String testBoxedPrimitivesInsert = String.format("INSERT INTO %s (aBoolean, aDouble, " +
            "aFloat, anInteger, aLong, aShort) VALUES (%s, %s, %s, %s, %s, %s);",
        getTableName(TestBoxedPrimitivesTable.class), expectedBoolean, expectedDouble,
        expectedFloat, expectedInt, expectedLong, expectedShort);

    // Act
    sqliteOpenHelper.getWritableDatabase().execSQL(testBoxedPrimitivesInsert);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + getTableName(TestBoxedPrimitivesTable.class), null);

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
    final int expectedBoolean = 1;
    final double expectedDouble = 2900;
    final float expectedFloat = -3.5f;
    final long expectedLong = 3000000;
    final int expectedInt = -100;
    final short expectedShort = 23;

    String tableName = getTableName(TestPrimitiveTable.class);

    String testPrimitiveInsert = String.format("INSERT INTO %s (aShort, anInt," +
        "aLong, aFloat, aDouble, aBoolean) VALUES (%s, %s, %s, %s, %s, %s);",
        tableName, expectedShort, expectedInt,
        expectedLong, expectedFloat, expectedDouble, expectedBoolean);

    // Act
    sqliteOpenHelper.getWritableDatabase().execSQL(testPrimitiveInsert);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + tableName, null);
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
    final String expectedString = "TestString";
    final Date expectedDate = new Date();
    final String testJavaObjectsInsert = String.format("INSERT INTO %s (aString, aDate)" +
        " VALUES ('%s', %s);", getTableName(TestJavaObjectsTable.class),
        expectedString, expectedDate.getTime());

    // Act
    sqliteOpenHelper.getWritableDatabase().execSQL(testJavaObjectsInsert);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + getTableName(TestJavaObjectsTable.class), null);

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
    sqliteOpenHelper.getWritableDatabase().insert(getTableName(TestBlobs.class), null, values);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + getTableName(TestBlobs.class), null);

    List<TestBlobs> result = shillelagh.map(TestBlobs.class, cursor);

    // Assert
    assertThat(result.size()).isEqualTo(1);
    TestBlobs resultRow = result.get(0);
    assertThat(resultRow.getId()).isEqualTo(1);
    assertThat(resultRow.getaByteArray()).isEqualTo(expectedByteArray);
    assertThat(resultRow.getAnotherByteArray()).isEqualTo(expectedOtherByteArray);
    assertThat(resultRow.getaTestBlobObject()).isEqualsToByComparingFields(expectedTestBlobObject);
  }

  public void testMapOneToOne() {
    // Arrange
    final String expected = "TEST STRING";
    final TestOneToOne.OneToOneChild expectedChild = new TestOneToOne.OneToOneChild(expected);
    final TestOneToOne expectedOneToOne = new TestOneToOne(expectedChild);

    final ContentValues childContentValues = new ContentValues();
    childContentValues.put("childName", expected);

    final ContentValues oneToOneContentValues = new ContentValues();
    oneToOneContentValues.put("child", 1);

    final String tableName = getTableName(TestOneToOne.class);

    // Act
    sqliteOpenHelper.getWritableDatabase()
        .insert(getTableName(TestOneToOne.OneToOneChild.class), null, childContentValues);
    sqliteOpenHelper.getWritableDatabase()
        .insert(tableName, null, oneToOneContentValues);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase()
        .rawQuery("SELECT * FROM " + tableName, null);

    List<TestOneToOne> result = shillelagh.map(TestOneToOne.class, cursor);

    // Assert
    assertThat(result.size()).isEqualTo(1);
    TestOneToOne resultRow = result.get(0);
    assertThat(resultRow).isEqualsToByComparingFields(expectedOneToOne);
  }

  public void testMapOneToMany() {
    // Arrange
    final String childExpected = "TestString";
    final int childIntExpected = 12345;
    final String parentExpected = "SomeValue";
    final TestOneToMany.OneToManyChild
        child = new TestOneToMany.OneToManyChild(childExpected, childIntExpected);
    final TestOneToMany parent = new TestOneToMany(parentExpected, Arrays.asList(child));

    final ContentValues childContentValues = new ContentValues();
    childContentValues.put("testString", childExpected);
    childContentValues.put("testInt", childIntExpected);
    childContentValues.put("TestOneToMany", 1);

    final ContentValues parentContentValues = new ContentValues();
    parentContentValues.put("someValue", parentExpected);

    final String tableName = getTableName(TestOneToMany.class);

    // Act
    sqliteOpenHelper.getWritableDatabase()
        .insert(tableName, null, parentContentValues);
    sqliteOpenHelper.getWritableDatabase()
        .insert(getTableName(TestOneToMany.OneToManyChild.class), null, childContentValues);
    Cursor cursor = sqliteOpenHelper.getReadableDatabase()
        .rawQuery("SELECT * FROM " + tableName, null);

    List<TestOneToMany> result = shillelagh.map(TestOneToMany.class, cursor);

    // Assert
    assertThat(result.size()).isEqualTo(1);
    TestOneToMany resultRow = result.get(0);
    assertThat(resultRow).isEqualToComparingFieldByField(parent);
    assertThat(resultRow.getChildren().size()).isEqualTo(1);
    assertThat(resultRow.getChildren().get(0)).isEqualToComparingFieldByField(child);
  }

  private <K> byte[] serialize(K object) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
    objectOutputStream.writeObject(object);
    return byteArrayOutputStream.toByteArray();
  }
}
