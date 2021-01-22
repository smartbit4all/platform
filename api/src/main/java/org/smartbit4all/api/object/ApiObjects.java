package org.smartbit4all.api.object;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.smartbit4all.core.utility.ReflectionUtility;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ApiObjects {

  static final String SET = "set";
  static final String GET = "get";
  /**
   * The cache of the api object for better editing.
   */
  private static final Cache<Class<?>, BeanMeta> cache = CacheBuilder.newBuilder().build();

  /**
   * The api objects is a utility.
   */
  private ApiObjects() {
    super();
  }

  public static final BeanMeta meta(Class<?> apiClass) throws ExecutionException {
    return cache.get(apiClass, new Callable<BeanMeta>() {

      @Override
      public BeanMeta call() throws Exception {
        Set<Method> methods = ReflectionUtility.allMethods(apiClass, m -> {
          // We assume the methods with one parameter and void return value or the return value
          // without any parameter.
          return Modifier.isPublic(m.getModifiers()) && m.getParameterCount() < 2;
        });
        BeanMeta meta = new BeanMeta(apiClass);
        for (Method method : methods) {
          // We assume only the get / set methods.
          if (method.getName().startsWith(GET) || method.getName().startsWith(SET)) {
            processMethod(meta, method);
          }
        }
        return meta;
      }

    });
  }

  private static final void processMethod(BeanMeta meta, Method method) {
    String propertyName;
    propertyName = method.getName().substring(3);
    String propertyKey = propertyName.toUpperCase();
    Class<?> propertyType;
    boolean isGetter;
    if (method.getParameterCount() == 0) {
      propertyType = method.getReturnType();
      isGetter = true;
    } else {
      propertyType = method.getParameterTypes()[0];
      isGetter = false;
    }
    PropertyMeta propertyMeta = meta.getProperties().get(propertyKey);
    if (propertyMeta == null) {
      propertyMeta = new PropertyMeta(propertyName, propertyType);
      meta.getProperties().put(propertyKey, propertyMeta);
    }
    if (isGetter) {
      propertyMeta.setGetter(method);
    } else {
      propertyMeta.setSetter(method);
    }
  }
}
