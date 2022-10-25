package org.smartbit4all.api.retrieval;

import java.net.URI;
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
   * The state fo the object node. The retrieved node has {@link ObjectNodeState#NOP} state by
   * default. The newly added ones are {@link ObjectNodeState#NEW} of course and if we have a
   * retrieved node then it will be {@link ObjectNodeState#MODIFIED} after an update operation.
   * 
   * @author Peter Boros
   */
  public enum ObjectNodeState {
    NEW, MODIFIED, NOP
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
  private final Map<ReferenceDefinition, List<ObjectNode>> referenceListValues = new HashMap<>();

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

  public final Map<ReferenceDefinition, ObjectNode> getReferenceValues() {
    return referenceValues;
  }

  public final Map<ReferenceDefinition, List<ObjectNode>> getReferenceListValues() {
    return referenceListValues;
  }

  public final Map<ReferenceDefinition, Map<String, ObjectNode>> getReferenceMapValues() {
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
        getReferenceListValues().values().stream().flatMap(List::stream)
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
    Map<String, Object> map = definition.toMap(object);
    objectAsMap.putAll(map);
    setModifid();
  }

  /**
   * Set the values directly into the {@link #objectAsMap}.
   * 
   * @param values The map of values.
   */
  public void setValues(Map<String, Object> values) {
    objectAsMap.putAll(values);
    setModifid();
  }

  /**
   * Set the value directly into the {@link #objectAsMap}.
   * 
   * @param key The key of the value, the name of the property.
   * @param value The value object.
   */
  public void setValue(String key, Object value) {
    objectAsMap.put(key, value);
    setModifid();
  }

  /**
   * @return The current state of the {@link ObjectNode}.
   * @see ObjectNodeState
   */
  public final ObjectNodeState getState() {
    return state;
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
  final void setModifid() {
    if (state == ObjectNodeState.NOP) {
      state = ObjectNodeState.MODIFIED;
    }
  }

}
