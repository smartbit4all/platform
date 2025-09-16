package org.smartbit4all.api.commandexecutor;

import static org.smartbit4all.api.commandexecutor.CommandExecutorConstants.FFMPEG;
import static org.smartbit4all.api.commandexecutor.CommandExecutorConstants.FFPROBE;
import static org.smartbit4all.core.utility.StringConstant.DOT;
import static org.smartbit4all.core.utility.StringConstant.DOUBLE_QUOTE;
import static org.smartbit4all.core.utility.StringConstant.SPACE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import com.google.common.io.ByteStreams;

public class CommandExecutorFfmpegApi extends CommandExecutorApiAbs implements CommandExecutorApi {

  @Value("${fs.base.directory:../../dev-fs}")
  private String baseDirectory;

  @Value("${commandexecutor.path.ffmpeg:}")
  private String path;

  @Value("${commandexecutor.ext.ffmpeg:}")
  private String ext;

  @Override
  public Boolean isAvailable() {
    ProcessBuilder processBuilder = getProcessBuilder();
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(path);
    commandBuilder.append(FFMPEG);
    commandBuilder.append(ext);
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
      if (log.isInfoEnabled()) {
        log.info(outputString);
      }
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
    commandBuilder.append(path);
    commandBuilder.append(FFMPEG);
    commandBuilder.append(ext);
    commandBuilder.append(SPACE);

    // specifying that the next parameter will be the input file
    commandBuilder.append("-i");
    commandBuilder.append(SPACE);

    File tempFile = createTempFile(inputContentData);
    // adding the temporal input file's path
    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(tempFile.getPath());
    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(SPACE);

    if (isAudio(toExtension)) {
      commandBuilder.append("-vn");
      commandBuilder.append(SPACE);
    }

    Path uniqueOutputFilePath = getTempFilePath(inputContentData, toExtension);
    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(uniqueOutputFilePath.toString());
    commandBuilder.append(DOUBLE_QUOTE);

    processBuilder.command().add(commandBuilder.toString());
    Process process = processBuilder.start();
    // transfer the logging of the process to the standard out
    logProcessInputStream(process);
    try (InputStream in = Files.newInputStream(uniqueOutputFilePath)) {
      return BinaryData.of(in);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  private static final Set<String> audioExtensions =
      Set.of(MimeTypeApi.MP3_EXT, MimeTypeApi.WEBM_AUDIO_EXT);

  private boolean isAudio(String toMimeType) {
    return audioExtensions.contains(toMimeType);
  }

  public BinaryData split(BinaryContentData inputContentData, Long start, Long end)
      throws IOException {
    ProcessBuilder processBuilder = getProcessBuilder();

    // Using StringBuilder
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(path);
    commandBuilder.append(FFMPEG);
    commandBuilder.append(ext);
    commandBuilder.append(SPACE);

    // specifying that the next parameter will be the input file
    commandBuilder.append("-i");
    commandBuilder.append(SPACE);

    File tempFile = createTempFile(inputContentData);
    // adding the temporal input file's path
    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(tempFile.getPath());
    commandBuilder.append(DOUBLE_QUOTE);
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

    Path uniqueOutputFilePath = getTempFilePath(inputContentData, inputContentData.getExtension());
    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(uniqueOutputFilePath.toString());
    commandBuilder.append(DOUBLE_QUOTE);

    processBuilder.command().add(commandBuilder.toString());
    Process process = processBuilder.start();
    // transfer the logging of the process to the standard out
    logProcessInputStream(process);
    try (InputStream in = Files.newInputStream(uniqueOutputFilePath)) {
      return BinaryData.of(in);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public Long getDuration(BinaryContentData inputContentData)
      throws IOException {
    ProcessBuilder processBuilder = getProcessBuilder();

    // Using StringBuilder
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(path);
    commandBuilder.append(FFPROBE);
    commandBuilder.append(ext);
    commandBuilder.append(SPACE);

    // specifying that the next parameter will be the input file
    commandBuilder.append("-i");
    commandBuilder.append(SPACE);

    File tempFile = createTempFile(inputContentData);

    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(tempFile.getPath());
    commandBuilder.append(DOUBLE_QUOTE);
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
    } catch (NumberFormatException e) {
      BinaryContentData contentCopy =
          ObjectSerializerByObjectMapper.deepCopy(inputContentData, BinaryContentData.class);
      BinaryData fixedStuff = fixTimestamping(contentCopy);
      BinaryDataObject tempObj = fixedStuff.asObject();
      URI tempUri = objectApi.saveAsNew(PlatformApiConfig.SCHEMA_TEMP, tempObj);
      contentCopy.setDataUri(tempUri);
      return getDuration(contentCopy);
    }
  }

  public BinaryData fixTimestamping(BinaryContentData inputContentData) throws IOException {
    ProcessBuilder processBuilder = getProcessBuilder();

    // Using StringBuilder
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(path);
    commandBuilder.append(FFMPEG);
    commandBuilder.append(ext);
    commandBuilder.append(SPACE);

    // specifying that the next parameter will be the input file
    commandBuilder.append("-i");
    commandBuilder.append(SPACE);

    File tempFile = createTempFile(inputContentData);

    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(tempFile.getPath());
    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(SPACE);

    commandBuilder.append("-c");
    commandBuilder.append(SPACE);
    commandBuilder.append("copy");
    commandBuilder.append(SPACE);

    commandBuilder.append("-map");
    commandBuilder.append(SPACE);
    commandBuilder.append("0");
    commandBuilder.append(SPACE);

    commandBuilder.append("-fflags");
    commandBuilder.append(SPACE);
    commandBuilder.append("+genpts");
    commandBuilder.append(SPACE);

    commandBuilder.append("-f");
    commandBuilder.append(SPACE);
    commandBuilder.append(inputContentData.getExtension());
    commandBuilder.append(SPACE);

    Path uniqueOutputFilePath = getTempFilePath(inputContentData, inputContentData.getExtension());
    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(uniqueOutputFilePath.toString());
    commandBuilder.append(DOUBLE_QUOTE);

    processBuilder.command().add(commandBuilder.toString());
    Process process = processBuilder.start();
    // transfer the logging of the process to the standard out
    logProcessInputStream(process);
    try (InputStream in = Files.newInputStream(uniqueOutputFilePath)) {
      return BinaryData.of(in);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }

  }

  private Path getTempFilePath(BinaryContentData inputContentData, String toExtension) {
    int end = inputContentData.getFileName().lastIndexOf(DOT);
    String outputFileName = inputContentData.getFileName().substring(0, end)
        .concat(DOT).concat(toExtension);
    UUID uuid = UUID.randomUUID();
    String uniqueOutputFileName = uuid.toString() + outputFileName;
    return Paths.get(baseDirectory).resolve(uniqueOutputFileName);
  }

  private File createTempFile(BinaryContentData inputContentData)
      throws IOException {
    File tempFile = File.createTempFile(FFMPEG,
        PROCESS + DOT + inputContentData.getExtension());
    FileOutputStream fos = new FileOutputStream(tempFile);
    BinaryData inputData = objectApi.loadLatest(inputContentData.getDataUri())
        .getObject(BinaryDataObject.class).getBinaryData();
    ByteStreams.copy(inputData.inputStream(), fos);
    fos.flush();
    fos.close();
    return tempFile;
  }


}
