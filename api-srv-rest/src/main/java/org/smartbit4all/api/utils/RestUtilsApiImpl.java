package org.smartbit4all.api.utils;

import java.io.IOException;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class RestUtilsApiImpl implements RestUtilsApi {

  @Autowired
  private ObjectApi objectApi;
  @Autowired(required = false)
  private SessionApi sessionApi;
  @Autowired
  private MimeTypeApi mimeTypeApi;

  @Override
  public BinaryContentData constructBinaryContentDataFromMultipartFile(String schema,
      MultipartFile file) throws IOException {
    String mimeType = mimeTypeApi.getMimeType(file.getOriginalFilename());
    BinaryData binaryData = BinaryData.of(file.getInputStream());
    UserActivityLog activityLog = sessionApi == null ? null : sessionApi.createActivityLog();
    return new BinaryContentData()
        .dataUri(objectApi.saveAsNew(schema,
            binaryData.asObject()))
        .fileName(file.getOriginalFilename())
        .created(activityLog)
        .updated(activityLog)
        .mimeType(mimeType)
        .extension(mimeTypeApi.getExtension(mimeType))
        .size(file.getSize())
        .contentHash(binaryData.hashIfPresent());
  }
}
