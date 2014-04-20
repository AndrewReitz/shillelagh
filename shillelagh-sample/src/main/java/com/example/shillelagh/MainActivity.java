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

    ShillelaghApp shillelaghApp = ShillelaghApp.get(this);
    Shillelagh shillelagh = shillelaghApp.getShillelagh();

    Author author1 = new Author();
    author1.setName("Icculus");

    Author author2 = new Author();
    author2.setName("Col. Forbin");

    Author author3 = new Author();
    author3.setName("Tela");

    Book book = new Book();
    book.setPublished(Calendar.getInstance().getTime());
    book.setTitle("The Helping Phriendly Book");

    shillelagh.insert(author1);
    shillelagh.insert(author2);
    shillelagh.insert(author3);
    shillelagh.insert(book);

    author1.setName("Wilson");
    shillelagh.update(author1);

    Cursor cursor = shillelagh.rawQuery("SELECT * FROM Author WHERE name = \'" + author1.getName() + "\'");
    List<Author> authors = Shillelagh.map(Author.class, cursor);
    for(Author a : authors) {
      Log.d(TAG, String.format("Author single select: %s", a.getName()));
    }

    authors = shillelagh.rawQuery(Author.class, "SELECT * FROM Author");
    for(Author a : authors) {
      Log.d(TAG, String.format("Author: %s", a.getName()));
    }

    Cursor bookCurson = shillelagh.rawQuery("SELECT * FROM Book");
    List<Book> books = Shillelagh.map(Book.class, bookCurson);
    for(Book b : books) {
      Log.d(TAG, String.format("Book: %s", b));
      shillelagh.delete(Book.class, b.getId());
    }

    for (Author a : authors) {
      shillelagh.delete(a);
    }
  }
}
