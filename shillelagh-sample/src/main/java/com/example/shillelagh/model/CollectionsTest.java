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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import shillelagh.Column;
import shillelagh.Id;
import shillelagh.Table;

@Table
public final class CollectionsTest {
  @Id long id;
  @Column(isBlob = true) Map<String, String> maps;
  @Column(isBlob = true) List<String> lists;

  public CollectionsTest(Map<String, String> maps, List<String> lists) {
    this.maps = maps == null ? Collections.<String, String>emptyMap() : maps;
    this.lists = lists == null ? Collections.<String>emptyList() : lists;
  }

  public Map<String, String> getMaps() {
    return maps;
  }

  public List<String> getLists() {
    return lists;
  }

  @Override public String toString() {
    return "CollectionsTest{" +
        "id=" + id +
        ", maps=" + maps +
        ", lists=" + lists +
        '}';
  }
}
