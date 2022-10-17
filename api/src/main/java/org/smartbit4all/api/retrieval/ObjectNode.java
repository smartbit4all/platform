package org.smartbit4all.api.retrieval;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
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
   * For better destruction.
   */
  private WeakReference<ObjectModel> modelRef;

  ObjectNode(ObjectModel model, ObjectDefinition<?> definition,
      String storageScheme) {
    super();
    this.definition = definition;
    this.storageScheme = storageScheme;
    modelRef = new WeakReference<>(model);
  }

  /**
   * The version uri of the object.
   */
  private URI uri;

  /**
   * The object values of the request. The existing properties of the map are going to set during
   * the save and the rest of the property value will remain the same. If the {@link #object} is set
   * then we don't use this map!
   */
  private Map<String, Object> objectAsMap;

  /**
   * If we have the object the {@link #objectAsMap} is not used!
   */
  private Object object;

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

  public final URI getUri() {
    return uri;
  }

  public final void setUri(URI uri) {
    this.uri = uri;
  }

  public final ObjectDefinition<?> getDefinition() {
    return definition;
  }

  public final Map<String, Object> getObjectAsMap() {
    return objectAsMap;
  }

  public final void setObjectAsMap(Map<String, Object> objectAsMap) {
    this.objectAsMap = objectAsMap;
  }

  public final String getStorageScheme() {
    return storageScheme;
  }

  public final Object getObject() {
    return object;
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

  Stream<ObjectNode> allNodes() {
    // return Stream.concat(
    // Stream.of(this),
    // Stream.concat(getReferenceValues().values().stream().flatMap(ObjectNode::allNodes),
    // getReferenceListValues().values().stream().flatMap(List::stream)
    // .flatMap(ObjectNode::allNodes)));
    return Stream.of(
        Stream.of(this),
        getReferenceValues().values().stream().flatMap(ObjectNode::allNodes),
        getReferenceListValues().values().stream().flatMap(List::stream)
            .flatMap(ObjectNode::allNodes),
        getReferenceMapValues().values().stream().flatMap(m -> m.values().stream())
            .flatMap(ObjectNode::allNodes))
        .flatMap(s -> s);
  }

}
