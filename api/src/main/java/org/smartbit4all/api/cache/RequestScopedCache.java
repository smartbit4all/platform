package org.smartbit4all.api.cache;

import java.util.HashMap;
import java.util.Map;

public class RequestScopedCache {
  private static final ThreadLocal<Map<String, Map<String, Object>>> caches = new ThreadLocal<>();

  public void startRequest() {
    caches.remove();
    caches.set(new HashMap<>());
  }

  public void put(String cache, String key, Object value) {
    getCache(cache).put(key, value);
  }

  public Map<String, Object> getCache(String cache) {
    return caches.get()
        .computeIfAbsent(cache, k -> new HashMap<>());
  }

  public Object get(String cache, String key) {
    return getCache(cache).get(key);
  }

  public void endRequest() {
    caches.remove();
  }

  public void clearAll() {
    if (caches.get() != null) {
      caches.get().clear();
    }
  }

  public boolean isWorking() {
    return caches.get() != null;
  }
}
