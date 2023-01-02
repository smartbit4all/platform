package org.smartbit4all.api.collection;

/**
 * The stored reference is a special collection for storing one objects as a whole object.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public interface StoredReference<T> {

  void set(T object);

  T get();

  void clear();

}
