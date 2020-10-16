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
