package com.example.shillelagh.model;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table public class TestBlobs {
  @Id long id;
  @Field(isBlob = true) Byte[] aByteArray;
  @Field(isBlob = true) byte[] anotherByteArray;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Byte[] getaByteArray() {
    return aByteArray;
  }

  public void setaByteArray(Byte[] aByteArray) {
    this.aByteArray = aByteArray;
  }

  public byte[] getAnotherByteArray() {
    return anotherByteArray;
  }

  public void setAnotherByteArray(byte[] anotherByteArray) {
    this.anotherByteArray = anotherByteArray;
  }
}
