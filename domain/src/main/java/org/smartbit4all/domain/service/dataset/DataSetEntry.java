package org.smartbit4all.domain.service.dataset;

import java.net.URI;

/**
 * This API object is a descriptor for the data set. This is used by the {@link DataSetApi}.
 * 
 * @author Peter Boros
 */
public class DataSetEntry {

  /**
   * The identification of the data set. Consists of the provider as scheme. The provider is a kind
   * of storage for the data set. The path refers the storage inside the provider. Something like
   * numbers or strings. Usually it is the table name for example in case of RDBMS storage. The
   * fragment will identify the data set as a name inside the given provider.
   */
  private URI uri;

  /**
   * The URI of the property that is stored in the data set. It's important to see what is the
   * content. It can help to identify how to apply this set in a filtering situation. The URI looks
   * like this: provider:/entity#property
   */
  private URI propertyUri;

}
