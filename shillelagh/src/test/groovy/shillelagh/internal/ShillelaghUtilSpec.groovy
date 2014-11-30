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

import spock.lang.Specification

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

  def "create new instance"() {
    when: "no params"
      NoParamConstructor noParams = createInstance(NoParamConstructor.class)

    then: "not null"
      noParams != null

    when: "lots of params"
      LotsOfParamConstructor lotsOfParams = createInstance(LotsOfParamConstructor.class)

    then: "not null"
      lotsOfParams != null
  }

  final static class TestSerializeClass implements Serializable {
    String testString
    int testInt

    TestSerializeClass(String testString, int testInt) {
      this.testString = testString
      this.testInt = testInt
    }
  }

  final static class NoParamConstructor {
    String test
  }

  final static class LotsOfParamConstructor {
    byte testByte
    short testShort
    int testInt
    long testLong
    float testFloat
    double testDouble
    char testChar
    boolean testBool
    String testString
    NoParamConstructor testObject
  }
}
