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

package com.example.shillelagh.model;

import java.util.Date;
import java.util.List;

import shillelagh.Column;
import shillelagh.Table;

@Table
public final class Book extends Base {
  @Column String title;
  @Column Author author;
  @Column Date published;
  @Column List<Chapter> chapters;
  @Column(isBlob = true) Image image;

  public Book(String title, Author author, Date published, List<Chapter> chapters, Image image) {
    this.title = title;
    this.author = author;
    this.published = published;
    this.chapters = chapters;
    this.image = image;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public Date getPublished() {
    return published;
  }

  public List<Chapter> getChapters() {
    return chapters;
  }

  public Image getImage() {
    return image;
  }

  @Override public String toString() {
    return "Book{" +
        "title='" + title + '\'' +
        ", author=" + author +
        ", published=" + published +
        ", chapters=" + chapters +
        ", image=" + image +
        '}';
  }
}
