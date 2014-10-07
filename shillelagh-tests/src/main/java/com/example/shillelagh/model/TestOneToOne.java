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

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.OrmOnly;
import shillelagh.Table;

@Table public class TestOneToOne {
  @Id long id;
  @Field Child child;

  @OrmOnly TestOneToOne() { }

  public TestOneToOne(Child child) {
    this.child = child;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setChild(Child child) {
    this.child = child;
  }

  public Child getChild() {
    return child;
  }

  @Table public static class Child {
    @Id long id;
    @Field String childName;

    public long getId() {
      return id;
    }

    public void setId(long id) {
      this.id = id;
    }

    @OrmOnly Child() {}

    public Child(String childName) {
      this.childName = childName;
    }

    public String getChildName() {
      return childName;
    }

    public void setChildName(String childName) {
      this.childName = childName;
    }
  }
}
