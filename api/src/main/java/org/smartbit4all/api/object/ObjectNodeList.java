package org.smartbit4all.api.object;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import org.smartbit4all.api.object.ObjectNode.ObjectNodeState;

final class ObjectNodeList implements List<ObjectNode> {

  /**
   * The original List stored in the {@link ObjectNode} that owns the given reference list.
   */
  private final List<ObjectNode> originalList;

  private final List<ObjectNode> list;

  ObjectNodeList(List<ObjectNode> originalList) {
    super();
    this.originalList = originalList;
    list = originalList.stream().filter(ObjectNode::notRemoved).collect(Collectors.toList());
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return list.contains(o);
  }

  @Override
  public Iterator<ObjectNode> iterator() {
    return list.iterator();
  }

  @Override
  public Object[] toArray() {
    return list.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return list.toArray(a);
  }

  @Override
  public boolean add(ObjectNode e) {
    if (list.add(e)) {
      e.setState(ObjectNodeState.NEW);
      originalList.add(e);
      return true;
    }
    return false;
  }

  @Override
  public boolean remove(Object o) {
    int indexOf = indexOf(o);
    if (indexOf != -1) {
      remove(indexOf);
      return true;
    }
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return list.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends ObjectNode> c) {
    if (c != null) {
      c.stream().forEach(n -> n.setState(ObjectNodeState.NEW));
    }
    list.addAll(c);
    return true;
  }

  @Override
  public boolean addAll(int index, Collection<? extends ObjectNode> c) {
    if (c != null) {
      c.stream().forEach(n -> n.setState(ObjectNodeState.NEW));
    }
    list.addAll(index, c);
    return true;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    if (c != null) {
      for (Object object : c) {
        remove(object);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    for (int i = list.size() - 1; i >= 0; i--) {
      remove(i);
    }
  }

  @Override
  public ObjectNode get(int index) {
    return list.get(index);
  }

  @Override
  public ObjectNode set(int index, ObjectNode element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, ObjectNode element) {
    list.add(index, element);
    element.setState(ObjectNodeState.NEW);
    originalList.add(element);
  }

  @Override
  public ObjectNode remove(int index) {
    ObjectNode removed = list.remove(index);
    removed.setState(ObjectNodeState.REMOVED);
    return removed;
  }

  @Override
  public int indexOf(Object o) {
    return list.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return list.lastIndexOf(o);
  }

  @Override
  public ListIterator<ObjectNode> listIterator() {
    return list.listIterator();
  }

  @Override
  public ListIterator<ObjectNode> listIterator(int index) {
    return listIterator(index);
  }

  @Override
  public List<ObjectNode> subList(int fromIndex, int toIndex) {
    return list.subList(fromIndex, toIndex);
  }

}