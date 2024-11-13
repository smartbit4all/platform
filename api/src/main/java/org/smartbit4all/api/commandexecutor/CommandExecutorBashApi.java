package org.smartbit4all.api.commandexecutor;

import java.io.IOException;
import org.smartbit4all.api.binarydata.BinaryData;

public class CommandExecutorBashApi implements CommandExecutorApi {

  @Override
  public String getCliName() {
    return "bash";
  }

  public String executeBashScript(String script) throws IOException {
    ProcessBuilder processBuilder = getProcessBuilder();
    processBuilder.command().add("-c");
    processBuilder.command().add(script);
    Process process = processBuilder.start();
    return new String(process.getInputStream().readAllBytes());
  }

  public String executeBashScript(BinaryData scriptFile) throws IOException {
    return executeBashScript(new String(scriptFile.inputStream().readAllBytes()));
  }

}
