package org.smartbit4all.core.object;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

class ApiObjectListProxy<T> implements List<T> {

  private final ApiObjectCollection collection;

  public ApiObjectListProxy(ApiObjectCollection collection) {
    super();
    this.collection = collection;
  }

  @Override
  public int size() {
    return collection.size();
  }

  @Override
  public boolean isEmpty() {
    return collection.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return collection.containsObject(o);
  }

  @Override
  public Iterator<T> iterator() {
    Iterator<ApiObjectRef> iterator = collection.iterator();
    return new Iterator<T>() {

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @SuppressWarnings("unchecked")
      @Override
      public T next() {
        ApiObjectRef next = iterator.next();
        return next != null ? (T) next.getWrapper() : null;
      }
    };
  }

  @Override
  public Object[] toArray() {
    Object[] result = new Object[size()];
    int i = 0;
    for (ApiObjectRef apiObjectRef : collection) {
      result[i++] = apiObjectRef.getWrapper();
    }
    return result;
  }

  @Override
  public <T> T[] toArray(T[] a) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean add(T e) {
    return collection.addObject(e) != null;
  }

  @Override
  public boolean remove(Object o) {
    return collection.removeObject(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return collection.containsAllObject(c);
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    if (c == null) {
      return false;
    }
    for (T t : c) {
      collection.addObject(t);
    }
    return true;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    if (c == null) {
      return false;
    }
    for (Object t : c) {
      collection.addObject(t);
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
    collection.clear();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T get(int index) {
    ApiObjectRef apiObjectRef = collection.get(index);
    if (apiObjectRef != null)
      return (T) apiObjectRef.getWrapper(apiObjectRef.getMeta().getClazz());
    else
      return null;
  }

  @Override
  public T set(int index, T element) {
    ApiObjectRef apiObjectRef = collection.setObject(index, element);
    if (apiObjectRef != null) {
      return (T) apiObjectRef.getWrapper();
    }
    return null;
  }

  @Override
  public void add(int index, T element) {
    // TODO Auto-generated method stub

  }

  @SuppressWarnings("unchecked")
  @Override
  public T remove(int index) {
    ApiObjectRef remove = collection.remove(index);
    return remove != null ? (T) remove.getObject() : null;
  }

  @Override
  public int indexOf(Object o) {
    return collection.indexOfObject(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return collection.lastIndexOfObject(o);
  }

  @Override
  public ListIterator<T> listIterator() {
    ListIterator<ApiObjectRef> listIterator = collection.listIterator();
    return new ListIterator<T>() {

      @Override
      public boolean hasNext() {
        return listIterator.hasNext();
      }

      @SuppressWarnings("unchecked")
      @Override
      public T next() {
        ApiObjectRef next = listIterator.next();
        return (T) next.getWrapper();
      }

      @Override
      public boolean hasPrevious() {
        return listIterator.hasPrevious();
      }

      @SuppressWarnings("unchecked")
      @Override
      public T previous() {
        ApiObjectRef previous = listIterator.previous();
        return (T) previous.getWrapper();
      }

      @Override
      public int nextIndex() {
        return listIterator.nextIndex();
      }

      @Override
      public int previousIndex() {
        return listIterator.previousIndex();
      }

      @Override
      public void remove() {
        listIterator.remove();
      }

      @Override
      public void set(T e) {
        // TODO Auto-generated method stub

      }

      @Override
      public void add(T e) {
        // TODO Auto-generated method stub

      }
    };
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    // TODO Auto-generated method stub
    return null;
  }


}
