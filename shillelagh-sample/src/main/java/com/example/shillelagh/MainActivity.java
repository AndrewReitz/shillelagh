package com.example.shillelagh;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.shillelagh.model.Author;
import com.example.shillelagh.model.Book;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import shillelagh.Shillelagh;

public class MainActivity extends Activity {

  private static final String TAG = "ShillelaghTest";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Author author = new Author();
    author.setName("Icculus");

    Author author2 = new Author();
    author2.setName("Test Author");

    Book book = new Book();
    book.setPublished(Calendar.getInstance().getTime());
    book.setTitle("The Helping Phriendly Book");

    Shillelagh.insert(author);
    Shillelagh.insert(author2);
    Shillelagh.insert(book);

    author.setName("Wilson");
    Shillelagh.update(author);

    Cursor cursor = Shillelagh.rawQuery("SELECT * FROM Author"); //id, name
    List<Author> authors = Shillelagh.map(Author.class, cursor);
    Log.d(TAG, String.format("Author List Size: %d", authors.size()));
    for(Author a : authors) {
      Log.d(TAG, String.format("Author: %s", a.getName()));
    }

    Shillelagh.delete(author);
    Shillelagh.delete(Book.class, book.getId());
  }
}
