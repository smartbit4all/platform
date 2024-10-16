package org.smartbit4all.api.binarydata;

import java.net.URI;
import org.smartbit4all.api.attachment.bean.BinaryContentData;

public interface ExternalResourceApi {

  BinaryContentData downloadFile(String schema, URI url);

}
