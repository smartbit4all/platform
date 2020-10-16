package org.smartbit4all.domain.meta;

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
   * Creates a new operand with the given property.
   * 
   * @param property
   */
  public OperandProperty(Property<T> property) {
    super();
    this.property = property;
  }

  @Override
  public T value() {
    return boundValue == null ? null : boundValue.getValue();
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
        : StringConstant.LEFT_SQUARE + boundValue.getValue() + StringConstant.RIGHT_SQUARE);
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

}
