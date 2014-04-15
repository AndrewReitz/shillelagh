package com.example.shillelagh;

import android.app.Activity;
import android.os.Bundle;

import com.example.shillelagh.model.Author;
import com.example.shillelagh.model.Book;

import java.util.Calendar;

import shillelagh.Shillelagh;

public class MainActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Author author = new Author();
    author.setName("Icculus");

    Book book = new Book();
    book.setPublished(Calendar.getInstance().getTime());
    book.setTitle("The Helping Phriendly Book");

    Shillelagh.insert(author);
    Shillelagh.insert(book);

    author.setName("Wilson");
    Shillelagh.update(author);
  }
}
