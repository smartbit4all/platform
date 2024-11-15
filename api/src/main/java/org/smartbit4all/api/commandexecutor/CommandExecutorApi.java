package org.smartbit4all.api.commandexecutor;

import java.io.IOException;

public interface CommandExecutorApi {

  default boolean isAvailable() {
    ProcessBuilder processBuilder = getProcessBuilder();
    try {
      processBuilder.start();
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  String getCliName();

  default ProcessBuilder getProcessBuilder() {
    ProcessBuilder processBuilder = new ProcessBuilder(getCliName());
    processBuilder.redirectErrorStream(true);
    return processBuilder;
  }

}
