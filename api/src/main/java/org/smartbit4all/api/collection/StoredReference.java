package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.function.UnaryOperator;

/**
 * The stored reference is a special collection for storing one objects as a whole object.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public interface StoredReference<T> {

  void set(T object);

  void update(UnaryOperator<T> update);

  T get();

  void clear();

  URI getUri();

}
