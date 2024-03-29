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
package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import org.smartbit4all.core.utility.StringConstant;

/**
 * In a condition an operand that means a property. If we need to evaluate the condition then we
 * have to bind the value of the property.
 *
 * @author Peter Boros
 *
 * @param <T>
 */
public class OperandProperty<T> extends Operand<T> {

  /**
   * The property that is the meta of the given operand.
   */
  Property<T> property;

  /**
   * If the value is bound then this field contains the accessor.
   */
  OperandBoundValue<T> boundValue;

  /**
   * In case of JDBC statements it could be important to store the qualification (table alias in the
   * SQL) for the given expression. If it's null then we ignore it.
   */
  private String qualifier;

  /**
   * One {@link OperandProperty} can depend on other properties. The {@link OperandProperty} form of
   * these properties are held in this list.
   */
  private List<OperandProperty<?>> requiredOperandProperties = new ArrayList<>();

  private UnaryOperator<T> processor;

  /**
   * Creates a new operand with the given property.
   *
   * @param property
   */
  public OperandProperty(Property<T> property) {
    super();
    this.property = property;
    initRequiredProperties();
  }

  private void initRequiredProperties() {
    // handle required properties from property functions
    PropertyFunction propertyFunction = property.getPropertyFunction();
    if (propertyFunction != null) {
      List<Property<?>> requiredProperties = propertyFunction.getRequiredProperties();
      if (requiredProperties != null) {
        requiredProperties.forEach(p -> requiredOperandProperties.add(new OperandProperty<>(p)));
      }
    }
  }

  /**
   * @return The bound value of the given operand if set. The value is modified by the given
   *         function if the function is well known like upper, lower etc.
   */
  public T applyFunction(T value) {
    T result = value;
    PropertyFunction propertyFunction = property.getPropertyFunction();
    if (propertyFunction != null) {
      if ("field".equals(propertyFunction)) {
        // return objectApi.asType(node, propertyFunction.getParameterString())
      } else if (value instanceof String) {
        if (PropertyFunction.LOWER.getStatement().equals(propertyFunction.getStatement())) {
          result = (T) ((String) value).toLowerCase();
        } else if (PropertyFunction.UPPER.getClass().equals(propertyFunction.getStatement())) {
          result = (T) ((String) value).toUpperCase();
        }
      }
    }
    return result;
  }



  @Override
  public T value() {
    return boundValue == null ? null : getValue();
  }

  private T getValue() {
    if (processor != null) {
      return processor.apply(boundValue.getValue());
    }

    return boundValue.getValue();
  }

  @Override
  public JDBCDataConverter<?, ?> getConverter() {
    return property().jdbcConverter();
  }

  /**
   * Sets the bound value for the given operand. It's a kind of lambda to effectively access the
   * bound value from the actual context.
   *
   * @param boundValue The bound value instance that contains all the information to access the
   *        value for the property.
   */
  @SuppressWarnings("unchecked")
  public void bind(OperandBoundValue<?> boundValue) {
    this.boundValue = (OperandBoundValue<T>) boundValue;
  }

  /**
   * @return Returns the {@link Property} of this operand.
   */
  public final Property<T> property() {
    return property;
  }

  /**
   * @return Returns the bound value object if any. It's the accessor of the bound value at the
   *         given context.
   */
  public final OperandBoundValue<T> getBoundValue() {
    return boundValue;
  }

  @Override
  public String toString() {
    return property.getName() + (boundValue == null ? StringConstant.EMPTY
        : StringConstant.LEFT_SQUARE + getValue() + StringConstant.RIGHT_SQUARE);
  }

  /**
   * In case of JDBC statements it could be important to store the qualification (table alias in the
   * SQL) for the given expression. If it's null then we ignore it.
   *
   * @return
   */
  public String getQualifier() {
    return qualifier;
  }

  /**
   * In case of JDBC statements it could be important to store the qualification (table alias in the
   * SQL) for the given expression. If it's null then we ignore it.
   *
   * @param qualifier
   */
  public void setQualifier(String qualifier) {
    this.qualifier = qualifier;
  }

  /**
   * Set the property.
   *
   * @param property
   */
  void setProperty(Property<T> property) {
    this.property = property;
  }

  public List<OperandProperty<?>> getRequiredOperandProperties() {
    return requiredOperandProperties;
  }

  public void setProcessor(UnaryOperator<T> processor) {
    this.processor = processor;
  }

}
