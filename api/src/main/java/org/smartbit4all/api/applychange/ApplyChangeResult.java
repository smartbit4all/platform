package org.smartbit4all.api.applychange;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Peter Boros
 *
 */
public class ApplyChangeResult {

  private Map<ObjectChangeRequest, URI> processedRequests = new HashMap<>();

  public ApplyChangeResult(Map<ObjectChangeRequest, URI> processedRequests) {
    super();
    this.processedRequests = processedRequests;
  }

  public final Map<ObjectChangeRequest, URI> getProcessedRequests() {
    return processedRequests;
  }

}
