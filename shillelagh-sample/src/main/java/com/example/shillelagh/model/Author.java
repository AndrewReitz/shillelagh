package com.example.shillelagh.model;

import shillelagh.Field;
import shillelagh.Table;

@Table
public class Author extends Base {
  @Field String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
