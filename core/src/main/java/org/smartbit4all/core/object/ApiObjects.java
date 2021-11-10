package org.smartbit4all.core.object;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The APi object reflection utility.
 * 
 * @author Peter Boros
 */
final class ApiObjects {

  /**
   * The global cache of the api object for better editing.
   */
  private static final Cache<Class<?>, BeanMeta> cacheForAll = CacheBuilder.newBuilder().build();

  /**
   * The cache of the api object for better editing, stored separately for different qualifiers.
   */
  private static final Map<String, Cache<Class<?>, BeanMeta>> cacheByQualifier = new HashMap<>();

  /**
   * The api objects is a utility.
   */
  private ApiObjects() {
    super();
  }

  /**
   * The meta descriptor for the given api class.
   * 
   * @param apiClass
   * @param allDomainClasses The related classes are the other bean classes referred by the object
   *        hierarchy.
   * @return The BeanMeta descriptor.
   * @throws ExecutionException
   */
  public static final BeanMeta meta(Class<?> apiClass, Map<Class<?>, ApiBeanDescriptor> descriptors,
      String qualifier)
      throws ExecutionException {

    Cache<Class<?>, BeanMeta> cache;
    if (qualifier != null) {
      Cache<Class<?>, BeanMeta> cacheForQualifier = cacheByQualifier.get(qualifier);
      if (cacheForQualifier == null) {
        cacheForQualifier = CacheBuilder.newBuilder().build();
        cacheByQualifier.put(qualifier, cacheForQualifier);
      }
      cache = cacheForQualifier;
    } else {
      cache = cacheForAll;
    }
    return cache.get(apiClass, new Callable<BeanMeta>() {

      @Override
      public BeanMeta call() throws Exception {
        return BeanMetaUtil.meta(apiClass, descriptors);
      }
    });
  }

}
