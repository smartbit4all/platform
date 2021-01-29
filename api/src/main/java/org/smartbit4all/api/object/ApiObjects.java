package org.smartbit4all.api.object;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.smartbit4all.api.object.PropertyMeta.PropertyKind;
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

  /**
   * The meta descriptor for the given api class.
   * 
   * @param apiClass
   * @param allDomainClasses The related classes are the other bean classes referred by the object
   *        hierarchy.
   * @return The BeanMeta descriptor.
   * @throws ExecutionException
   */
  public static final BeanMeta meta(Class<?> apiClass, Map<Class<?>, ApiBeanDescriptor> descriptors)
      throws ExecutionException {
    return cache.get(apiClass, new Callable<BeanMeta>() {

      @Override
      public BeanMeta call() throws Exception {
        Set<Method> methods = ReflectionUtility.allMethods(apiClass, m -> {
          // We assume the methods with one parameter and void return value or the return value
          // without any parameter.
          return Modifier.isPublic(m.getModifiers()) && m.getParameterCount() < 2
              && !m.getName().equals("getClass");
        });
        BeanMeta meta = new BeanMeta(apiClass);
        for (Method method : methods) {
          // We assume only the get / set methods.
          if (method.getName().startsWith(GET) || method.getName().startsWith(SET)) {
            processMethod(meta, method, descriptors.get(apiClass));
          }
        }
        // Clean up the invalid properties
        List<String> invalidPropertyNames =
            meta.getProperties().values().stream().filter(p -> p.getGetter() == null)
                .map(PropertyMeta::getName).collect(Collectors.toList());
        invalidPropertyNames.forEach(meta.getProperties()::remove);
        return meta;
      }

    });
  }

  private static final void processMethod(BeanMeta meta, Method method,
      ApiBeanDescriptor descriptor) {
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
      propertyMeta = new PropertyMeta(propertyName, propertyType, meta);
      meta.getProperties().put(propertyKey, propertyMeta);
    }
    if (isGetter) {
      propertyMeta.setGetter(method);
    } else {
      propertyMeta.setSetter(method);
    }
    // We can detect the references and the related collections by the allClasses set. If we have a
    // property with the type of a bean class.
    if (descriptor.getAllApiBeanClass().contains(propertyType)) {
      // In this case it's a direct reference to another api object.
      propertyMeta.setKind(PropertyKind.REFERENCE);
    }
    // We identifies the collection as list of references. In any other cases the list is just a
    // value that can be set as a list of value at once.
    if (propertyType.isAssignableFrom(List.class)) {
      ApiBeanDescriptor apiBeanDescriptor =
          descriptor.getDetailDescriptors().get(propertyMeta.getName());
      // If have a list of api object then set collection as kind.
      if (apiBeanDescriptor != null) {
        propertyMeta.setKind(PropertyKind.COLLECTION);
      }
    }
  }

}
