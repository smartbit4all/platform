package org.smartbit4all.api.applychange;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.applychange.ObjectChangeRequest.ObjectChangeOperation;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;

/**
 * The api implementations can use this builder api based object to construct a request.
 * 
 * @author Peter Boros
 */
public final class ApplyChangeRequest {

  /**
   * The branch of the modification. It it is null than we use the main branch of the given object.
   */
  private final URI branchUri;

  /**
   * The object api to access the {@link ObjectDefinition}s for the objects.
   */
  private final ObjectApi objectApi;

  ApplyChangeRequest(URI branchUri, ObjectApi objectApi) {
    super();
    this.branchUri = branchUri;
    this.objectApi = objectApi;
  }

  /**
   * The objects to create.
   */
  private final List<ObjectChangeRequest> objectChangeRequests = new ArrayList<>();

  public final URI getBranchUri() {
    return branchUri;
  }

  public final List<ObjectChangeRequest> getObjectChangeRequests() {
    return objectChangeRequests;
  }

  private final ObjectChangeRequest addAndReturn(ObjectChangeRequest request) {
    objectChangeRequests.add(request);
    return request;
  }

  /**
   * This will add a new root object to the request to create as new. By executing the request the
   * object will be saved and its uri will be set.
   * 
   * @param object The object that can be serialized as JSON.
   * @return The object change request is the request node attached to this object.
   */
  public final ObjectChangeRequest createAsNew(String scheme, Object object) {
    if (object == null) {
      throw new IllegalArgumentException("The object to create must not be null");
    }
    return addAndReturn(new ObjectChangeRequest(this, objectApi.definition(object.getClass()),
        scheme, ObjectChangeOperation.NEW).object(object));
  }

  /**
   * This will add a new root object to the request to find with the URI and replace with the object
   * we set here. By executing the request the object will be saved and as a result we will have a
   * new version URI depending on having a branch or not.
   * 
   * @param uri The URI of the object to update with the given object. The uri must be the proper
   *        version uri on the main branch or on the branch that we gave. The modification on the
   *        given object will lead to modification on its container. If we have container then the
   *        container is going to be included in the request implicitly.
   * @param object The object that can be serialized as JSON.
   * @return The object change request is the request node attached to this object.
   */
  public final ObjectChangeRequest replaceWith(String scheme, URI uri, Object object) {
    if (object == null) {
      throw new IllegalArgumentException("The object to create must not be null");
    }
    return addAndReturn(new ObjectChangeRequest(this, objectApi.definition(object.getClass()),
        scheme, ObjectChangeOperation.UPDATE).object(object).uri(uri));
  }

  /**
   * This will add a new root object to the request to find with the URI and set values to update on
   * the object. If we call this function multiple times then the values of the subsequent calls
   * will be merged into the previous ones. At the end we will have the union of all the values to
   * set. If we also set the object itself to replace then first we set the object, construct the
   * Map based representation and update with the map based values we set with this function calls.
   * 
   * @param uri The URI of the object to update with the given object. The uri must be the proper
   *        version uri on the main branch or on the branch that we gave. The modification on the
   *        given object will lead to modification on its container. If we have container then the
   *        container is going to be included in the request implicitly.
   * @param values The map of the values to set in the object. If we have
   *        {@link #replaceWith(String, URI, Object)} instruction and values also then first we set
   *        the object and the next stage is the updating the result map with this values parameter.
   * @return The object change request is the request node attached to this object.
   */
  public final ObjectChangeRequest updateWith(String scheme, URI uri, Map<String, Object> values) {
    if (values == null || values.isEmpty()) {
      throw new IllegalArgumentException("To update an object we must set property values.");
    }
    // Here we have no idea about the class of the given object.
    return addAndReturn(new ObjectChangeRequest(this, null,
        scheme, ObjectChangeOperation.UPDATE).objectAsMap(values).uri(uri));
  }

}
