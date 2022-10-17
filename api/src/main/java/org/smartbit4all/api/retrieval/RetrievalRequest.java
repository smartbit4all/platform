package org.smartbit4all.api.retrieval;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.core.object.ObjectDefinition;

/**
 * The retrieval request define the objects and their references to retrieve.
 * 
 * @author Peter Boros
 */
public final class RetrievalRequest {

  /**
   * The root object to start loading with. Their URI is unique so the key is the URI for the object
   * requests.
   */
  final List<ObjectRetrievalRequest> startWithObjects = new ArrayList<>();

  /**
   * The api reference.
   */
  private final WeakReference<RetrievalApi> apiRef;

  /**
   * The request is constructed by the {@link RetrievalApi}.
   * 
   * @param api The API.
   */
  RetrievalRequest(RetrievalApi api) {
    super();
    this.apiRef = new WeakReference<>(api);
  }

  /**
   * @return Load the object model defined by the request.
   */
  public ObjectModel load() {
    return apiRef.get().load(this);
  }

  /**
   * Add a new URI to start the retrieve with.
   * 
   * @param definition
   * @param uris Add the starting uris.
   * @return
   */
  public ObjectRetrievalRequest startWith(ObjectDefinition<?> definition, URI... uris) {
    ObjectRetrievalRequest result = new ObjectRetrievalRequest(definition, false);
    if (uris != null) {
      Collections.addAll(result.getUriList(), uris);
    }
    startWithObjects.add(result);
    return result;
  }

  /**
   * Add a new URI to start the retrieve with.
   * 
   * @param definition
   * @param uris Add the starting uris.
   * @return
   */
  public ObjectRetrievalRequest startWith(ObjectDefinition<?> definition, Collection<URI> uris) {
    ObjectRetrievalRequest result = new ObjectRetrievalRequest(definition, false);
    if (uris != null) {
      result.getUriList().addAll(uris);
    }
    startWithObjects.add(result);
    return result;
  }

}
