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

  /**
   * This function checks if the given uri exists in the list. If not then the uri will be added to
   * the list as the first uri. If exists then the uri will be moved to the first position. After
   * the operation the the size of the list is checked and the last uri is removed if the maxSize
   * exceeded.
   * 
   * @param uri The uri to add.
   * @param maxSize The maximum size of the list after the operation.
   * @param assumeLatestUri If true then the list will contains the laster uri even if the version
   *        uri is passed.
   */
  void addOrMoveFirst(URI uri, int maxSize, boolean assumeLatestUri);

}
