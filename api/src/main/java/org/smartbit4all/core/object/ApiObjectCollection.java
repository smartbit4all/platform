package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The {@link ApiObjectRef}s might have references to other ApiObjects and also can have collections
 * with list of references. If we manage the ApiObjects as a hierarchical tree structure then the
 * collection is structural element of it. This collection is ordered so it's important to know the
 * serial number of the references inside.
 * 
 * @author Peter Boros
 */
public final class ApiObjectCollection implements List<ApiObjectRef> {

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
   * The list of object references in a specific collection owned by an other {@link ApiObjectRef}.
   */
  private final List<ApiObjectRef> items = new ArrayList<>();


  /**
   * If an object reference is removed from the collection then we remember this till the event
   * rendering.
   */
  private final List<ApiObjectRef> removedObjects = new ArrayList<>();

  /**
   * The reference of the original collection in the bean. It can be replace at once and can be
   * edited item by item.
   */
  List<Object> originalCollection;

  private AtomicLong sequence = new AtomicLong();

  /**
   * Constructs a collection reference that manages the changes of a collection. Doesn't sets /
   * initializes original collection, it should be done separately with
   * {@link #setOriginalCollection(Collection)}.
   * 
   * @param objectRef
   * @param collectionProperty
   */
  ApiObjectCollection(ApiObjectRef objectRef, PropertyMeta collectionProperty) {
    super();
    this.objectRef = objectRef;
    this.property = collectionProperty;
    this.path = objectRef.getPropertyPath(collectionProperty);
  }

  /**
   * Constructs a new item in the list.
   * 
   * @param refObject
   * @return
   */
  private final ApiObjectRef constructObjectRef(Object refObject) {
    return new ApiObjectRef(path + StringConstant.SLASH + sequence.getAndIncrement(), refObject,
        objectRef.getDescriptors(), objectRef.getQualifier());
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
  public boolean contains(Object o) {
    return items.contains(o);
  }

  public boolean containsObject(Object o) {
    return originalCollection.contains(o);
  }

  @Override
  public Iterator<ApiObjectRef> iterator() {
    return items.iterator();
  }

  @Override
  public Object[] toArray() {
    return items.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return items.toArray(a);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean add(ApiObjectRef e) {
    if (e == null) {
      throw new NullPointerException();
    }
    e.setCurrentState(ChangeState.NEW);
    items.add(e);
    originalCollection.add(e.getObject());
    return true;
  }

  /**
   * Add a new object of the original list. It will create the {@link ApiObjectRef}.
   * 
   * @param o
   * @return
   */
  public ApiObjectRef addObject(Object o) {
    if (o != null) {
      ApiObjectRef itemRef = constructObjectRef(o);
      add(itemRef);
      return itemRef;
    }
    return null;
  }

  @Override
  public boolean remove(Object o) {
    if (!(o instanceof ApiObjectRef)) {
      return false;
    }
    int index = items.indexOf(o);
    items.remove(o);
    ApiObjectRef apiObjectRef = (ApiObjectRef) o;
    removedObjects.add(apiObjectRef);
    originalCollection.remove(index);
    return true;
  }

  /**
   * Removes an object by the content of the original collection.
   * 
   * @param o
   * @return
   */
  public boolean removeObject(Object o) {
    int index = originalCollection.indexOf(o);
    if (index != -1) {
      ApiObjectRef apiObjectRef = items.remove(index);
      removedObjects.add(apiObjectRef);
      originalCollection.remove(o);
    }
    return false;
  }

  /**
   * Removes all of the elements of this collection that satisfy the given predicate.
   * 
   * @param filter
   * @return
   */
  @Override
  public boolean removeIf(Predicate filter) {

    List toRemove = (List) originalCollection.stream().filter(filter).collect(Collectors.toList());

    for (Object object : toRemove) {
      removeObject(object);
    }
    return true;
  }

  public boolean removeByIdx(String idxPath) {
    for (ApiObjectRef item : items) {
      if (idxPath.equals(PathUtility.getLastPath(item.getPath()))) {
        return remove(item);
      }
    }
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return items.containsAll(c);
  }

  @SuppressWarnings("unchecked")
  public boolean containsAllObject(Collection<?> c) {
    return originalCollection.containsAll(c);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean addAll(Collection<? extends ApiObjectRef> c) {
    if (c == null) {
      return false;
    }
    items.addAll(c);
    originalCollection.addAll(c.stream().map(ApiObjectRef::getObject).collect(Collectors.toList()));
    return true;
  }

  @Override
  public boolean addAll(int index, Collection<? extends ApiObjectRef> c) {
    if (c == null) {
      return false;
    }
    return true;
  }

  /**
   * Sets original collection to a new value merging the contained elements based on their order.
   * 
   * @param newValue
   */
  public void setOriginalCollection(Collection<Object> newValue) {

    if (originalCollection == null) {
      if (newValue != null) {
        originalCollection = (List<Object>) newValue;
        for (Object refObject : originalCollection) {
          // TODO What would be the expectation in case of null in the list?
          if (refObject != null) {
            items.add(constructObjectRef(refObject));
          }
        }
      } else {
        // do nothing
      }
    } else {
      if (newValue != null) {
        // merge the values
        Iterator<Object> newValueIter = newValue.iterator();
        Iterator<ApiObjectRef> itemsIter = items.iterator();

        while (itemsIter.hasNext()) {
          ApiObjectRef objectRef = itemsIter.next();

          if (newValueIter.hasNext()) {
            // it's a new value on the place of on old one -> merge it
            Object currentNewObject = newValueIter.next();
            if (objectRef.getObject() == currentNewObject) {
              // no need to merge when they are equal objects
              continue;
            }
            // TODO What would be the expectation in case of null in the list?
            if (currentNewObject != null) {
              objectRef.setObject(currentNewObject);
            }
          } else {
            // no more new values -> remove the old ones
            removedObjects.add(objectRef);
            itemsIter.remove();
          }
        }
        // end of old values, maybe there are more new ones...
        while (newValueIter.hasNext()) {
          // there is another new value -> add this as apiObjectRef
          Object currentNewObject = newValueIter.next();
          // TODO What would be the expectation in case of null in the list?
          if (currentNewObject != null) {
            items.add(constructObjectRef(currentNewObject));
          }
        }
      } else {
        // no new value -> clear
        removedObjects.addAll(items);
        items.clear();
      }
      if (newValue != null) {
        originalCollection = (List<Object>) newValue;
      } else {
        originalCollection = null;
      }
    }

  }

  @Override
  public boolean removeAll(Collection<?> c) {
    if (c == null) {
      return false;
    }
    for (Object apiObjectRef : c) {
      if (apiObjectRef instanceof ApiObjectRef) {
        remove(apiObjectRef);
      }
    }
    return true;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void clear() {
    removedObjects.addAll(items);
    items.clear();
    if (originalCollection != null) {
      originalCollection.clear();
    }
  }

  @Override
  public ApiObjectRef get(int index) {
    return items.get(index);
  }

  public ApiObjectRef getByIdx(String idxPath) {
    for (ApiObjectRef item : items) {
      if (idxPath.equals(PathUtility.getLastPath(item.getPath()))) {
        return item;
      }
    }
    return null;
  }

  @Override
  public ApiObjectRef set(int index, ApiObjectRef element) {
    // Implement as a removal and an insertion at the same time.
    ApiObjectRef currentRef = items.get(index);
    if (currentRef != null) {
      element.setCurrentState(ChangeState.NEW);
      items.set(index, element);
      removedObjects.add(currentRef);
      return currentRef;
    }
    return null;
  }

  public ApiObjectRef setObject(int index, Object o) {
    // Implement as a removal and an insertion at the same time.
    ApiObjectRef currentRef = items.get(index);
    if (currentRef != null) {
      ApiObjectRef element = constructObjectRef(o);
      element.setCurrentState(ChangeState.NEW);
      items.set(index, element);
      removedObjects.add(currentRef);
      originalCollection.set(index, o);
      return currentRef;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void add(int index, ApiObjectRef element) {
    if (element != null) {
      items.add(index, element);
      originalCollection.add(index, element.getObject());
    }
  }

  @Override
  public ApiObjectRef remove(int index) {
    ApiObjectRef currentRef = items.get(index);
    if (currentRef != null) {
      items.remove(index);
      removedObjects.add(currentRef);
      return currentRef;
    }
    return null;
  }

  @Override
  public int indexOf(Object o) {
    return items.indexOf(o);
  }

  public int indexOfObject(Object o) {
    return originalCollection.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return items.lastIndexOf(o);
  }

  public int lastIndexOfObject(Object o) {
    return originalCollection.lastIndexOf(o);
  }

  @Override
  public ListIterator<ApiObjectRef> listIterator() {
    return items.listIterator();
  }

  @Override
  public ListIterator<ApiObjectRef> listIterator(int index) {
    return items.listIterator(index);
  }

  @Override
  public List<ApiObjectRef> subList(int fromIndex, int toIndex) {
    return items.subList(fromIndex, toIndex);
  }

  final Optional<CollectionChanges> renderAndCleanChanges() {
    CollectionChange collectionChange = null;
    CollectionObjectChange collectionObjectChange = null;
    for (ApiObjectRef ref : removedObjects) {
      if (collectionChange == null) {
        collectionChange = new CollectionChange(objectRef.getPath(), property.getName());
        collectionObjectChange =
            new CollectionObjectChange(objectRef.getPath(), property.getName());
      }
      collectionChange.getChanges().add(new ObjectChange(ref.getPath(), ChangeState.DELETED));
      collectionObjectChange.getChanges()
          .add(new ObjectChangeSimple(ref.getPath(), ChangeState.DELETED, ref.getObject()));
    }
    for (ApiObjectRef item : items) {
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
    removedObjects.clear();
    return Optional.ofNullable(result);
  }

  /**
   * The Proxy for the original list.
   */
  private ApiObjectListProxy<?> proxy = null;

  /**
   * Creates and returns a Proxy for a list that hides away the {@link ApiObjectCollection} itself.
   * We can use this list to add and remove items without knowing that it is an instrumented
   * {@link ApiObjectCollection} in the background. The Proxy is created for the original list
   * reference. So if we replace the whole list then the previously retrieved Proxy becomes invalid!
   * 
   * @return
   */
  @SuppressWarnings("rawtypes")
  public List<?> getProxy() {
    if (proxy == null) {
      proxy = new ApiObjectListProxy(this);
    }
    return proxy;
  }

  static class CollectionChanges {
    protected final CollectionChange collectionChanges;
    protected final CollectionObjectChange collectionObjectChanges;

    CollectionChanges(CollectionChange collectionChanges,
        CollectionObjectChange collectionObjectChanges) {
      this.collectionChanges = collectionChanges;
      this.collectionObjectChanges = collectionObjectChanges;
    }
  }

  public String getPath() {
    return objectRef.getPath();
  }

  public String getName() {
    return property.getName();
  }

  @Override
  public String toString() {
    return items.toString();
  }
}
