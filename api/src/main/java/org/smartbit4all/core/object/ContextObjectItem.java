package org.smartbit4all.core.object;

import static org.smartbit4all.core.utility.ObjectDefinitionUtils.isValue;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.api.object.bean.ContextObjectDataItem;

/**
 * The context object inner structure that contains the name, uri and the loaded object node if it
 * was already referred.
 * 
 * @author Peter Boros
 */
public class ContextObjectItem {

  ContextObjectDataItem data;

  ObjectNode loadedObjectNode;

  final WeakReference<ObjectApi> objectApiRef;

  private static final String DEFAULT_SCHEMA = "defaultSchema";

  ContextObjectItem(ObjectApi objectApi, ContextObjectDataItem item) {
    super();
    objectApiRef = new WeakReference<>(objectApi);
    this.data = item;
  }

  ContextObjectItem(ObjectApi objectApi, String name, URI uri) {
    super();
    objectApiRef = new WeakReference<>(objectApi);
    this.data = new ContextObjectDataItem().name(name).uri(uri);
  }

  ContextObjectItem(ObjectApi objectApi, String name, Object object) {
    super();
    objectApiRef = new WeakReference<>(objectApi);
    this.data = new ContextObjectDataItem().name(name)._object(object);
  }

  ContextObjectItem(ObjectApi objectApi, String name, ObjectNode node) {
    super();
    Objects.requireNonNull(node);
    objectApiRef = new WeakReference<>(objectApi);
    this.data =
        new ContextObjectDataItem().name(name).uri(node.getObjectUri());
    this.loadedObjectNode = node;
  }

  /**
   * Retrieves the object node of the context object if it was set earlier of there is an URI to
   * load.
   * 
   * @return The {@link ObjectNode} if it is available. Can be null in case of a Map based context
   *         object.
   */
  @SuppressWarnings("unchecked")
  public ObjectNode objectNode() {
    if (loadedObjectNode == null) {
      if (data.getUri() != null) {
        // Let's load the object identified by the uri.
        loadedObjectNode = objectApi().load(data.getUri());
      } else if (data.getObject() instanceof Map) {
        loadedObjectNode =
            objectApi().create(DEFAULT_SCHEMA, objectApi().definition(Map.class),
                (Map<String, Object>) data.getObject());
      } else if (data.getObject() != null) {
        loadedObjectNode = objectApi().create(DEFAULT_SCHEMA, data.getObject());
      }
    }
    return loadedObjectNode;
  }

  /**
   * Retrive the context value as is. Constructs the object from the object node if it is possible.
   * 
   * @return The value of the context object itself.
   */
  Object getValue() {
    if (isValue(data.getObject()) && data.getObject() != null) {
      return data.getObject();
    }
    ObjectNode objectNode = objectNode();
    if (objectNode != null) {
      return objectNode.getObject();
    }
    return null;
  }

  ObjectApi objectApi() {
    return objectApiRef.get();
  }

  String getName() {
    return data.getName();
  }

  URI getUri() {
    return data.getUri();
  }

}
