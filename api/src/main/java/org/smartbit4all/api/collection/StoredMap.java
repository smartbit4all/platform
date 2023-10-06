package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * This map can be used to store a map of uri globally or scoped for an object.
 * 
 * @author Peter Boros
 */
public interface StoredMap {

  /**
   * The uris stored in the given container. Be careful this function reads the given collection
   * every time you call it.
   * 
   * @return
   */
  Map<String, URI> uris();

  /**
   * Locks the given collection object and put a new uri.
   * 
   * @param key
   * @param uri
   */
  void put(String key, URI uri);

  /**
   * Locks the given collection object and put all the entries from the parameter map.
   * 
   * @param values
   */
  void putAll(Map<String, URI> values);

  void put(Stream<StoredMapEntry> values);

  void remove(String key);

  void remove(Collection<String> keys);

  void remove(Stream<String> keys);

  Map<String, URI> update(UnaryOperator<Map<String, URI>> update);

  /**
   * If we set this branch then we access the branched object if it exists.
   * 
   * @param branchUri
   */
  void branch(URI branchUri);

  boolean exists();

  Long getLastModified();

}
