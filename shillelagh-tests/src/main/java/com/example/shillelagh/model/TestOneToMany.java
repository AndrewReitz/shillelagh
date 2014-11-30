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

import java.util.List;

import shillelagh.Column;
import shillelagh.Id;
import shillelagh.Table;

@Table public class TestOneToMany {
  @Id long id;
  @Column String someValue;
  @Column List<OneToManyChild> children;

  public TestOneToMany(String someValue, List<OneToManyChild> children) {
    this.someValue = someValue;
    this.children = children;
  }

  public void setSomeValue(String someValue) {
    this.someValue = someValue;
  }

  public List<OneToManyChild> getChildren() {
    return children;
  }

  @Table public static class OneToManyChild {
    @Id long id;
    @Column String testString;
    @Column int testInt;

    public OneToManyChild(String testString, int testInt) {
      this.testString = testString;
      this.testInt = testInt;
    }

    public void setTestString(String testString) {
      this.testString = testString;
    }

    @Override public String toString() {
      return "Child{" +
          "id=" + id +
          ", testString='" + testString + '\'' +
          ", testInt=" + testInt +
          '}';
    }
  }
}
