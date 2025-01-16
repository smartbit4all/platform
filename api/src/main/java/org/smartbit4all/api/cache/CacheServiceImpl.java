package org.smartbit4all.api.cache;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class CacheServiceImpl implements CacheService {

  private static final Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);

  private RequestScopedCache requestScopedCache = new RequestScopedCache();

  @Override
  public Map<String, Object> getRequestScopedCache() {
    return getRequestScopedCache(getDefaultCacheName());
  }

  @Override
  public Map<String, Object> getRequestScopedCache(String cache) {
    if (StringUtils.isEmpty(cache)) {
      cache = getDefaultCacheName();
    }
    if (requestScopedCache.isWorking()) {
      return requestScopedCache.getCache(cache);
    }
    log.warn("Requestion cache for {} when it is not yet initialized", cache);
    return new HashMap<>();
  }

  @Override
  public void startRequestScope() {
    // TODO handle a stack of scopes, handle multiple start/end pairs
    requestScopedCache.startRequest();
  }

  @Override
  public void endRequestScope() {
    requestScopedCache.endRequest();
  }

  protected String getDefaultCacheName() {
    return CacheService.class.getName();
  }

  @Override
  public void clearRequestScopedCache(String cache) {
    getRequestScopedCache(cache).clear();
  }

  @Override
  public void clearAllRequestScopedCaches() {
    requestScopedCache.clearAll();
  }
}
