package org.smartbit4all.api.mimetype;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

public abstract class ContentConversionContributionApiImpl extends ContributionApiImpl
    implements ContentConversionContributionApi {

  @Autowired
  protected ObjectApi objectApi;
  @Autowired
  protected MasterDataManagementApi mdmApi;
  @Autowired
  protected MimeTypeApi mimeTypeApi;
  @Autowired
  protected LocaleSettingApi localeSettingApi;


  public final BusinessLogicException getFailedException(BinaryContentData content,
      String toMimeType,
      Exception e) {
    return new BusinessLogicException(
        MessageFormat.format(localeSettingApi.get(EXCEPTION_CONVERSION_FAIL),
            content.getMimeType(), toMimeType, content.getFileName()),
        e);
  }

  public final BusinessLogicException getFailedException(BinaryContentData content,
      String toMimeType) {
    return new BusinessLogicException(
        MessageFormat.format(localeSettingApi.get(EXCEPTION_CONVERSION_FAIL),
            content.getMimeType(), toMimeType, content.getFileName()));
  }

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

  @Override
  public boolean isMultiOutput() {
    return false;
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

  protected List<BinaryData> convertToMultipleFilesInternal(BinaryContentData content, String toMimeType,
      Map<String, Object> parameters) {
    return Collections.emptyList();
  }

  protected void mergeParams(Map<String, Object> params) {
    ServiceConnection serviceConnection = getServiceConnection();
    if (serviceConnection == null) {
      return;
    }
    serviceConnection.getParameters().entrySet()
        .forEach(e -> params.putIfAbsent(e.getKey(), e.getValue()));
  }

  @Override
  public URI convert(BinaryContentData content, String toMimeType,
      String logicalSchema, Map<String, Object> parameters) {
    if (Objects.equals(content.getMimeType(), toMimeType)) {
      return objectApi.saveAsNew(logicalSchema,
          objectApi.loadLatest(content.getDataUri()).getObject(BinaryDataObject.class)
              .getBinaryData().asObject());
    }
    BinaryData binaryData = convertInternal(content, toMimeType, parameters);
    if (binaryData == null) {
      throw new BusinessLogicException(
          MessageFormat
              .format(localeSettingApi.get("exception.conversion.fail"), content.getMimeType(),
                  toMimeType, content.getFileName()));
    }
    return objectApi.saveAsNew(logicalSchema,
        binaryData.asObject());
  }

  @Override
  public List<URI> convertToMultipleFiles(BinaryContentData content, String toMimeType,
      String logicalSchema, Map<String, Object> parameters) {
    if (Objects.equals(content.getMimeType(), toMimeType)) {
      return List.of(objectApi.saveAsNew(logicalSchema,
          objectApi.loadLatest(content.getDataUri()).getObject(BinaryDataObject.class)
              .getBinaryData().asObject()));
    }
    List<BinaryData> binaryDatas = convertToMultipleFilesInternal(content, toMimeType, parameters);
    if (CollectionUtils.isEmpty(binaryDatas)) {
      throw new BusinessLogicException(
          MessageFormat
              .format(localeSettingApi.get("exception.conversion.fail"), content.getMimeType(),
                  toMimeType, content.getFileName()));
    }

    return binaryDatas.stream().map(data -> objectApi.saveAsNew(logicalSchema,
        data.asObject())).collect(Collectors.toList());
  }

}
