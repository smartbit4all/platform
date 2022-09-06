package org.smartbit4all.api.applychange;

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

}
