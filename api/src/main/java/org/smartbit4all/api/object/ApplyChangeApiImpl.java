package org.smartbit4all.api.object;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.smartbit4all.api.object.ObjectChangeRequest.ObjectChangeOperation;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchOperation;
import org.smartbit4all.api.object.bean.BranchOperation.OperationTypeEnum;
import org.smartbit4all.api.object.bean.BranchedObject;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectNodeReference;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplyChangeApiImpl implements ApplyChangeApi {

  @Autowired
  private ModifyApi modifyApi;

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  @Override
  public ApplyChangeResult save(ApplyChangeRequest request) {
    List<ObjectChangeRequest> finalList =
        request.getObjectChangeRequests().stream()
            .map(this::preProcessObjectRequest)
            .collect(Collectors.toList());

    Map<ObjectChangeRequest, Object> processedRequests = new HashMap<>();
    for (ObjectChangeRequest objectChangeRequest : finalList) {
      execute(objectChangeRequest, request.getBranchEntry(), processedRequests);
    }

    return new ApplyChangeResult(processedRequests).branchEntry(request.getBranchEntry());
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
  private Object execute(ObjectChangeRequest objectChangeRequest, BranchEntry branchEntry,
      Map<ObjectChangeRequest, Object> processedRequests) {

    // If this object request was already processed then return the URI without processing again.
    Object uri = processedRequests.get(objectChangeRequest);
    if (uri != null) {
      return uri;
    }

    for (Entry<String, ReferenceChangeRequest> entry : objectChangeRequest
        .getReferenceChanges().entrySet()) {
      // We need a linked hash map to reserve the order in the containers.
      Map<ObjectChangeRequest, Object> changes = new LinkedHashMap<>();
      for (ObjectChangeRequest refObjRequest : entry.getValue().changes()) {
        Object refUri = execute(refObjRequest, branchEntry, processedRequests);
        changes.put(refObjRequest, refUri);
      }
      entry.getValue().apply(objectChangeRequest,
          changes);
    }

    Object result;
    if (objectChangeRequest.getUriToSaveUri() != null) {
      // if we have the referenced URI set before, then we use it
      result = objectChangeRequest.getUriToSaveUri();
    } else if (objectChangeRequest.getDefinition() != null
        && objectChangeRequest.getDefinition().getUriGetter() == null) {
      switch (objectChangeRequest.getOperation()) {
        case NEW:
        case UPDATE:
          result = objectChangeRequest.getObjectAsMap();
          break;
        case DELETE:
          result = null;
        default:
          result = objectChangeRequest.getObjectAsMap();
          break;
      }
    } else {
      switch (objectChangeRequest.getOperation()) {
        case NEW:
          // The new object is created as new so we have to save the branch as the initiating branch
          // for the object. This object and all of its versions belong to this branch.
          result = modifyApi.createNewObject(objectChangeRequest.getDefinition(),
              objectChangeRequest.getStorageScheme(), objectChangeRequest.getObjectNode());
          registerNewObjectToBranch(branchEntry, (URI) result);
          break;

        case UPDATE:
          // First of all we have to compare the new object with the current version. If there is
          // change then we have to know if we are already on the right branch. If no then we have
          // to
          // branch the given object version. At the end we have to update if there was any change.
          // If we have a branch then we must figure out if it is an update on the branch or a new
          // branched object from the source.
          if (branchEntry != null) {
            URI uriWithoutVersion =
                ObjectStorageImpl.getUriWithoutVersion(objectChangeRequest.getUri());
            BranchedObject alreadyExistingBranchedObject =
                branchEntry.getBranchedObjects().get(uriWithoutVersion.toString());
            if (alreadyExistingBranchedObject == null) {
              // try to find the already existing by the target uri.
              alreadyExistingBranchedObject = branchEntry.getBranchedObjects().values().stream()
                  .filter(bo -> bo.getBranchedObjectLatestUri().equals(uriWithoutVersion))
                  .findFirst().orElse(null);
            }
            // Check if it is new object to avoid register it as modified also.
            if (alreadyExistingBranchedObject == null) {
              alreadyExistingBranchedObject =
                  branchEntry.getNewObjects().get(uriWithoutVersion.toString());
            }
            if (alreadyExistingBranchedObject == null) {
              result = modifyApi.createNewObject(objectChangeRequest.getDefinition(),
                  objectChangeRequest.getStorageScheme(),
                  objectChangeRequest.getObjectNode());
              registerBranchedObjectToBranch(branchEntry, uriWithoutVersion, (URI) result);
            } else {
              // We modify the existing branched object whatever URI we have in the original
              // request.
              result = modifyApi.updateObject(objectChangeRequest.getDefinition(),
                  alreadyExistingBranchedObject.getBranchedObjectLatestUri(),
                  objectChangeRequest.getObjectNode());
            }
          } else {
            result = modifyApi.updateObject(objectChangeRequest.getDefinition(),
                objectChangeRequest.getUri(),
                objectChangeRequest.getObjectNode());
          }
          break;

        case DELETE:
          // We can skip it because it is just about making unavailable. It is not the deletion of
          // the given object, but referenced URI should be null.
          // TODO modifyApi.deleteObject()
          result = null;
          break;

        default:
          // NOP - referenced uri stays the same.
          result = objectChangeRequest.getUri();
          break;
      }
    }

    // Add it to the already processed map.
    processedRequests.put(objectChangeRequest, result);
    if (result instanceof URI) {
      objectChangeRequest.setResult((URI) result);
    }
    if (result == null) {
      objectChangeRequest.setResult(null);
    }
    return result;
  }

  private void registerNewObjectToBranch(BranchEntry branchEntry, URI result) {
    if (branchEntry != null) {
      // We should add this new object to new object of the branch entry.
      URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(result);
      branchEntry.putNewObjectsItem(uriWithoutVersion.toString(),
          new BranchedObject().branchedObjectLatestUri(uriWithoutVersion));
    }
  }

  private void registerBranchedObjectToBranch(BranchEntry branchEntry, URI sourceUri, URI result) {
    if (branchEntry != null) {
      // We should add this new object to new object of the branch entry.
      URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(result);
      URI sourceLatestUri = ObjectStorageImpl.getUriWithoutVersion(sourceUri);
      branchEntry.putBranchedObjectsItem(
          sourceLatestUri.toString(),
          new BranchedObject().sourceObjectLatestUri(sourceLatestUri)
              .branchedObjectLatestUri(uriWithoutVersion).addOperationsItem(
                  new BranchOperation().operationType(OperationTypeEnum.INIT)
                      .sourceUri(sourceUri).targetUri(result)));
    }
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
  public ApplyChangeRequest request(BranchEntry branchEntry) {
    return new ApplyChangeRequest(objectDefinitionApi, branchEntry);
  }

  @Override
  public URI applyChanges(ObjectNode rootNode, BranchEntry branchEntry) {
    return constructRequestAndSave(rootNode, branchEntry);
  }

  private URI constructRequestAndSave(ObjectNode rootNode, BranchEntry branchEntry) {
    ApplyChangeRequest request = request(branchEntry);
    // Now we assume that the root container is the object that is set here. So we add modification
    // to this object if it is necessary.
    ObjectChangeRequest objectChangeRequest = constructRequest(rootNode);
    if (objectChangeRequest.getOperation() == ObjectChangeOperation.NEW
        || objectChangeRequest.getOperation() == ObjectChangeOperation.UPDATE) {
      request.getObjectChangeRequests().add(objectChangeRequest);
    }
    ApplyChangeResult applyChangeResult = save(request);
    Object result = applyChangeResult.getProcessedRequests().get(objectChangeRequest);
    if (result instanceof URI) {
      return (URI) result;
    }
    return null;
  }

  private ObjectChangeRequest constructRequest(ObjectNode node) {
    boolean containmentChanged = false;

    ObjectChangeRequest result = new ObjectChangeRequest(node);

    // Recurse on the values.
    for (Entry<String, ObjectNodeReference> entry : node.getReferences().entrySet()) {
      ObjectChangeRequest changeRequest = constructRequest(entry.getValue());
      if (changeRequest.getOperation() != ObjectChangeOperation.NOP ||
          entry.getValue().getState() != ObjectNodeState.NOP) {
        containmentChanged = true;
        result.referenceValue(entry.getKey()).value(changeRequest);
      }
    }

    // Recurse on the lists
    for (Entry<String, ObjectNodeList> entry : node.getReferenceLists().entrySet()) {
      for (ObjectNodeReference ref : entry.getValue().references()) {
        ObjectChangeRequest changeRequest = constructRequest(ref);
        if (changeRequest.getOperation() == ObjectChangeOperation.DELETE) {
          // list has changed, should be applied, without this reference
          containmentChanged = true;
          result.referenceList(entry.getKey());
        } else {
          if (changeRequest.getOperation() != ObjectChangeOperation.NOP) {
            containmentChanged = true;
          }
          // even NOP references must be added to list, because whole list will be applied
          result.referenceList(entry.getKey()).add(changeRequest);
        }
      }
    }
    // TODO recurse on Maps!

    if (containmentChanged && node.getState() == ObjectNodeState.NOP) {
      result.setOperation(ObjectChangeOperation.UPDATE);
    }
    return result;

  }

  private ObjectChangeRequest constructRequest(ObjectNodeReference ref) {
    if (ref.isLoaded()) {
      return constructRequest(ref.get());
    } else {
      return new ObjectChangeRequest(ref);
    }
  }

}
