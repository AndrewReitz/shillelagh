package com.example.shillelagh;

import android.app.Application;

import shillelagh.Shillelagh;

public class ShillelaghApp extends Application {
  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      Shillelagh.setDebug(true);
    }
  }
}
