package org.smartbit4all.api.retrieval;

import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * The retrieval API is responsible for reading the objects across the {@link ReferenceDefinition}s.
 * We can define a request to load a portion of the object graph and later on we can append the rest
 * if necessary.
 * 
 * @author Peter Boros
 */
public interface RetrievalApi {

  /**
   * Executes the request and retrieve the objects.
   * 
   * @param request The request object that defines the root objects and the references to load.
   * @return The result is an object model that contains every object that is retrieved by the
   *         request.
   */
  ObjectModel load(RetrievalRequest request);

  /**
   * @return Initiate a new request.
   */
  RetrievalRequest request();

}
