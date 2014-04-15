package com.example.shillelagh;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import shillelagh.Shillelagh;

public class ShillelaghApp extends Application {
  @Override public void onCreate() {
    super.onCreate();

    SQLiteOpenHelper sqliteOpenHelper = new ExampleSqliteHelper(this);
    Shillelagh.init(sqliteOpenHelper);

    if (BuildConfig.DEBUG) {
      Shillelagh.setDebug(true);
    }
  }
}
