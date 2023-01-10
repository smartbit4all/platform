package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * This list can be used to store a list of uri globally or scoped for an object.
 * 
 * @author Peter Boros
 */
public interface StoredList {

  List<URI> uris();

  void add(URI uri);

  void addAll(Collection<URI> uris);

  void addAll(Stream<URI> uris);

  List<URI> update(UnaryOperator<List<URI>> update);

  void removeAll(Collection<URI> uris);

  void remove(URI uri);

  boolean exists();

}
