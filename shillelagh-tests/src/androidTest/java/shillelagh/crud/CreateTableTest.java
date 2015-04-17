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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestBlobs;
import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestNotTableObject;
import com.example.shillelagh.model.TestOneToMany;
import com.example.shillelagh.model.TestOneToOne;
import com.example.shillelagh.model.TestPrimitiveTable;

import java.io.File;

import java.util.List;
import java.util.Map;
import shillelagh.Column;
import shillelagh.Id;
import shillelagh.Shillelagh;
import shillelagh.Table;

import static org.assertj.core.api.Assertions.assertThat;
import static shillelagh.Shillelagh.getTableName;

public class CreateTableTest extends AndroidTestCase {

  private static final int CURRENT_DATABASE_VERSION = 1;
  private static final String TABLE_INFO_QUERY = "PRAGMA table_info(%S)";
  private static final int TABLE_INFO_NAME_COLUMN = 1;
  private static final int TABLE_INFO_TYPE_COLUMN = 2;
  private static final int TABLE_INFO_NULLABLE_COLUMN = 3;
  private static final int TABLE_INFO_DEFAULT_VALUE_COLUMN = 4;
  private static final int TABLE_INFO_PRIMARAY_KEY_COLUMN = 5;
  private static final String SQL_INTEGER = "INTEGER";
  private static final String SQL_REAL = "REAL";
  private static final String SQL_TEXT = "TEXT";
  private static final String SQL_BLOB = "BLOB";
  private SQLiteDatabase database;
  private File databaseFile;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    File dbDir = getContext().getDir("tests", Context.MODE_PRIVATE);
    databaseFile = new File(dbDir, "database_test.db");

    if (databaseFile.exists()) {
      //noinspection ResultOfMethodCallIgnored
      databaseFile.delete();
    }
    database = SQLiteDatabase.openOrCreateDatabase(databaseFile.getPath(), null);
    assertNotNull(database);
    database.setVersion(CURRENT_DATABASE_VERSION);
  }

  @Override
  protected void tearDown() throws Exception {
    database.close();
    //noinspection ResultOfMethodCallIgnored
    databaseFile.delete();
    super.tearDown();
  }

  public void testShouldCreatePrimitiveDatabase() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestPrimitiveTable.class);
    final Cursor cursor =
        database.rawQuery(String.format(TABLE_INFO_QUERY, getTableName(TestPrimitiveTable.class)),
            null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(7);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aShort");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("anInt");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aLong");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aFloat");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_REAL);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aDouble");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_REAL);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aBoolean");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateBoxedPrimitivesTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestBoxedPrimitivesTable.class);
    final Cursor cursor = database.rawQuery(
        String.format(TABLE_INFO_QUERY, getTableName(TestBoxedPrimitivesTable.class)), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(7);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("_id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aBoolean");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aDouble");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_REAL);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aFloat");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_REAL);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("anInteger");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aLong");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aShort");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateJavaObjectTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestJavaObjectsTable.class);
    final Cursor cursor =
        database.rawQuery(String.format(TABLE_INFO_QUERY, getTableName(TestJavaObjectsTable.class)),
            null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(3);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aString");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_TEXT);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aDate");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateBlobObjectsTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestBlobs.class);
    final Cursor cursor =
        database.rawQuery(String.format(TABLE_INFO_QUERY, getTableName(TestBlobs.class)), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(4);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aByteArray");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_BLOB);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("anotherByteArray");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_BLOB);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("aTestBlobObject");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_BLOB);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateOneToOneTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestOneToOne.class);
    final Cursor cursor =
        database.rawQuery(String.format(TABLE_INFO_QUERY, getTableName(TestOneToOne.class)), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(2);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("child");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateOneToManyParentTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestOneToMany.class);
    final Cursor cursor =
        database.rawQuery(String.format(TABLE_INFO_QUERY, getTableName(TestOneToMany.class)), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(2);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("someValue");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_TEXT);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldCreateOneToManyChildTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestOneToMany.OneToManyChild.class);
    final Cursor cursor = database.rawQuery(
        String.format(TABLE_INFO_QUERY, getTableName(TestOneToMany.OneToManyChild.class)), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(4);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("testString");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_TEXT);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("testInt");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualToIgnoringCase("TestOneToMany");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  /** Test for checking that a internal class can be created */
  public void testShouldCreateInnerObjectTable() {
    // Arrange

    // Act
    Shillelagh.createTable(database, TestOneToOne.OneToOneChild.class);
    final Cursor cursor = database.rawQuery(
        String.format(TABLE_INFO_QUERY, getTableName(TestOneToOne.OneToOneChild.class)), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(2);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("childName");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_TEXT);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  public void testShouldFailOnNonAnnotatedClass() {
    try {
      Shillelagh.createTable(database, TestNotTableObject.class);
    } catch (RuntimeException e) {
      assertThat(e.getMessage()).isEqualTo("com.example.shillelagh.model.TestNotTableObject is "
          + "not a table object.");
      return;
    }

    throw new AssertionError("Expected Exception Not Thrown");
  }

  public void testCreateCollectionsTable() {
    setName("Should create a table with a list and map as blobs");

    // Act
    Shillelagh.createTable(database, CollectionsTestTable.class);
    final Cursor cursor = database.rawQuery(
        String.format(TABLE_INFO_QUERY, getTableName(CollectionsTestTable.class)), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(3);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("id");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("maps");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_BLOB);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("lists");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_BLOB);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  @Table
  static final class CollectionsTestTable {
    @Id long id;
    @Column(isBlob = true) Map<String, String> maps;
    @Column(isBlob = true) List<String> lists;
  }

  public void testCreateTableWithCustomNames() {
    // Act
    Shillelagh.createTable(database, TestCustomNames.class);
    final Cursor cursor = database.rawQuery(
        String.format(TABLE_INFO_QUERY, getTableName(TestCustomNames.class)), null);

    // Assert
    assertThat(cursor.getCount()).isEqualTo(2);

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("MyIdColumn");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_INTEGER);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("1");

    assertThat(cursor.moveToNext()).isTrue();
    assertThat(cursor.getString(TABLE_INFO_NAME_COLUMN)).isEqualTo("MyColumn");
    assertThat(cursor.getString(TABLE_INFO_TYPE_COLUMN)).isEqualTo(SQL_TEXT);
    assertThat(cursor.getString(TABLE_INFO_NULLABLE_COLUMN)).isEqualTo("0");
    assertThat(cursor.getString(TABLE_INFO_PRIMARAY_KEY_COLUMN)).isEqualTo("0");

    assertThat(cursor.moveToNext()).isFalse();
    cursor.close();
  }

  @Table(name = "MyTable")
  static final class TestCustomNames {
    @Id(name = "MyIdColumn") long id;
    @Column(name = "MyColumn") String someColumn;
  }
}
