package com.example.shillelagh;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import shillelagh.Shillelagh;

public class ShillelaghApp extends Application {

  /** Shillelagh Singleton */
  private Shillelagh shillelagh;

  @Override public void onCreate() {
    super.onCreate();

    SQLiteOpenHelper sqliteOpenHelper = new ExampleSqliteHelper(this);
    shillelagh = new Shillelagh(sqliteOpenHelper);

    if (BuildConfig.DEBUG) {
      Shillelagh.setDebug(true);
    }
  }

  /** Returns the instance of shillelagh for this application */
  public Shillelagh getShillelagh() {
    return shillelagh;
  }

  public static ShillelaghApp get(Context context) {
    return (ShillelaghApp) context.getApplicationContext();
  }
}
