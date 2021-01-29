package org.smartbit4all.api.object;

import java.lang.reflect.Method;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.smartbit4all.domain.service.transfer.convert.Converter;

/**
 * The property meta of an api bean object.
 * 
 * @author Peter Boros
 */
class PropertyMeta {

  /**
   * The bean meta that contains the given property.
   */
  private final BeanMeta beanMeta;

  /**
   * The name calculated by the name of the setter / getter method.
   */
  private final String name;

  /**
   * If there is any problem with the given property like (the name of the setter and getter is not
   * conform or anything else this warning will be set to be able to identify the problematic bean
   * property later on in log entries.
   */
  private String warning;

  /**
   * The setter method for the given property.
   */
  private Method setter;

  /**
   * The getter method for thge property.
   */
  private Method getter;

  /**
   * The type of the property. We need this to transfer the value and identifies the
   * {@link TransferService} {@link Converter}.
   */
  private final Class<?> type;

  /**
   * The kind is by default the {@link PropertyKind#VALUE}. Can be modified later on during the
   * discovering of the class.
   */
  private PropertyKind kind = PropertyKind.VALUE;

  /**
   * The kind of the property meta.
   * 
   * @author Peter Boros
   */
  enum PropertyKind {
    /**
     * The value a property that holds a value that can be changed as is.
     */
    VALUE,
    /**
     * The reference is a named reference to another api object.
     */
    REFERENCE,
    /**
     * The collection is a container of references to other api objects.
     */
    COLLECTION
  }

  PropertyMeta(String name, Class<?> type, BeanMeta beanMeta) {
    super();
    this.name = name;
    this.type = type;
    this.beanMeta = beanMeta;
  }

  public final String getWarning() {
    return warning;
  }

  final void setWarning(String warning) {
    this.warning = warning;
  }

  public final Method getSetter() {
    return setter;
  }

  final void setSetter(Method setter) {
    this.setter = setter;
  }

  public final Method getGetter() {
    return getter;
  }

  final void setGetter(Method getter) {
    this.getter = getter;
  }

  public final String getName() {
    return name;
  }

  public final PropertyKind getKind() {
    return kind;
  }

  final void setKind(PropertyKind kind) {
    this.kind = kind;
  }

  final void appendTo(StringBuilder sb) {
    sb.append(name);
    sb.append(StringConstant.LEFT_PARENTHESIS);
    sb.append(type.getName());
    sb.append(StringConstant.RIGHT_PARENTHESIS);
    sb.append(StringConstant.SPACE);
    sb.append(ApiObjects.GET);
    sb.append(StringConstant.COLON_SPACE);
    sb.append(getter == null ? StringConstant.MINUS_SIGN : getter.getName());
    sb.append(StringConstant.SPACE);
    sb.append(ApiObjects.SET);
    sb.append(StringConstant.COLON_SPACE);
    sb.append(setter == null ? StringConstant.MINUS_SIGN : setter.getName());
  }

  Object getValue(Object object) {
    try {
      return getter.invoke(object);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          getName() + " property can't be retreived in " + beanMeta.getClazz().getName());
    }
  }

  void setValue(Object object, Object value) {
    if (setter == null) {
      throw new IllegalArgumentException("Unable to write read only " +
          getName() + " property in " + beanMeta.getClazz().getName());
    }
    try {
      setter.invoke(object, value);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          getName() + " property can't be set to " + value + " in "
              + beanMeta.getClazz().getName());
    }
  }

  public final BeanMeta getBeanMeta() {
    return beanMeta;
  }

}
