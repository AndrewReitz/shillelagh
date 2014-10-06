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
import java.util.Arrays;
import java.util.Date;

public class Image implements Serializable {
  private String name;
  private Date created;
  private byte[] data;

  public Image(String name, Date created, byte[] data) {
    this.name = name;
    this.created = created;
    this.data = data;
  }

  public String getName() {
    return name;
  }

  public Date getCreated() {
    return created;
  }

  public byte[] getData() {
    return data;
  }

  @Override public String toString() {
    return "Image{" +
        "name='" + name + '\'' +
        ", created=" + created +
        ", data=" + Arrays.toString(data) +
        '}';
  }
}
