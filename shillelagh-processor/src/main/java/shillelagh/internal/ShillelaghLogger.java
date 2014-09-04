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
