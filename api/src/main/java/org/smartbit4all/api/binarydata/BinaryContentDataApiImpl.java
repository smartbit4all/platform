package org.smartbit4all.api.binarydata;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.io.ByteStreams;

public class BinaryContentDataApiImpl implements BinaryContentDataApi {

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private MimeTypeApi mimeTypeApi;

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
    InputStream in =
        this.getClass()
            .getResourceAsStream(fileRelativePath);
    Path path = Paths.get(fileRelativePath);
    String fileName = path.getFileName().toString();
    String extensionFromFileName = mimeTypeApi.getExtensionFromFileName(fileName);
    BinaryData binaryData = BinaryData.of(in);
    if (binaryData == null) {
      throw new IllegalArgumentException("Unable to load binary data from " + fileRelativePath);
    }
    return new BinaryContentData()
        .dataUri(
            objectApi.saveAsNew(schemaToSave, binaryData.asObject()))
        .fileName(fileName).extension(extensionFromFileName);

  }

}
