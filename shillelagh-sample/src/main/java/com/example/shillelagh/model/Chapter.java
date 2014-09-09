package com.example.shillelagh.model;

import shillelagh.Field;
import shillelagh.OrmOnly;
import shillelagh.Table;

@Table
public class Chapter extends Base {
  @Field String chapter;

  /**
   * Used internally by Shillelagh. OrmOnly Annotation is only a documentation annotation, and
   * is not required for Shillelagh usage.
   */
  @OrmOnly Chapter() { }

  public Chapter(String chapter) {
    this.chapter = chapter;
  }

  public String getChapter() {
    return chapter;
  }

  @Override public String toString() {
    return "Chapter{" +
        "chapter='" + chapter + '\'' +
        '}';
  }
}
