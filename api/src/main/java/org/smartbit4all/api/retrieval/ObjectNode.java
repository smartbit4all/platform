package org.smartbit4all.api.retrieval;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.smartbit4all.api.applychange.ApplyChangeApi;
import org.smartbit4all.api.applychange.ApplyChangeRequest;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * The object node contains an object returned by the {@link RetrievalApi}. It can manage the state
 * of the object and we can use it to produce {@link ApplyChangeRequest} at the end of the
 * modification.
 * 
 * @author Peter Boros
 *
 */
public class ObjectNode {

  /**
   * The object definition to access the meta of the given object.
   */
  private final ObjectDefinition<?> definition;

  /**
   * The logical storage scheme if it is used in the given storage implementation.
   */
  private final String storageScheme;

  /**
   * The state of the object node. The retrieved node has {@link ObjectNodeState#NOP} state by
   * default. The newly added ones are {@link ObjectNodeState#NEW} of course and if we have a
   * retrieved node then it will be {@link ObjectNodeState#MODIFIED} after an update operation. The
   * {@link #REMOVED} state is responsible for removing from a container. It doesn't mean that we
   * delete the given object we just remove it from the given container.
   * 
   * @author Peter Boros
   */
  public enum ObjectNodeState {
    NEW, MODIFIED, NOP, REMOVED
  }

  ObjectNode(ObjectDefinition<?> definition, String storageScheme) {
    super();
    this.definition = definition;
    this.storageScheme = storageScheme;
  }

  /**
   * The version uri of the object.
   */
  private URI uri;

  /**
   * The state of the {@link ObjectNode}.
   */
  private ObjectNodeState state = ObjectNodeState.NOP;

  /**
   * The values of the object by the property names as key.
   */
  private Map<String, Object> objectAsMap;

  /**
   * The referred objects.
   */
  private final Map<ReferenceDefinition, ObjectNode> referenceValues = new HashMap<>();

  /**
   * The referred object lists.
   */
  private final Map<ReferenceDefinition, ReferenceListEntry> referenceListValues = new HashMap<>();

  /**
   * The referred object maps.
   */
  private final Map<ReferenceDefinition, Map<String, ObjectNode>> referenceMapValues =
      new HashMap<>();

  /**
   * @return The version URI of the object when it was retrieved.
   */
  public final URI getUri() {
    return uri;
  }

  /**
   * @return The {@link ObjectDefinition} for the given object.
   */
  public final ObjectDefinition<?> getDefinition() {
    return definition;
  }

  public final Map<String, Object> getObjectAsMap() {
    return objectAsMap;
  }

  /**
   * The read can initiate the {@link #objectAsMap}.
   * 
   * @param objectAsMap
   */
  final void setObjectAsMap(Map<String, Object> objectAsMap) {
    this.objectAsMap = objectAsMap;
  }

  public final String getStorageScheme() {
    return storageScheme;
  }

  /**
   * @return A copy from the current {@link #objectAsMap}.
   */
  public final Object getObject() {
    return definition.fromMap(objectAsMap);
  }

  /**
   * @param objectDefinition The object definition of the required object.
   * @return A copy from the current {@link #objectAsMap}.
   */
  public final <T> T getObject(ObjectDefinition<T> objectDefinition) {
    return objectDefinition.fromMap(objectAsMap);
  }

  final Map<ReferenceDefinition, ObjectNode> getReferenceValues() {
    return referenceValues;
  }

  final Map<ReferenceDefinition, ReferenceListEntry> getReferenceListValues() {
    return referenceListValues;
  }

  final Map<ReferenceDefinition, Map<String, ObjectNode>> getReferenceMapValues() {
    return referenceMapValues;
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
        getReferenceValues().values().stream().flatMap(ObjectNode::allNodes),
        getReferenceListValues().values().stream().flatMap(e -> e.nodeList.stream())
            .flatMap(ObjectNode::allNodes),
        getReferenceMapValues().values().stream().flatMap(m -> m.values().stream())
            .flatMap(ObjectNode::allNodes))
        .flatMap(s -> s);
  }



  /**
   * Update the given {@link ObjectNode} as java object. It retrieves the current value of the bean
   * and can return another bean or the same with modified values. We don't have to care about
   * setting the proper URIs for the references because they will be managed by the
   * {@link RetrievalApi} and the {@link ApplyChangeApi}.
   * 
   * @param <T> The type class of the bean. If we already have the object then it will be passed to
   *        the updateFunction. Else the function read the given object from the
   *        {@link #objectAsMap}.
   * @param object The object that contains the values to set. This object is going to be serialized
   *        and the the values are set into the {@link #objectAsMap} that is the single point of
   *        truth. If we pass null then nothing will happen with the values. The null is assumed as
   *        an empty map to set.
   */
  public <T> void setObject(T object) {
    if (object == null) {
      return;
    }
    // Now we can accept only the object itself.
    setValues(definition.toMap(object));
  }

  /**
   * Set the values directly into the {@link #objectAsMap}.
   * 
   * @param values The map of values.
   */
  public void setValues(Map<String, Object> values) {
    objectAsMap.putAll(values);
    setModified();
  }

  /**
   * Set the value directly into the {@link #objectAsMap}.
   * 
   * @param key The key of the value, the name of the property.
   * @param value The value object.
   */
  public void setValue(String key, Object value) {
    objectAsMap.put(key, value);
    setModified();
  }

  /**
   * @return The current state of the {@link ObjectNode}.
   * @see ObjectNodeState
   */
  public final ObjectNodeState getState() {
    return state;
  }

  final boolean isRemoved() {
    return state == ObjectNodeState.REMOVED;
  }

  /**
   * Modify the state of the node. It's not public so it can be called inside the
   * {@link RetrievalApi} and the {@link ApplyChangeApi} implementations.
   * 
   * @param state
   */
  final void setState(ObjectNodeState state) {
    this.state = state;
  }

  /**
   * Set the state to modified if the state is {@link ObjectNodeState#NOP} else the state remains
   * the same.
   */
  final void setModified() {
    if (state == ObjectNodeState.NOP) {
      state = ObjectNodeState.MODIFIED;
    }
  }

  /**
   * The reference value is returned by the {@link ReferenceDefinition} that is one outgoing
   * reference of the current {@link ObjectDefinition}.
   * 
   * @param reference An outgoing reference of the given object.
   * @return The {@link ObjectNode} belong to the given reference. If the reference is not loaded
   *         then we get null.
   */
  public ObjectNode referenceNode(ReferenceDefinition reference) {
    return referenceValues.get(reference);
  }

  /**
   * The reference value is returned by the {@link ReferenceDefinition} that is one outgoing
   * reference of the current {@link ObjectDefinition}.
   * 
   * @param reference An outgoing reference of the given object.
   * @param storageScheme The schema of the newly created referred object.
   * @return The {@link ObjectNode} belong to the given reference. If the reference is not loaded
   *         then we get null.
   */
  public ObjectNode referenceNodeInitIfAbsent(ReferenceDefinition reference, String storageScheme) {
    return referenceValues.computeIfAbsent(reference,
        r -> new ObjectNode(r.getTarget(), storageScheme));
  }

  /**
   * The reference value is returned by the {@link ReferenceDefinition} that is one outgoing
   * reference of the current {@link ObjectDefinition}.
   * 
   * @param <T>
   * @param reference An outgoing reference of the given object.
   * @return The copy of the current value.
   */
  public <T> T reference(ReferenceDefinition reference, ObjectDefinition<T> objectDefinition) {
    ObjectNode referenceNode = referenceNode(reference);
    if (referenceNode != null) {
      return referenceNode.getObject(objectDefinition);
    }
    return null;
  }

  /**
   * @param reference
   * @return If the given reference is empty, doesn't have any object set then we get back an empty
   *         map.
   */
  public Map<String, Object> reference(ReferenceDefinition reference) {
    ObjectNode referenceNode = referenceNode(reference);
    if (referenceNode != null) {
      return referenceNode.getObjectAsMap();
    }
    return Collections.emptyMap();
  }

  /**
   * @param <T>
   * @param reference
   * @param o The object value for the reference. This object is not necessarily comes from the kind
   *        of the referred object definition. We can use any object that has relevant values.
   * @param storageScheme The storage schema for the object to set.
   */
  public <T> void setReference(ReferenceDefinition reference, Object o, String storageScheme) {
    ObjectNode objectNode = referenceNodeInitIfAbsent(reference, storageScheme);
    objectNode.setObject(o);
  }

  /**
   * Set the referred object node to deletion if it exists. If it doesn't exist then nothing
   * happens.
   * 
   * @param reference
   */
  public void clearReference(ReferenceDefinition reference) {
    ObjectNode objectNode = referenceNode(reference);
    if (objectNode != null) {
      objectNode.setState(ObjectNodeState.REMOVED);
    }
  }

  /**
   * This retrieves the currently available nodes belong to the given reference.
   * 
   * @param reference The reference.
   * @return Returns a list that contains all the currently available nodes. It can be modified in
   *         the following ways: Adding a new Node will create a new node in the list. We can use
   *         the node directly. Remove a node from the list will set the {@link ObjectNode#state} to
   *         {@link ObjectNodeState#REMOVED} and the given node will disappear from the list. In any
   *         other case we can use the {@link ObjectNode} directly to modify the object if
   *         necessary.
   */
  public List<ObjectNode> referenceNodeList(ReferenceDefinition reference) {
    ReferenceListEntry referenceListEntry = referenceListValues.get(reference);
    if (referenceListEntry != null) {
      return referenceListEntry.getPublicNodeList();
    }
    return Collections.emptyList();
  }

}
