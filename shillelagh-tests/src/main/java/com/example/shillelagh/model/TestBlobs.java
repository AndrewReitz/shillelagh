/*
 * Copyright 2014 Andrew Reitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.shillelagh.model;

import java.io.Serializable;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table public class TestBlobs {
  @Id long id;
  @Field(isBlob = true) Byte[] aByteArray;
  @Field(isBlob = true) byte[] anotherByteArray; // TODO Make this not require isBlob == true
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
