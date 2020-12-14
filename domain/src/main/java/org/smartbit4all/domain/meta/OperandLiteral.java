/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import org.smartbit4all.core.utility.StringConstant;

public class OperandLiteral<T> extends Operand<T> {

  /**
   * The value of the literal.
   */
  T value;

  /**
   * The type handler for the given literal. Must not match with the value because the type handler
   */
  JDBCDataConverter<T, ?> typeHandler;

  /**
   * Creates a new instance from the literal with the given value.
   * 
   * @param value The value must not be null or else the
   */
  public OperandLiteral(T value, JDBCDataConverter<T, ?> typeHandler) {
    super();
    this.value = value;
    this.typeHandler = typeHandler;
  }

  public OperandLiteral(Class<T> type) {
    super();
    this.value = null;
  }

  @Override
  public T value() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  @SuppressWarnings("unchecked")
  public void setValueUnchecked(Object value) {
    this.value = (T) value;
  }

  @Override
  public String toString() {
    return text(value);
  }

  /**
   * Utility function to produce textual representation for the given value.
   * 
   * @param value
   * @return
   */
  static final String text(Object value) {
    if (value == null) {
      return StringConstant.NULL;
    }
    if (value instanceof String) {
      return StringConstant.DOUBLE_QUOTE + value + StringConstant.DOUBLE_QUOTE;
    }
    return value.toString();
  }

  /**
   * @return The {@link #typeHandler()} of the given literal.
   */
  public JDBCDataConverter<T, ?> typeHandler() {
    return typeHandler;
  }

}
