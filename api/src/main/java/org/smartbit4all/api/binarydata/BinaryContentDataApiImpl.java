package org.smartbit4all.api.binarydata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.io.ByteStreams;

public class BinaryContentDataApiImpl implements BinaryContentDataApi {

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private MimeTypeApi mimeTypeApi;
  @Autowired(required = false)
  private SessionApi sessionApi;

  @Override
  public String readStringContent(URI dataUri, Charset charset) throws IOException {
    return new String(
        ByteStreams.toByteArray(objectApi.loadLatest(dataUri)
            .getObject(BinaryDataObject.class).getBinaryData().inputStream()),
        charset);
  }

  @Override
  public BinaryContentData constructFromClassResource(String fileRelativePath,
      String schemaToSave) {
    InputStream in = this.getClass().getResourceAsStream(fileRelativePath);
    Path path = Paths.get(fileRelativePath);
    return constructBinaryContentData(fileRelativePath, path, schemaToSave, in);
  }

  @Override
  public BinaryContentData constructFromFile(String fileRelativePath, String schemaToSave) {
    Path path = Paths.get(fileRelativePath);
    InputStream in;
    try {
      in = Files.newInputStream(path);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to load file from " + fileRelativePath);
    }
    return constructBinaryContentData(fileRelativePath, path, schemaToSave, in);
  }

  private BinaryContentData constructBinaryContentData(String fileRelativePath, Path path,
      String schemaToSave,
      InputStream in) {
    String fileName = path.getFileName().toString();
    String extensionFromFileName = mimeTypeApi.getExtensionFromFileName(fileName);
    BinaryData binaryData = BinaryData.of(in);
    if (binaryData == null) {
      throw new IllegalArgumentException("Unable to load binary data from " + fileRelativePath);
    }
    UserActivityLog activityLog = sessionApi != null ? sessionApi.createActivityLog() : null;
    return new BinaryContentData()
        .dataUri(
            objectApi.saveAsNew(schemaToSave, binaryData.asObject()))
        .fileName(fileName)
        .extension(extensionFromFileName)
        .mimeType(mimeTypeApi.getMimeType(fileName))
        .created(activityLog)
        .updated(activityLog)
        .size(binaryData.length())
        .contentHash(binaryData.hashIfPresent());
  }

}
