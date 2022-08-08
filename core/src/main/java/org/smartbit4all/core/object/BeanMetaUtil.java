package org.smartbit4all.core.object;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.object.PropertyMeta.PropertyKind;
import org.smartbit4all.core.utility.ReflectionUtility;

/**
 * This utility can analyze the java Beans to discover the bean properties.
 * 
 * @author Peter Boros
 */
public final class BeanMetaUtil {

  public static final String GET = "get";
  public static final String IS = "is";
  public static final String SET = "set";
  public static final String ADDITEM_PREFIX = "add";
  public static final String ADDITEM_POSTFIX = "Item";
  public static final String PUTITEM_PREFIX = "put";
  public static final String PUTITEM_POSTFIX = "Item";

  private BeanMetaUtil() {}

  public static BeanMeta meta(Class<?> apiClass) {
    return meta(apiClass, null);
  }

  public static BeanMeta meta(Class<?> apiClass,
      Map<Class<?>, ApiBeanDescriptor> descriptors) {
    BeanMeta meta = new BeanMeta(apiClass);
    ApiBeanDescriptor descriptor = descriptors == null ? null : descriptors.get(apiClass);
    Set<Method> allMethods = ReflectionUtility.allMethods(apiClass, null);
    // process all getters => find all properties
    // e.g. for PropertyType property -> PropertyType getProperty();
    allMethods.stream()
        .filter(method -> Modifier.isPublic(method.getModifiers())
            && isGetterMethodName(method.getName())
            && method.getParameterCount() == 0
            && method.getReturnType() != null
            && !Void.TYPE.equals(method.getReturnType())
            && !method.getName().equals("getClass"))
        .forEach(getter -> processGetterMethod(apiClass, meta, getter, descriptor));

    // process all explicit setter
    // e.g. Type property; -> void setProperty(Type property);
    allMethods.stream()
        .filter(method -> Modifier.isPublic(method.getModifiers())
            && method.getName().startsWith(SET)
            && method.getName().length() > 3
            && Character.isUpperCase(method.getName().charAt(3))
            && method.getParameterCount() == 1
            && Void.TYPE.equals(method.getReturnType()))
        .forEach(setter -> processSetterMethod(setter, meta));

    // process all fluid setters
    // e.g. Type property in Bean class -> Bean property(Type value);
    allMethods.stream()
        .filter(method -> Modifier.isPublic(method.getModifiers())
            && method.getParameterCount() == 1
            && apiClass.equals(method.getReturnType()))
        .forEach(setter -> processFluidSetterMethod(setter, meta));

    // process all list adders: Bean addPropertiesItem(Collect)
    // e.g. List<Type> values in Bean class -> Bean addValuesItem(Type valuesItem);
    allMethods.stream()
        .filter(method -> Modifier.isPublic(method.getModifiers())
            && method.getName().startsWith(ADDITEM_PREFIX)
            && method.getName().length() > (ADDITEM_PREFIX.length() + ADDITEM_POSTFIX.length())
            && Character.isUpperCase(method.getName().charAt(ADDITEM_PREFIX.length()))
            && method.getName().endsWith(ADDITEM_POSTFIX)
            && method.getParameterCount() == 1
            && apiClass.equals(method.getReturnType()))
        .forEach(setter -> processItemAdderMethod(setter, meta));

    // process all map put item methods: Bean putPropertiesItem(string key, item)
    // e.g. Map<String, Type> values in Bean class -> Bean putValuesItem(String key, Type
    // valuesItem);
    allMethods.stream()
        .filter(method -> Modifier.isPublic(method.getModifiers())
            && method.getName().startsWith(PUTITEM_PREFIX)
            && method.getName().length() > (PUTITEM_PREFIX.length() + PUTITEM_POSTFIX.length())
            && Character.isUpperCase(method.getName().charAt(PUTITEM_PREFIX.length()))
            && method.getName().endsWith(PUTITEM_POSTFIX)
            && method.getParameterCount() == 2
            && apiClass.equals(method.getReturnType()))
        .forEach(setter -> processItemPutMethod(setter, meta));

    return meta;
  }

  private static boolean isGetterMethodName(String name) {
    boolean isGetter = name.startsWith(GET)
        && name.length() > 3
        && Character.isUpperCase(name.charAt(3));
    if (!isGetter) {
      isGetter = name.startsWith(IS)
          && name.length() > 2
          && Character.isUpperCase(name.charAt(2));
    }
    return isGetter;
  }

  private static final void processGetterMethod(Class<?> apiClass, BeanMeta meta, Method method,
      ApiBeanDescriptor descriptor) {
    // create property
    String propertyName;
    if (method.getName().startsWith(GET)) {
      // e.g. getValid for valid property
      propertyName = method.getName().substring(3);
    } else if (method.getName().startsWith(IS)) {
      // e.g. isValid for valid property
      propertyName = method.getName().substring(2);
    } else {
      throw new IllegalArgumentException(
          "Unknown getter method " + apiClass.getName() + "." + method.getName());
    }
    propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
    String propertyKey = propertyName.toUpperCase();
    Class<?> propertyType = method.getReturnType();
    PropertyMeta propertyMeta = meta.getProperties().get(propertyKey);
    if (propertyMeta == null) {
      propertyMeta = new PropertyMeta(propertyName, propertyType, meta);
      meta.getProperties().put(propertyKey, propertyMeta);
    } else {
      // possible for example with getName() and getNAME()
      throw new IllegalArgumentException(
          "Duplicate property name " + apiClass.getName() + "." + propertyName + "!");
    }
    propertyMeta.setGetter(method);

    // set property kind
    // We can detect the references and the related collections by the allClasses set. If we have a
    // property with the type of a bean class.
    if (descriptor != null && descriptor.getAllApiBeanClass().contains(propertyType)) {
      // In this case it's a direct reference to another api object.
      propertyMeta.setKind(PropertyKind.REFERENCE);
    }
    // We identifies the collection as list of references. In any other cases the list is just a
    // value that can be set as a list of value at once.
    if (propertyType.isAssignableFrom(List.class)) {
      // If we don't define the detail meta then try to identify the field for the bean property.
      Class<?> genericType = lookupFieldGenericType(apiClass, propertyName, List.class);
      if (genericType != null
          && (descriptor != null && descriptor.getAllApiBeanClass().contains(genericType))) {
        propertyMeta.setKind(PropertyKind.COLLECTION);
      }
    }
    if (propertyType.isAssignableFrom(Map.class)) {
      // If we don't define the detail meta then try to identify the field for the bean property.
      Class<?> genericType = lookupFieldGenericType(apiClass, propertyName, Map.class);
      if (genericType != null
          && (descriptor != null && descriptor.getAllApiBeanClass().contains(genericType))) {
        propertyMeta.setKind(PropertyKind.MAP);
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
    String propertyKey = name
        .substring(ADDITEM_PREFIX.length(), name.length() - ADDITEM_POSTFIX.length()).toUpperCase();
    PropertyMeta propertyMeta = meta.getProperties().get(propertyKey);
    if (propertyMeta != null) {
      // method without getter won't be processed
      propertyMeta.setItemAdder(method);
    }
  }

  private static final void processItemPutMethod(Method method, BeanMeta meta) {
    String name = method.getName();
    String propertyKey = name
        .substring(PUTITEM_PREFIX.length(), name.length() - PUTITEM_POSTFIX.length()).toUpperCase();
    PropertyMeta propertyMeta = meta.getProperties().get(propertyKey);
    if (propertyMeta != null) {
      // method without getter won't be processed
      propertyMeta.setItemPutter(method);
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
          if (actualTypeArguments != null) {
            if (type.isAssignableFrom(List.class)) {
              if (actualTypeArguments.length >= 1) {
                Type typeArg = actualTypeArguments[0];
                if (typeArg instanceof Class<?>) {
                  return (Class<?>) typeArg;
                }
              }
            } else if (type.isAssignableFrom(Map.class) && actualTypeArguments.length >= 2) {
              Type typeArg = actualTypeArguments[1];
              if (typeArg instanceof Class<?>) {
                return (Class<?>) typeArg;
              }
            }
          }
        }
      }
    }
    return null;
  }


}
