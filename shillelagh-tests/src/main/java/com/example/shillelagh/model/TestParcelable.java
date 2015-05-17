/*
 * Copyright 2015 Andrew Reitz
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

import android.os.Parcel;
import android.os.Parcelable;
import shillelagh.Column;
import shillelagh.Id;
import shillelagh.Table;

@Table
public class TestParcelable implements Parcelable {
  @Id long id;
  @Column int testInt;
  @Column String testString;

  public TestParcelable(int testInt, String testString) {
    this.testInt = testInt;
    this.testString = testString;
  }

  private TestParcelable(Parcel parcel) {
    this.id = parcel.readLong();
    this.testInt = parcel.readInt();
    this.testString = parcel.readString();
  }

  public String getTestString() {
    return testString;
  }

  public int getTestInt() {
    return testInt;
  }

  public long getId() {
    return id;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeLong(this.id);
    dest.writeInt(this.testInt);
    dest.writeString(this.testString);
  }

  public static final Parcelable.Creator<TestParcelable> CREATOR = new Parcelable.Creator<TestParcelable>() {
    @Override
    public TestParcelable createFromParcel(Parcel source) {
      return new TestParcelable(source);
    }

    @Override
    public TestParcelable[] newArray(int size) {
      return new TestParcelable[size];
    }
  };
}
