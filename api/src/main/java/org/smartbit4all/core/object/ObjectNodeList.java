package org.smartbit4all.core.object;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;

public final class ObjectNodeList {

  private final List<ObjectNodeReference> list;

  private final ObjectNode referrerNode;

  ObjectNodeList(ObjectApi objectApi, ObjectNode referrerNode, List<URI> originalUris,
      List<ObjectNodeData> originalList) {
    super();
    Objects.requireNonNull(referrerNode, "ReferrerNode must not be null!");
    Objects.requireNonNull(originalUris, "OriginalUris must not be null!");

    this.referrerNode = referrerNode;
    if (originalList != null) {
      // loaded
      if (originalList.size() != originalUris.size()) {
        throw new IllegalArgumentException("originalList and originalUris size doesn't match!");
      }
      list = new ArrayList<>(originalList.size());
      Iterator<URI> uris = originalUris.iterator();
      for (ObjectNodeData data : originalList) {
        URI uri = uris.next();
        list.add(new ObjectNodeReference(referrerNode, uri, objectApi.node(data)));
      }
    } else {
      // not loaded
      list = originalUris.stream()
          .map(uri -> new ObjectNodeReference(referrerNode, uri, null))
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

  public ObjectNodeReference get(int index) {
    return list.get(index);
  }

  public ObjectNode addNewObject(Object object) {
    ObjectNode newNode = referrerNode.objectApi.node(referrerNode.getStorageScheme(), object);
    newNode.setState(ObjectNodeState.NEW);
    add(newNode);
    return newNode;
  }

  public boolean add(ObjectNode node) {
    Objects.requireNonNull(node, "Node must be not null!");
    ObjectNodeReference ref = new ObjectNodeReference(referrerNode, null, null);
    ref.set(node);
    return list.add(ref);
  }

  public boolean add(URI uri) {
    Objects.requireNonNull(uri, "Node must be not null!");
    ObjectNodeReference ref = new ObjectNodeReference(referrerNode, null, null);
    ref.set(uri);
    return list.add(ref);
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

}
