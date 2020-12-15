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
package org.smartbit4all.json.mixin;

import java.util.Comparator;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.OperandLiteral;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.meta.Expression2Operand.Operator;
import org.smartbit4all.json.deserializer.EntityDefinitionDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("Expression2Operand")
public abstract class Expression2OperandMixin<T> extends ExpressionMixin {

  @JsonProperty
  OperandProperty<T> op;

  @JsonProperty
  Operator operator;

  @JsonProperty
  OperandLiteral<T> literal;

  @JsonProperty
  Comparator<T> comparator;

  @JsonCreator
  public Expression2OperandMixin(@JsonProperty("op") OperandProperty<T> op,
      @JsonProperty("operator") Operator operator,
      @JsonProperty("literal") OperandLiteral<T> literal) {
  }
}
