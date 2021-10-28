package org.smartbit4all.core.object;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.smartbit4all.core.object.ApiObjectCollection.CollectionChanges;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The {@link ApiObjectRef}s might have references to other ApiObjects and also can have maps with
 * references uniquely identified by a string. If we manage the ApiObjects as a hierarchical tree
 * structure then the map is structural element of it. We can use the key of the map to identify the
 * given object so it can be programmatically managed..
 * 
 * @author Peter Boros
 */
public final class ApiObjectMap implements Map<String, ApiObjectRef> {

  /**
   * The path of the given collection.
   */
  private final String path;

  /**
   * The {@link ApiObjectRef} of the parent object. It contains the property that is reference of
   * the collection.
   */
  final ApiObjectRef objectRef;

  /**
   * The property that is reference of the given connection in the parent object.
   */
  private final PropertyMeta property;

  /**
   * The items of the map.
   */
  private Map<String, ApiObjectRef> items = new HashMap<>();

  /**
   * The removed items of the map.
   */
  private Map<String, ApiObjectRef> removedItems = new HashMap<>();

  /**
   * The reference of the original map in the original object.
   */
  private Map<String, Object> originalMap;

  /**
   * Constructs a collection reference that manages the changes of a collection. Doesn't sets /
   * initializes original collection, it should be done separately with
   * {@link #setOriginalCollection(Collection)}.
   * 
   * @param objectRef
   * @param collectionProperty
   */
  ApiObjectMap(ApiObjectRef objectRef, PropertyMeta mapProperty) {
    super();
    this.objectRef = objectRef;
    this.property = mapProperty;
    this.path = objectRef.getPropertyPath(mapProperty);
  }

  /**
   * Constructs a new item in the map.
   *
   * @param key The key of the object to add.
   * @param refObject The reference itself.
   * @return The newly constructed object ref.
   */
  private final ApiObjectRef constructObjectRef(String key, Object refObject) {
    return new ApiObjectRef(path + StringConstant.SLASH + key, refObject,
        objectRef.getDescriptors());
  }

  @Override
  public int size() {
    return items.size();
  }

  @Override
  public boolean isEmpty() {
    return items.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return items.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return items.containsValue(value);
  }

  public boolean containsValueObject(Object value) {
    return originalMap.containsValue(value);
  }

  @Override
  public ApiObjectRef get(Object key) {
    return items.get(key);
  }

  @Override
  public ApiObjectRef put(String key, ApiObjectRef value) {
    if (value == null) {
      throw new NullPointerException();
    }
    ApiObjectRef currentObjectRef = items.get(key);
    if (currentObjectRef != null) {
      // If we currently have an object with this identifier then the new object is going to be
      // merged into and result modification rather then new event.
      currentObjectRef.mergeObject(value.getObject());
      return currentObjectRef;
    } else {
      value.setCurrentState(ChangeState.NEW);
      items.put(key, value);
      originalMap.put(key, value.getObject());
    }
    return null;
  }

  @Override
  public ApiObjectRef remove(Object key) {
    ApiObjectRef currentRef = items.remove(key);
    if (currentRef != null) {
      removedItems.put(path, currentRef);
      originalMap.remove(key);
      return currentRef;
    }
    return null;
  }

  @Override
  public void putAll(Map<? extends String, ? extends ApiObjectRef> m) {
    if (m == null) {
      return;
    }
    items.putAll(m);
    originalMap.putAll(m.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getObject())));
  }

  @Override
  public void clear() {
    removedItems.putAll(items);
    items.clear();
    if (originalMap != null) {
      originalMap.clear();
    }
  }

  @Override
  public Set<String> keySet() {
    return items.keySet();
  }

  @Override
  public Collection<ApiObjectRef> values() {
    return items.values();
  }

  @Override
  public Set<Entry<String, ApiObjectRef>> entrySet() {
    return items.entrySet();
  }

  public final void setOriginalMap(Map<String, Object> newValue) {
    if (originalMap == null) {
      if (newValue != null) {
        originalMap = newValue;
        for (Entry<String, Object> entry : originalMap.entrySet()) {
          items.put(entry.getKey(), constructObjectRef(entry.getKey(), entry.getValue()));
        }
      } else {
        // do nothing
      }
    } else {
      if (newValue != null) {
        // merge the values of the existing maps.
        // Set up a set for the keys before the operation. Every key from the new map will be
        // removed during the operation. At the end we will remove the rest.
        Set<String> keysBefore = items.keySet();

        for (Entry<String, Object> entry : newValue.entrySet()) {
          if (keysBefore.remove(entry.getKey())) {
            // We have the object with this key currently.
            ApiObjectRef currentObjectRef = items.get(entry.getKey());
            // If the currentObjectRef is not null then we merge the new object. Else we put it as a
            // brand new object!
            if (currentObjectRef != null) {
              currentObjectRef.mergeObject(entry.getValue());
            } else {
              items.put(entry.getKey(), constructObjectRef(entry.getKey(), entry.getValue()));
              originalMap.put(entry.getKey(), entry.getValue());
            }
          }
        }

        // Remove all the remaining keys.
        for (String key : keysBefore) {
          remove(key);
        }
      }
    }
  }

  /**
   * Add a new object of the original list. It will create the {@link ApiObjectRef}.
   * 
   * @param o
   * @return
   */
  public ApiObjectRef putObject(String key, Object o) {
    if (o != null) {
      ApiObjectRef itemRef = constructObjectRef(key, o);
      put(key, itemRef);
      return itemRef;
    }
    return null;
  }

  /**
   * The Proxy for the original map.
   */
  private ApiObjectMapProxy<Object> proxy = null;

  /**
   * Creates and returns a Proxy for a map that hides away the {@link ApiObjectMap} itself. We can
   * use this list to add and remove items without knowing that it is an instrumented
   * {@link ApiObjectMap} in the background. The Proxy is created for the original map reference. So
   * if we replace the whole map then the previously retrieved Proxy becomes invalid!
   * 
   * @return
   */
  @SuppressWarnings("rawtypes")
  public Map<String, Object> getProxy() {
    if (proxy == null) {
      proxy = new ApiObjectMapProxy(this);
    }
    return proxy;
  }

  final Optional<CollectionChanges> renderAndCleanChanges() {
    CollectionChange collectionChange = null;
    CollectionObjectChange collectionObjectChange = null;
    for (ApiObjectRef ref : removedItems.values()) {
      if (collectionChange == null) {
        collectionChange = new CollectionChange(objectRef.getPath(), property.getName());
        collectionObjectChange =
            new CollectionObjectChange(objectRef.getPath(), property.getName());
      }
      collectionChange.getChanges().add(new ObjectChange(ref.getPath(), ChangeState.DELETED));
      collectionObjectChange.getChanges()
          .add(new ObjectChangeSimple(ref.getPath(), ChangeState.DELETED, ref.getObject()));
    }
    for (ApiObjectRef item : items.values()) {
      Optional<ObjectChange> itemChange = item.renderAndCleanChanges();
      if (itemChange.isPresent()) {
        if (collectionChange == null) {
          collectionChange = new CollectionChange(objectRef.getPath(), property.getName());
          collectionObjectChange =
              new CollectionObjectChange(objectRef.getPath(), property.getName());
        }
        collectionChange.getChanges().add(itemChange.get());
        collectionObjectChange.getChanges()
            .add(new ObjectChangeSimple(item.getPath(), itemChange.get().getOperation(),
                item.getObject()));
      }
    }
    CollectionChanges result = null;
    if (collectionChange != null) {
      result = new CollectionChanges(collectionChange, collectionObjectChange);
    }
    removedItems.clear();
    return Optional.ofNullable(result);
  }

}
