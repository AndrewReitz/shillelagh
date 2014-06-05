package com.example.shillelagh.model;

import java.util.Date;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table
public class TestJavaObjectsTable {
  @Id long id;

  @Field String aString;
  @Field Date aDate;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getaString() {
    return aString;
  }

  public void setaString(String aString) {
    this.aString = aString;
  }

  public Date getaDate() {
    return aDate;
  }

  public void setaDate(Date aDate) {
    this.aDate = aDate;
  }
}
