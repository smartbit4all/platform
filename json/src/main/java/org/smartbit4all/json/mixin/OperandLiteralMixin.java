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

import org.smartbit4all.domain.meta.JDBCDataConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("OperandLiteral")
public abstract class OperandLiteralMixin<T> implements OperandMixin<T> {

  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
  @JsonProperty
  T value;

  @JsonProperty
  JDBCDataConverter<T, ?> typeHandler;

  @JsonCreator
  public OperandLiteralMixin(
      @JsonProperty("value") T value,
      @JsonProperty("typeHandler") JDBCDataConverter<T, ?> typeHandler) {
    this.value = value;
    this.typeHandler = typeHandler;
  }
}
