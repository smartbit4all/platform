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
package org.smartbit4all.api.object;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * The API object reference is a pair for the object participate in an object hierarchy. It refers
 * to the object itself, contains the unique identifier of the object. This identifies the given
 * occurrence not the object itself! The bare reference is not typed. The types version is an
 * extension.
 * 
 * TODO There can be a common interface like ApiObject to hide the implementation of the API object
 * itself.
 * 
 * @author Peter Boros
 */
public class ApiObjectRef {

  /**
   * The unique identifier generate at the construction.
   */
  private final UUID id;

  /**
   * The URI of the object that can be used to identify the referred API object on the publishing
   * API.
   */
  private final URI objectUri;

  /**
   * The reference of the original object.
   */
  private final Object object;

  /**
   * The meta structure of the given bean.
   */
  private final BeanMeta meta;

  /**
   * To reserve the order of changes and to ensure the fast access of the change objects we use this
   * map.
   */
  private LinkedHashMap<PropertyMeta, PropertyChange> propertyChanges = new LinkedHashMap<>();

  /**
   * This is a map about the referenced objects mapped by the property that owns the reference..
   */
  private Map<PropertyMeta, ApiObjectRef> references = new HashMap<>();

  /**
   * To reserve the order of changes and to ensure the fast access of the change objects we use this
   * map.
   */
  private Map<PropertyMeta, ApiObjectCollection> collections = new HashMap<>();

  /**
   * The classes of the domain beans. We need them to be able to identify the Api objects.
   */
  private final ApiBeanDescriptor descriptor;

  private final Map<Class<?>, ApiBeanDescriptor> descriptors;

  /**
   * The api object current state. The default is the new.
   */
  private ChangeState currentState = ChangeState.NEW;

  /**
   * Constructs a new api object reference.
   * 
   * @param object
   * @param objectUri
   * @param allBeanClasses All the bean classes we have in this api domain.
   */
  ApiObjectRef(Object object, URI objectUri, Map<Class<?>, ApiBeanDescriptor> descriptors) {
    super();
    this.descriptors = descriptors;
    if (object == null) {
      throw new IllegalArgumentException("The object must be set in an ApiObjectRef");
    }
    this.id = UUID.randomUUID();
    this.objectUri = objectUri;
    this.object = object;
    try {
      this.descriptor = descriptors.get(object.getClass());
      meta = ApiObjects.meta(object.getClass(), descriptors);
      init();
    } catch (ExecutionException e) {
      throw new IllegalArgumentException(
          "The object can't be processed as bean " + object.getClass(), e);
    }
  }

  /**
   * Discover the current values of the object to identify the initial set of events.
   */
  private final void init() {
    for (PropertyMeta propertyMeta : meta.getProperties().values()) {
      switch (propertyMeta.getKind()) {
        case VALUE:
          // If we have a non null value in the given property then we add a property changes from
          // null to new value.
          Object value = propertyMeta.getValue(object);
          if (value != null) {
            propertyChanges.put(propertyMeta,
                new PropertyChange(id, propertyMeta.getName(), null, value));
          }
          break;

        case REFERENCE:
          // If we have a reference to other api object and it's not null then we construct the
          // ApiObjectRef also for this instance.
          Object reference = propertyMeta.getValue(object);
          if (reference != null) {
            // Construct the ApiObject reference and save it.
            ApiObjectRef ref = new ApiObjectRef(reference, null, descriptors);
            references.put(propertyMeta, ref);
          }

          break;

        case COLLECTION:
          // If we have an api object reference collection then we initiate the collection and
          // construct all the existing object as reference.
          // TODO Later on we can manage other containers but list.
          ApiObjectCollection collection = new ApiObjectCollection(this, propertyMeta);
          collections.put(propertyMeta, collection);

          break;
        default:
          break;
      }
    }
  }

  public final UUID getId() {
    return id;
  }

  public final URI getObjectUri() {
    return objectUri;
  }

  public final Object getObject() {
    return object;
  }

  public void setValue(String propertyName, Object value) {
    PropertyMeta propertyMeta = meta.getProperties().get(propertyName.toUpperCase());
    if (propertyMeta == null) {
      throw new IllegalArgumentException(
          propertyName + " property not found in " + meta.getClazz().getName());
    }
    Method setter = propertyMeta.getSetter();
    if (setter == null) {
      throw new IllegalArgumentException("Unable to set read only " +
          propertyName + " property to " + value + " in " + meta.getClazz().getName());
    }
    try {
      setter.invoke(object, value);
      PropertyChange propertyChange = propertyChanges.get(propertyMeta);
      if (propertyChange == null) {
        // We create a new change.
        propertyChange = new PropertyChange(id, propertyName, null, value);
        propertyChanges.put(propertyMeta, propertyChange);
      } else {
        // We append the change.
        propertyChange.setNewValue(value);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          propertyName + " property is not set to " + value + " in " + meta.getClazz().getName());
    }
  }

  public Object getValue(String propertyName) {
    PropertyMeta propertyMeta = meta.getProperties().get(propertyName.toUpperCase());
    if (propertyMeta == null) {
      throw new IllegalArgumentException(
          propertyName + " property not found in " + meta.getClazz().getName());
    }
    return propertyMeta.getValue(object);
  }

  @SuppressWarnings("unchecked")
  public <O, T> void setValue(BiConsumer<O, T> setter, T value) {
    try {
      setter.accept(((O) object), value);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          setter.getClass() + " property is not set to " + value + " in "
              + meta.getClazz().getName());
    }
  }


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

  public final List<ChangeItem> renderAndCleanChanges() {
    List<ChangeItem> result = new ArrayList<>();
    renderAndCleanChangesReq(null, "root", result);
    return result;
  }

  final void renderAndCleanChangesReq(UUID parentId, String name, List<ChangeItem> result) {
    if (currentState != ChangeState.NOP) {
      // In this case we start with a new or modified reference.
      result.add(new ReferenceChange(null, "root", new ObjectChange(null, id)));
      currentState = ChangeState.NOP;
    }
    result.addAll(propertyChanges.values());
    propertyChanges.clear();
    // Find the modified references.
    for (Entry<PropertyMeta, ApiObjectRef> referenceEntry : references.entrySet()) {
      referenceEntry.getValue().renderAndCleanChangesReq(id, referenceEntry.getKey().getName(),
          result);
    }
  }

}
