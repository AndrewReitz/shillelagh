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

package shillelagh.test;

public final class LotsOfParamConstructor {
  private final byte testByte;
  private final short testShort;
  private final int testInt;
  private final long testLong;
  private final float testFloat;
  private final double testDouble;
  private final char testChar;
  private final boolean testBool;
  private final String testString;

  public LotsOfParamConstructor(byte testByte, short testShort, int testInt, long testLong,
      float testFloat, double testDouble, char testChar, boolean testBool, String testString) {
    this.testByte = testByte;
    this.testShort = testShort;
    this.testInt = testInt;
    this.testLong = testLong;
    this.testFloat = testFloat;
    this.testDouble = testDouble;
    this.testChar = testChar;
    this.testBool = testBool;
    this.testString = testString;
  }

  public byte getTestByte() {
    return testByte;
  }

  public short getTestShort() {
    return testShort;
  }

  public int getTestInt() {
    return testInt;
  }

  public long getTestLong() {
    return testLong;
  }

  public float getTestFloat() {
    return testFloat;
  }

  public double getTestDouble() {
    return testDouble;
  }

  public char getTestChar() {
    return testChar;
  }

  public boolean isTestBool() {
    return testBool;
  }

  public String getTestString() {
    return testString;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LotsOfParamConstructor that = (LotsOfParamConstructor) o;

    if (testBool != that.testBool) return false;
    if (testByte != that.testByte) return false;
    if (testChar != that.testChar) return false;
    if (Double.compare(that.testDouble, testDouble) != 0) return false;
    if (Float.compare(that.testFloat, testFloat) != 0) return false;
    if (testInt != that.testInt) return false;
    if (testLong != that.testLong) return false;
    if (testShort != that.testShort) return false;
    if (testString != null ? !testString.equals(that.testString) : that.testString != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = (int) testByte;
    result = 31 * result + (int) testShort;
    result = 31 * result + testInt;
    result = 31 * result + (int) (testLong ^ (testLong >>> 32));
    result = 31 * result + (testFloat != +0.0f ? Float.floatToIntBits(testFloat) : 0);
    temp = Double.doubleToLongBits(testDouble);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (int) testChar;
    result = 31 * result + (testBool ? 1 : 0);
    result = 31 * result + (testString != null ? testString.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return "LotsOfParamConstructor{"
        + "testByte=" + testByte
        + ", testShort=" + testShort
        + ", testInt=" + testInt
        + ", testLong=" + testLong
        + ", testFloat=" + testFloat
        + ", testDouble=" + testDouble
        + ", testChar=" + testChar
        + ", testBool=" + testBool
        + ", testString='" + testString + '\''
        + '}';
  }
}
