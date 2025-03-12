package org.smartbit4all.api.commandexecutor;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;

public interface CommandExecutorApi {

  default ProcessBuilder getProcessBuilder() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.redirectErrorStream(true);
    processBuilder.redirectOutput(Redirect.PIPE);
    List<String> command = processBuilder.command();

    if (isWindows()) {
      command.add("cmd.exe");
      command.add("/c");
    } else {
      command.add("/bin/bash");
      command.add("-c");
    }

    return processBuilder;
  }

  default boolean isWindows() {
    String os = System.getProperty("os.name").toLowerCase();
    return os.contains("win");
  }

  boolean isAvailable();

}
