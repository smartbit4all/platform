package org.smartbit4all.api.commandexecutor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.io.ByteStreams;

public class CommandExecutorFfmpegApi implements CommandExecutorApi {

  @Value("${fs.base.directory:./test-fs}")
  private String baseDirectory;

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
    int end = inputContentData.getFileName().lastIndexOf(StringConstant.DOT);
    String outputFileName = inputContentData.getFileName().substring(0, end)
        .concat(StringConstant.DOT).concat(toExtension);
    final UUID uuid = UUID.randomUUID();
    String tempOutputName = uuid.toString() + outputFileName;
    Path tempOutputPath = Paths.get(baseDirectory).resolve(tempOutputName);
    command.add(tempOutputPath.toString());
    Process process = processBuilder.inheritIO().redirectOutput(Redirect.PIPE).start();
    // process.getInputStream().transferTo(System.out);
    // TODO remove this, use fileSystem watcher
    Thread.sleep(60000);
    try (InputStream in = Files.newInputStream(tempOutputPath)) {
      return BinaryData.of(in);
    } catch (Exception e) {
      return null;
    }
  }

  // public List<BinaryData> split(BinaryData input, Integer parts) {
  //
  // }
  //
  // public List<BinaryData> split(BinaryData input, Long maxBytes) {
  // long length = input.length();
  // int parts = Math.round(((float) length) / maxBytes);
  // return split(input, parts);
  // }

}
