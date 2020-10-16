package org.smartbit4all.json.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ExpressionBoolean")
public abstract class ExpressionBooleanMixin extends ExpressionMixin {

  @JsonProperty
  boolean value;

  @JsonCreator
  public ExpressionBooleanMixin(@JsonProperty("value") boolean value) {}
}
