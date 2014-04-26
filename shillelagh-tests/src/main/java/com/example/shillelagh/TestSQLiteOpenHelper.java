package com.example.shillelagh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestPrimitiveTable;

import shillelagh.Shillelagh;

public class TestSQLiteOpenHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "shillelagh_test.db";
  private static final int DATABASE_VERSION = 1;

  public TestSQLiteOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    Shillelagh.createTable(db, TestBoxedPrimitivesTable.class);
    Shillelagh.createTable(db, TestPrimitiveTable.class);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Simplistic solution, you will lose your data though, good for debug builds bad for prod
    Shillelagh.dropTable(db, TestPrimitiveTable.class);
    Shillelagh.dropTable(db, TestBoxedPrimitivesTable.class);
    onCreate(db);
  }
}
