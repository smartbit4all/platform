package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * This api can be used to make modification on the object graph defined by the
 * {@link ObjectDefinition}s and {@link ReferenceDefinition}s.
 *
 * @author Peter Boros
 */
public interface ApplyChangeApi {

  /**
   * The save changes analyzes the object request as a graph and calculates the necessary operation
   * to execute the save.
   *
   * @param request
   * @return
   */
  ApplyChangeResult save(ApplyChangeRequest request);

  /**
   * Construct a new request for the apply change API.
   *
   * @return The new request instance.
   */
  default ApplyChangeRequest request() {
    return request(null);
  }

  /**
   * Construct a new request for the apply change API that is going to be executed in the given
   * branch.
   *
   * @param branchEntry The branch for the apply change operation.
   * @return The new request instance.
   */
  ApplyChangeRequest request(BranchEntry branchEntry);

  /**
   * Constructs the {@link ApplyChangeRequest} based on the modification of the {@link ObjectNode}.
   *
   * @param rootNode The root node of the modified {@link ObjectNode}.
   * @param branchEntry The branch entry to save into. If null then no branch is used.
   * @return The {@link ApplyChangeResult} that is result.
   */
  URI applyChanges(ObjectNode rootNode, BranchEntry branchEntry);

  /**
   * Constructs the {@link ApplyChangeRequest} based on the modification of the {@link ObjectNode}.
   * With no branch it will save the result in the current object state.
   *
   * @param rootNode The root node of the modified {@link ObjectNode}.
   * @return The {@link ApplyChangeResult} that is result.
   */
  default URI applyChanges(ObjectNode rootNode) {
    return applyChanges(rootNode, null);
  }

}
