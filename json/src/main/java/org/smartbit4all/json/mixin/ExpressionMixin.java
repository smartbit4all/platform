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
