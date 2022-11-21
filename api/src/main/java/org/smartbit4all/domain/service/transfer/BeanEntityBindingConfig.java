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
package org.smartbit4all.domain.service.transfer;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * Can be used to configure binding between a bean class and a domain entity.
 * 
 * @author Peter Boros
 */
public class BeanEntityBindingConfig {

  /**
   * The bean class that we bind with the entity.
   */
  Class<?> beanClass;

  /**
   * The name of the entity in the binding.
   */
  String entityName;

  public BeanEntityBindingConfig(Class<?> beanClass, String entityName) {
    super();
    this.beanClass = beanClass;
    this.entityName = entityName;
  }

  /**
   * The bindings based on a Property.
   */
  Map<String, Accessor> accessorByProperty = new HashMap<>();

  static class Accessor {

    String getter;

    String setter;

    String converterName;

    public Accessor(String getter, String setter, String converterName) {
      super();
      this.getter = getter;
      this.setter = setter;
      this.converterName = converterName;
    }

  }

  public BeanEntityBindingConfig(Class<?> beanClass) {
    super();
    this.beanClass = beanClass;
  }

  /**
   * Adding a new bind. Builder API so we can add new binding again by the return value.
   * 
   * @param propertyName The Property of the {@link EntityDefinition}.
   * @param getMethodName
   * @param setMethodName
   * @param converterName
   * @return
   * @throws SecurityException
   * @throws NoSuchMethodException
   */
  public BeanEntityBindingConfig bind(String propertyName, String getMethodName,
      String setMethodName, String converterName) throws NoSuchMethodException, SecurityException {
    Accessor accessor = new Accessor(getMethodName, setMethodName, converterName);
    accessorByProperty.put(propertyName, accessor);
    return this;
  }

  /**
   * Adding a new bind. Builder API so we can add new binding again by the return value.
   * 
   * @param propertyName The Property of the {@link EntityDefinition}.
   * @param getMethodName
   * @param setMethodName
   * @return
   * @throws SecurityException
   * @throws NoSuchMethodException
   */
  public BeanEntityBindingConfig bind(String propertyName, String getMethodName,
      String setMethodName) throws NoSuchMethodException, SecurityException {
    bind(propertyName, getMethodName, setMethodName, null);
    return this;
  }

}
