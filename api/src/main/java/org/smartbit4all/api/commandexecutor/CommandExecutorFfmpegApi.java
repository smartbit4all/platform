package org.smartbit4all.api.commandexecutor;

import static org.smartbit4all.api.commandexecutor.CommandExecutorConstants.FFMPEG;
import static org.smartbit4all.api.commandexecutor.CommandExecutorConstants.FFPROBE;
import static org.smartbit4all.api.commandexecutor.CommandExecutorConstants.PROCESS;
import static org.smartbit4all.core.utility.StringConstant.DOT;
import static org.smartbit4all.core.utility.StringConstant.SPACE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  @Value("${fs.base.directory:../../dev-fs}")
  private String baseDirectory;

  @Autowired
  private ObjectApi objectApi;

  @Override
  public boolean isAvailable() {
    ProcessBuilder processBuilder = getProcessBuilder();
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(FFMPEG);
    commandBuilder.append(SPACE);
    commandBuilder.append("-version");
    processBuilder.command().add(commandBuilder.toString());
    Process process;
    try {
      process = processBuilder.start();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return false;
    }
    try (InputStream in = process.getInputStream()) {
      byte[] allBytes = FileIO.readInputStreamToByteArray(in);
      String outputString = new String(allBytes);
      return outputString.contains("FFmpeg");
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  public BinaryData convert(BinaryContentData inputContentData, String toExtension)
      throws IOException {
    ProcessBuilder processBuilder = getProcessBuilder();

    // Using StringBuilder
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(FFMPEG);
    commandBuilder.append(SPACE);

    // specifying that the next parameter will be the input file
    commandBuilder.append("-i");
    commandBuilder.append(SPACE);

    // temporal file for ffmpeg
    File tempFile = File.createTempFile(FFMPEG,
        PROCESS + DOT + inputContentData.getExtension());
    FileOutputStream fos = new FileOutputStream(tempFile);
    BinaryData inputData = objectApi.loadLatest(inputContentData.getDataUri())
        .getObject(BinaryDataObject.class).getBinaryData();
    ByteStreams.copy(inputData.inputStream(), fos);
    fos.flush();
    fos.close();
    // adding the temporal input file's path
    commandBuilder.append(tempFile.getPath());
    commandBuilder.append(SPACE);

    // specifying the output file. The extension of the output file is responsible for determining
    // which format to convert to
    int end = inputContentData.getFileName().lastIndexOf(DOT);
    String outputFileName = inputContentData.getFileName().substring(0, end)
        .concat(DOT).concat(toExtension);
    UUID uuid = UUID.randomUUID();
    String uniqueOutputFileName = uuid.toString() + outputFileName;
    Path uniqueOutputFilePath = Paths.get(baseDirectory).resolve(uniqueOutputFileName);
    commandBuilder.append(uniqueOutputFilePath.toString());

    processBuilder.command().add(commandBuilder.toString());
    Process process = processBuilder.start();
    // transfer the logging of the process to the standard out
    transferInputStreamToSysOut(process);
    try (InputStream in = Files.newInputStream(uniqueOutputFilePath)) {
      return BinaryData.of(in);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public BinaryData split(BinaryContentData inputContentData, Long start, Long end)
      throws IOException {
    ProcessBuilder processBuilder = getProcessBuilder();

    // Using StringBuilder
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(FFMPEG);
    commandBuilder.append(SPACE);

    // specifying that the next parameter will be the input file
    commandBuilder.append("-i");
    commandBuilder.append(SPACE);

    // temp file for ffmpeg
    File tempFile = File.createTempFile(FFMPEG,
        PROCESS + DOT + inputContentData.getExtension());
    FileOutputStream fos = new FileOutputStream(tempFile);
    BinaryData inputData = objectApi.loadLatest(inputContentData.getDataUri())
        .getObject(BinaryDataObject.class).getBinaryData();
    ByteStreams.copy(inputData.inputStream(), fos);
    fos.flush();
    fos.close();
    // adding the temporal input file's path
    commandBuilder.append(tempFile.getPath());
    commandBuilder.append(SPACE);

    // specifying the start and end time of the snippet
    commandBuilder.append("-ss");
    commandBuilder.append(SPACE);
    commandBuilder.append(start.toString());
    commandBuilder.append(SPACE);
    commandBuilder.append("-to");
    commandBuilder.append(SPACE);
    commandBuilder.append(end.toString());
    commandBuilder.append(SPACE);

    // specifying the output file. The file name will be "filename_start_end.ext"
    int fileNameEnd = inputContentData.getFileName().lastIndexOf(DOT);
    String outputFileName = inputContentData.getFileName().substring(0, fileNameEnd)
        + StringConstant.UNDERLINE + start + StringConstant.UNDERLINE + end + DOT
        + inputContentData.getExtension();
    UUID uuid = UUID.randomUUID();
    String uniqueOutputFileName = uuid.toString() + outputFileName;
    Path uniqueOutputFilePath = Paths.get(baseDirectory).resolve(uniqueOutputFileName);
    commandBuilder.append(uniqueOutputFilePath.toString());

    processBuilder.command().add(commandBuilder.toString());
    Process process = processBuilder.start();
    // transfer the logging of the process to the standard out
    transferInputStreamToSysOut(process);
    try (InputStream in = Files.newInputStream(uniqueOutputFilePath)) {
      return BinaryData.of(in);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public Long getDuration(BinaryContentData inputContentData)
      throws IOException, InterruptedException {
    ProcessBuilder processBuilder = getProcessBuilder();

    // Using StringBuilder
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(FFPROBE);
    commandBuilder.append(SPACE);

    // specifying that the next parameter will be the input file
    commandBuilder.append("-i");
    commandBuilder.append(SPACE);


    // temporal file for ffmpeg
    File tempFile = File.createTempFile(FFMPEG,
        PROCESS + DOT + inputContentData.getExtension());
    FileOutputStream fos = new FileOutputStream(tempFile);
    BinaryData inputData = objectApi.loadLatest(inputContentData.getDataUri())
        .getObject(BinaryDataObject.class).getBinaryData();
    ByteStreams.copy(inputData.inputStream(), fos);
    fos.flush();
    fos.close();
    commandBuilder.append(tempFile.getPath());
    commandBuilder.append(SPACE);

    // outputs only the duration information
    commandBuilder.append("-show_entries");
    commandBuilder.append(SPACE);
    commandBuilder.append("format=duration");
    commandBuilder.append(SPACE);
    // suppresses all logs except the requested output
    commandBuilder.append("-v");
    commandBuilder.append(SPACE);
    commandBuilder.append("quiet");
    commandBuilder.append(SPACE);
    // formats the output as plain text (just the duration value)
    commandBuilder.append("-of");
    commandBuilder.append(SPACE);
    commandBuilder.append("csv=\"p=0\"");
    commandBuilder.append(SPACE);

    processBuilder.command().add(commandBuilder.toString());
    Process process = processBuilder.start();
    try (InputStream in = process.getInputStream()) {
      byte[] allBytes = FileIO.readInputStreamToByteArray(in);
      String outputString = new String(allBytes);
      float seconds = Float.parseFloat(outputString);
      return (long) Math.round(seconds);
    } catch (IOException e) {
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
