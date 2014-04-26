package com.example.shillelagh.model;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table
public class TestBoxedPrimitivesTable {
  @Id Long _id;

  @Field Boolean aBoolean;
  @Field Byte aByte;
  @Field Character aCharacter;
  @Field Double aDouble;
  @Field Float aFloat;
  @Field Integer anInteger;
  @Field Long aLong;
  @Field Short aShort;
  @Field String aString;

  public Long getId() {
    return _id;
  }

  public void setId(Long id) {
    this._id = id;
  }

  public Boolean getaBoolean() {
    return aBoolean;
  }

  public void setaBoolean(Boolean aBoolean) {
    this.aBoolean = aBoolean;
  }

  public Byte getaByte() {
    return aByte;
  }

  public void setaByte(Byte aByte) {
    this.aByte = aByte;
  }

  public Character getaCharacter() {
    return aCharacter;
  }

  public void setaCharacter(Character aCharacter) {
    this.aCharacter = aCharacter;
  }

  public Double getaDouble() {
    return aDouble;
  }

  public void setaDouble(Double aDouble) {
    this.aDouble = aDouble;
  }

  public Float getaFloat() {
    return aFloat;
  }

  public void setaFloat(Float aFloat) {
    this.aFloat = aFloat;
  }

  public Integer getAnInteger() {
    return anInteger;
  }

  public void setAnInteger(Integer anInteger) {
    this.anInteger = anInteger;
  }

  public Long getaLong() {
    return aLong;
  }

  public void setaLong(Long aLong) {
    this.aLong = aLong;
  }

  public Short getaShort() {
    return aShort;
  }

  public void setaShort(Short aShort) {
    this.aShort = aShort;
  }

  public String getaString() {
    return aString;
  }

  public void setaString(String aString) {
    this.aString = aString;
  }
}
