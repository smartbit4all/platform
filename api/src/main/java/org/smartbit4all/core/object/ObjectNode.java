package org.smartbit4all.core.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.object.bean.SnapshotData;
import org.smartbit4all.api.object.bean.SnapshotDataRef;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.core.utility.UriUtils;
import com.google.common.base.Strings;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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

  ObjectNode(ObjectApi objectApi, SnapshotData snapshot) {
    super();
    this.objectApi = objectApi;
    this.definition = objectApi.definition(snapshot.getQualifiedName());
    ObjectNodeData nodeData = fromSnapshot(snapshot);
    this.data = nodeData;
    references = initReferenceNodes();
    referenceLists = initReferenceLists();
    referenceMaps = initReferenceMaps();
    // TODO postProcess partially loaded ObjectNodeLists/Maps
    postProcessFromSnapshot(this, snapshot);
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
              ReferenceDefinition ref = e.getValue();
              Object uri = data.getObjectAsMap().get(ref.getSourcePropertyPath());
              return new ObjectNodeReference(this, ref, UriUtils.asUri(uri), node);
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
              ReferenceDefinition ref = e.getValue();
              List<?> uris = (List<?>) data.getObjectAsMap().get(ref.getSourcePropertyPath());
              if (uris == null) {
                uris = new ArrayList<>();
              }
              return new ObjectNodeList(objectApi, this, ref,
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
              ReferenceDefinition ref = e.getValue();
              Map<?, ?> uris = (Map<?, ?>) data.getObjectAsMap().get(ref.getSourcePropertyPath());
              if (uris == null) {
                uris = new HashMap<>();
              }
              return new ObjectNodeMap(objectApi, this, ref,
                  UriUtils.asUriMap(uris), map);
            }));
  }

  public ObjectNodeData getData() {
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
   * A safe way to update the value of the object node without the risk of forgotten
   * {@link #setObject(Object)}.
   *
   * @param <T>
   * @param clazz
   * @param update
   */
  public <T> ObjectNode modify(Class<T> clazz, UnaryOperator<T> update) {
    T object = getObject(clazz);
    object = update.apply(object);
    setObject(object);
    return this;
  }

  /**
   * Set the values directly into the data.
   *
   * @param values The map of values.
   */
  public ObjectNode setValues(Map<String, Object> values) {
    data.getObjectAsMap().putAll(values);
    setModified();
    return this;
  }

  // /**
  // * Set the value directly into the data.
  // *
  // * @param key The key of the value, the name of the property.
  // * @param value The value object.
  // */
  // public ObjectNode setValue(String key, Object value) {
  // data.getObjectAsMap().put(key, value);
  // setModified();
  // return this;
  // }

  /**
   * Sets the value in the data map, specified by path. Path cannot contain references!
   *
   * @param value
   * @param paths
   * @return
   */
  @SuppressWarnings("unchecked")
  public ObjectNode setValue(Object value, String... paths) {
    if (paths == null || paths.length == 0) {
      throw new IllegalArgumentException("Path cannot be null or empty!");
    }
    if (paths.length == 1) {
      setValueInMap(data.getObjectAsMap(), value, paths);
      setModified();
      return this;
    }
    // path minus the last part
    String[] targetPath = Arrays.copyOfRange(paths, 0, paths.length - 1);
    Object targetObject = getValue(targetPath);
    // only the last part
    String valuePath = paths[paths.length - 1];
    if (targetObject instanceof ObjectNode) {
      ((ObjectNode) targetObject).setValue(value, valuePath);
    } else if (targetObject instanceof Map<?, ?>) {
      if (value instanceof Map<?, ?>) {
        // If we set values to a map then it is a put all not a replacement of the whole map. In
        // this case we ignore the rest of the value path.
        ((Map<String, Object>) targetObject).putAll((Map<String, Object>) value);
      } else {
        setValueInMap((Map<String, Object>) targetObject, value, valuePath);
      }
    } else if (targetObject instanceof ObjectNodeReference) {
      ((ObjectNodeReference) targetObject).get().setValue(value, valuePath);
    } else if (targetObject instanceof ObjectNodeList ||
        targetObject instanceof ObjectNodeMap) {
      // valuePath points to an entry in ObjectNodeList/Map -> ObjectNodeReference
      ObjectNodeReference reference;
      if (targetObject instanceof ObjectNodeList) {
        reference = getItemFromList((ObjectNodeList) targetObject, valuePath);
      } else {
        reference = getItemFromMap((ObjectNodeMap) targetObject, valuePath);
      }
      if (value instanceof ObjectNode) {
        reference.set((ObjectNode) value);
      } else {
        reference.setNewObject(value);
      }
    } else {
      throw new IllegalArgumentException("Invalid object on path, unable to set: " +
          String.join(".", paths));
    }
    setModified();
    return this;
  }

  private void setValueInMap(Map<String, Object> map, Object value, String... paths) {
    String path = paths[0];
    if (Strings.isNullOrEmpty(path)) {
      throw new IllegalArgumentException("Path part cannot be null or empty");
    }
    if (paths.length == 1) {
      // If we set a map then try to add the values and not override.
      if (value instanceof Map) {
        Object targetMap = map.get(path);
        if (targetMap instanceof Map) {
          ((Map<String, Object>) targetMap).putAll((Map<String, Object>) value);
        } else {
          map.put(path, value);
        }
      } else {
        map.put(path, value);
      }
    } else {
      Map<String, Object> subMap = (Map<String, Object>) map.get(path);
      String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
      setValueInMap(subMap, value, subPaths);
    }
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
  final ObjectNode setState(ObjectNodeState state) {
    this.data.setState(state);
    return this;
  }

  /**
   * Set the state to modified if the state is {@link ObjectNodeState#NOP} else the state remains
   * the same.
   */
  final ObjectNode setModified() {
    if (getState() == ObjectNodeState.NOP) {
      data.setState(ObjectNodeState.MODIFIED);
    }
    return this;
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

  public String getValueAsString(String... paths) {
    return getValue(String.class, paths);
  }

  public <T> T getValue(Class<T> clazz, String... paths) {
    Object value = getValue(paths);
    try {
      return objectApi.asType(clazz, value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Unable to load the " + definition.getQualifiedName()
          + " " + Arrays.toString(paths) + " field as " + clazz + "(" + value + ")", e);
    }
  }

  public <E> List<E> getValueAsList(Class<E> clazz, String... paths) {
    Object value = getValue(paths);
    if (value instanceof List) {
      return objectApi.asList(clazz, (List<?>) value);
    }
    if (value instanceof ObjectNodeList) {
      return ((ObjectNodeList) value).stream(clazz)
          .collect(toList());
    }
    if (value == null) {
      return Collections.emptyList();
    }
    throw new ClassCastException("Value is not a List on path");
  }

  @SuppressWarnings("unchecked")
  public <V> Map<String, V> getValueAsMap(Class<V> clazz, String... paths) {
    Object value = getValue(paths);
    if (value instanceof Map) {
      return objectApi.asMap(clazz, (Map<String, ?>) value);
    }
    if (value instanceof ObjectNodeMap) {
      return ((ObjectNodeMap) value).entrySet().stream()
          .collect(toMap(
              Entry::getKey,
              e -> e.getValue().get().getObject(clazz)));
    }
    if (value == null) {
      return Collections.emptyMap();
    }
    throw new ClassCastException("Value is not a Map on path");
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
        if (!refNode.isPresent()) {
          return null;
        }
        String[] subPaths = Arrays.copyOfRange(paths, 1, paths.length);
        return refNode.get().getValue(subPaths);
      }
      ObjectNodeList refList = referenceLists.get(path);
      if (refList != null) {
        if (paths.length == 1) {
          return refList;
        }
        ObjectNodeReference reference = getItemFromList(refList, paths[1]);
        String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
        return reference.get().getValue(subPaths);
      }
      ObjectNodeMap refMap = referenceMaps.get(path);
      if (refMap != null) {
        if (paths.length == 1) {
          return refMap;
        }
        String key = paths[1];
        ObjectNodeReference reference = getItemFromMap(refMap, key);
        String[] subPaths = Arrays.copyOfRange(paths, 2, paths.length);
        return reference.get().getValue(subPaths);
      }
      return objectApi.getValueFromObjectMap(getObjectAsMap(), paths);
    }
    return this;
  }

  private ObjectNodeReference getItemFromList(ObjectNodeList refList, String idxString) {
    try {
      Integer idx = Integer.valueOf(idxString);
      return refList.get(idx);
    } catch (NumberFormatException ex1) {
      throw new IllegalArgumentException("List index is not a number: "
          + "(" + idxString + ")");
    } catch (IndexOutOfBoundsException ex2) {
      throw new IllegalArgumentException("List item not found by index: "
          + "(" + idxString + ")");
    }
  }

  private ObjectNodeReference getItemFromMap(ObjectNodeMap refMap, String key) {
    if (refMap.containsKey(key)) {
      return refMap.get(key);
    }
    throw new IllegalArgumentException(
        "Map item not found by index: " + "(" + key + ")");
  }

  public Object getVersionNr() {
    return data.getVersionNr();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ObjectNode objectNode = (ObjectNode) o;
    return Objects.equals(this.data.getObjectUri(), objectNode.data.getObjectUri()) &&
        Objects.equals(this.data.getObjectAsMap(), objectNode.data.getObjectAsMap()) &&
        Objects.equals(this.definition.getQualifiedName(), objectNode.definition.getQualifiedName())
        &&
        Objects.equals(this.references, objectNode.references) &&
        Objects.equals(this.referenceLists, objectNode.referenceLists) &&
        Objects.equals(this.referenceMaps, objectNode.referenceMaps);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data.getObjectUri(), data.getObjectAsMap(), definition.getQualifiedName(),
        references, referenceLists, referenceMaps);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // @formatter:off
    sb.append("class ObjectNode{\n");
    sb.append("    data.objectUri: ").append(toIndentedString(data.getObjectUri())).append("\n");
    sb.append("    data.objectAsMap: ").append(toIndentedString(data.getObjectAsMap())).append("\n");
    sb.append("    def.qualifiedName: ").append(toIndentedString(definition.getQualifiedName())).append("\n");
    sb.append("    references: ").append(toIndentedString(references)).append("\n");
    sb.append("    referenceLists: ").append(toIndentedString(referenceLists)).append("\n");
    sb.append("    referenceMaps: ").append(toIndentedString(referenceMaps)).append("\n");
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

  // TODO make it package private after moving to API
  public void setResult(URI resultUri) {
    data.setResultUri(resultUri);
  }

  public URI getResultUri() {
    return data.getResultUri();
  }

  /**
   * Creates a snapshot from the current state of this ObjectNode. Only use it when there isn't any
   * change, since this cannot be is a snapshot.
   *
   * @return
   */
  public SnapshotData snapshot() {
    return snapshotNode(this);
  }

  private SnapshotData snapshotNode(ObjectNode node) {
    if (node.getState() != ObjectNodeState.NOP) {
      throw new IllegalStateException(
          "Node is not in NOP state, creating snapshot is unavailable!");
    }
    return new SnapshotData()
        .objectUri(node.data.getObjectUri())
        .qualifiedName(node.data.getQualifiedName())
        .storageSchema(node.data.getStorageSchema())
        .objectAsMap(node.data.getObjectAsMap())
        .versionNr(node.data.getVersionNr())
        .qualifiedName(node.data.getQualifiedName())
        .references(snapshotRefs(node.getReferences()))
        .referenceLists(snapshotRefLists(node.getReferenceLists()))
        .referenceMaps(snapshotRefMaps(node.getReferenceMaps()));
  }

  private Map<String, SnapshotDataRef> snapshotRefs(Map<String, ObjectNodeReference> refs) {
    return refs.entrySet().stream()
        .collect(toMap(
            Entry::getKey,
            ref -> snapshotRef(ref.getValue())));
  }

  private Map<String, List<SnapshotDataRef>> snapshotRefLists(Map<String, ObjectNodeList> lists) {
    return lists.entrySet().stream()
        .collect(toMap(
            Entry::getKey,
            ref -> snapshotList(ref.getValue())));
  }

  private Map<String, Map<String, SnapshotDataRef>> snapshotRefMaps(
      Map<String, ObjectNodeMap> maps) {
    return maps.entrySet().stream()
        .collect(toMap(
            Entry::getKey,
            map -> snapshotMap(map.getValue())));
  }

  private SnapshotDataRef snapshotRef(ObjectNodeReference ref) {
    SnapshotDataRef result = new SnapshotDataRef()
        .objectUri(ref.getObjectUri())
        .isLoaded(ref.isLoaded());
    if (Boolean.TRUE.equals(ref.isLoaded())) {
      result.setData(snapshotNode(ref.get()));
    }
    return result;
  }

  private List<SnapshotDataRef> snapshotList(ObjectNodeList list) {
    return list.stream()
        .map(this::snapshotRef)
        .collect(toList());
  }

  private Map<String, SnapshotDataRef> snapshotMap(ObjectNodeMap map) {
    return map.entrySet().stream()
        .collect(toMap(
            Entry::getKey,
            ref -> snapshotRef(ref.getValue())));
  }

  private ObjectNodeData fromSnapshot(SnapshotData snapshot) {
    return new ObjectNodeData()
        .objectUri(snapshot.getObjectUri())
        .qualifiedName(snapshot.getQualifiedName())
        .storageSchema(snapshot.getStorageSchema())
        .objectAsMap(convertObjectMap(snapshot))
        .versionNr(snapshot.getVersionNr())
        .qualifiedName(snapshot.getQualifiedName())
        .references(fromSnapshotRef(snapshot.getReferences()))
        .referenceLists(fromSnapshotRefList(snapshot.getReferenceLists()))
        .referenceMaps(fromSnapshotRefMap(snapshot.getReferenceMaps()));
  }

  private Map<String, Object> convertObjectMap(SnapshotData snapshot) {
    Map<String, Object> objectAsMap = snapshot.getObjectAsMap();
    Object objUri = objectAsMap.get("uri");
    if (objUri instanceof String) {
      objUri = URI.create((String) objUri);
      objectAsMap.put("uri", objUri);
    }
    return objectAsMap;
  }

  private Map<String, ObjectNodeData> fromSnapshotRef(Map<String, SnapshotDataRef> refs) {
    return refs.entrySet().stream()
        .filter(e -> e.getValue().getIsLoaded())
        .collect(toMap(
            Entry::getKey,
            e -> fromSnapshot(e.getValue().getData())));
  }

  private Map<String, List<ObjectNodeData>> fromSnapshotRefList(
      Map<String, List<SnapshotDataRef>> lists) {
    return lists.entrySet().stream()
        .filter(e -> isLoaded(e.getValue()))
        .collect(toMap(
            Entry::getKey,
            e -> e.getValue().stream()
                .map(ref -> fromSnapshot(ref.getData()))
                .collect(toList())));
  }

  private Map<String, Map<String, ObjectNodeData>> fromSnapshotRefMap(
      Map<String, Map<String, SnapshotDataRef>> maps) {
    return maps.entrySet().stream()
        .filter(e -> isLoaded(e.getValue().values()))
        .collect(toMap(
            Entry::getKey,
            e -> e.getValue().entrySet().stream()
                .collect(toMap(
                    Entry::getKey,
                    e2 -> fromSnapshot(e2.getValue().getData())))));
  }

  private boolean isLoaded(Collection<SnapshotDataRef> refs) {
    return refs.stream()
        .allMatch(SnapshotDataRef::getIsLoaded);
  }

  private void postProcessFromSnapshot(ObjectNode node, SnapshotData snapshot) {
    node.referenceLists.entrySet().stream()
        .filter(list -> snapshot.getReferenceLists().containsKey(list.getKey()))
        .forEach(list -> postProcessList(
            list.getValue(),
            snapshot.getReferenceLists().get(list.getKey())));
    node.referenceMaps.entrySet().stream()
        .filter(list -> snapshot.getReferenceLists().containsKey(list.getKey()))
        .forEach(list -> postProcessMap(
            list.getValue(),
            snapshot.getReferenceMaps().get(list.getKey())));

  }

  private void postProcessList(ObjectNodeList nodeList, List<SnapshotDataRef> snapList) {
    if (nodeList.size() != snapList.size()) {
      throw new IllegalArgumentException("nodeList and refList size doesn't match");
    }
    for (int i = 0; i < nodeList.size(); i++) {
      SnapshotDataRef snapRef = snapList.get(i);
      ObjectNodeReference nodeRef = nodeList.get(i);
      postProcessReferences(snapRef, nodeRef);
    }
  }

  private void postProcessMap(ObjectNodeMap nodeMap, Map<String, SnapshotDataRef> snapMap) {
    if (nodeMap.size() != snapMap.size()) {
      throw new IllegalArgumentException("nodeList and refList size doesn't match");
    }
    for (Entry<String, ObjectNodeReference> nodeEntry : nodeMap.entrySet()) {
      SnapshotDataRef snapRef = snapMap.get(nodeEntry.getKey());
      ObjectNodeReference nodeRef = nodeEntry.getValue();
      postProcessReferences(snapRef, nodeRef);
    }
  }

  private void postProcessReferences(SnapshotDataRef snapRef, ObjectNodeReference nodeRef) {
    if (Boolean.TRUE.equals(snapRef.getIsLoaded()) && !nodeRef.isLoaded()) {
      ObjectNode refNode = new ObjectNode(objectApi, snapRef.getData());
      nodeRef.set(refNode);
    }
  }

}
