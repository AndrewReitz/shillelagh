package com.example.shillelagh;

import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

public class ShillelaghTest extends AndroidTestCase {

  public void testShouldCreateDatabase() {
    // Arrange
    SQLiteOpenHelper sqLiteOpenHelper = new TestSQLiteOpenHelper(getContext());

    // Act

    // Assert
  }
}
