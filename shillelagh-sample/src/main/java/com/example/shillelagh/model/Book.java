package com.example.shillelagh.model;

import java.util.Date;

import shillelagh.Field;
import shillelagh.Table;

@Table
public class Book extends Base {
  @Field String title;
//  @Field Author author;
  @Field Date published;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

//  public Author getAuthor() {
//    return author;
//  }
//
//  public void setAuthor(Author author) {
//    this.author = author;
//  }

  public Date getPublished() {
    return published;
  }

  public void setPublished(Date published) {
    this.published = published;
  }

  @Override public String toString() {
    return String.format("Title: %s, Published: %s", title, published);
  }
}
