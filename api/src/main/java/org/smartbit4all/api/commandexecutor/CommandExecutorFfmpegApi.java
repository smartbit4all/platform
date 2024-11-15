package org.smartbit4all.api.commandexecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.io.ByteStreams;

public class CommandExecutorFfmpegApi implements CommandExecutorApi {

  @Autowired
  private ObjectApi objectApi;

  @Override
  public String getCliName() {
    return "ffmpeg";
  }

  public BinaryData convert(BinaryContentData inputContentData, String toExtension)
      throws IOException, InterruptedException {
    ProcessBuilder processBuilder = getProcessBuilder();
    List<String> command = processBuilder.command();

    command.add("-i");

    // temp file to send to ffmpeg
    File tempFile = null;
    tempFile = File.createTempFile("aasdasd", "basdasasd" + inputContentData.getExtension());
    FileOutputStream fos = new FileOutputStream(tempFile);
    BinaryData inputData = objectApi.loadLatest(inputContentData.getDataUri())
        .getObject(BinaryDataObject.class).getBinaryData();
    ByteStreams.copy(inputData.inputStream(), fos);
    fos.flush();
    fos.close();
    command.add(tempFile.getPath());
    command.add("-f");
    int end = inputContentData.getFileName().lastIndexOf(StringConstant.DOT);
    String outputFileName = inputContentData.getFileName().substring(0, end).concat(toExtension);
    command.add(outputFileName);

    Process process = processBuilder.start();
    boolean waitFor = process.waitFor(300, TimeUnit.SECONDS);
    if (!waitFor) {
      return null;
    }
    return BinaryData.of(process.getInputStream());
  }

}
