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
    return getProcessBuilder(getCliName());
  }

  default ProcessBuilder getProcessBuilder(String cli) {
    ProcessBuilder processBuilder = new ProcessBuilder(cli);
    processBuilder.redirectErrorStream(true);
    return processBuilder;
  }

}
