package com.example.shillelagh;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import shillelagh.Shillelagh;

public class ShillelaghApp extends Application {
  @Override public void onCreate() {
    super.onCreate();

    SQLiteOpenHelper sqliteOpenHelper = new ExampleSqliteHelper(this);
    Shillelagh.init(sqliteOpenHelper);

    SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();

    if (BuildConfig.DEBUG) {
      Shillelagh.setDebug(true);
    }
  }
}
