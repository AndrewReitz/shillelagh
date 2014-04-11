package com.example.shillelagh;

import shillelagh.Field;
import shillelagh.Table;

@Table
public class Author extends BaseDbModel {
  @Field String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
