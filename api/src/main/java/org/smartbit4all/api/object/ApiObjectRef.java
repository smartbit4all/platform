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
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The API object reference is a pair for the object participate in an object hierarchy. It refers
 * to the object itself, contains the unique identifier of the object. This identifies the given
 * occurrence not the object itself!
 * 
 * TODO There can be a common interface like ApiObject to hide the implementation of the API object
 * itself.
 * 
 * @author Peter Boros
 */
public class ApiObjectRef<O> {

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
  private final O object;

  private final BeanMeta meta;

  /**
   * Constructs a new api object reference.
   * 
   * @param object
   * @param objectUri
   */
  ApiObjectRef(O object, URI objectUri) {
    super();
    if (object == null) {
      throw new IllegalArgumentException("The object must be set in an ApiObjectRef");
    }
    this.id = UUID.randomUUID();
    this.objectUri = objectUri;
    this.object = object;
    try {
      meta = ApiObjects.meta(object.getClass());
    } catch (ExecutionException e) {
      throw new IllegalArgumentException(
          "The object can't be processed as bean " + object.getClass(), e);
    }
  }

  public final UUID getId() {
    return id;
  }

  public final URI getObjectUri() {
    return objectUri;
  }

  public final O getObject() {
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
    Method getter = propertyMeta.getSetter();
    if (getter == null) {
      throw new IllegalArgumentException("Unable to read " +
          propertyName + " property in " + meta.getClazz().getName());
    }
    try {
      return getter.invoke(object);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          propertyName + " property can't be retreived in " + meta.getClazz().getName());
    }
  }

  public <T> void setValue(Consumer<T> setter, T value) {
    try {
      setter.accept(value);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          setter.getClass() + " property is not set to " + value + " in "
              + meta.getClazz().getName());
    }
  }

  public <T> T getValue(Supplier<T> getter) {
    return getter.get();
  }

}
