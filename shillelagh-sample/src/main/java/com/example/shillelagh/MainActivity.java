package com.example.shillelagh;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class MainActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SQLiteOpenHelper sqliteOpenHelper = new ExampleSqliteHelper(this);
    SQLiteDatabase writableDatabase = sqliteOpenHelper.getWritableDatabase();
  }
}
