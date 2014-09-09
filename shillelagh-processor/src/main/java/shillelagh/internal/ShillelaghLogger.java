/*
 * Copyright ${year} Andrew Reitz
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

package shillelagh.internal;

import javax.annotation.processing.Messager;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static shillelagh.internal.ShillelaghProcessor.DEBUG;

final class ShillelaghLogger {
  private final Messager messenger;

  ShillelaghLogger(Messager messenger) {
    this.messenger = messenger;
  }

  /**
   * Print out debug logs, will only print if {@link shillelagh.internal.ShillelaghProcessor#DEBUG}
   * is true.
   */
  void d(String message, Object... args) {
    if (DEBUG) {
      messenger.printMessage(NOTE, formatString(message, args));
    }
  }

  /** Print out notes */
  void n(String message, Object... args) {
    messenger.printMessage(NOTE, formatString(message, args));
  }

  /** Print out errors, this will stop the build from succeeding */
  void e(String message, Object... args) {
    messenger.printMessage(ERROR, formatString(message, args));
  }

  private String formatString(String message, Object... args) {
    return args.length == 0 ? message : String.format(message, args);
  }
}
