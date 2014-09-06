package com.example.shillelagh.model;

import java.util.List;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.OrmOnly;
import shillelagh.Table;

@Table public class TestOneToMany {
  @Id long id;
  @Field String someValue;
  @Field List<Child> children;

  @OrmOnly TestOneToMany() {}

  public TestOneToMany(String someValue, List<Child> children) {
    this.someValue = someValue;
    this.children = children;
  }

  @Table public static class Child {
    @Id long id;
    @Field String testString;
    @Field int testInt;

    @OrmOnly Child() { }

    public Child(String testString, int testInt) {
      this.testString = testString;
      this.testInt = testInt;
    }
  }
}
