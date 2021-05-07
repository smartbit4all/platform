package org.smartbit4all.core.object.store;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ObjectStoreApiImpl implements ObjectStoreApi {

  private static final Logger log = LoggerFactory.getLogger(ObjectStoreApiImpl.class);
  
  private LoadingCache<URI, Object> cache;
  
  private Map<ObjectStoreUriMatcher, ObjectStoreObjectFactory<?>> objectFactoriesByMatchers = new HashMap<>();
  
  
  public ObjectStoreApiImpl(long ttlInMillis) {
    CacheLoader<URI, Object> cacheLoader = new CacheLoader<URI, Object>() {

      @Override
      public Object load(URI key) throws Exception {
        List<ObjectStoreUriMatcher> matchers = objectFactoriesByMatchers.keySet().stream()
            .filter(m -> m.isMatchingUri(key))
            .collect(Collectors.toList());
        if (matchers.isEmpty()) {
          return null;
        }
        if (matchers.size() > 1) {
          log.warn("There are more than one matching object factories for uri: {}", key);
        }
        ObjectStoreUriMatcher matcher = matchers.get(0);
        ObjectStoreObjectFactory<?> objectFactory = objectFactoriesByMatchers.get(matcher);
        if (objectFactory != null) {
          return objectFactory.createObject(key);
        }
        return null;
      }
      
    };
    
    cache = CacheBuilder.newBuilder()
        .expireAfterAccess(ttlInMillis, TimeUnit.MILLISECONDS)
        .build(cacheLoader);
  }
  
  @Override
  public void store(URI objectUri, Object object) {
    cache.put(objectUri, object);
  }

  @Override
  public Object get(URI objectUri) {
    return cache.getUnchecked(objectUri);
  }

  @Override
  public <T> T get(URI objectUri, Class<T> clazz) {
    return clazz.cast(get(objectUri));
  }

  public <T> void addObjectFactory(ObjectStoreUriMatcher uriMatcher, ObjectStoreObjectFactory<T> objectFactory) {
    objectFactoriesByMatchers.put(uriMatcher, objectFactory);
  }

  public static interface ObjectStoreObjectFactory<T> {
    T createObject(URI objectUri) throws Exception;
  }
  
  public static interface ObjectStoreUriMatcher {
    public boolean isMatchingUri(URI uri);
  }
  
}
