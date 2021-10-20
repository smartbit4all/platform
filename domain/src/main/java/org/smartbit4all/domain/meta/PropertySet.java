/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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

  /**
   * It constructs a new property set bound to the entity definition.
   * 
   */
  public PropertySet(Collection<Property<?>> collection) {
    super();
    this.addAll(collection);
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
