package org.smartbit4all.domain.meta;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This definition class folds a few properties of a given entity. The entity is referred by its
 * interface class. So it can be accessed dynamically runtime.
 * 
 * @author Peter Boros
 * @param <E>
 *
 */
public class PropertySet implements Set<Property<?>> {

  /**
   * The set of properties.
   */
  private Set<Property<?>> properties = new HashSet<>();

  /**
   * It constructs a new property set bound to the entity definition.
   * 
   */
  public PropertySet() {
    super();
  }

  @Override
  public int size() {
    return properties.size();
  }

  @Override
  public boolean isEmpty() {
    return properties.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return properties.contains(o);
  }

  @Override
  public Iterator<Property<?>> iterator() {
    return properties.iterator();
  }

  @Override
  public Object[] toArray() {
    return properties.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return properties.toArray(a);
  }

  @Override
  public boolean add(Property<?> e) {
    return properties.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return properties.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return properties.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends Property<?>> c) {
    return properties.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return properties.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return properties.removeAll(c);
  }

  @Override
  public void clear() {
    properties.clear();
  }

}
