package org.smartbit4all.core.object;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;

public final class ObjectNodeList {

  private final List<ObjectNodeReference> list;

  private final ObjectNode referrerNode;
  private final ReferenceDefinition referenceDefinition;

  ObjectNodeList(ObjectApi objectApi, ObjectNode referrerNode,
      ReferenceDefinition referenceDefinition,
      List<URI> originalUris, List<ObjectNodeData> originalList) {
    super();
    Objects.requireNonNull(referrerNode, "ReferrerNode must not be null!");
    Objects.requireNonNull(originalUris, "OriginalUris must not be null!");

    this.referrerNode = referrerNode;
    this.referenceDefinition = referenceDefinition;
    if (originalList != null) {
      // loaded
      if (originalList.size() != originalUris.size()) {
        throw new IllegalArgumentException("originalList and originalUris size doesn't match!");
      }
      list = new ArrayList<>(originalList.size());
      Iterator<URI> uris = originalUris.iterator();
      for (ObjectNodeData data : originalList) {
        URI uri = uris.next();
        list.add(new ObjectNodeReference(referrerNode, referenceDefinition, uri,
            new ObjectNode(objectApi, data)));
      }
    } else {
      // not loaded
      list = originalUris.stream()
          .map(uri -> new ObjectNodeReference(referrerNode, referenceDefinition, uri, null))
          .collect(toList());
    }
  }

  public int size() {
    return list.size();
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public Iterable<ObjectNodeReference> references() {
    return list::iterator;
  }

  public Stream<ObjectNodeReference> stream() {
    return StreamSupport.stream(list.spliterator(), false);
  }

  public Stream<ObjectNode> nodeStream() {
    return stream().map(ObjectNodeReference::get);
  }

  public <T> Stream<T> stream(Class<T> clazz) {
    return nodeStream().map(node -> node.getObject(clazz));
  }

  public ObjectNodeReference get(int index) {
    return list.get(index);
  }

  public ObjectNode addNewObject(Object object) {
    ObjectNode newNode = referrerNode.objectApi.create(referrerNode.getStorageScheme(), object);
    newNode.setState(ObjectNodeState.NEW);
    add(newNode);
    return newNode;
  }

  public List<ObjectNode> addAllNewObjects(Collection<Object> objects) {
    if (objects == null) {
      return Collections.emptyList();
    }
    List<ObjectNode> result = new ArrayList<>();
    for (Object node : objects) {
      result.add(addNewObject(node));
    }
    return result;
  }

  public boolean add(ObjectNode node) {
    Objects.requireNonNull(node, "Node must be not null!");
    ObjectNodeReference ref =
        new ObjectNodeReference(referrerNode, referenceDefinition, null, null);
    ref.set(node);
    return list.add(ref);
  }

  public boolean addAllNodes(Collection<ObjectNode> nodes) {
    if (nodes == null) {
      return false;
    }
    boolean result = false;
    for (ObjectNode node : nodes) {
      result = result || add(node);
    }
    return result;
  }

  public boolean add(URI uri) {
    Objects.requireNonNull(uri, "URI must be not null!");
    ObjectNodeReference ref =
        new ObjectNodeReference(referrerNode, referenceDefinition, null, null);
    ref.set(uri);
    return list.add(ref);
  }

  public boolean addAll(Collection<URI> uris) {
    if (uris == null || uris.isEmpty()) {
      return false;
    }
    for (URI uri : uris) {
      add(uri);
    }
    return true;
  }

  public ObjectNode set(int idx, ObjectNode node) {
    ObjectNodeReference ref = list.get(idx);
    ObjectNode result = ref.get();
    ref.set(node);
    return result;
  }

  public URI set(int idx, URI uri) {
    ObjectNodeReference ref = list.get(idx);
    URI result = ref.getObjectUri();
    ref.set(uri);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectNodeList otherList = (ObjectNodeList) o;
    return Objects.equals(this.list, otherList.list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(list);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // @formatter:off
    sb.append("class ObjectNodeList{\n");
    sb.append("    list: ").append(toIndentedString(list)).append("\n");
    sb.append("}");
    // @formatter:on
    return sb.toString();
  }

  /**
   * Clears all references. List size won't change!
   */
  public void clear() {
    stream().forEach(ref -> ref.clear());
  }

  /**
   * Removes the references matching the given filter
   * 
   * @param filter a predicate which returns {@code true} for elements to be removed
   * @return {@code true} if any elements were removed
   */
  public boolean removeIf(Predicate<ObjectNodeReference> filter) {
    return stream()
        .filter(filter)
        .map(ref -> {
          ref.clear();
          return ref;
        })
        .count() > 0;
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
}
