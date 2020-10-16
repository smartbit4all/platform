package org.smartbit4all.domain.service.transfer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.service.transfer.BeanEntityBindingConfig.Accessor;
import org.smartbit4all.domain.service.transfer.convert.Converter;

/**
 * This interface is responsible for managing the Bean interfaces generated as views from the domain
 * model.
 * 
 * @author Peter Boros
 */
public final class BeanEntityBinding {

  /**
   * The bean class that we bind with the entity.
   */
  Class<?> beanClass;

  /**
   * The entity in the binding.
   */
  EntityDefinition entityDef;

  Map<Property<?>, List<Binding>> bindingsByProperties = new HashMap<>();

  List<Binding> bindings;

  public static class Binding {

    public Property<?> property;

    public Method getter;

    public Method setter;

    public Converter<?, ?> converter;

    public Binding(Property<?> property, Method getter, Method setter, Converter<?, ?> converter) {
      super();
      this.property = property;
      this.getter = getter;
      this.setter = setter;
      this.converter = converter;
    }

  }

  public BeanEntityBinding(Class<?> beanClass, EntityDefinition entityDef) {
    super();
    this.beanClass = beanClass;
    this.entityDef = entityDef;
  }

  void add(TransferService transferService, String propertyName, Accessor accessor)
      throws Exception {
    Property<?> property = entityDef.getProperty(propertyName);
    // We know the mapping and the entityDef. We can discover the bean for the methods.
    Method getter = beanClass.getMethod(accessor.getter);
    // We use the return type of the getter method to find the possible setter. If this type
    // differs from the type of the property then we try to find a converter. It's a default
    // conversion.
    // TODO Later on we can use converter set in the configuration by name.
    Method setter = beanClass.getMethod(accessor.setter, getter.getReturnType());
    Converter<?, ?> converter = null;
    if (!property.type().equals(getter.getReturnType())) {
      converter = transferService.converter(null, getter.getReturnType(), property.type());
    }

    add(property, getter, setter, converter);

  }

  void add(Property<?> property, Method getter, Method setter, Converter<?, ?> converter) {
    Binding binding = new Binding(property, getter, setter, converter);

    List<Binding> bindings = bindingsByProperties.get(property);
    if (bindings == null) {
      bindings = new ArrayList<>();
      bindingsByProperties.put(property, bindings);
    }
    bindings.add(binding);
  }

  /**
   * The getter methods are mapped by the properties.
   * 
   * @return
   */
  public final Map<Property<?>, List<Binding>> bindingsByProperties() {
    return Collections.unmodifiableMap(bindingsByProperties);
  }

  String generateKey() {
    return generateKey(beanClass, entityDef);
  }

  static String generateKey(Class<?> beanClass, EntityDefinition entityDef) {
    return entityDef.getClass().getName() + StringConstant.HYPHEN + beanClass.getName();
  }

}
