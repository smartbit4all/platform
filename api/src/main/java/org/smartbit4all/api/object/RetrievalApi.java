package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import java.util.concurrent.locks.Lock;
import org.smartbit4all.api.object.bean.BranchEntry;
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
   * @param branchEntry The branch entry if the retrieve is performed on the branch.
   * @return The result of the object retrieve as an {@link ObjectNode}.
   */
  ObjectNodeData load(RetrievalRequest request, URI uri, BranchEntry branchEntry);

  default ObjectNodeData load(RetrievalRequest request, URI uri) {
    return load(request, uri, null);
  }

  /**
   * Executes the request and retrieve the object.
   *
   * @param request The request object that defines the root objects and the references to load.
   * @param uris The uri list of the objects to load.
   * @param branchEntry The branch entry if the retrieve is performed on the branch.
   * @return The result of the object retrieve as an {@link ObjectNodeData}.
   */
  List<ObjectNodeData> load(RetrievalRequest request, List<URI> uris, BranchEntry branchEntry);

  default List<ObjectNodeData> load(RetrievalRequest request, List<URI> uris) {
    return load(request, uris, null);
  }

  /**
   * Get a lock object for the given URI. The URI is not necessarily exists at the moment of the
   * lock creation. We can use this lock one time to place a lock and remove it at the end.
   * 
   * @param uri The URI of the object. It doesn't matter if it is latest or not the lock will be
   *        applied on the object not on the version of the object.
   * @return A {@link Lock} object that can be used like a normal Java Lock.
   */
  Lock getLock(URI uri);

  /**
   * Retrieves the last modification of the given object identified by the URI.
   * 
   * @param uri The uri of the object
   * @return return the last modification time epoch or null if the object doesn't exist.
   */
  Long getLastModified(URI uri);

  /**
   * Perform a quick check for existence of a given uri on the given branch.
   * 
   * @param uri The object uri to check.
   * @param branchEntry The branch entry if the retrieve is performed on the branch.
   * @return Return true if the given entry exists. Doesn't check the consistency of the data
   *         because it's not loading data itself.
   */
  boolean exists(URI uri, BranchEntry branchEntry);



}
