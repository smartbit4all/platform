package org.smartbit4all.api.mimetype;

import java.net.URI;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ContentConversionContributionApiImpl extends ContributionApiImpl
    implements ContentConversionContributionApi {

  @Autowired
  SessionApi sessionApi;
  @Autowired
  ObjectApi objectApi;

  protected ContentConversionContributionApiImpl(String apiName) {
    super(apiName);
  }

  /**
   * Handles the conversion itself.
   */
  protected abstract BinaryData convertInternal(BinaryData content);

  /**
   * Handles the conversion with a remote API via the connection defined in the serviceConnection.
   */
  protected abstract BinaryData convertInternal(BinaryData content, ServiceConnection serviceConnection);

  @Override
  public URI convert(BinaryContentData content, String logicalSchema) {
    BinaryDataObject binaryDataObject =
        objectApi.loadLatest(content.getDataUri()).getObject(BinaryDataObject.class);
    BinaryData convertedData = convertInternal(binaryDataObject.getBinaryData());
    return objectApi.saveAsNew(logicalSchema, convertedData.asObject());
  }

  @Override
  public URI convert(BinaryContentData content,
      ServiceConnection serviceConnection, String logicalSchema) {
    BinaryDataObject binaryDataObject =
        objectApi.loadLatest(content.getDataUri()).getObject(BinaryDataObject.class);
    BinaryData convertedData = convertInternal(binaryDataObject.getBinaryData(), serviceConnection);
    return objectApi.saveAsNew(logicalSchema, convertedData.asObject());
  }

}
