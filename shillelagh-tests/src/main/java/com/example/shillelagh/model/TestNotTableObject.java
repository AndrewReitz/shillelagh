package com.example.shillelagh.model;

/** Class not annotated with Table for testing exceptions */
public class TestNotTableObject {
  private String name;
  private int value;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}
