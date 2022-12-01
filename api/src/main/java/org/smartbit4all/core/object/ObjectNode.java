package org.smartbit4all.core.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import com.google.common.base.Strings;

/**
 * The object node contains an object returned by the <code>RetrievalApi</code>. It can manage the
 * state of the object and we can use it to produce <code>ApplyChangeRequest</code> at the end of
 * the modification.
 * 
 * @author Peter Boros
 *
 */
public class ObjectNode {

  /**
   * This bean contains all serializable information about this ObjectNode.
   */
  private final ObjectNodeData data;

  /**
   * The object definition to access the meta of the given object.
   */
  private final ObjectDefinition<?> definition;

  /**
   * ObjectApi is used for creating other ObjectNodes
   */
  final ObjectApi objectApi;

  private final Map<String, ObjectNodeReference> references;
  private final Map<String, ObjectNodeList> referenceLists;
  private final Map<String, ObjectNodeMap> referenceMaps;

  ObjectNode(ObjectApi objectApi, ObjectNodeData data) {
    this(objectApi, objectApi.definition(data.getQualifiedName()), data);
  }

  ObjectNode(ObjectApi objectApi, ObjectDefinition<?> definition, ObjectNodeData data) {
    super();
    this.objectApi = objectApi;
    this.definition = definition;
    this.data = data;
    references = initReferenceNodes();
    referenceLists = initReferenceLists();
    referenceMaps = initReferenceMaps();
  }

  private Map<String, ObjectNodeReference> initReferenceNodes() {
    return definition.getOutgoingReferences().entrySet().stream()
        .filter(e -> e.getValue().getReferencePropertyKind() == ReferencePropertyKind.REFERENCE)
        .collect(Collectors.toMap(
            Entry::getKey,
            e -> {
              ObjectNode node = null;
              if (data.getReferences().containsKey(e.getKey())) {
                node = new ObjectNode(objectApi, data.getReferences().get(e.getKey()));
              }
              Object uri = data.getObjectAsMap().get(e.getValue().getSourcePropertyPath());
              return new ObjectNodeReference(this, UriUtils.asUri(uri), node);
            }));
  }

  private Map<String, ObjectNodeList> initReferenceLists() {
    return definition.getOutgoingReferences().entrySet().stream()
        .filter(e -> e.getValue().getReferencePropertyKind() == ReferencePropertyKind.LIST)
        .collect(Collectors.toMap(
            Entry::getKey,
            e -> {
              List<ObjectNodeData> list = null;
              if (data.getReferenceLists().containsKey(e.getKey())) {
                list = data.getReferenceLists().get(e.getKey());
              }
              List<?> uris =
                  (List<?>) data.getObjectAsMap().get(e.getValue().getSourcePropertyPath());
              if (uris == null) {
                uris = new ArrayList<>();
              }
              return new ObjectNodeList(objectApi, this,
                  UriUtils.asUriList(uris), list);
            }));
  }

  private Map<String, ObjectNodeMap> initReferenceMaps() {
    return definition.getOutgoingReferences().entrySet().stream()
        .filter(e -> e.getValue().getReferencePropertyKind() == ReferencePropertyKind.MAP)
        .collect(Collectors.toMap(
            Entry::getKey,
            e -> {
              Map<String, ObjectNodeData> map = null;
              if (data.getReferenceMaps().containsKey(e.getKey())) {
                map = data.getReferenceMaps().get(e.getKey());
              }
              Map<?, ?> uris =
                  (Map<?, ?>) data.getObjectAsMap().get(e.getValue().getSourcePropertyPath());
              if (uris == null) {
                uris = new HashMap<>();
              }
              return new ObjectNodeMap(objectApi, this,
                  UriUtils.asUriMap(uris), map);
            }));
  }

  ObjectNodeData getData() {
    return data;
  }

  public final URI getObjectUri() {
    return data.getObjectUri();
  }

  /**
   * @return The {@link ObjectDefinition} for the given object.
   */
  public final ObjectDefinition<?> getDefinition() {
    return definition;
  }

  /**
   * Return the object as map.
   * 
   * @return
   */
  public final Map<String, Object> getObjectAsMap() {
    return data.getObjectAsMap();
  }

  public final String getStorageScheme() {
    return data.getStorageSchema();
  }

  /**
   * @return A copy from the current data.
   */
  public final Object getObject() {
    return definition.fromMap(getObjectAsMap());
  }

  /**
   * Returns the stored data as an object of class T. It is a copy, deserialized from map by
   * ObjectDefinition<T>.
   * 
   * @param clazz The class of the required object.
   * @return A copy from the current data.
   */
  public final <T> T getObject(Class<T> clazz) {
    return objectApi.definition(clazz).fromMap(getObjectAsMap());
  }

  public final Map<String, ObjectNodeReference> getReferences() {
    return references;
  }

  public final Map<String, ObjectNodeList> getReferenceLists() {
    return referenceLists;
  }

  public final Map<String, ObjectNodeMap> getReferenceMaps() {
    return referenceMaps;
  }

  // TODO implement
  // /**
  // * A recursive function to collect all nodes inclusively itself into a {@link Stream} for
  // further
  // * processing.
  // *
  // * @return The nodes {@link Stream}.
  // */
  // public Stream<ObjectNode> allNodes() {
  // return Stream.of(
  // Stream.of(this),
  // getReferenceNodes().values().stream().map(ObjectNodeReference::get)
  // .flatMap(ObjectNode::allNodes),
  // getReferenceLists().values().stream().flatMap(List::stream).map(ObjectNodeReference::get)
  // .flatMap(ObjectNode::allNodes),
  // getReferenceMaps().values().stream()
  // .flatMap(n -> n.values().stream())
  // .map(ObjectNodeReference::get)
  // .flatMap(ObjectNode::allNodes))
  // .flatMap(s -> s);
  // }

  /**
   * Update the given {@link ObjectNode} as java object. It retrieves the current value of the bean
   * and can return another bean or the same with modified values. We don't have to care about
   * setting the proper URIs for the references because they will be managed by the
   * <code>RetrievalApi</code> and the <code>ApplyChangeApi</code>.
   * 
   * @param <T> The type class of the bean. If we already have the object then it will be passed to
   *        the updateFunction. Else the function read the given object from the data.
   * @param object The object that contains the values to set. This object is going to be serialized
   *        and the the values are set into the data that is the single point of truth. If we pass
   *        null then nothing will happen with the values. The null is assumed as an empty map to
   *        set.
   */
  public <T> void setObject(T object) {
    if (object == null) {
      return;
    }
    // Now we can accept only the object itself.
    setValues(definition.toMap(object));
  }

  /**
   * Set the values directly into the data.
   * 
   * @param values The map of values.
   */
  public void setValues(Map<String, Object> values) {
    data.getObjectAsMap().putAll(values);
    setModified();
  }

  /**
   * Set the value directly into the data.
   * 
   * @param key The key of the value, the name of the property.
   * @param value The value object.
   */
  public void setValue(String key, Object value) {
    data.getObjectAsMap().put(key, value);
    setModified();
  }

  /**
   * @return The current state of the {@link ObjectNode}.
   * @see ObjectNodeState
   */
  public final ObjectNodeState getState() {
    return data.getState();
  }

  /**
   * Modify the state of the node. It's not public so it can be called inside the
   * <code>RetrievalApi</code> and the <code>ApplyChangeApi</code> implementations.
   * 
   * @param state
   */
  final void setState(ObjectNodeState state) {
    this.data.setState(state);
  }

  /**
   * Set the state to modified if the state is {@link ObjectNodeState#NOP} else the state remains
   * the same.
   */
  final void setModified() {
    if (getState() == ObjectNodeState.NOP) {
      data.setState(ObjectNodeState.MODIFIED);
    }
  }

  public ObjectNodeReference ref(String... paths) {
    ObjectNodeReference result = getValueAs(ObjectNodeReference.class, paths);
    if (result == null) {
      throw new IllegalArgumentException("The "
          + Stream.of(paths).collect(Collectors.joining(StringConstant.SLASH))
          + " path is not a valid reference in the " + definition.getQualifiedName() + " object.");
    }
    return result;
  }

  public ObjectNodeList list(String... paths) {
    ObjectNodeList result = getValueAs(ObjectNodeList.class, paths);
    if (result == null) {
      throw new IllegalArgumentException("The "
          + Stream.of(paths).collect(Collectors.joining(StringConstant.SLASH))
          + " path is not a valid reference in the " + definition.getQualifiedName() + " object.");
    }
    return result;
  }

  public ObjectNodeMap map(String... paths) {
    ObjectNodeMap result = getValueAs(ObjectNodeMap.class, paths);
    if (result == null) {
      throw new IllegalArgumentException("The "
          + Stream.of(paths).collect(Collectors.joining(StringConstant.SLASH))
          + " path is not a valid reference in the " + definition.getQualifiedName() + " object.");
    }
    return result;
  }

  public <T> T getValue(Class<T> clazz, String... paths) {
    Object value = getValue(paths);
    if (value instanceof ObjectNodeReference
        && ((ObjectNodeReference) value).get() instanceof ObjectNode) {
      ObjectNodeReference ref = (ObjectNodeReference) value;
      return ((ObjectNodeReference) value).get().getObject(clazz);
    }
    // TODO string, uri, uuid, number, boolean, etc.? Objectmapper?
    return (T) value;
  }

  @SuppressWarnings("unchecked")
  private <T> T getValueAs(Class<T> clazz, String... paths) {
    Object value = getValue(paths);
    if (value == null) {
      // TODO is it right, possible??
      return null;
    }
    if (clazz.isAssignableFrom(value.getClass())) {
      return (T) value;
    }
    String path = paths == null ? "empty path" : String.join(",", paths);
    throw new IllegalArgumentException(
        "Value on path is not " + clazz.getSimpleName() + "!" + path);

  }

  public Object getValue(String... paths) {
    if (paths != null && paths.length > 0) {
      String path = paths[0];
      if (Strings.isNullOrEmpty(path)) {
        throw new IllegalArgumentException("Path part cannot be null or empty");
      }
      ObjectNodeReference refNode = references.get(path);
      if (refNode != null) {
        if (paths.length == 1) {
          return refNode;
        }
        String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
        return refNode.get().getValue(subPaths);
      }
      ObjectNodeList refList = referenceLists.get(path);
      if (refList != null) {
        if (paths.length == 1) {
          return refList;
        }
        String idxString = paths[1];
        try {
          Integer idx = Integer.valueOf(idxString);
          String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
          return refList.get(idx).get().getValue(subPaths);
        } catch (NumberFormatException ex1) {
          throw new IllegalArgumentException("List index is not a number: "
              + path + "(" + idxString + ")");
        } catch (IndexOutOfBoundsException ex2) {
          throw new IllegalArgumentException("List item not found by index: "
              + path + "(" + idxString + ")");
        }
      }
      ObjectNodeMap refMap = referenceMaps.get(path);
      if (refMap != null) {
        if (paths.length == 1) {
          return refMap;
        }
        String key = paths[1];
        if (refMap.containsKey(key)) {
          String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
          return refMap.get(key).get().getValue(subPaths);
        }
        throw new IllegalArgumentException(
            "Map item not found by index: " + path + "(" + key + ")");
      }
      return getValueFromObjectMap(getObjectAsMap(), paths);
    }
    return this;
  }

  private Object getValueFromObjectMap(Map<String, Object> map, String... paths) {
    if (paths != null && paths.length > 0) {
      String path = paths[0];
      Object value = map.get(path);
      if (paths.length == 1) {
        return value;
      }
      if (value instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> subMap = (Map<String, Object>) value;
        String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
        return getValueFromObjectMap(subMap, subPaths);
      }
      return null;
    }
    return map;
  }

  public Object getVersionNr() {
    return data.getVersionNr();
  }

}
