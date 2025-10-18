package org.smartbit4all.api.binarydata.extraction;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.attachment.bean.DataExtraction;
import org.smartbit4all.api.attachment.bean.DataExtractionDescriptor;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class DataExtractionApiImpl implements DataExtractionApi {

  private static final Logger log = LoggerFactory.getLogger(DataExtractionApiImpl.class);

  private static final String DATA_EXTRACTION_REF = "__dataExtraction";

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private CollectionApi collectionApi;
  @Autowired
  private MasterDataManagementApi mdmApi;
  @Autowired(required = false)
  private SessionApi sessionApi;
  @Autowired
  private InvocationApi invocationApi;

  private StoredReference<DataExtraction> dataExtractionRef(final URI objectUri) {
    return collectionApi.reference(
        objectUri,
        objectUri.getScheme(),
        DATA_EXTRACTION_REF,
        DataExtraction.class);
  }

  @Override
  public boolean hasDataExtractor(URI objectUri) {
    return dataExtractionRef(objectUri).exists();
  }

  @Override
  public void setDataExtractor(URI objectUri, DataExtractionDescriptor descriptor) {
    Objects.requireNonNull(objectUri, "Object URI cannot be null!");

    final StoredReference<DataExtraction> dataExtractionRef = dataExtractionRef(objectUri);
    if (descriptor == null) {
      if (dataExtractionRef.exists()) {
        dataExtractionRef.clear();
      }
      return;
    }

    dataExtractionRef.set(new DataExtraction()
        .created(activityLog())
        .extractorId(descriptor.getIdentifier()));
  }

  private UserActivityLog activityLog() {
    if (sessionApi == null) {
      return new UserActivityLog().timestamp(OffsetDateTime.now());
    }

    return sessionApi.createActivityLog();
  }

  @Override
  public void extractData(URI objectUri) {
    final StoredReference<DataExtraction> dataExtractionRef = dataExtractionRef(objectUri);
    if (!dataExtractionRef.exists()) {
      throw new BusinessLogicException("data-extractor.not-configured");
    }

    final DataExtraction dataExtraction = dataExtractionRef.get();
    final String extractorId = dataExtraction.getExtractorId();

    final MDMEntryApi extractionDescriptors = mdmApi.getApi(
        MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
        PlatformApiConfig.DATA_EXTRACTION_DESCRIPTORS);
    final DataExtractionDescriptor descriptor = extractionDescriptors.getList().nodes()
        .filter(it -> extractorId.equals(it.getValueAsString(DataExtractionDescriptor.IDENTIFIER)))
        .findFirst()
        .map(it -> it.getObject(DataExtractionDescriptor.class))
        .orElseThrow(() -> new BusinessLogicException("data-extractor.unknown"));

    final Lock lock = objectApi.getLock(objectUri);
    lock.lock();
    try {

      final var invocationResult = invocationApi.invoke(descriptor.getExtractorFn(), objectUri);
      if (invocationResult == null || invocationResult.getValue() == null) {
        throw new BusinessLogicException("data-extractor.no-result");
      }

      final var result = invocationResult.getValue();
      final ObjectNode resultNode = objectApi.create(objectUri.getScheme(), result);

      final ObjectNode objectNode = objectApi.loadLatest(objectUri);
      objectNode.ref(descriptor.getTargetPath().toArray(String[]::new)).set(resultNode);
      objectApi.save(objectNode);
      dataExtractionRef.update(it -> it.updated(activityLog()));

    } catch (BusinessLogicException e) {
      throw e;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      // TODO: Set error marker in object or wherever.
    } finally {
      lock.unlock();
    }

  }

}
