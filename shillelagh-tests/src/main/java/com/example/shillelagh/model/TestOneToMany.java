package com.example.shillelagh.model;

import java.util.List;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table public class TestOneToMany {
  @Id long id;
  @Field String someValue;
  @Field List<Child> children;

  @Table public static class Child {
    @Id long id;
    @Field String testString;
    @Field int testInt;
  }
}
