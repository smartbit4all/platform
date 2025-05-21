package org.smartbit4all.api.commandexecutor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CommandExecutorApiAbs implements CommandExecutorApi {

  protected static final String PROCESS = "process";

  protected static final String STDOUT = "stdout";

  protected final Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  protected ObjectApi objectApi;

  protected ProcessBuilder getProcessBuilder() {
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

  protected boolean isWindows() {
    String os = System.getProperty("os.name").toLowerCase();
    return os.contains("win");
  }

  protected void logProcessInputStream(Process process) throws IOException {
    try (InputStream inputStream = process.getInputStream()) {
      byte[] buffer = new byte[8192]; // Buffer size
      while (inputStream.read(buffer) != -1) {
        String outputString = new String(buffer);
        if (log.isDebugEnabled()) {
          log.debug(outputString);
        }
      }
    }
  }

}
