package org.smartbit4all.domain.data.storage;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.smartbit4all.core.object.BeanMeta;
import org.smartbit4all.core.object.BeanMetaUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This utility is responsible for caching the storage api objects with reflection.
 * 
 * @author Peter Boros
 */
final class StorageApiUtils {

  /**
   * The cache of the object meta.
   */
  private static final Cache<Class<?>, BeanMeta> cache = CacheBuilder.newBuilder().build();

  private StorageApiUtils() {
    super();
  }

  public static final BeanMeta meta(Class<?> apiClass) {
    if (apiClass == null) {
      return null;
    }
    try {
      return cache.get(apiClass, new Callable<BeanMeta>() {

        @Override
        public BeanMeta call() throws Exception {
          return BeanMetaUtil.meta(apiClass);
        }

      });
    } catch (ExecutionException e) {
      throw new IllegalArgumentException("Unable to analyze the " + apiClass + " bean.", e);
    }
  }

}
