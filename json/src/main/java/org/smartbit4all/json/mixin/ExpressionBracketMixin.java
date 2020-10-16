package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.meta.Expression;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ExpressionBracket")
public abstract class ExpressionBracketMixin extends ExpressionMixin {

  @JsonProperty
  Expression expression;

  @JsonCreator
  public ExpressionBracketMixin(@JsonProperty("expression") Expression expression) {}
}
