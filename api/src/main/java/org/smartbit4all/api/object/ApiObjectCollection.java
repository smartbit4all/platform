package org.smartbit4all.api.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@link ApiObjectRef}s might have references to other ApiObjects and also can have collections
 * with list of references. If we manage the ApiObjects as a hierarchical tree structure then the
 * collection is structural element of it. This collection is ordered so it's important to know the
 * serial number of the references inside.
 * 
 * @author Peter Boros
 * @param <R>
 * @param <O>
 */
public class ApiObjectCollection implements List<ApiObjectRef> {

  /**
   * The {@link ApiObjectRef} of the parent object. It contains the property that is reference of
   * the collection.
   */
  ApiObjectRef objectRef;

  /**
   * The property that is reference of the given connection in the parent object.
   */
  private final PropertyMeta property;

  /**
   * The state of the collection.
   */
  private ChangeState state = ChangeState.NEW;

  /**
   * The list of object references in a specific collection owned by an other {@link ApiObjectRef}.
   */
  private List<ApiObjectRef> items = new ArrayList<>();

  /**
   * The item map to find the reference quickly with the original object.
   */
  private Map<Object, ApiObjectRef> itemsByObject = new HashMap<>();

  /**
   * If an object reference is removed from the collection then we remember this till the event
   * rendering.
   */
  private List<ApiObjectRef> removedObjects = new ArrayList<>();

  private List originalCollection;

  /**
   * Constructs a collection reference that manages the changes of a collection
   * 
   * @param objectRef
   * @param collectionProperty
   */
  public ApiObjectCollection(ApiObjectRef objectRef, PropertyMeta collectionProperty) {
    super();
    this.objectRef = objectRef;
    this.property = collectionProperty;
    build();
  }

  /**
   * Process the collection to initiate the data structure of the collection. The build is an
   * initial modification of the collection.
   */
  private final void build() {
    Object collectionValue = property.getValue(objectRef.getObject());
    if (collectionValue != null) {
      originalCollection = (List<?>) collectionValue;
      for (Object refObject : originalCollection) {
        // TODO What would be the expectation in case of null in the list?
        if (refObject != null) {
          ApiObjectRef ref = new ApiObjectRef(refObject, null, objectRef.getDescriptors());
          addRef(ref);
        }
      }
    }

  }

  /**
   * Add the given {@link ApiObjectRef}
   */
  private final void addRef(ApiObjectRef objectRef) {
    if (objectRef != null) {
      items.add(objectRef);
      itemsByObject.put(objectRef.getObject(), objectRef);
    }
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
    // Here we add this reference so the reference will be new here.
    if (e == null) {
      return false;
    }
    e.setCurrentState(ChangeState.NEW);
    addRef(e);
    originalCollection.add(e.getObject());
    return true;
  }

  @Override
  public boolean remove(Object o) {
    if (!(o instanceof ApiObjectRef)) {
      return false;
    }
    items.remove(o);
    ApiObjectRef apiObjectRef = (ApiObjectRef) o;
    itemsByObject.remove(apiObjectRef.getObject());
    removedObjects.add(apiObjectRef);
    originalCollection.remove(apiObjectRef.getObject());
    return true;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return items.containsAll(c);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean addAll(Collection<? extends ApiObjectRef> c) {
    if (c == null) {
      return false;
    }
    items.addAll(c);
    for (ApiObjectRef apiObjectRef : c) {
      if (apiObjectRef != null) {
        itemsByObject.put(apiObjectRef.getObject(), apiObjectRef);
      }
    }
    originalCollection.addAll(c.stream().map(ApiObjectRef::getObject).collect(Collectors.toList()));
    return true;
  }

  @Override
  public boolean addAll(int index, Collection<? extends ApiObjectRef> c) {
    if (c == null) {
      return false;
    }
    // TODO correct implementation
    // items.addAll(index, c);
    // for (ApiObjectRef apiObjectRef : c) {
    // if (apiObjectRef != null) {
    // itemsByObject.put(apiObjectRef.getObject(), apiObjectRef);
    // }
    // }
    return true;
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
    itemsByObject.clear();
    originalCollection.clear();
  }

  @Override
  public ApiObjectRef get(int index) {
    return items.get(index);
  }

  @Override
  public ApiObjectRef set(int index, ApiObjectRef element) {
    // Implement as a removal and an insertion at the same time.
    ApiObjectRef currentRef = items.get(index);
    if (currentRef != null) {
      element.setCurrentState(ChangeState.NEW);
      items.set(index, element);
      removedObjects.add(currentRef);
      itemsByObject.remove(currentRef.getObject());
      return currentRef;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void add(int index, ApiObjectRef element) {
    if (element != null) {
      items.add(index, element);
      itemsByObject.put(element.getObject(), element);
      originalCollection.add(index, element.getObject());
    }
  }

  @Override
  public ApiObjectRef remove(int index) {
    ApiObjectRef currentRef = items.get(index);
    if (currentRef != null) {
      items.remove(index);
      removedObjects.add(currentRef);
      itemsByObject.remove(currentRef.getObject());
      return currentRef;
    }
    return null;
  }

  @Override
  public int indexOf(Object o) {
    return items.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return items.lastIndexOf(o);
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


}
