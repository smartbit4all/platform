package org.smartbit4all.core.object;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The base implementation of the cache.
 * 
 * @author Peter Boros
 *
 * @param <T> The object to cache.
 */
public class ObjectCacheEntryImpl<T> implements ObjectCacheEntry<T> {

  private static final Logger log = LoggerFactory.getLogger(ObjectCacheEntryImpl.class);

  private ObjectApi objectApi;

  private final Cache<URI, CachedObject> cache;

  private Class<T> clazz;

  ObjectCacheEntryImpl(Class<T> clazz) {
    super();
    this.clazz = clazz;
    cache = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T get(URI uri) {
    URI latestUri = objectApi.getLatestUri(uri);
    CachedObject cachedObject = null;
    try {
      Long lastModified = objectApi.getLastModified(latestUri);
      cachedObject = cache.getIfPresent(latestUri);
      if (cachedObject == null
          || (lastModified != null && !lastModified.equals(cachedObject.lastModification))) {
        if (cachedObject != null) {
          cache.invalidate(latestUri);
        }
        cachedObject = cache.get(latestUri, () -> {
          ObjectNode objectNode = objectApi.load(latestUri);
          return new CachedObject(objectNode.getLastModified(),
              objectNode.getObject(clazz));
        });
      }
    } catch (ExecutionException e) {
      log.error("Unable to retrieve the branch entry " + uri, e);
    }
    return cachedObject != null ? (T) cachedObject.object : null;
  }

  ObjectCacheEntry<T> objectApi(ObjectApi objectApi) {
    this.objectApi = objectApi;
    return this;
  }

}
