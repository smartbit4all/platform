package org.smartbit4all.api.utils;

import java.io.IOException;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.springframework.web.multipart.MultipartFile;

public interface RestUtilsApi {

  BinaryContentData constructBinaryContentDataFromMultipartFile(String schema,
      MultipartFile multipartFile)
      throws IOException;

}
