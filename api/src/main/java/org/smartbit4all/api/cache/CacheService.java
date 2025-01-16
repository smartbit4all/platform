package org.smartbit4all.api.cache;

import java.util.Map;

public interface CacheService {

  /**
   * Returns default request scoped cache (Map).
   *
   * @return
   */
  Map<String, Object> getRequestScopedCache();

  /**
   * Returns named request scoped cache (Map). If cache parameter is null, returns default cache
   * instead.
   *
   * @param cache
   * @return
   */
  Map<String, Object> getRequestScopedCache(String cache);

  void startRequestScope();

  void endRequestScope();

  void clearRequestScopedCache(String cache);

  void clearAllRequestScopedCaches();

}
