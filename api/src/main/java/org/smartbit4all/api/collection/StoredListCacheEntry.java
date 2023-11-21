package org.smartbit4all.api.collection;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.smartbit4all.core.object.ObjectNode;

class StoredListCacheEntry {

  /**
   * The last refreshment time of the cache.
   */
  long lastCacheRefreshmentTime = System.currentTimeMillis();

  /**
   * The cached nodes in atomic reference to avoid duoble loading at a given time.
   */
  AtomicReference<List<ObjectNode>> cacheRef = new AtomicReference<>();

}
