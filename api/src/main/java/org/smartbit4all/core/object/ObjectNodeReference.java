package org.smartbit4all.core.object;

import java.net.URI;
import java.util.Objects;
import org.smartbit4all.api.object.RetrievalRequest;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.object.bean.RetrievalMode;

public class ObjectNodeReference {

  private final URI originalObjectUri;

  private final ObjectNode originalNode;

  private final ObjectNode referrerNode;

  private URI objectUri;

  private boolean isLoaded;

  private boolean isPresent;

  private final ReferenceDefinition referenceDefinition;

  private URI resultUri;

  /**
   * Referred {@link ObjectNode}. If is null, then either reference is empty (if originalObjectUri
   * == null), or not loaded.
   */
  private ObjectNode node;

  ObjectNodeReference(ObjectNode referrerNode, ReferenceDefinition referenceDefinition,
      URI objectUri, ObjectNode node) {
    this.referrerNode = referrerNode;
    this.referenceDefinition = referenceDefinition;
    if (node != null && !Objects.equals(node.getObjectUri(), objectUri)) {
      URI versionless1 = referrerNode.objectApi.getLatestUri(node.getObjectUri());
      URI versionless2 = referrerNode.objectApi.getLatestUri(objectUri);
      if (!Objects.equals(versionless1, versionless2)) {
        throw new IllegalArgumentException("node.getObjectUri must match to objectUri");
      }
      // node is another version -> RetrievalApi loaded it, we should accept it!
      this.originalObjectUri = node.getObjectUri();
    } else {
      this.originalObjectUri = objectUri;
    }
    this.originalNode = node;
    this.objectUri = originalObjectUri;
    this.node = originalNode;
    this.isLoaded = this.node != null;
    this.isPresent = isLoaded || objectUri != null;
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
  }

  public void set(URI objectUri) {
    if (isLoaded) {
      throw new IllegalArgumentException(
          "Setting objectUri is not allowed when ObjectNode already set!");
    }
    this.objectUri = objectUri;
    this.isPresent = objectUri != null;
  }

  public ObjectNode get() {
    if (isLoaded) {
      return node;
    }
    if (objectUri != null) {
      RetrievalRequest request =
          referrerNode.objectApi.request(referenceDefinition.getTarget().getClazz());
      request.setLoadLatest(
          RetrievalRequest.calcLoadLatest(referenceDefinition, RetrievalMode.NORMAL));
      node = referrerNode.objectApi.load(request, objectUri, referrerNode.branchUri);
      isLoaded = true;
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

  public boolean isEmpty() {
    return !isPresent;
  }

  public boolean isLoaded() {
    return isLoaded;
  }

  public ObjectNodeState getState() {
    if (originalObjectUri == null && isPresent()) {
      return ObjectNodeState.NEW;
    }
    if (objectUri == null && isPresent()) {
      // new ObjectNode is present
      return ObjectNodeState.NEW;
    }
    if (originalObjectUri != null && !isPresent()) {
      return ObjectNodeState.REMOVED;
    }
    if (Objects.equals(originalObjectUri, objectUri)) {
      if (node != null && node.getState() == ObjectNodeState.MODIFIED) {
        return ObjectNodeState.MODIFIED;
      }
      return ObjectNodeState.NOP;
    }
    return ObjectNodeState.MODIFIED;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectNodeReference ref = (ObjectNodeReference) o;
    return Objects.equals(this.isLoaded, ref.isLoaded) &&
        Objects.equals(this.isPresent, ref.isPresent) &&
        Objects.equals(this.node, ref.node) &&
        Objects.equals(this.originalObjectUri, ref.originalObjectUri) &&
        Objects.equals(this.objectUri, ref.objectUri) &&
        Objects.equals(this.getState(), ref.getState()) &&
        Objects.equals(this.referrerNode.getObjectUri(), ref.referrerNode.getObjectUri());
    // originalNode is not examined, it may not exist, if loaded later, but ref should be equal.
    // adding referredNode will cause stackOverflow
  }

  @Override
  public int hashCode() {
    return Objects.hash(isLoaded, isPresent, node, originalObjectUri, objectUri,
        referrerNode.getObjectUri(), getState());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // @formatter:off
    sb.append("class ObjectNodeReference{\n");
    sb.append("    originalObjectUri: ").append(toIndentedString(originalObjectUri)).append("\n");
    sb.append("    objectUri: ").append(toIndentedString(objectUri)).append("\n");
    sb.append("    referrerNode.objectUri: ").append(toIndentedString(referrerNode.getObjectUri())).append("\n");
    sb.append("    node: ").append(toIndentedString(node)).append("\n");
    sb.append("    isLoaded: ").append(toIndentedString(isLoaded)).append("\n");
    sb.append("    isPresent: ").append(toIndentedString(isPresent)).append("\n");
    sb.append("    state: ").append(toIndentedString(getState())).append("\n");
    sb.append("}");
    // @formatter:on
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  public void setResult(URI resultUri) {
    this.resultUri = resultUri;
  }

  public URI getResultUri() {
    if (node != null) {
      return node.getResultUri();
    }
    return resultUri;
  }


}
