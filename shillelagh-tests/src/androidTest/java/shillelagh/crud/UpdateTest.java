/*
 * Copyright ${year} Andrew Reitz
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

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.example.shillelagh.TestSQLiteOpenHelper;
import com.example.shillelagh.model.TestBlobs;
import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestNotTableObject;
import com.example.shillelagh.model.TestOneToMany;
import com.example.shillelagh.model.TestOneToOne;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Date;

import shillelagh.Shillelagh;

import static shillelagh.Shillelagh.getTableName;
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
    getContext().deleteDatabase(TestSQLiteOpenHelper.DATABASE_NAME);
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
        "SELECT * FROM " + getTableName(TestPrimitiveTable.class), null);

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
        "SELECT * FROM " + getTableName(TestBoxedPrimitivesTable.class), null);

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
        "SELECT * FROM " + getTableName(TestJavaObjectsTable.class), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1);
    assertThat(cursor.getString(1)).isEqualTo(expected);
    assertThat(cursor.getLong(2)).isEqualTo(now.getTime());

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testUpdateBlob() throws IOException, ClassNotFoundException {
    // Arrange
    Byte[] expectedByteArray = new Byte[5];
    for (int i = 0; i < expectedByteArray.length; i++) {
      expectedByteArray[i] = (byte) (expectedByteArray.length - i);
    }

    byte[] expectedOtherByteArray = new byte[10];
    for (byte i = 0; i < expectedOtherByteArray.length; i++) {
      expectedOtherByteArray[i] = i;
    }

    TestBlobs.TestBlobObject expectedTestBlobObject = new TestBlobs.TestBlobObject();
    expectedTestBlobObject.testString = "hello world!!";

    TestBlobs insertTestBlob = new TestBlobs();
    insertTestBlob.setaTestBlobObject(new TestBlobs.TestBlobObject());
    insertTestBlob.setaByteArray(new Byte[5]);
    insertTestBlob.setAnotherByteArray(new byte[40]);

    // Act
    shillelagh.insert(insertTestBlob);

    TestBlobs updateTestBlob = new TestBlobs();
    updateTestBlob.setId(insertTestBlob.getId());
    updateTestBlob.setaByteArray(expectedByteArray);
    updateTestBlob.setAnotherByteArray(expectedOtherByteArray);
    updateTestBlob.setaTestBlobObject(expectedTestBlobObject);

    shillelagh.update(updateTestBlob);

    // Assert
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + getTableName(TestBlobs.class), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1);
    assertThat(this.<Byte[]>deserialize(cursor.getBlob(1))).isEqualTo(expectedByteArray);
    assertThat(cursor.getBlob(2)).isEqualTo(expectedOtherByteArray);
    assertThat(this.<TestBlobs.TestBlobObject>deserialize(cursor.getBlob(3)))
        .isEqualsToByComparingFields(expectedTestBlobObject);

    cursor.close();
  }

  public void testUpdateOneToOne() {
    // Arrange
    final String expectedString = "TEST STRING";
    final TestOneToOne.Child expectedChild = new TestOneToOne.Child(expectedString);
    final TestOneToOne expectedOneToOne = new TestOneToOne(expectedChild);

    final TestOneToOne.Child insertTestChild = new TestOneToOne.Child("Unexpected");
    final TestOneToOne insertTestOneToOne = new TestOneToOne(insertTestChild);

    // Act
    shillelagh.insert(insertTestOneToOne);
    shillelagh.insert(expectedChild);

    expectedOneToOne.setId(insertTestOneToOne.getId());
    shillelagh.update(expectedOneToOne);

    // Assert
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + getTableName(TestOneToOne.class), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1);
    assertThat(cursor.getLong(1)).isEqualTo(2);

    cursor.close();

    cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        String.format("SELECT * FROM %s WHERE Id = %d",
            getTableName(TestOneToOne.Child.class), 2), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(2);
    assertThat(cursor.getString(1)).isEqualTo(expectedString);

    cursor.close();
  }

  public void testUpdateOneToMany() {
    // Arrange
    final String expectedStringParent = "Test String";
    final String expectedStringChild = "another test string";
    final int expectedIntChild = 34;
    final TestOneToMany.Child expectedChild = new TestOneToMany.Child(
        "unexpected", expectedIntChild);

    TestOneToMany testOneToMany = new TestOneToMany("Some Value", Arrays.asList(expectedChild));

    // Act
    shillelagh.insert(testOneToMany);

    expectedChild.setTestString(expectedStringChild);
    testOneToMany.setSomeValue(expectedStringParent);
    shillelagh.update(testOneToMany);

    // Assert
    Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        "SELECT * FROM " + getTableName(TestOneToMany.class), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1);
    assertThat(cursor.getString(1)).isEqualTo(expectedStringParent);

    cursor.close();

    cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(
        String.format("SELECT * FROM %s WHERE com_example_shillelagh_model_testonetomany = %d",
            getTableName(TestOneToMany.Child.class), 1), null);

    assertThat(cursor.getCount()).isEqualTo(1);

    assertThat(cursor.moveToFirst()).isTrue();
    assertThat(cursor.getLong(0)).isEqualTo(1);
    assertThat(cursor.getString(1)).isEqualTo(expectedStringChild);
    assertThat(cursor.getInt(2)).isEqualTo(expectedIntChild);
    assertThat(cursor.getInt(3)).isEqualTo(1);

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

  @SuppressWarnings("unchecked")
  private <K> K deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
    return (K) objectInputStream.readObject();
  }
}
