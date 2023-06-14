package org.smartbit4all.core.object;

import java.net.URI;

/**
 * The object cache entry is a parameterization option to define a caching mechanism for a given
 * kind of object. It is parameterized by the object it manages. The object is stored directly in
 * the cache so it can be used only for read strictly. It can not be the object for a modification
 * service.
 * 
 * @author Peter Boros
 * 
 * @param <T> The object type for the cache.
 * 
 */
public interface ObjectCacheEntry<T> {

  T get(URI uri);

}
