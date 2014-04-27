package com.example.shillelagh.model;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table
public class  TestPrimitiveTable {
  @Id long id;

  @Field short aShort;
  @Field int anInt;
  @Field long aLong;
  @Field float aFloat;
  @Field double aDouble;
  @Field boolean aBoolean;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public short getaShort() {
    return aShort;
  }

  public void setaShort(short aShort) {
    this.aShort = aShort;
  }

  public int getAnInt() {
    return anInt;
  }

  public void setAnInt(int anInt) {
    this.anInt = anInt;
  }

  public long getaLong() {
    return aLong;
  }

  public void setaLong(long aLong) {
    this.aLong = aLong;
  }

  public float getaFloat() {
    return aFloat;
  }

  public void setaFloat(float aFloat) {
    this.aFloat = aFloat;
  }

  public double getaDouble() {
    return aDouble;
  }

  public void setaDouble(double aDouble) {
    this.aDouble = aDouble;
  }

  public boolean isaBoolean() {
    return aBoolean;
  }

  public void setaBoolean(boolean aBoolean) {
    this.aBoolean = aBoolean;
  }
}
