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

import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.ExpressionBetween;
import org.smartbit4all.domain.meta.ExpressionBoolean;
import org.smartbit4all.domain.meta.ExpressionBracket;
import org.smartbit4all.domain.meta.ExpressionClause;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionIsNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, 
include = As.PROPERTY, property = "type") @JsonSubTypes({

@JsonSubTypes.Type(value = Expression2Operand.class, name = "Expression2Operand"),
@JsonSubTypes.Type(value = ExpressionBetween.class, name = "ExpressionBetween"),
@JsonSubTypes.Type(value = ExpressionBoolean.class, name = "ExpressionBoolean"),
@JsonSubTypes.Type(value = ExpressionBracket.class, name = "ExpressionBracket"),
@JsonSubTypes.Type(value = ExpressionClause.class, name = "ExpressionClause"),
@JsonSubTypes.Type(value = ExpressionIn.class, name = "ExpressionIn"),
@JsonSubTypes.Type(value = ExpressionIsNull.class, name = "ExpressionIsNull")
})
public abstract class ExpressionMixin {

  @JsonProperty
  boolean negate;

}
