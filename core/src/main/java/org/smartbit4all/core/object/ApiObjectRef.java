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
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.smartbit4all.core.object.ApiObjectCollection.CollectionChanges;
import org.smartbit4all.core.object.PropertyMeta.PropertyKind;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.InvocationHandler;
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
  private final Object object;

  /**
   * The meta structure of the given bean.
   */
  private final BeanMeta meta;

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
  private final ApiBeanDescriptor descriptor;

  /**
   * The descriptors for the domain includes the given bean.
   */
  private final Map<Class<?>, ApiBeanDescriptor> descriptors;

  /**
   * The api object current state. The default is the new.
   */
  private ChangeState currentState = ChangeState.NEW;

  /**
   * Constructs a new api object reference.
   *
   * @param path The path for the current ApiObjectRef instance. The root is an empty path.
   * @param object The api object managed by the reference.
   * @param allBeanClasses All the bean classes we have in this api domain.
   */
  public ApiObjectRef(String path, Object object, Map<Class<?>, ApiBeanDescriptor> descriptors) {
    super();
    this.descriptors = descriptors;
    if (object == null) {
      throw new IllegalArgumentException("The object must be set in an ApiObjectRef");
    }
    this.path = (path == null ? StringConstant.EMPTY : path);
    this.object = object;
    try {
      this.descriptor = descriptors.get(object.getClass());
      meta = ApiObjects.meta(object.getClass(), descriptors);
      init(path);
    } catch (ExecutionException e) {
      throw new IllegalArgumentException(
          "The object can't be processed as bean " + object.getClass(), e);
    }
  }

  /**
   * Discover the current values of the object to identify the initial set of events.
   */
  private final void init(String path) {
    for (PropertyMeta propertyMeta : meta.getProperties().values()) {
      PropertyEntry entry =
          new PropertyEntry(path, propertyMeta);
      properties.put(propertyMeta.getName().toUpperCase(), entry);
      propertiesByMethod.put(propertyMeta.getGetter(), entry);
      if (propertyMeta.getSetter() != null) {
        propertiesByMethod.put(propertyMeta.getSetter(), entry);
      }
      switch (propertyMeta.getKind()) {
        case VALUE:
          // If we have a non null value in the given property then we add a property changes from
          // null to new value.
          Object value = propertyMeta.getValue(object);
          if (value != null) {
            entry.setChangedValue(null, value);
          }
          break;

        case REFERENCE:
          // If we have a reference to other api object and it's not null then we construct the
          // ApiObjectRef also for this instance.
          Object reference = propertyMeta.getValue(object);
          if (reference != null) {
            // Construct the ApiObject reference and save it.
            ApiObjectRef ref = new ApiObjectRef(entry.getPath(), reference, descriptors);
            entry.setReference(ref);
          }

          break;

        case COLLECTION:
          // If we have an api object reference collection then we initiate the collection and
          // construct all the existing object as reference.
          // TODO Later on we can manage other containers but list.
          ApiObjectCollection collection = new ApiObjectCollection(this, propertyMeta);
          entry.setCollection(collection);

          break;
        default:
          break;
      }
    }
  }


  public String getPath() {
    return path;
  }

  public final Object getObject() {
    return object;
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
    PropertyEntry entry = properties.get(propertyName.toUpperCase());
    if (entry == null) {
      throw new IllegalArgumentException(
          propertyName + " property not found in " + meta.getClazz().getName());
    }
    setValueInner(value, entry);
  }

  final void setValueInner(Object value, PropertyEntry entry) {
    String propertyName = entry.getMeta().getName();
    try {
      switch (entry.getMeta().getKind()) {
        case VALUE:
          entry.setChangedValue(entry.getMeta().getValue(object), value);
          // At last we set the value
          entry.getMeta().setValue(object, value);

          break;

        case REFERENCE:
          // If the reference is not the same then we assume it as a brand new object. We build it
          // again.
          if (value != null) {
            // We setup a new reference from scratch.
            ApiObjectRef newRef = new ApiObjectRef(entry.getPath(), value, descriptors);
            entry.setReference(newRef);
          } else {
            // TODO Manage the deletion of a reference.
            entry.setReference(null);
          }
          // We set the value as a property at the end.
          entry.getMeta().setValue(object, value);
          break;

        case COLLECTION:
          ApiObjectCollection collection = entry.getCollection();
          collection.set((Collection<Object>) value);
          break;

        default:
          break;
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
    PropertyEntry propertyEntry = properties.get(propertyName);

    switch (propertyEntry.getMeta().getKind()) {
      case VALUE:
        setValueInner(value, propertyEntry);
        break;
      case REFERENCE:
        // Call the setValueByPath on reference with new path
        if (PathUtility.getPathSize(path) == 1) {
          setValueInner(value, propertyEntry);
        } else {
          propertyEntry.getReference().setValueByPath(PathUtility.nextFullPath(path), value);
        }
        break;
      case COLLECTION:
        ApiObjectCollection collection = propertyEntry.getCollection();
        if (PathUtility.getPathSize(path) == 1) {
          setValueInner(value, propertyEntry);
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
    PropertyEntry propertyEntry = properties.get(propertyName);

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
    PropertyEntry propertyEntry = properties.get(propertyName);

    int pathSize = PathUtility.getPathSize(path);
    switch (propertyEntry.getMeta().getKind()) {
      case VALUE:
        return this;
      case REFERENCE:
        if (pathSize == 1) {
          return propertyEntry.getReference();
        } else {
          return propertyEntry.getReference().getValueRefByPath(PathUtility.nextFullPath(path));
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
      default:
        break;
    }
    return null;
  }

  public void removeValueByPath(String path) {
    // TODO
    path = path.toUpperCase();

    String propertyName = PathUtility.getRootPath(path);
    PropertyEntry propertyEntry = properties.get(propertyName);

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
    PropertyEntry propertyEntry = properties.get(propertyName.toUpperCase());
    if (propertyEntry == null) {
      throw new IllegalArgumentException(
          propertyName + " property not found in " + meta.getClazz().getName());
    }
    return getValueInner(propertyEntry);
  }

  final Object getValueInner(PropertyEntry propertyEntry) {
    switch (propertyEntry.getMeta().getKind()) {
      case VALUE:
        return propertyEntry.getMeta().getValue(object);
      case COLLECTION:
        return propertyEntry.getCollection();
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

  public final ApiBeanDescriptor getDescriptor() {
    return descriptor;
  }

  public final Map<Class<?>, ApiBeanDescriptor> getDescriptors() {
    return descriptors;
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
    if (currentState == ChangeState.NEW) {
      // In this case we have a change on the object. The modified comes from the modification of
      // the properties.
      result = new ObjectChange(path, ChangeState.NEW);
      currentState = ChangeState.NOP;
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

              result.getReferencedObjects()
                  .add(new ReferencedObjectChange(path, entry.getMeta().getName(),
                      new ObjectChangeSimple(ref.getPath(), refChange.getOperation(),
                          ref.getObject())));
            }
          }
          break;

        default:
          break;
      }
    }
    return Optional.ofNullable(result);
  }

  /**
   * The wrapper is the single instance of {@link Enhancer} proxy that was created for this
   * reference.
   */
  private Object wrapper = null;

  public Object getWrapper() {
    return getWrapper(getMeta().getClazz());
  }

  @SuppressWarnings("unchecked")
  public <T> T getWrapper(Class<T> beanClass) {
    if (wrapper == null) {

      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass(beanClass);
      enhancer.setCallback(new InvocationHandler() {

        @Override
        public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {
          PropertyEntry propertyEntry = propertiesByMethod.get(arg1);
          if (propertyEntry != null) {
            if (arg1.equals(propertyEntry.getMeta().getGetter())) {
              Object valueInner = getValueInner(propertyEntry);
              if (valueInner instanceof ApiObjectRef) {
                return ((ApiObjectRef) valueInner).getWrapper(propertyEntry.getMeta().getType());
              } else if (valueInner instanceof ApiObjectCollection) {
                return ((ApiObjectCollection) valueInner).getProxy();
              }
              return valueInner;
            } else if (arg1.equals(propertyEntry.getMeta().getSetter())) {
              setValueInner(arg2[0], propertyEntry);
              return null;
            }
          }
          return arg1.invoke(object, arg2);
        }
      });
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

}
