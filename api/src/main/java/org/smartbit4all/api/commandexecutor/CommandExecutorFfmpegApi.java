package org.smartbit4all.api.commandexecutor;

import static org.smartbit4all.core.utility.StringConstant.DOT;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.io.ByteStreams;

public class CommandExecutorFfmpegApi implements CommandExecutorApi {

  private static final Logger log = LoggerFactory.getLogger(CommandExecutorFfmpegApi.class);

  public static final String FFPROBE = "ffprobe";

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
    // temp file for ffmpeg
    File tempFile = null;
    tempFile = File.createTempFile("ffmpeg",
        "convert" + DOT + inputContentData.getExtension());
    FileOutputStream fos = new FileOutputStream(tempFile);
    BinaryData inputData = objectApi.loadLatest(inputContentData.getDataUri())
        .getObject(BinaryDataObject.class).getBinaryData();
    ByteStreams.copy(inputData.inputStream(), fos);
    fos.flush();
    fos.close();
    command.add(tempFile.getPath());
    int end = inputContentData.getFileName().lastIndexOf(DOT);
    String outputFileName = inputContentData.getFileName().substring(0, end)
        .concat(DOT).concat(toExtension);
    UUID uuid = UUID.randomUUID();
    String tempOutputName = uuid.toString() + outputFileName;
    Path tempOutputPath = Paths.get(baseDirectory).resolve(tempOutputName);
    command.add(tempOutputPath.toString());
    Process process = processBuilder.start();
    transferInputStreamToSysOut(process);
    try (InputStream in = Files.newInputStream(tempOutputPath)) {
      return BinaryData.of(in);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public BinaryData split(BinaryContentData inputContentData, Long start, Long end)
      throws IOException, InterruptedException {
    ProcessBuilder processBuilder = getProcessBuilder();
    List<String> command = processBuilder.command();
    command.add("-i");
    // temp file for ffmpeg
    File tempFile = null;
    tempFile = File.createTempFile("ffmpeg",
        "convert" + DOT + inputContentData.getExtension());
    FileOutputStream fos = new FileOutputStream(tempFile);
    BinaryData inputData = objectApi.loadLatest(inputContentData.getDataUri())
        .getObject(BinaryDataObject.class).getBinaryData();
    ByteStreams.copy(inputData.inputStream(), fos);
    fos.flush();
    fos.close();
    command.add(tempFile.getPath());
    int fileNameEnd = inputContentData.getFileName().lastIndexOf(DOT);
    String outputFileName = inputContentData.getFileName().substring(0, fileNameEnd)
        + StringConstant.UNDERLINE + start + StringConstant.UNDERLINE + end + DOT
        + inputContentData.getExtension();
    command.add("-ss");
    command.add(start.toString());
    command.add("-to");
    command.add(end.toString());
    UUID uuid = UUID.randomUUID();
    String tempOutputName = uuid.toString() + outputFileName;
    Path tempOutputPath = Paths.get(baseDirectory).resolve(tempOutputName);
    command.add(tempOutputPath.toString());
    Process process = processBuilder.start();
    transferInputStreamToSysOut(process);
    try (InputStream in = Files.newInputStream(tempOutputPath)) {
      return BinaryData.of(in);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public Long getDuration(BinaryContentData inputContentData)
      throws IOException, InterruptedException {
    ProcessBuilder processBuilder = getProcessBuilder(FFPROBE);
    List<String> command = processBuilder.command();
    command.add("-i");
    // temp file for ffmpeg
    File tempFile = null;
    tempFile = File.createTempFile("ffmpeg",
        "convert" + DOT + inputContentData.getExtension());
    FileOutputStream fos = new FileOutputStream(tempFile);
    BinaryData inputData = objectApi.loadLatest(inputContentData.getDataUri())
        .getObject(BinaryDataObject.class).getBinaryData();
    ByteStreams.copy(inputData.inputStream(), fos);
    fos.flush();
    fos.close();
    command.add(tempFile.getPath());
    // outputs only the duration information
    command.add("-show_entries");
    command.add("format=duration");
    // suppresses all logs except the requested output
    command.add("-v");
    command.add("quiet");
    // formats the output as plain text (just the duration value)
    command.add("-of");
    command.add("csv=\"p=0\"");
    Process process = processBuilder.start();
    try (InputStream in = process.getInputStream()) {
      byte[] allBytes = FileIO.readInputStreamToByteArray(in);
      String outputString = new String(allBytes);
      float seconds = Float.parseFloat(outputString);
      return (long) Math.round(seconds);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  private void transferInputStreamToSysOut(Process process) throws IOException {
    OutputStream outputStream = System.out;
    try (InputStream inputStream = process.getInputStream()) {
      byte[] buffer = new byte[8192]; // Buffer size
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
    }
  }

}
