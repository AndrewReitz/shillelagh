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

  void d(String message) {
    if (DEBUG) {
      messenger.printMessage(NOTE, message);
    }
  }

  void e(String message) {
    messenger.printMessage(ERROR, message);
  }
}
