package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.meta.OperandBoundValue;
import org.smartbit4all.domain.meta.Property;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("OperandProperty")
public abstract class OperandPropertyMixin<T> implements OperandMixin<T> {

  @JsonProperty
  Property<T> property;

  @JsonProperty
  OperandBoundValue<T> boundValue;

  @JsonProperty
  String qualifier;

  @JsonCreator
  public OperandPropertyMixin(@JsonProperty("property") Property<T> property) {}
}
