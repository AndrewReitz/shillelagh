package com.example.shillelagh;

import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.example.shillelagh.model.TestPrimitiveTable;

import shillelagh.Shillelagh;

public class InsertTest extends AndroidTestCase {

  private Shillelagh shillelagh;

  @Override protected void setUp() throws Exception {
    super.setUp();

    final SQLiteOpenHelper sqliteOpenHelper = new TestSQLiteOpenHelper(getContext());
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

    // Act
    shillelagh.insert(object);

    // Assert
  }
}
