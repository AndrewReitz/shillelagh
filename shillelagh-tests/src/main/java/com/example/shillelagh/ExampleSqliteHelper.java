package com.example.shillelagh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.shillelagh.model.Author;
import com.example.shillelagh.model.Book;

import shillelagh.Shillelagh;

public class ExampleSqliteHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "shillelagh_example.db";
  private static final int DATABASE_VERSION = 3;

  public ExampleSqliteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    Shillelagh.createTable(db, Author.class);
    Shillelagh.createTable(db, Book.class);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Simplistic solution, you will lose your data though, good for debug builds bad for prod
    Shillelagh.dropTable(db, Author.class);
    Shillelagh.dropTable(db, Book.class);
    onCreate(db);
  }
}
