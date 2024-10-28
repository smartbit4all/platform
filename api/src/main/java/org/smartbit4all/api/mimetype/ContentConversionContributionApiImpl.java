package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ContentConversionContributionApiImpl extends ContributionApiImpl
    implements ContentConversionContributionApi {

  @Autowired
  ObjectApi objectApi;

  @Autowired
  MasterDataManagementApi mdmApi;

  /**
   * This option is set for every conversion api. If set true then the
   * {@link MasterDataManagementApi#MDM_DEFINITION_SYSTEM_INTEGRATION}
   * {@link ContentConversionApi#MDM_CONVERSION_SERVICES} entry must contains a ServiceConnection
   * record named bound with the contribution api.
   */
  protected boolean needServiceConnection = false;

  protected ContentConversionContributionApiImpl(String apiName) {
    super(apiName);
  }

  @Override
  public boolean isAvailable() {
    if (needServiceConnection) {
      ServiceConnection serviceConnection = getServiceConnection();
      if (serviceConnection == null) {
        return false;
      }
    }
    return true;
  }

  protected final ServiceConnection getServiceConnection() {
    MDMEntryApi entryApi =
        mdmApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            ContentConversionApi.MDM_CONVERSION_SERVICES);
    return entryApi.lookup().findByUnique(
        new ObjectPropertyValue().addPathItem(ServiceConnection.NAME).value(getApiName()),
        ServiceConnection.class);
  }

  /**
   * Handles the conversion itself.
   */
  protected abstract BinaryData convertInternal(BinaryContentData content, String toMimeType,
      Map<String, Object> parameters);

  @Override
  public URI convert(BinaryContentData content, String toMimeType,
      String logicalSchema, Map<String, Object> parameters) {
    if (Objects.equals(content.getMimeType(), toMimeType)) {
      return objectApi.saveAsNew(logicalSchema,
          objectApi.loadLatest(content.getDataUri()).getObject(BinaryDataObject.class)
              .getBinaryData().asObject());
    }
    return objectApi.saveAsNew(logicalSchema,
        convertInternal(content, toMimeType, parameters).asObject());
  }

}
