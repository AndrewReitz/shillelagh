/*
 * Copyright ${year} Andrew Reitz
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

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.shillelagh.model.Author;
import com.example.shillelagh.model.Book;
import com.example.shillelagh.model.Chapter;
import com.example.shillelagh.model.Image;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import rx.Notification;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import shillelagh.Shillelagh;

import static shillelagh.Shillelagh.getTableName;

public class MainActivity extends Activity {

  private static final String TAG = "ShillelaghExample";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ShillelaghApp shillelaghApp = ShillelaghApp.get(this);
    final Shillelagh shillelagh = shillelaghApp.getShillelagh();

    Author author1 = new Author("Icculus");

    final List<Chapter> chapters = Arrays.asList(
        new Chapter("Chapter 1"),
        new Chapter("Chapter 2"),
        new Chapter("Chapter 3")
    );

    final byte[] imageData = { 1, 2, 3, 4, 5 };
    final Image image = new Image("Awesome Image", new GregorianCalendar(2000, 1, 1).getTime(),
        imageData);

    final Date published = Calendar.getInstance().getTime();
    Book book = new Book("The Helping Phriendly Book", author1, published, chapters, image);

    shillelagh.insert(book);

    author1.setName("Wilson");
    shillelagh.update(author1);

    List<Author> authors = shillelagh.rawQuery(Author.class,
        "SELECT * FROM %s WHERE name = \'%s\'", getTableName(Author.class), author1.getName());
    for (Author a : authors) {
      Log.d(TAG, String.format("Author single select: %s", a.getName()));
    }

    Cursor bookCursor = shillelagh.rawQuery("SELECT * FROM %s", getTableName(Book.class));
    List<Book> books = shillelagh.map(Book.class, bookCursor);
    for (Book b : books) {
      Log.d(TAG, String.format("Book: %s", b));
      shillelagh.delete(Book.class, b.getId());
    }

    for (Author a : authors) {
      shillelagh.delete(a);
    }

    shillelagh.createQuery(Chapter.class)
        .toObservable()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .doOnNext(new Action1<Chapter>() {
          @Override public void call(Chapter chapter) {
            shillelagh.delete(chapter);
          }
        })
        .map(new Func1<Chapter, String>() {
          @Override public String call(Chapter chapter) {
            return chapter.getChapter();
          }
        })
        .subscribe(new Action1<String>() {
          @Override public void call(String title) {
            Log.d(TAG, title);
          }
        });
  }
}
