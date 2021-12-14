package org.smartbit4all.core.object;

import java.lang.reflect.Method;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The property meta of an api bean object.
 * 
 * @author Peter Boros
 */
public class PropertyMeta {

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
   * The getter method for the property (e.g <code>String getName()</code>).
   */
  private Method getter;

  /**
   * The setter method for the property (e.g. <code>void setName(String name)</code>).
   */
  private Method setter;

  /**
   * The fluid setter method for the property (eg. <code>Person name(String name)</code>).
   */
  private Method fluidSetter;

  /**
   * The fluid item adder method for the property (eg.
   * <code>Person addAddressesItem(Address address)</code>).
   */
  private Method itemAdder;

  /**
   * The fluid item putter method for the property (eg.
   * <code>Person putAddressesItem(String addressId, Address address)</code>).
   */
  private Method itemPutter;

  /**
   * The type of the property. We need this to transfer the value and identifies the
   * <code>TransferService</code> <code>Converter</code>.
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
  public static enum PropertyKind {
    /**
     * The value a property that holds a value that can be changed as is.
     */
    VALUE,
    /**
     * The reference is a named reference to another api object.
     */
    REFERENCE,
    /**
     * The collection is a container of references to other api objects. It's an ordered collection
     * (a list) where the items are identified by their position.
     */
    COLLECTION,
    /**
     * The map is also a container of references to other api objects but string unique identifier
     * is provided where the identifier is used in the change events and property path. So the
     * identifier must be sanitized to be able to include in a path (there shouldn't be any / for
     * example).
     */
    MAP
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

  public final Method getGetter() {
    return getter;
  }

  final void setGetter(Method getter) {
    this.getter = getter;
  }

  public final Method getSetter() {
    return setter;
  }

  final void setSetter(Method setter) {
    this.setter = setter;
  }

  public Method getFluidSetter() {
    return fluidSetter;
  }

  public void setFluidSetter(Method fluidSetter) {
    this.fluidSetter = fluidSetter;
  }

  public Method getItemAdder() {
    return itemAdder;
  }

  public void setItemAdder(Method itemAdder) {
    this.itemAdder = itemAdder;
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
    sb.append(BeanMetaUtil.GET);
    sb.append(StringConstant.COLON_SPACE);
    sb.append(getter == null ? StringConstant.MINUS_SIGN : getter.getName());
    sb.append(StringConstant.SPACE);
    sb.append(BeanMetaUtil.SET);
    sb.append(StringConstant.COLON_SPACE);
    sb.append(setter == null ? StringConstant.MINUS_SIGN : setter.getName());
    sb.append(StringConstant.COMMA_SPACE);
    sb.append(fluidSetter == null ? StringConstant.MINUS_SIGN : fluidSetter.getName());
    sb.append(StringConstant.SPACE);
    sb.append(BeanMetaUtil.ADDITEM_PREFIX);
    sb.append(BeanMetaUtil.ADDITEM_POSTFIX);
    sb.append(StringConstant.COLON_SPACE);
    sb.append(itemAdder == null ? StringConstant.MINUS_SIGN : itemAdder.getName());
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
              + beanMeta.getClazz().getName(),
          e);
    }
  }

  public final BeanMeta getBeanMeta() {
    return beanMeta;
  }

  public final Class<?> getType() {
    return type;
  }

  public final Method getItemPutter() {
    return itemPutter;
  }

  public final void setItemPutter(Method itemPutter) {
    this.itemPutter = itemPutter;
  }

}
