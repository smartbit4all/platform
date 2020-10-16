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
   * @param property The Property of the {@link EntityDefinition}.
   * @param getterMethod
   * @param setterMethod
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
   * @param property The Property of the {@link EntityDefinition}.
   * @param getterMethod
   * @param setterMethod
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
