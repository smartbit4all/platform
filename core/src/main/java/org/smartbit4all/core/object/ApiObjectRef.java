/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core.object;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.smartbit4all.core.object.ApiObjectCollection.CollectionChanges;
import org.smartbit4all.core.object.PropertyMeta.PropertyKind;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.Factory;
import com.google.common.base.Strings;

/**
 * The API object reference is a pair for the object participate in an object hierarchy. It refers
 * to the object itself, contains the unique identifier of the object. This identifies the given
 * occurrence not the object itself! The bare reference is not typed. The types version is an
 * extension.
 * 
 * The API object has a naming that ensure the uniqueness of the api object elements inside the
 * {@link ApiObjectRef} hierarchy.
 * 
 * The root has an empty path. If we would like to name its properties then they its name is
 * /myProperty. If it's a reference then we can continue the path with the properties of the
 * referenced bean like /myReference/refProperty and so on.
 * 
 * In case of collection the naming contains the ordinal number also. But the ApiObjectRef of the
 * collection aren't contain their parent in their path. Because changing the order of the list it
 * could lead to a changing the identifiers. So it would be hard to identify the subscriptions for
 * the given object. Therefore we can subscribe to the changes of the collection and independently
 * we can subscribe to the changes of the given item inside. If we change the object itself then
 * it's not the change of the collection. But if we modify the collection itself then we can manage
 * it at higher level. At higher level we can go through the inner object changes if we need. So we
 * can manage the collections as a complex change also.
 * 
 * @author Peter Boros
 */
public class ApiObjectRef {

  /**
   * The path identifies the object reference inside the namespace. The namespace is an object
   * hierarchy starting from an object and recursively contains the references path. Using this we
   * can uniquely name the properties. In case of a collection the collection itself is also a
   * property of the master hierarchy but every item of the collection starts a new hierarchy with
   * the same naming. From master level the objects of a collection can be accessed by knowing it's
   * ordinal number. But it is not necessary!
   */
  private final String path;

  /**
   * The reference of the original object.
   */
  private Object object;

  /**
   * The meta structure of the given bean.
   */
  private BeanMeta meta;

  /**
   * The properties of the given api object instance. The property name as a key is upper case so we
   * can search in case insensitive mode.
   */
  final Map<String, PropertyEntry> properties = new HashMap<>();

  /**
   * The list of the properties in alphabetic order.
   */
  List<PropertyEntry> propertyList = null;

  /**
   * The property entries can be accessed in an optimized way by the method reference.
   */
  final Map<Method, PropertyEntry> propertiesByMethod = new HashMap<>();

  /**
   * The classes of the domain beans. We need them to be able to identify the Api objects.
   */
  private ApiBeanDescriptor descriptor;

  /**
   * The descriptors for the domain includes the given bean.
   */
  private final Map<Class<?>, ApiBeanDescriptor> descriptors;

  private String qualifier;

  /**
   * The api object current state. The default is the new.
   */
  private ChangeState currentState = ChangeState.NEW;

  /**
   * The wrapper is the single instance of {@link Enhancer} proxy that was created for this
   * reference.
   */
  private Object wrapper = null;

  private Class<? extends Object> objectClass;

  public ApiObjectRef(String path, Object object, Map<Class<?>, ApiBeanDescriptor> descriptors) {
    this(path, object, descriptors, null);
  }

  /**
   * Constructs a new api object reference.
   *
   * @param path The path for the current ApiObjectRef instance. The root is an empty path.
   * @param object The api object managed by the reference.
   * @param allBeanClasses All the bean classes we have in this api domain.
   */
  public ApiObjectRef(String path, Object object, Map<Class<?>, ApiBeanDescriptor> descriptors,
      String qualifier) {
    super();
    this.descriptors = descriptors;
    if (object == null) {
      throw new IllegalArgumentException("The object must be set in an ApiObjectRef");
    }
    this.path = (path == null ? StringConstant.EMPTY : path);
    initMeta(object, descriptors, qualifier);
    initPropertyEntries(path);
    setObjectInternal(object, ChangeState.NEW);
  }

  private void initMeta(Object object, Map<Class<?>, ApiBeanDescriptor> descriptors,
      String qualifier) {
    Class<?> objectClass = object.getClass();
    if (object instanceof Factory) {
      Callback callback = ((Factory) object).getCallback(0);
      if (callback instanceof ApiObjectRefInvocationHandler) {
        ApiObjectRef otherRef = ((ApiObjectRefInvocationHandler) callback).getRef();
        this.meta = otherRef.getMeta();
        this.descriptor = otherRef.getDescriptor();
        this.objectClass = otherRef.getObjectClass();
      } else {
        throw new IllegalArgumentException(
            "The object can't be processed as bean " + objectClass);
      }
    } else {
      try {
        // if (Strings.isNullOrEmpty(qualifier)) {
        // this.qualifier = objectClass.getName();
        // } else {
        // this.qualifier = qualifier + "-" + objectClass.getName();
        // }
        this.qualifier = qualifier;
        this.meta = ApiObjects.meta(objectClass, descriptors, this.qualifier);
        this.descriptor = descriptors.get(objectClass);
        this.objectClass = objectClass;
      } catch (ExecutionException e) {
        throw new IllegalArgumentException(
            "The object can't be processed as bean " + objectClass, e);
      }
    }
  }

  /**
   * Initiates property entries and handler methods.
   */
  private final void initPropertyEntries(String path) {
    for (PropertyMeta meta : meta.getProperties().values()) {
      PropertyEntry entry = new PropertyEntry(path, meta);
      properties.put(meta.getName().toUpperCase(), entry);
      addMethodToEntry(meta.getGetter(), entry);
      addMethodToEntry(meta.getSetter(), entry);
      addMethodToEntry(meta.getFluidSetter(), entry);
      addMethodToEntry(meta.getItemAdder(), entry);
      addMethodToEntry(meta.getItemPutter(), entry);
      if (meta.getKind() == PropertyKind.COLLECTION) {
        ApiObjectCollection collection = new ApiObjectCollection(this, meta);
        entry.setCollection(collection);
      } else if (meta.getKind() == PropertyKind.MAP) {
        ApiObjectMap map = new ApiObjectMap(this, meta);
        entry.setMap(map);
      }
    }
  }

  /**
   * Replaces original object with a new one. After this call renderAndCleanChanges will contain all
   * differences between the old and the new object (if there were any changes before this call,
   * those will also be there).
   * 
   * @param object
   */
  public void setObject(Object object) {
    setObjectInternal(object, ChangeState.MODIFIED);
  }

  private void setObjectInternal(Object object, ChangeState state) {
    Object unwrappedObject = checkObjectCompatibility(object);
    processNewValues(unwrappedObject, false);
    this.object = unwrappedObject;
    setCurrentState(state);
  }

  /**
   * Merges parameter object's values into current object. All changes will be recorded.
   * 
   * @param object
   */
  public void mergeObject(Object object) {
    Object unwrappedObject = checkObjectCompatibility(object);
    processNewValues(unwrappedObject, true);
    setCurrentState(ChangeState.MODIFIED);
  }

  private Object checkObjectCompatibility(Object object) {
    if (object == null) {
      throw new IllegalArgumentException("The object must be set in an ApiObjectRef");
    }
    Object unwrappedObject = unwrapObject(object);
    Class<?> unwrappedObjectClass = unwrappedObject.getClass();
    // TODO equals vs inheritance?
    if (!objectClass.equals(unwrappedObjectClass)) {
      throw new IllegalArgumentException(
          "The object must be of class " + this.object.getClass().getName());
    }
    return unwrappedObject;
  }

  private void processNewValues(Object unwrappedObject, boolean setObjectValue) {
    for (PropertyEntry entry : properties.values()) {
      switch (entry.getMeta().getKind()) {
        case VALUE:
          Object oldValue;
          if (object == null) {
            oldValue = null;
          } else {
            if (entry.getValueChange() != null) {
              oldValue = entry.getValueChange().getOldValue();
            } else {
              oldValue = entry.getMeta().getValue(object);
            }
          }
          Object newValue = entry.getMeta().getValue(unwrappedObject);
          if (!Objects.equals(oldValue, newValue)) { // TODO maybe oldValue != newValue?
            setValueInner(newValue, entry, setObjectValue);
          }
          break;
        case REFERENCE:
        case COLLECTION:
          // setValueInner all the time, ApiObjectRef / ApiObjectCollection should handle value
          // comparisons recursively
          Object newCollection = entry.getMeta().getValue(unwrappedObject);
          setValueInner(newCollection, entry, setObjectValue);
          break;
        case MAP:
          // setValueInner all the time, ApiObjectRef / ApiObjectCollection should handle value
          // comparisons recursively
          Object newMap = entry.getMeta().getValue(unwrappedObject);
          setValueInner(newMap, entry, setObjectValue);
          break;
        default:
          break;
      }
    }
  }

  private void addMethodToEntry(Method method, PropertyEntry entry) {
    if (method != null) {
      propertiesByMethod.put(method, entry);
    }
  }

  public String getPath() {
    return path;
  }

  public final Object getObject() {
    return object;
  }

  public Class<? extends Object> getObjectClass() {
    return objectClass;
  }

  /**
   * The setValue is the generic value modification function on the {@link ApiObjectRef} interface.
   * If we set a simple value then the subscribers will be notified with {@link PropertyChange}.
   * 
   * If we set a reference as a value then it's must be implemented as a replacement of the current
   * reference.
   * 
   * If we set a collections as an object then we implement this as a compare and apply on the
   * current collection.
   * 
   * @param propertyName
   * @param value
   */
  public void setValue(String propertyName, Object value) {
    PropertyEntry entry = getPropertyEntryByName(propertyName);
    setValueInner(value, entry, true);
  }

  final void setValueInner(Object value, PropertyEntry entry, boolean setObjectValue) {
    String propertyName = entry.getMeta().getName();
    try {
      switch (entry.getMeta().getKind()) {
        case VALUE:
          entry.setChangedValue(object == null ? null : entry.getMeta().getValue(object), value);
          break;

        case REFERENCE:
          if (value != null) {
            if (entry.getReference() == null) {
              ApiObjectRef newRef =
                  new ApiObjectRef(entry.getPath(), value, descriptors, qualifier);
              entry.setReference(newRef);
            } else {
              entry.getReference().setObject(value);
            }
          } else {
            if (entry.getReference() != null) {
              entry.getReference().setCurrentState(ChangeState.DELETED);
            }
          }
          break;

        case COLLECTION:
          entry.getCollection().setOriginalCollection((Collection<Object>) value);
          break;

        case MAP:
          entry.getMap().setOriginalMap((Map<String, Object>) value);
          break;

        default:
          break;
      }
      if (setObjectValue) {
        entry.getMeta().setValue(object, value);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          propertyName + " property is not set to " + value + " in " + meta.getClazz().getName(),
          e);
    }
  }

  public void setValueByPath(String path, Object value) {
    // TODO
    path = path.toUpperCase();

    String propertyName = PathUtility.getRootPath(path);
    PropertyEntry propertyEntry = getPropertyEntryByName(propertyName);

    switch (propertyEntry.getMeta().getKind()) {
      case VALUE:
        setValueInner(value, propertyEntry, true);
        break;
      case REFERENCE:
        // Call the setValueByPath on reference with new path
        if (PathUtility.getPathSize(path) == 1) {
          setValueInner(value, propertyEntry, true);
        } else {
          propertyEntry.getReference().setValueByPath(PathUtility.nextFullPath(path), value);
        }
        break;
      case COLLECTION:
        ApiObjectCollection collection = propertyEntry.getCollection();
        if (PathUtility.getPathSize(path) == 1) {
          setValueInner(value, propertyEntry, true);
        } else if (PathUtility.getPathSize(path) == 2) {
          // Set the collection element to the value
          String newPath = PathUtility.nextFullPath(path);
          String collectionId = PathUtility.getRootPath(newPath);
          collection.setObject(Integer.valueOf(collectionId), value);
        } else {
          // call the setValueByPath on the collection element
          String nextPath = PathUtility.nextFullPath(path);
          String collectionId = PathUtility.getRootPath(nextPath);
          ApiObjectRef nextRef = collection.getByIdx(collectionId);
          nextRef.setValueByPath(PathUtility.nextFullPath(nextPath), value);
        }
        break;
      default:
        break;
    }
  }

  public ApiObjectRef addValueByPath(String path, Object value) {
    // TODO
    path = path.toUpperCase();
    ApiObjectRef addedRef = null;
    String propertyName = PathUtility.getRootPath(path);
    PropertyEntry propertyEntry = getPropertyEntryByName(propertyName);

    int pathSize = PathUtility.getPathSize(path);
    switch (propertyEntry.getMeta().getKind()) {
      case VALUE:
        // TODO exception?
        break;
      case REFERENCE:
        // Call the addValueByPath on reference with new path
        addedRef =
            propertyEntry.getReference().addValueByPath(PathUtility.nextFullPath(path), value);
        break;
      case COLLECTION:
        ApiObjectCollection collection = propertyEntry.getCollection();
        if (pathSize == 1) {
          // Add the value to the collection
          addedRef = collection.addObject(value);
        } else {
          // call the setValueByPath on the collection element
          String nextPath = PathUtility.nextFullPath(path);
          ApiObjectRef nextRef = collection.getByIdx(PathUtility.getRootPath(nextPath));
          addedRef = nextRef.addValueByPath(PathUtility.nextFullPath(nextPath), value);
        }
        break;
      case MAP:
        ApiObjectMap map = propertyEntry.getMap();
        if (pathSize == 1) {
          // Add the value to the collection
          addedRef = map.putObject(path, value);
        } else {
          // call the setValueByPath on the collection element
          String nextPath = PathUtility.nextFullPath(path);
          ApiObjectRef nextRef = map.get(PathUtility.getRootPath(nextPath));
          addedRef = nextRef.addValueByPath(PathUtility.nextFullPath(nextPath), value);
        }
        break;
      default:
        break;
    }
    return addedRef;
  }

  public ApiObjectRef getValueRefByPath(String path) {
    if (Strings.isNullOrEmpty(path)) {
      return this;
    }
    // TODO
    path = path.toUpperCase();

    String propertyName = PathUtility.getRootPath(path);
    PropertyEntry propertyEntry = getPropertyEntryByName(propertyName);

    int pathSize = PathUtility.getPathSize(path);
    switch (propertyEntry.getMeta().getKind()) {
      case VALUE:
        return this;
      case REFERENCE:
        if (pathSize == 1) {
          return propertyEntry.getReference();
        } else {
          ApiObjectRef reference = propertyEntry.getReference();
          if (reference == null) {
            return null;
          }
          return reference.getValueRefByPath(PathUtility.nextFullPath(path));
        }
      case COLLECTION:
        ApiObjectCollection collection = propertyEntry.getCollection();
        if (pathSize == 2) {
          return collection.getByIdx(PathUtility.getLastPath(path));
        } else {
          String nextPath = PathUtility.nextFullPath(path);
          ApiObjectRef nextRef = collection.getByIdx(PathUtility.getRootPath(nextPath));
          if (nextRef == null) {
            return null;
          }
          return nextRef.getValueRefByPath(PathUtility.nextFullPath(nextPath));
        }
      case MAP:
        ApiObjectMap map = propertyEntry.getMap();
        if (pathSize == 2) {
          return map.get(PathUtility.getLastPath(path));
        } else {
          String nextPath = PathUtility.nextFullPath(path);
          ApiObjectRef nextRef = map.get(PathUtility.getRootPath(nextPath));
          if (nextRef == null) {
            return null;
          }
          return nextRef.getValueRefByPath(PathUtility.nextFullPath(nextPath));
        }
      default:
        break;
    }
    return null;
  }

  public void removeValueByPath(String path) {
    // TODO
    path = path.toUpperCase();

    String propertyName = PathUtility.getRootPath(path);
    PropertyEntry propertyEntry = getPropertyEntryByName(propertyName);

    int pathSize = PathUtility.getPathSize(path);
    switch (propertyEntry.getMeta().getKind()) {
      case VALUE:
        // TODO exception
        break;
      case REFERENCE:
        // Call the removeValueByPath on reference with new path
        propertyEntry.getReference().removeValueByPath(PathUtility.nextFullPath(path));
        break;
      case COLLECTION:
        ApiObjectCollection collection = propertyEntry.getCollection();
        if (pathSize == 2) {
          // Remove the index to the collection
          collection.removeByIdx(PathUtility.getLastPath(path));
        } else {
          // call the removeValueByPath on the collection element
          String nextPath = PathUtility.nextFullPath(path);
          ApiObjectRef nextRef = collection.getByIdx(PathUtility.getRootPath(nextPath));
          nextRef.removeValueByPath(PathUtility.nextFullPath(nextPath));
        }
        break;
      case MAP:
        ApiObjectMap map = propertyEntry.getMap();
        if (pathSize == 2) {
          // Remove the index to the collection
          map.remove(PathUtility.getLastPath(path));
        } else {
          // call the removeValueByPath on the collection element
          String nextPath = PathUtility.nextFullPath(path);
          ApiObjectRef nextRef = map.get(PathUtility.getRootPath(nextPath));
          nextRef.removeValueByPath(PathUtility.nextFullPath(nextPath));
        }
        break;
      default:
        break;
    }
  }

  /**
   * In case of {@link PropertyKind#VALUE} we get the value itself. But if it's a
   * {@link PropertyKind#REFERENCE} or a {@link PropertyKind#COLLECTION} we get back the
   * {@link ApiObjectRef} or the {@link ApiObjectCollection}.
   * 
   * @param propertyName The name of the property.
   * @return
   */
  public Object getValue(String propertyName) {
    int slashIndex = propertyName.lastIndexOf(StringConstant.SLASH);
    if (slashIndex != -1) {
      if (slashIndex + 1 == propertyName.length()) {
        throw new IllegalArgumentException(
            "Invalid parameter " + propertyName);
      }
      String path = propertyName.substring(0, slashIndex);
      ApiObjectRef reference = getValueRefByPath(path);
      if (reference == null) {
        return null;
      }
      return reference.getValue(propertyName.substring(slashIndex + 1));
    }
    return getValueInner(getPropertyEntryByName(propertyName));
  }

  /**
   * Get the value of a given path. Works only with properties and references and ignore the
   * collections. Always return the value let it be a bean or even a collection.
   * 
   * @param propertyPath The name of the property.
   * @return
   */
  public Object getValueByPath(String propertyPath) {
    String upperPath = propertyPath.toUpperCase();

    String propertyName = PathUtility.getRootPath(upperPath);
    PropertyEntry propertyEntry = getPropertyEntryByName(propertyName);

    int pathSize = PathUtility.getPathSize(upperPath);
    switch (propertyEntry.getMeta().getKind()) {
      case VALUE:
        return propertyEntry.getMeta().getValue(object);
      case REFERENCE:
        // Call the getValueByPath on reference with new path

        ApiObjectRef reference = propertyEntry.getReference();
        if (reference == null) {
          return null;
        }
        return reference.getValueByPath(PathUtility.nextFullPath(upperPath));
      case COLLECTION:
        ApiObjectCollection collection = propertyEntry.getCollection();
        if (pathSize == 2) {
          return collection.getByIdx(PathUtility.getLastPath(upperPath)).getObject();
        } else {
          String nextPath = PathUtility.nextFullPath(upperPath);
          ApiObjectRef nextRef = collection.getByIdx(PathUtility.getRootPath(nextPath));
          return nextRef.getValueByPath(PathUtility.nextFullPath(nextPath));
        }
      case MAP:
        ApiObjectMap map = propertyEntry.getMap();
        if (pathSize == 2) {
          return map.get(PathUtility.getLastPath(upperPath)).getObject();
        } else {
          String nextPath = PathUtility.nextFullPath(upperPath);
          ApiObjectRef nextRef = map.get(PathUtility.getRootPath(nextPath));
          return nextRef.getValueByPath(PathUtility.nextFullPath(nextPath));
        }
      default:
        return null;
    }
  }

  final Object getValueInner(PropertyEntry propertyEntry) {
    switch (propertyEntry.getMeta().getKind()) {
      case VALUE:
        return propertyEntry.getMeta().getValue(object);
      case COLLECTION:
        return propertyEntry.getCollection();
      case MAP:
        return propertyEntry.getMap();
      case REFERENCE:
        return propertyEntry.getReference();
      default:
        break;
    }
    return null;
  }

  // @SuppressWarnings("unchecked")
  // public <O, T> void setValue(BiConsumer<O, T> setter, T value) {
  // try {
  // setter.accept(((O) object), value);
  // } catch (Exception e) {
  // throw new IllegalArgumentException(
  // setter.getClass() + " property is not set to " + value + " in "
  // + meta.getClazz().getName());
  // }
  // }


  public <T> T getValue(Supplier<T> getter) {
    return getter.get();
  }

  public final ChangeState getCurrentState() {
    return currentState;
  }

  final void setCurrentState(ChangeState currentState) {
    this.currentState = currentState;
  }

  final ApiBeanDescriptor getDescriptor() {
    return descriptor;
  }

  final Map<Class<?>, ApiBeanDescriptor> getDescriptors() {
    return descriptors;
  }

  final String getQualifier() {
    return qualifier;
  }

  /**
   * Constructs the changes by the current modification state and clear the modification states. The
   * {@link ApiObjectRef} is a specific wrapper around the api objects that provides a modification
   * api with collecting the changes. After a transaction we can get back changes. All the modified
   * root objects will produce one ObjectChange. If we have a simple referential hierarchy then we
   * will have one change item. But if we have collections then all the modified items will produce
   * one individual {@link ObjectChange}. The path of the root is empty but the path of the
   * collections will contains the path of the property that holds the collection itself. Plus a
   * uniquely generated identifier (a simple number) that can be used in the subscription mechanism
   * as unique identifier.
   * 
   * @return The {@link ObjectChange} in the order of hierarchy traversal. We will find first the
   *         changes of the context object and later we will find the item changes of the inner
   *         collections.
   */
  public final Optional<ObjectChange> renderAndCleanChanges() {
    ObjectChange result = null;
    if (getCurrentState() != null && getCurrentState() != ChangeState.NOP) {
      result = new ObjectChange(path, getCurrentState());
      setCurrentState(ChangeState.NOP);
    }
    for (PropertyEntry entry : properties.values()) {
      switch (entry.getMeta().getKind()) {
        case VALUE:
          if (entry.getValueChange() != null) {
            if (result == null) {
              result = new ObjectChange(path, ChangeState.MODIFIED);
            }
            result.getProperties().add(entry.getValueChange());
            entry.clearValueChange();
          }
          break;
        case COLLECTION:
          Optional<CollectionChanges> changes = entry.getCollection().renderAndCleanChanges();
          if (changes.isPresent()) {
            if (result == null) {
              result = new ObjectChange(path, ChangeState.MODIFIED);
            }
            result.getCollections().add(changes.get().collectionChanges);
            result.getCollectionObjects().add(changes.get().collectionObjectChanges);
          }
          break;
        case MAP:
          Optional<CollectionChanges> changesMap = entry.getMap().renderAndCleanChanges();
          if (changesMap.isPresent()) {
            if (result == null) {
              result = new ObjectChange(path, ChangeState.MODIFIED);
            }
            result.getCollections().add(changesMap.get().collectionChanges);
            result.getCollectionObjects().add(changesMap.get().collectionObjectChanges);
          }
          break;
        case REFERENCE:
          if (entry.getReference() != null) {
            ApiObjectRef ref = entry.getReference();
            Optional<ObjectChange> refChangeOpt = ref.renderAndCleanChanges();
            if (refChangeOpt.isPresent()) {
              ObjectChange refChange = refChangeOpt.get();
              if (result == null) {
                result = new ObjectChange(path, ChangeState.MODIFIED);
              }
              result.getReferences().add(
                  new ReferenceChange(path, entry.getMeta().getName(), refChange));

              ObjectChangeSimple objectChange =
                  new ObjectChangeSimple(ref.getPath(), refChange.getOperation(),
                      refChange.getOperation() == ChangeState.DELETED ? null : ref.getObject());
              result.getReferencedObjects()
                  .add(new ReferencedObjectChange(path, entry.getMeta().getName(), objectChange));

              if (refChange.getOperation() == ChangeState.DELETED) {
                entry.setReference(null);
              }
            }
          }
          // }
          break;

        default:
          break;
      }
    }
    return Optional.ofNullable(result);
  }

  /**
   * Use this instead of {@link #getWrapper(Class)}. It must be the same!
   * 
   * @return
   */
  public Object getWrapper() {
    return getWrapperInner(getMeta().getClazz());
  }

  @SuppressWarnings("unchecked")
  public <T> T getWrapper(Class<T> beanClass) {
    return getWrapperInner(beanClass);
  }

  private final <T> T getWrapperInner(Class<T> beanClass) {
    if (wrapper == null) {

      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(beanClass);
      enhancer.setUseFactory(true);
      enhancer.setCallback(new ApiObjectRefInvocationHandler(this));
      wrapper = enhancer.create();
    }

    return (T) wrapper;
  }

  public final BeanMeta getMeta() {
    return meta;
  }

  /**
   * The list of the properties in alphabetic order.
   * 
   * @return
   */
  public final List<PropertyEntry> getProperties() {
    if (propertyList == null) {
      propertyList = properties.values().stream()
          .sorted((p1, p2) -> p1.getMeta().getName().compareTo(p2.getMeta().getName()))
          .collect(Collectors.toList());
    }
    return propertyList;
  }

  final String getPropertyPath(PropertyMeta propertyMeta) {
    return (Strings.emptyToNull(getPath()) == null ? StringConstant.EMPTY
        : (getPath() + StringConstant.SLASH))
        + propertyMeta.getName();

  }

  public static <T> boolean isWrappedObject(T object) {
    Class<?> objectClass = object.getClass();
    if (object instanceof Factory) {
      Callback callback = ((Factory) object).getCallback(0);
      if (callback instanceof ApiObjectRefInvocationHandler) {
        return true;
      } else {
        throw new IllegalArgumentException(
            "The object can't be processed as bean " + objectClass);
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  public static <T> T unwrapObject(T object) {
    Class<?> objectClass = object.getClass();
    if (object instanceof Factory) {
      Callback callback = ((Factory) object).getCallback(0);
      if (callback instanceof ApiObjectRefInvocationHandler) {
        ApiObjectRef ref = ((ApiObjectRefInvocationHandler) callback).getRef();
        object = (T) ref.getObject();
      } else {
        throw new IllegalArgumentException(
            "The object can't be processed as bean " + objectClass);
      }
    }
    return object;
  }

  private PropertyEntry getPropertyEntryByName(String propertyName) {
    PropertyEntry entry = properties.get(propertyName.toUpperCase());
    if (entry == null) {
      throw new IllegalArgumentException(
          propertyName + " property not found in " + meta.getClazz().getName());
    }
    return entry;
  }
}
