package org.smartbit4all.domain.service.dataset;

import java.net.URI;

/**
 * This API is responsible for storing, accessing and using set of data. We can add a new set of
 * data by identifying it with an URI. This URI can be used to identify the data set later on. The
 * URI can be used to contribute a set. We can union, intersect, minus etc. to sets identified by an
 * URI and the result will be an updated set even if it's a new one or an existing.
 * 
 * @author Peter Boros
 */
public interface DataSetApi {

  /**
   * We can retrieve the {@link DataSetEntry} identified by the given URI.
   * 
   * @param uri
   * @return
   */
  DataSetEntry getEntry(URI uri);



}
