package org.smartbit4all.api.binarydata;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import org.smartbit4all.api.attachment.bean.BinaryContentData;

/**
 * This api provides useful functions to manage the {@link BinaryContentData} - attachment objects.
 * 
 * @author Peter Boros
 */
public interface BinaryContentDataApi {

  String readStringContent(URI dataUri, Charset charset) throws IOException;

  BinaryContentData constructFromClassResource(String fileRelativePath,
      String schemaToSave);

  BinaryContentData constructFromFile(String fileRelativePath,
      String schemaToSave);

}
