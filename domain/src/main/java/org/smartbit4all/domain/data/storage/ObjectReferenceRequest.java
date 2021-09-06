package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.ObjectReferenceList;
import org.smartbit4all.core.utility.StringConstant;

/**
 * In generally if we have an object stored by a {@link Storage} then we might need to store some
 * related objects for this. The related object set is more or less a list of URI or other
 * identifier easy to serialize in every storage mechanism. The Storage can publish events when the
 * given object is changed. This can be filtered by this register. The relation can be temporary so
 * we can define a time limit when the {@link Storage} will remove the relation. The relation can be
 * renewed by adding it again and again.
 * 
 * @author Peter Boros
 */
public final class ObjectReferenceRequest {

  /**
   * The URI of the root object that we store the references for. Can be null if we call the save
   * without knowing the URI.
   */
  private URI objectUri;

  /**
   * The type class for the references.
   */
  private Class<?> typeClass;

  /**
   * The references to create while storing the root object.
   */
  private List<ObjectReference> referencesToAdd = null;

  /**
   * The references to remove while storing the root object.
   */
  private List<ObjectReference> referencesToRemove = null;

  public ObjectReferenceRequest(URI objectUri, Class<?> typeClass) {
    super();
    this.objectUri = objectUri;
    this.typeClass = typeClass;
  }

  public final URI getObjectUri() {
    return objectUri;
  }

  public final void setObjectUri(URI objectUri) {
    this.objectUri = objectUri;
  }

  public final ObjectReferenceRequest objectUri(URI objectUri) {
    this.objectUri = objectUri;
    return this;
  }

  public final ObjectReferenceRequest create(ObjectReference ref) {
    if (referencesToAdd == null) {
      referencesToAdd = new ArrayList<>();
    }
    referencesToAdd.add(ref);
    return this;
  }

  public final ObjectReferenceRequest add(String refId) {
    return create(new ObjectReference().referenceId(refId));
  }

  public final ObjectReferenceRequest add(String refId,
      LocalDateTime expirationTime) {
    // TODO Expiration time --> LocalDateTime
    return create(new ObjectReference().referenceId(refId));
  }

  public final ObjectReferenceRequest delete(ObjectReference ref) {
    if (referencesToRemove == null) {
      referencesToRemove = new ArrayList<>();
    }
    referencesToRemove.add(ref);
    return this;
  }

  public final ObjectReferenceRequest delete(String refId, String typeClassName) {
    return delete(new ObjectReference().referenceId(refId));
  }

  public final List<ObjectReference> getReferencesToCreate() {
    return referencesToAdd == null ? Collections.emptyList() : referencesToAdd;
  }

  public final void setReferencesToCreate(List<ObjectReference> referencesToCreate) {
    this.referencesToAdd = referencesToCreate;
  }

  public final List<ObjectReference> getReferencesToRemove() {
    return referencesToRemove == null ? Collections.emptyList() : referencesToRemove;
  }

  public final void setReferencesToRemove(List<ObjectReference> referencesToRemove) {
    this.referencesToRemove = referencesToRemove;
  }

  public boolean updateReferences(ObjectReferenceList refList) {
    Map<String, ObjectReference> referencesMap =
        refList.getReferences().stream().collect(Collectors.toMap(r -> r.getReferenceId(), r -> r));
    if (referencesToAdd != null) {
      referencesToAdd.forEach(r -> {
        referencesMap.put(r.getReferenceId(), r);
      });
    }
    if (referencesToRemove != null) {
      referencesToRemove.forEach(r -> referencesMap.remove(r.getReferenceId()));
    }
    refList.setReferences(referencesMap.values().stream().collect(Collectors.toList()));
    return !referencesMap.isEmpty();
  }

  public final Class<?> getTypeClass() {
    return typeClass;
  }

  public final void setTypeClass(Class<?> typeClass) {
    this.typeClass = typeClass;
  }

  public final String getTypeClassName() {
    return typeClass == null ? StringConstant.EMPTY : typeClass.getName();
  }

}
