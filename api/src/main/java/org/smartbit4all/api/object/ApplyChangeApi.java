package org.smartbit4all.api.object;

import java.net.URI;
import org.smartbit4all.core.object.ObjectDefinition;
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
  ApplyChangeRequest request();

  /**
   * Construct a new request for the apply change API that is going to be executed in the given
   * branch.
   * 
   * @param branchUri The URI of the branch for the apply change operation.
   * @return The new request instance.
   */
  ApplyChangeRequest request(URI branchUri);

  /**
   * Constructs the {@link ApplyChangeRequest} based on the modification of the {@link ObjectNode}.
   * 
   * @param rootNode The root node of the modified {@link ObjectNode}.
   * @param branchUri The branch URI to save into. If null then no branch is used.
   * @return The {@link ApplyChangeResult} that is result.
   */
  URI applyChanges(ObjectNode rootNode, URI branchUri);

}
