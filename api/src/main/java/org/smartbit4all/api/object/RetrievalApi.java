package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.core.object.ObjectNode;
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
   * Executes the request and retrieve the object.
   * 
   * @param request The request object that defines the root objects and the references to load.
   * @param uri The uri of the object to load.
   * @return The result of the object retrieve as an {@link ObjectNode}.
   */
  ObjectNodeData load(ObjectRetrievalRequest request, URI uri);

  /**
   * Executes the request and retrieve the object.
   * 
   * @param request The request object that defines the root objects and the references to load.
   * @param uris The uris of the objects to load.
   * @return The result of the object retrieve as an {@link ObjectNode}.
   */
  List<ObjectNodeData> load(ObjectRetrievalRequest request, URI... uris);

  /**
   * Executes the request and retrieve the object.
   * 
   * @param request The request object that defines the root objects and the references to load.
   * @param uris The uri list of the objects to load.
   * @return The result of the object retrieve as an {@link ObjectNodeData}.
   */
  List<ObjectNodeData> load(ObjectRetrievalRequest request, List<URI> uris);

}
