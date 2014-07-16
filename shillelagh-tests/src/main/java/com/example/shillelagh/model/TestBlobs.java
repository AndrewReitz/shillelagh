package com.example.shillelagh.model;

import java.io.Serializable;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table public class TestBlobs {
  @Id long id;
  @Field(isBlob = true) Byte[] aByteArray;
  @Field(isBlob = true) byte[] anotherByteArray;
  @Field(isBlob = true) TestBlobObject aTestBlobObject;

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

  public void setaTestBlobObject(TestBlobObject testBlobObject) {
    this.aTestBlobObject = testBlobObject;
  }

  public TestBlobObject getaTestBlobObject() {
    return this.aTestBlobObject;
  }

  public static class TestBlobObject implements Serializable {
    public String testString;
  }
}
