package org.smartbit4all.api.commandexecutor;

import static org.smartbit4all.api.commandexecutor.CommandExecutorConstants.TESSERACT;
import static org.smartbit4all.core.utility.StringConstant.DOUBLE_QUOTE;
import static org.smartbit4all.core.utility.StringConstant.SPACE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import com.google.common.io.ByteStreams;

public class CommandExecutorTesseractApi extends CommandExecutorApiAbs
    implements CommandExecutorApi {

  @Value("${fs.base.directory:../../dev-fs}")
  private String baseDirectory;

  @Value("${commandexecutor.path.tesseract:}")
  private String path;

  @Value("${commandexecutor.ext.tesseract:}")
  private String ext;

  @Override
  public Boolean isAvailable() {
    ProcessBuilder processBuilder = getProcessBuilder();
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(path);
    commandBuilder.append(TESSERACT);
    commandBuilder.append(ext);
    commandBuilder.append(SPACE);
    commandBuilder.append("--version");
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
      if (log.isDebugEnabled()) {
        log.info(outputString);
      }
      return outputString.contains("libarchive");
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return false;
    }
  }

  public String getTextFromFileWithOcr(BinaryContentData inputContentData) throws IOException {
    return getTextFromFileWithOcr(inputContentData, null);
  }

  public String getTextFromFileWithOcr(BinaryContentData inputContentData, String language)
      throws IOException {
    ProcessBuilder processBuilder = getProcessBuilder();

    // Using StringBuilder
    StringBuilder commandBuilder = new StringBuilder();
    commandBuilder.append(path);
    commandBuilder.append(TESSERACT);
    commandBuilder.append(ext);
    commandBuilder.append(SPACE);

    // temporal file for tesseract
    File tempFile = File.createTempFile(TESSERACT,
        PROCESS + StringConstant.DOT + inputContentData.getExtension());
    FileOutputStream fos = new FileOutputStream(tempFile);
    BinaryData inputData = objectApi.loadLatest(inputContentData.getDataUri())
        .getObject(BinaryDataObject.class).getBinaryData();
    ByteStreams.copy(inputData.inputStream(), fos);
    fos.flush();
    fos.close();
    // adding the temporal input file's path
    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(tempFile.getPath());
    commandBuilder.append(DOUBLE_QUOTE);
    commandBuilder.append(SPACE);

    commandBuilder.append(STDOUT);

    if (!ObjectUtils.isEmpty(language)) {
      commandBuilder.append(SPACE);
      commandBuilder.append("-l");
      commandBuilder.append(SPACE);
      commandBuilder.append(language);
    }

    processBuilder.command().add(commandBuilder.toString());
    Process process = processBuilder.start();
    try (InputStream in = process.getInputStream()) {
      byte[] allBytes = FileIO.readInputStreamToByteArray(in);
      String outputString = new String(allBytes);
      return outputString;
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

}
