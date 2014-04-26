package com.example.shillelagh.model;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table
public class TestPrimitiveTable {
  @Id long id;

  @Field byte aByte;
  @Field short aShort;
  @Field int anInt;
  @Field long aLong;
  @Field float aFloat;
  @Field double aDouble;
  @Field boolean aBoolean;
  @Field char aChar;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public byte getaByte() {
    return aByte;
  }

  public void setaByte(byte aByte) {
    this.aByte = aByte;
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

  public char getaChar() {
    return aChar;
  }

  public void setaChar(char aChar) {
    this.aChar = aChar;
  }
}
