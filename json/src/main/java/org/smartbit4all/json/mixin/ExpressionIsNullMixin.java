package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.meta.Operand;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ExpressionIsNull")
public abstract class ExpressionIsNullMixin extends ExpressionMixin {

  @JsonProperty
  Operand<?> op;

  @JsonCreator
  public ExpressionIsNullMixin(@JsonProperty("op") Operand<?> op) {}
}
