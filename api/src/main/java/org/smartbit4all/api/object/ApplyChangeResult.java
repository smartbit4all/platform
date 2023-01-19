package org.smartbit4all.api.object;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Peter Boros
 *
 */
public class ApplyChangeResult {

  private Map<ObjectChangeRequest, Object> processedRequests = new HashMap<>();

  public ApplyChangeResult(Map<ObjectChangeRequest, Object> processedRequests) {
    super();
    this.processedRequests = processedRequests;
  }

  public final Map<ObjectChangeRequest, Object> getProcessedRequests() {
    return processedRequests;
  }

}
