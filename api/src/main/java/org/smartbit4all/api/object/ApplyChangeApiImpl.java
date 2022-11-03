package org.smartbit4all.api.object;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.smartbit4all.api.object.ObjectChangeRequest.ObjectChangeOperation;
import org.smartbit4all.api.object.ObjectNode.ObjectNodeState;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplyChangeApiImpl implements ApplyChangeApi {

  @Autowired
  private BranchApi branchApi;

  @Autowired
  private ModifyApi modifyApi;

  @Autowired
  private ObjectApi objectApi;

  @Override
  public ApplyChangeResult save(ApplyChangeRequest request) {
    List<ObjectChangeRequest> finalList =
        request.getObjectChangeRequests().stream().map(r -> preProcessObjectRequest(r))
            .collect(Collectors.toList());

    Map<ObjectChangeRequest, URI> processedRequests = new HashMap<>();
    for (ObjectChangeRequest objectChangeRequest : finalList) {
      execute(objectChangeRequest, request.getBranchUri(), processedRequests);
    }

    return new ApplyChangeResult(processedRequests);
  }

  /**
   * The recursive algorithm is a pre order traversal of the containment DAG. It starts from the
   * deepest nodes. Saves them and create new versions if necessary. The new version depend on the
   * deep compare to avoid creating unnecessary versions! After this the new version is written into
   * and saved into the parents and so on. At the end the root object is updated and we will have
   * another version from this.
   * 
   * @param objectChangeRequest
   */
  private URI execute(ObjectChangeRequest objectChangeRequest, URI branchUri,
      Map<ObjectChangeRequest, URI> processedRequests) {

    // If this object request was already processed then return the URI without processing again.
    URI uri = processedRequests.get(objectChangeRequest);
    if (uri != null) {
      return uri;
    }

    for (Entry<ReferenceDefinition, ReferenceChangeRequest> entry : objectChangeRequest
        .getReferenceChanges().entrySet()) {
      Map<ObjectChangeRequest, URI> changes = new HashMap<>();
      for (ObjectChangeRequest refObjRequest : entry.getValue().changes()) {
        URI refUri = execute(refObjRequest, branchUri, processedRequests);
        changes.put(refObjRequest, refUri);
      }
      entry.getValue().apply(objectChangeRequest,
          changes);
    }

    // By default we return the original uri.
    URI result = objectChangeRequest.getUri();

    switch (objectChangeRequest.getOperation()) {
      case NEW:
        // The new object is created as new so we have to save the branch as the initiating branch
        // for the object. This object and all of its versions belong to this branch.
        result = modifyApi.createNewObject(objectChangeRequest.getDefinition(),
            objectChangeRequest.getStorageScheme(), objectChangeRequest.getOrCreateObjectAsMap(),
            branchUri);
        break;

      case UPDATE:
        // First of all we have to compare the new object with the current version. If there is
        // change then we have to know if we are already on the right branch. If no then we have to
        // branch the given object version. At the end we have to update if there was any change.
        result = modifyApi.updateObject(objectChangeRequest.getDefinition(),
            objectChangeRequest.getUri(),
            objectChangeRequest.getOrCreateObjectAsMap(),
            branchUri);
        break;

      case DELETE:
        // We can skip it because it is just about making unavailable. It is not the deletion of the
        // given object.

        break;

      default:
        break;
    }

    // Add it to the already processed map.
    processedRequests.put(objectChangeRequest, result);
    return result;
  }

  /**
   * TODO Now it's a naive implementation.
   * 
   * This operation must consider the containment relation between the objects and we must find the
   * root objects to start the modification with.
   * 
   * @param objectChangeRequest
   * @return
   */
  private ObjectChangeRequest preProcessObjectRequest(ObjectChangeRequest objectChangeRequest) {
    return objectChangeRequest;
  }

  @Override
  public ApplyChangeRequest request() {
    return request(null);
  }

  @Override
  public ApplyChangeRequest request(URI branchUri) {
    return new ApplyChangeRequest(branchUri, objectApi);
  }

  @Override
  public ApplyChangeResult applyChanges(ObjectNode rootNode, URI branchUri) {
    ApplyChangeRequest request = constructRequest(rootNode, branchUri);
    return save(request);
  }

  private ApplyChangeRequest constructRequest(ObjectNode rootNode, URI branchUri) {
    ApplyChangeRequest request = request(branchUri);
    // Now we assume that the root container is the object that is set here. So we add modification
    // to this object if it is necessary.
    ObjectChangeRequest objectChangeRequest = constructRequest(rootNode, request);
    if (objectChangeRequest.getOperation() == ObjectChangeOperation.NEW
        || objectChangeRequest.getOperation() == ObjectChangeOperation.UPDATE) {
      request.getObjectChangeRequests().add(objectChangeRequest);
    }
    return request;
  }

  private ObjectChangeRequest constructRequest(ObjectNode node, ApplyChangeRequest request) {
    boolean containmentChanged = false;

    // Constructs the result with NOP operation code.
    ObjectChangeRequest result = new ObjectChangeRequest(request, node.getDefinition(),
        node.getStorageScheme(), ObjectChangeOperation.NOP);
    result.setUri(node.getUri());
    if (node.getState() == ObjectNodeState.NEW) {
      result.setOperation(ObjectChangeOperation.NEW);
      result.setObjectAsMap(node.getObjectAsMap());
    } else if (node.getState() == ObjectNodeState.MODIFIED) {
      result.setOperation(ObjectChangeOperation.UPDATE);
      result.setObjectAsMap(node.getObjectAsMap());
    }

    // Recurse on the values.
    for (Entry<ReferenceDefinition, ObjectNode> entry : node.getReferenceValues().entrySet()) {
      ObjectChangeRequest changeRequest = constructRequest(entry.getValue(), request);
      if (changeRequest.getOperation() != ObjectChangeOperation.NOP) {
        containmentChanged = true;
        result.referenceValue(entry.getKey()).value(changeRequest);
      }
    }

    // Recurse on the lists
    for (Entry<ReferenceDefinition, ReferenceListEntry> entry : node.getReferenceListValues()
        .entrySet()) {
      for (ObjectNode objectNode : entry.getValue().getNodeList()) {
        if (objectNode.getState() != ObjectNodeState.REMOVED) {
          ObjectChangeRequest changeRequest = constructRequest(objectNode, request);
          if (changeRequest.getOperation() != ObjectChangeOperation.NOP) {
            containmentChanged = true;
          }
          result.referenceList(entry.getKey()).add(changeRequest);
        }
      }
    }

    if (containmentChanged && node.getState() == ObjectNodeState.NOP) {
      result.setOperation(ObjectChangeOperation.UPDATE);
    }

    return result;

  }

}
