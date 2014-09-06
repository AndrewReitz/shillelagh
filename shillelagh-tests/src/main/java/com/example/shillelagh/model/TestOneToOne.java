package com.example.shillelagh.model;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.OrmOnly;
import shillelagh.Table;

@Table public class TestOneToOne {
  @Id long id;
  @Field Child child;

  @OrmOnly TestOneToOne() { }

  public TestOneToOne(Child child) {
    this.child = child;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setChild(Child child) {
    this.child = child;
  }

  public Child getChild() {
    return child;
  }

  @Table public static class Child {
    @Id long id;
    @Field String childName;

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    @OrmOnly Child() {}

    public Child(String childName) {
      this.childName = childName;
    }

    public String getChildName() {
      return childName;
    }

    public void setChildName(String childName) {
      this.childName = childName;
    }
  }
}
