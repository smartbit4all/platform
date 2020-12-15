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
import org.smartbit4all.domain.meta.Operand;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ExpressionBetween")
public abstract class ExpressionBetweenMixin<T> extends ExpressionMixin {

  @JsonProperty
  boolean symmetric = false;

  @JsonProperty
  Operand<T> operand;

  @JsonProperty
  Operand<T> lowerBound;

  @JsonProperty
  Operand<T> upperBound;

  @JsonProperty
  Comparator<T> comparator = null;

  @JsonCreator
  public ExpressionBetweenMixin(@JsonProperty("operand") Operand<T> operand,
      @JsonProperty("lowerBound") Operand<T> lowerBound,
      @JsonProperty("upperBound") Operand<T> upperBound,
      @JsonProperty("symmetric") boolean symmetric) {}
}
