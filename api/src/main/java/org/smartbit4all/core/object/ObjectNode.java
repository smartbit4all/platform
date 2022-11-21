package org.smartbit4all.core.object;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;
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
  private final ObjectApi objectApi;

  private final Map<String, ObjectNodeReference> referenceNodes = new HashMap<>();
  private final Map<String, List<ObjectNode>> referenceLists = new HashMap<>();
  private final Map<String, Map<String, ObjectNode>> referenceMaps = new HashMap<>();

  ObjectNode(ObjectApi objectApi, ObjectNodeData data) {
    super();
    this.objectApi = objectApi;
    this.definition = objectApi.definition(data.getQualifiedName());
    this.data = data;
    initReferences();
  }

  ObjectNode(ObjectApi objectApi, String storageScheme, Object o) {
    super();
    this.objectApi = objectApi;
    this.definition = this.objectApi.definition(o.getClass());
    this.data = new ObjectNodeData()
        .objectUri(null)
        .qualifiedName(definition.getQualifiedName()) // TODO Alias?
        .storageSchema(storageScheme)
        .objectAsMap(definition.toMap(o))
        .state(ObjectNodeState.NEW)
        .versionNr(null);
    initReferences();
  }

  private void initReferences() {
    for (Entry<String, ObjectNodeData> entry : data.getReferenceValues().entrySet()) {
      ObjectNode node = new ObjectNode(objectApi, entry.getValue());
      referenceNodes.put(entry.getKey(), new ObjectNodeReference(node));
    }
    for (Entry<String, List<ObjectNodeData>> entry : data.getReferenceListValues().entrySet()) {
      referenceLists.put(entry.getKey(), new ObjectNodeList(objectApi, entry.getValue()));
    }
    for (Entry<String, Map<String, ObjectNodeData>> entry : data.getReferenceMapValues()
        .entrySet()) {
      referenceMaps.put(entry.getKey(), new ObjectNodeMap(objectApi, entry.getValue()));
    }
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
   * @param objectDefinition The object definition of the required object.
   * @return A copy from the current data.
   */
  public final <T> T getObject(ObjectDefinition<T> objectDefinition) {
    return objectDefinition.fromMap(getObjectAsMap());
  }

  public final Map<String, ObjectNodeReference> getReferenceNodes() {
    return referenceNodes;
  }

  public final Map<String, List<ObjectNode>> getReferenceLists() {
    return referenceLists;
  }

  public final Map<String, Map<String, ObjectNode>> getReferenceMaps() {
    return referenceMaps;
  }

  /**
   * A recursive function to collect all nodes inclusively itself into a {@link Stream} for further
   * processing.
   * 
   * @return The nodes {@link Stream}.
   */
  public Stream<ObjectNode> allNodes() {
    return Stream.of(
        Stream.of(this),
        getReferenceNodes().values().stream().map(ObjectNodeReference::get)
            .flatMap(ObjectNode::allNodes),
        getReferenceLists().values().stream().flatMap(List::stream)
            .flatMap(ObjectNode::allNodes),
        getReferenceMaps().values().stream()
            .flatMap(n -> n.values().stream())
            .flatMap(ObjectNode::allNodes))
        .flatMap(s -> s);
  }

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

  /**
   * Set the referred object node to deletion if it exists. If it doesn't exist then nothing
   * happens.
   * 
   * @param reference
   */
  public void clearReference(String reference) {
    ObjectNodeReference ref = referenceNodes.get(reference);
    if (ref != null && ref.isPresent()) {
      ref.clear();
    }
  }

  /**
   * This retrieves the currently available nodes belong to the given reference.
   * 
   * @param reference The reference.
   * @return Returns a list that contains all the currently available nodes. It can be modified in
   *         the following ways: Adding a new Node will create a new node in the list. We can use
   *         the node directly. Remove a node from the list will set the {@link ObjectNode#data} to
   *         {@link ObjectNodeState#REMOVED} state and the given node will disappear from the list.
   *         In any other case we can use the {@link ObjectNode} directly to modify the object if
   *         necessary.
   */
  public List<ObjectNode> referenceNodeList(String reference) {
    return referenceLists.computeIfAbsent(reference,
        r -> new ObjectNodeList(objectApi, null));
  }

  public ObjectNode getRef(String... paths) {
    return getValueAs(ObjectNode.class, paths);
  }

  public void setRef(String path, Object o) {
    ObjectNodeReference ref = referenceNodes.get(path);
    if (ref == null) {
      ObjectNode node = objectApi.node(getStorageScheme(), o);
      ref = new ObjectNodeReference(node);
    }
  }

  @SuppressWarnings("unchecked")
  public List<ObjectNode> getList(String... paths) {
    List<ObjectNode> result = getValueAs(List.class, paths);
    // if (result == null) {
    // result = new ArrayList<>();
    // }
    return result;
  }

  @SuppressWarnings("unchecked")
  public Map<String, ObjectNode> getMap(String... paths) {
    return getValueAs(Map.class, paths);
  }

  public <T> T getValue(Class<T> clazz, String... paths) {
    Object value = getValue(paths);
    if (value instanceof ObjectNode) {
      return ((ObjectNode) value).getObject(objectApi.definition(clazz));
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
      ObjectNodeReference nodeOnPath = referenceNodes.get(path);
      if (nodeOnPath != null && nodeOnPath.isPresent()) {
        String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
        return nodeOnPath.get().getValue(subPaths);
      }
      List<ObjectNode> listOnPath = referenceLists.get(path);
      if (listOnPath != null) {
        if (paths.length == 1) {
          return listOnPath;
        }
        String idxString = paths[1];
        try {
          Integer idx = Integer.valueOf(idxString);
          String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
          return listOnPath.get(idx).getValue(subPaths);
        } catch (NumberFormatException ex1) {
          throw new IllegalArgumentException("List index is not a number: "
              + path + "(" + idxString + ")");
        } catch (IndexOutOfBoundsException ex2) {
          throw new IllegalArgumentException("List item not found by index: "
              + path + "(" + idxString + ")");
        }
      }
      Map<String, ObjectNode> mapOnPath = referenceMaps.get(path);
      if (mapOnPath != null) {
        if (paths.length == 1) {
          return mapOnPath;
        }
        String key = paths[1];
        if (mapOnPath.containsKey(key)) {
          String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
          return mapOnPath.get(key).getValue(subPaths);
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
      if (value != null && value instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> subMap = (Map<String, Object>) value;
        String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
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
