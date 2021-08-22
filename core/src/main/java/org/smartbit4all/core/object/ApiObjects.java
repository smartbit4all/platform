package org.smartbit4all.core.object;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import org.smartbit4all.core.object.PropertyMeta.PropertyKind;
import org.smartbit4all.core.utility.ReflectionUtility;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * The APi object reflection utility.
 * 
 * @author Peter Boros
 */
class ApiObjects {

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
        BeanMeta meta = new BeanMeta(apiClass);
        ApiBeanDescriptor descriptor = descriptors.get(apiClass);
        Set<Method> allMethods = ReflectionUtility.allMethods(apiClass, null);
        // process all getters == find all properties
        allMethods.stream()
            .filter(method -> Modifier.isPublic(method.getModifiers())
                && method.getName().startsWith(GET)
                && method.getParameterCount() == 0
                && method.getReturnType() != null
                && !Void.TYPE.equals(method.getReturnType())
                && !method.getName().equals("getClass"))
            .forEach(getter -> processGetterMethod(apiClass, meta, getter, descriptor));

        // process all explicit setter
        allMethods.stream()
            .filter(method -> Modifier.isPublic(method.getModifiers())
                && method.getName().startsWith(SET)
                && method.getParameterCount() == 1
                && Void.TYPE.equals(method.getReturnType()))
            .forEach(setter -> processSetterMethod(setter, meta));

        // process all fluid setters
        allMethods.stream()
            .filter(method -> Modifier.isPublic(method.getModifiers())
                && method.getParameterCount() == 1
                && apiClass.equals(method.getReturnType()))
            .forEach(setter -> processFluidSetterMethod(setter, meta));

        // process all list adders
        allMethods.stream()
            .filter(method -> Modifier.isPublic(method.getModifiers())
                && method.getName().startsWith("add")
                && method.getName().endsWith("Item")
                && method.getParameterCount() == 1
                && apiClass.equals(method.getReturnType()))
            .forEach(setter -> processItemAdderMethod(setter, meta));

        return meta;
      }

    });
  }

  private static final void processGetterMethod(Class<?> apiClass, BeanMeta meta, Method method,
      ApiBeanDescriptor descriptor) {
    // create property
    String propertyName = method.getName().substring(3);
    String propertyKey = propertyName.toUpperCase();
    Class<?> propertyType = method.getReturnType();
    PropertyMeta propertyMeta = meta.getProperties().get(propertyKey);
    if (propertyMeta == null) {
      propertyMeta = new PropertyMeta(propertyName, propertyType, meta);
      meta.getProperties().put(propertyKey, propertyMeta);
    } else {
      // possible for example with getName() and getNAME()
      throw new RuntimeException(
          "Duplicate property name " + apiClass.getName() + "." + propertyName + "!");
    }
    propertyMeta.setGetter(method);

    // set property kind
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
      } else {
        // If we don't define the detail meta then try to identify the field for the bean property.
        Class<?> genericType = lookupFieldGenericType(apiClass, propertyName, List.class);
        if (genericType != null && descriptor.getAllApiBeanClass().contains(genericType)) {
          propertyMeta.setKind(PropertyKind.COLLECTION);
        }
      }
    }
  }

  private static final void processSetterMethod(Method method, BeanMeta meta) {
    String propertyKey = method.getName().substring(3).toUpperCase();
    PropertyMeta propertyMeta = meta.getProperties().get(propertyKey);
    if (propertyMeta != null) {
      // method without getter won't be processed
      propertyMeta.setSetter(method);
    }
  }

  private static final void processFluidSetterMethod(Method method, BeanMeta meta) {
    String propertyKey = method.getName().toUpperCase();
    PropertyMeta propertyMeta = meta.getProperties().get(propertyKey);
    if (propertyMeta != null) {
      // method without getter won't be processed
      propertyMeta.setFluidSetter(method);
    }
  }

  private static final void processItemAdderMethod(Method method, BeanMeta meta) {
    String name = method.getName();
    String propertyKey = name.substring(3, name.length() - 4).toUpperCase();
    PropertyMeta propertyMeta = meta.getProperties().get(propertyKey);
    if (propertyMeta != null) {
      // method without getter won't be processed
      propertyMeta.setItemAdder(method);
    }
  }

  private static final Class<?> lookupFieldGenericType(Class<?> apiClass, String propertyName,
      Class<?> type) {
    Field[] declaredFields = apiClass.getDeclaredFields();
    for (int i = 0; i < declaredFields.length; i++) {
      Field field = declaredFields[i];
      if (propertyName.equalsIgnoreCase(field.getName())
          && field.getType().isAssignableFrom(type)) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
          Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
          if (actualTypeArguments != null && actualTypeArguments.length > 0) {
            Type typeArg = actualTypeArguments[0];
            if (typeArg instanceof Class<?>) {
              return (Class<?>) typeArg;
            }
          }
        }
      }
    }
    return null;
  }

}
