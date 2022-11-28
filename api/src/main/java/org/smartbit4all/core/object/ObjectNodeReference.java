package org.smartbit4all.core.object;

import java.net.URI;
import java.util.Objects;
import org.smartbit4all.api.object.bean.ObjectNodeState;

public class ObjectNodeReference {

  private final URI originalObjectUri;

  private final ObjectNode originalNode;

  private final ObjectNode referrerNode;

  private URI objectUri;

  private boolean isLoaded;

  private boolean isPresent;

  /**
   * Referred {@link ObjectNode}. If is null, then either reference is empty (if originalObjectUri
   * == null), or not loaded.
   */
  private ObjectNode node;

  ObjectNodeReference(ObjectNode referrerNode, URI objectUri, ObjectNode node) {
    if (node != null && !Objects.equals(node.getObjectUri(), objectUri)) {
      throw new IllegalArgumentException("node.getObjectUri must match to objectUri");
    }
    this.referrerNode = referrerNode;
    this.originalObjectUri = objectUri;
    this.originalNode = node;
    this.objectUri = originalObjectUri;
    this.node = originalNode;
    this.isLoaded = this.node != null;
    this.isPresent = objectUri != null;
    // TODO is it true / necessary?
    refreshNodeState();
  }

  private void refreshNodeState() {
    if (isLoaded) {
      this.node.setState(getState());
    }
  }

  public ObjectNode setNewObject(Object object) {
    ObjectNode newNode = referrerNode.objectApi.create(referrerNode.getStorageScheme(), object);
    newNode.setState(ObjectNodeState.NEW);
    set(newNode);
    return newNode;
  }

  public void set(ObjectNode node) {
    Objects.requireNonNull(node, "ObjectNode must not be null! For clearing ref, use clear()");
    this.node = node;
    this.objectUri = node.getObjectUri();
    this.isLoaded = true;
    this.isPresent = true;
    refreshNodeState();
  }

  public void set(URI objectUri) {
    if (isLoaded) {
      throw new IllegalArgumentException(
          "Setting objectUri is not allowed when ObjectNode already set!");
    }
    this.objectUri = objectUri;
    this.isPresent = true;
  }

  public ObjectNode get() {
    if (isLoaded) {
      return node;
    }
    if (objectUri != null) {
      node = referrerNode.objectApi.load(objectUri);
      isLoaded = true;
      refreshNodeState();
    }
    return node;
  }

  public URI getObjectUri() {
    return objectUri;
  }

  public void clear() {
    objectUri = null;
    if (isLoaded) {
      node.setState(ObjectNodeState.REMOVED);
    }
    this.isPresent = false;
    // isLoaded stays true
  }

  public boolean isPresent() {
    return isPresent;
  }

  public boolean isLoaded() {
    return isLoaded;
  }

  public ObjectNodeState getState() {
    if (originalObjectUri == null && isPresent()) {
      return ObjectNodeState.NEW;
    }
    if (originalObjectUri != null && !isPresent()) {
      return ObjectNodeState.REMOVED;
    }
    if (Objects.equals(originalObjectUri, objectUri)) {
      // TODO do we need this??
      // if (node != null && node.getState() == ObjectNodeState.MODIFIED) {
      //   return ObjectNodeState.MODIFIED;
      // }
      return ObjectNodeState.NOP;
    }
    return ObjectNodeState.MODIFIED;
  }
}
