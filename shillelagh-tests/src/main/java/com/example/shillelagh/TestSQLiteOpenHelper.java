/*
 * Copyright 2014 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.shillelagh;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.shillelagh.model.TestBlobs;
import com.example.shillelagh.model.TestBoxedPrimitivesTable;
import com.example.shillelagh.model.TestJavaObjectsTable;
import com.example.shillelagh.model.TestOneToMany;
import com.example.shillelagh.model.TestOneToOne;
import com.example.shillelagh.model.TestParcelable;
import com.example.shillelagh.model.TestPrimitiveTable;

import shillelagh.Shillelagh;

public class TestSQLiteOpenHelper extends SQLiteOpenHelper {
  public static final String DATABASE_NAME = "shillelagh_test.db";
  private static final int DATABASE_VERSION = 7;

  public TestSQLiteOpenHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override public void onCreate(SQLiteDatabase db) {
    Shillelagh.createTable(db, TestBoxedPrimitivesTable.class);
    Shillelagh.createTable(db, TestPrimitiveTable.class);
    Shillelagh.createTable(db, TestJavaObjectsTable.class);
    Shillelagh.createTable(db, TestBlobs.class);
    Shillelagh.createTable(db, TestOneToOne.class);
    Shillelagh.createTable(db, TestOneToOne.OneToOneChild.class);
    Shillelagh.createTable(db, TestOneToMany.class);
    Shillelagh.createTable(db, TestOneToMany.OneToManyChild.class);
    Shillelagh.createTable(db, TestParcelable.class);
  }

  @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Simplistic solution, you will lose your data though, good for debug builds bad for prod
    Shillelagh.dropTable(db, TestPrimitiveTable.class);
    Shillelagh.dropTable(db, TestBoxedPrimitivesTable.class);
    Shillelagh.dropTable(db, TestPrimitiveTable.class);
    Shillelagh.dropTable(db, TestBlobs.class);
    Shillelagh.dropTable(db, TestOneToOne.class);
    Shillelagh.dropTable(db, TestOneToOne.OneToOneChild.class);
    Shillelagh.dropTable(db, TestOneToMany.class);
    Shillelagh.dropTable(db, TestOneToMany.OneToManyChild.class);
    Shillelagh.dropTable(db, TestParcelable.class);
    onCreate(db);
  }
}
