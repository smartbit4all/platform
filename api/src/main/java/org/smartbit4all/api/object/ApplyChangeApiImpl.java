package org.smartbit4all.api.object;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.smartbit4all.api.object.ObjectChangeRequest.ObjectChangeOperation;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplyChangeApiImpl implements ApplyChangeApi {

  @Autowired
  private BranchApi branchApi;

  @Autowired
  private ModifyApi modifyApi;

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

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

    for (Entry<String, ReferenceChangeRequest> entry : objectChangeRequest
        .getReferenceChanges().entrySet()) {
      // We need a linked hash map to reserve the order in the containers.
      Map<ObjectChangeRequest, URI> changes = new LinkedHashMap<>();
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
    return new ApplyChangeRequest(branchUri, objectDefinitionApi);
  }

  @Override
  public URI applyChanges(ObjectNode rootNode, URI branchUri) {
    return constructRequestAndSave(rootNode, branchUri);
  }

  private URI constructRequestAndSave(ObjectNode rootNode, URI branchUri) {
    ApplyChangeRequest request = request(branchUri);
    // Now we assume that the root container is the object that is set here. So we add modification
    // to this object if it is necessary.
    ObjectChangeRequest objectChangeRequest = constructRequest(rootNode, request);
    if (objectChangeRequest.getOperation() == ObjectChangeOperation.NEW
        || objectChangeRequest.getOperation() == ObjectChangeOperation.UPDATE) {
      request.getObjectChangeRequests().add(objectChangeRequest);
    }
    ApplyChangeResult result = save(request);
    return result.getProcessedRequests().get(objectChangeRequest);
  }

  private ObjectChangeRequest constructRequest(ObjectNode node, ApplyChangeRequest request) {
    boolean containmentChanged = false;

    // Constructs the result with NOP operation code.
    ObjectChangeRequest result = new ObjectChangeRequest(request, node.getDefinition(),
        node.getStorageScheme(), ObjectChangeOperation.NOP);
    result.setUri(node.getObjectUri());
    result.setObjectAsMap(node.getObjectAsMap());
    if (node.getState() == ObjectNodeState.NEW) {
      result.setOperation(ObjectChangeOperation.NEW);
    } else if (node.getState() == ObjectNodeState.MODIFIED) {
      result.setOperation(ObjectChangeOperation.UPDATE);
    }

    // Recurse on the values.
    for (Entry<String, ObjectNodeReference> entry : node.getReferences().entrySet()) {
      ObjectNodeReference ref = entry.getValue();
      String refName = entry.getKey();
      if (ref.isLoaded()) {
        ObjectChangeRequest changeRequest = constructRequest(ref.get(), request);
        if (changeRequest.getOperation() != ObjectChangeOperation.NOP) {
          containmentChanged = true;
          result.referenceValue(refName).value(changeRequest);
        }
      } else {
        if (ref.getState() != ObjectNodeState.NOP) {
          // ref URI has changed
          if (result.getOperation() == ObjectChangeOperation.NOP) {
            result.setOperation(ObjectChangeOperation.UPDATE);
          }
          ReferenceDefinition refDefinition = node.getDefinition().getOutgoingReference(refName);
          String refProperty = refDefinition.getSourcePropertyPath();
          result.getObjectAsMap().put(refProperty, ref.getObjectUri());
        }
      }
    }

    // Recurse on the lists
    for (Entry<String, ObjectNodeList> entry : node.getReferenceLists().entrySet()) {
      for (ObjectNodeReference ref : entry.getValue().references()) {
        if (ref.isLoaded()) {
          ObjectNode objectNode = ref.get();
          if (objectNode.getState() != ObjectNodeState.REMOVED) {
            ObjectChangeRequest changeRequest = constructRequest(objectNode, request);
            if (changeRequest.getOperation() != ObjectChangeOperation.NOP) {
              containmentChanged = true;
            }
            result.referenceList(entry.getKey()).add(changeRequest);
          }
        } else {
          // TODO not loaded may change the URI!
        }
      }
    }

    // TODO recurse on Maps!

    if (containmentChanged && node.getState() == ObjectNodeState.NOP) {
      result.setOperation(ObjectChangeOperation.UPDATE);
    }

    return result;

  }

}
