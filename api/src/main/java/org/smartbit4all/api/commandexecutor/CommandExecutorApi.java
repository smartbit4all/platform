package org.smartbit4all.api.commandexecutor;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;

public interface CommandExecutorApi {

  default ProcessBuilder getProcessBuilder() {
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.redirectErrorStream(true);
    processBuilder.redirectOutput(Redirect.PIPE);
    List<String> command = processBuilder.command();

    String os = System.getProperty("os.name").toLowerCase();
    boolean isWindows = os.contains("win");
    if (isWindows) {
      command.add("cmd.exe");
      command.add("/c");
    } else {
      command.add("/bin/bash");
      command.add("-c");
    }

    return processBuilder;
  }

  boolean isAvailable();

}
