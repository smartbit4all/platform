package org.smartbit4all.api.binarydata;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.io.ByteStreams;

public class BinaryContentDataApiImpl implements BinaryContentDataApi {

  @Autowired
  private ObjectApi objectApi;

  @Override
  public String readStringContent(URI dataUri, Charset charset) throws IOException {
    return new String(
        ByteStreams.toByteArray(objectApi.loadLatest(dataUri)
            .getObject(BinaryDataObject.class).getBinaryData().inputStream()),
        charset);
  }

}
