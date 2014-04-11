package com.example.shillelagh;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.util.Calendar;

import shillelagh.Shillelagh;

public class MainActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SQLiteOpenHelper sqliteOpenHelper = new ExampleSqliteHelper(this);
    SQLiteDatabase writableDatabase = sqliteOpenHelper.getWritableDatabase();

    Author author = new Author();
    author.setName("Icculus");

    Book book = new Book();
    book.setPublished(Calendar.getInstance().getTime());
    book.setTitle("The Helping Friendly Book");

    Shillelagh.insert(writableDatabase, author);
    Shillelagh.insert(writableDatabase, book);
  }
}
