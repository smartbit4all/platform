package org.smartbit4all.api.object.extraction;

import java.net.URI;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRun;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.DataExtraction;
import org.smartbit4all.api.object.bean.DataExtractionDescriptor;

/**
 * Facilitates extraction of structured data from any persistent object using a configurable
 * extraction procedure.
 * 
 * <p>
 * Any object persisted in the object storage can have a data extractor assigned to it. The data
 * extractor is an arbitrary {@code URI -> Persistable<any> } routine, in the form of an
 * {@link InvocationRequest} or an {@link InvocationRun}, saved under a unique identifier. This
 * information is represented by instances of {@link DataExtractionDescriptor}s, and managed by the
 * {@link MasterDataManagementApi#MDM_DEFINITION_SYSTEM_INTEGRATION} definition's
 * {@link PlatformApiConfig#DATA_EXTRACTION_DESCRIPTORS} entry list.
 * 
 * <p>
 * The configured mechanism extracts and saves the data <em>into the source object itself</em> at
 * the terminal of the provided {@link DataExtractionDescriptor#getTargetPath()} (resolved by
 * evaluating the path from the source object).
 */
public interface DataExtractionApi {

  /**
   * Checks if the given persisted domain object has an extractor configured or not.
   * 
   * @param objectUri the unique persistent {@link URI} identifier of the domain object, not null
   * @return true if an extractor is configured for the object, false otherwise
   */
  boolean hasDataExtractor(URI objectUri);

  /**
   * Sets the specified data extractor for a given persisted domain object.
   * 
   * <p>
   * Any previous extractor associated with this object is overwritten. If the specified
   * {@link DataExtractionDescriptor} is {@code null}, the associated extractor - if any - is
   * cleared.
   * 
   * @param objectUri the unique persistent {@link URI} identifier of the domain object, not null
   * @param descriptor the {@link DataExtractionDescriptor} to set; if null, the persisted domain
   *        object's data extractor configuration is purged
   */
  void setDataExtractor(URI objectUri, DataExtractionDescriptor descriptor);

  /**
   * Extracts structured data from the provided persisted domain object.
   * 
   * <p>
   * The domain object must have its data extractor configured.
   * 
   * <p>
   * Care must be taken when the extraction itself is implemented in an asynchronous fashion: this
   * method presupposes inline extractions and acquires a lock for the provided object {@link URI},
   * releasing it automatically when the extraction operation concludes (either with success or
   * failure).
   * 
   * @param objectUri the unique persistent {@link URI} identifier of the domain object, not null
   * @see DataExtractionDescriptor
   * @see DataExtraction
   * @throws BusinessLogicException if the configuration is missing, or if any fatal errors arise
   *         during the extraction operation itself.
   */
  void extractData(URI objectUri);

}
