package org.smartbit4all.api.commandexecutor;

import java.io.IOException;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.FileIO;

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
    byte[] byteArray = FileIO.readInputStreamToByteArray(process.getInputStream());
    return new String(byteArray);
  }

  public String executeBashScript(BinaryData scriptFile) throws IOException {
    byte[] byteArray = FileIO.readInputStreamToByteArray(scriptFile.inputStream());
    return executeBashScript(new String(byteArray));
  }

}
