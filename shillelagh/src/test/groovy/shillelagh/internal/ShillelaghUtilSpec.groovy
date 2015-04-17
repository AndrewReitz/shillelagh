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

package shillelagh.internal

import groovy.transform.TypeChecked
import shillelagh.test.BooleanConstructor
import shillelagh.test.ByteConstructor
import shillelagh.test.CharConstructor
import shillelagh.test.FloatConstructor
import shillelagh.test.IntConstructor
import shillelagh.test.LongConstructor
import shillelagh.test.LotsOfParamConstructor
import shillelagh.test.NoParamConstructor
import shillelagh.test.ShortConstructor
import shillelagh.test.StringConstructor
import spock.lang.Specification
import spock.lang.Unroll

import static shillelagh.internal.ShillelaghUtil.*

class ShillelaghUtilSpec extends Specification {
  def "serialize then deserialize"() {
    given:
    String expectedString = "ItWorks"
    int expectedInt = 11
    TestSerializeClass input = new TestSerializeClass(expectedString, expectedInt)

    when: "serialize then deserialize"
    TestSerializeClass output = deserialize(serialize(input))

    then: "expected values of output to be the same as input"
    output.testString == expectedString
    output.testInt == expectedInt
  }

  @Unroll
  def "should create new instance of #classToCreate"() {
    given:
    def object = createInstance(classToCreate)

    expect:
    object != null

    where:
    classToCreate          | _
    NoParamConstructor     | _
    BooleanConstructor     | _
    ByteConstructor        | _
    IntConstructor         | _
    ShortConstructor       | _
    LongConstructor        | _
    FloatConstructor       | _
    CharConstructor        | _
    BooleanConstructor     | _
    StringConstructor      | _
    LotsOfParamConstructor | _
    HashMap                | _
    List                   | _
    Map                    | _
  }

  @TypeChecked
  def "should create a new list"() {
    given:
    List<String> stringList = createInstance(List)

    when:
    stringList.add("Hello")

    then:
    noExceptionThrown()
    stringList.get(0) == "Hello"
  }

  @TypeChecked
  def "should create a new map"() {
    given:
    Map<String, String> stringMap = createInstance(Map)

    when:
    stringMap.put("John", "Doe")

    then:
    noExceptionThrown()
    stringMap.get("John") == "Doe"
  }

  final static class TestSerializeClass implements Serializable {
    String testString
    int testInt

    TestSerializeClass(String testString, int testInt) {
      this.testString = testString
      this.testInt = testInt
    }
  }
}
