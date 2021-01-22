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

  PropertyMeta(String name, Class<?> type) {
    super();
    this.name = name;
    this.type = type;
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

}
