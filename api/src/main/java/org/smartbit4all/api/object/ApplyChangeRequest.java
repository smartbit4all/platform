package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.object.ObjectChangeRequest.ObjectChangeOperation;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectNode;
import com.google.common.base.Strings;

/**
 * The api implementations can use this builder api based object to construct a request.
 *
 * @author Peter Boros
 */
public final class ApplyChangeRequest {

  /**
   * The branch of the modification. It it is null than we use the main branch of the given object.
   */
  private final BranchEntry branchEntry;

  /**
   * The object api to access the {@link ObjectDefinition}s for the objects.
   */
  private final ObjectDefinitionApi objectDefinitionApi;

  ApplyChangeRequest(ObjectDefinitionApi objectDefinitionApi, BranchEntry branchEntry) {
    super();
    this.branchEntry = branchEntry;
    this.objectDefinitionApi = objectDefinitionApi;
  }

  /**
   * The root object changes.
   */
  private final List<ObjectChangeRequest> objectChangeRequests = new ArrayList<>();

  public final BranchEntry getBranchEntry() {
    return branchEntry;
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
   * @param node The object that can be serialized as JSON.
   * @return The object change request is the request node attached to this object.
   */
  public final ObjectChangeRequest createAsNew(ObjectNode node) {
    if (node == null) {
      throw new IllegalArgumentException("The object node to create must not be null");
    }
    return addAndReturn(
        new ObjectChangeRequest(node));
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
   * @param values The map of the values to set in the object.
   * @return The object change request is the request node attached to this object.
   */
  public final ObjectChangeRequest updateWith(URI uri, Map<String, Object> values) {
    if (uri == null || Strings.isNullOrEmpty(uri.getScheme())) {
      throw new IllegalArgumentException("The uri is missing for the replacement.");
    }
    if (values == null || values.isEmpty()) {
      throw new IllegalArgumentException("To update an object we must set property values.");
    }
    // Here we have no idea about the class of the given object.
    return addAndReturn(new ObjectChangeRequest(null,
        uri.getScheme(), ObjectChangeOperation.UPDATE).objectAsMap(values).uri(uri));
  }

  /**
   * This will add a new root object to the request to find with the URI. In this case we don't want
   * to modify the values of the given object but we need this to modify its references. At the same
   * time it will result a modification on the object because the references are also
   *
   * @param uri The URI of the object to update with the given object. The uri must be the proper
   *        version uri on the main branch or on the branch that we gave. The modification on the
   *        given object will lead to modification on its container. If we have container then the
   *        container is going to be included in the request implicitly.
   * @return The object change request is the request node attached to this object.
   */
  public final ObjectChangeRequest include(URI uri) {
    if (uri == null || Strings.isNullOrEmpty(uri.getScheme())) {
      throw new IllegalArgumentException("The uri is missing for the replacement.");
    }
    // Here we have no idea about the class of the given object.
    return addAndReturn(new ObjectChangeRequest(null,
        uri.getScheme(), ObjectChangeOperation.NOP).uri(uri));
  }

}
