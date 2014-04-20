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

    Author author = new Author();
    author.setName("Icculus");

    Author author2 = new Author();
    author2.setName("Test Author");

    Book book = new Book();
    book.setPublished(Calendar.getInstance().getTime());
    book.setTitle("The Helping Phriendly Book");

    shillelagh.insert(author);
    shillelagh.insert(author2);
    shillelagh.insert(book);

    author.setName("Wilson");
    shillelagh.update(author);

    Cursor cursor = shillelagh.rawQuery("SELECT * FROM Author");
    List<Author> authors = Shillelagh.map(Author.class, cursor);
    for(Author a : authors) {
      Log.d(TAG, String.format("Author: %s", a.getName()));
    }

    Cursor bookCurson = shillelagh.rawQuery("SELECT * FROM Book");
    List<Book> books = Shillelagh.map(Book.class, bookCurson);
    for(Book b : books) {
      Log.d(TAG, String.format("Book: %s", book));
    }


    shillelagh.delete(Book.class, book.getId());

    for (Author author1 : authors) {
      shillelagh.delete(author1);
    }
  }
}
