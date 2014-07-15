package com.example.shillelagh.model;

import shillelagh.Field;
import shillelagh.Table;

@Table public class TestBlobs {
  @Field(isBlob = true) Byte[] aByteArray;
  @Field(isBlob = true) byte[] anotherByteArray;

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
