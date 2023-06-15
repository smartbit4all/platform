package org.smartbit4all.api.object;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.api.object.bean.BranchEntry;

/**
 * @author Peter Boros
 *
 */
public class ApplyChangeResult {

  private Map<ObjectChangeRequest, Object> processedRequests = new HashMap<>();

  private BranchEntry branchEntry;

  public ApplyChangeResult(Map<ObjectChangeRequest, Object> processedRequests) {
    super();
    this.processedRequests = processedRequests;
  }

  public final Map<ObjectChangeRequest, Object> getProcessedRequests() {
    return processedRequests;
  }

  public ApplyChangeResult branchEntry(BranchEntry branchEntry) {
    this.branchEntry = branchEntry;
    return this;
  }

}
