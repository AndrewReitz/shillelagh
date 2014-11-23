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

import shillelagh.Column;
import shillelagh.Id;
import shillelagh.OrmOnly;
import shillelagh.Table;

@Table
public class SimpleObject {

  @Id long id;
  @Column String name;
  @Column String address;
  @Column long customerId;

  @OrmOnly SimpleObject() { /* Used internally by shillelagh */ }

  public SimpleObject(String name, String address, long customerId) {
    this.name = name;
    this.address = address;
    this.customerId = customerId;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public long getCustomerId() {
    return customerId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SimpleObject that = (SimpleObject) o;

    if (id != that.id) return false;
    if (!address.equals(that.address)) return false;
    if (!name.equals(that.name)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + name.hashCode();
    result = 31 * result + address.hashCode();
    return result;
  }
}
