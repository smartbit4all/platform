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

  Map<String, URI> uris();

  void put(String key, URI uri);

  void putAll(Map<String, URI> values);

  void put(Stream<StoredMapEntry> values);

  void remove(String key);

  void remove(Collection<String> keys);

  void remove(Stream<String> keys);

  Map<String, URI> update(UnaryOperator<Map<String, URI>> update);

}
