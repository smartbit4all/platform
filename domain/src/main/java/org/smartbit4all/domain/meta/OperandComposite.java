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
import java.util.stream.Collectors;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.utility.CompositeValue;

/**
 * This operand is a list of properties. If we would like to evaluate an expression then we need to
 * bind all the properties in this object.
 * 
 * @author Peter Boros
 */
public class OperandComposite extends Operand<CompositeValue> {

  /**
   * The list of operands for the composite value
   */
  private final List<Operand<?>> operands = new ArrayList<>();

  public OperandComposite(List<Operand<?>> operands) {
    super();
    this.operands.addAll(operands);
  }

  public OperandComposite(Operand<?>... operandArray) {
    super();
    for (Operand<?> operand : operandArray) {
      this.operands.add(operand);
    }
  }

  @SuppressWarnings({"rawtypes"})
  @Override
  public CompositeValue value() {
    return new CompositeValue(operands.stream()
        .map(op -> {
          if (op instanceof OperandProperty<?>) {
            OperandProperty<?> opProp = (OperandProperty<?>) op;
            return (Comparable) (opProp.getBoundValue() != null
                ? opProp.getBoundValue().getValue()
                : null);
          } else if (op instanceof OperandLiteral<?>) {
            return (Comparable) op.value();
          }
          return (Comparable) null;
        }).collect(Collectors.toList()));

  }

  public final List<Operand<?>> getOperands() {
    return operands;
  }

  @Override
  public JDBCDataConverter<?, ?> getConverter() {
    // In this case we have multiple values so we don't have exactly one converter
    return null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(StringConstant.LEFT_PARENTHESIS);
    int cnt = 0;
    for (Operand<?> operand : operands) {
      sb.append(operand.toString());
      if (cnt < operands.size()) {
        sb.append(StringConstant.COMMA);
      }
    }
    sb.append(StringConstant.RIGHT_PARENTHESIS);
    return sb.toString();
  }

}
