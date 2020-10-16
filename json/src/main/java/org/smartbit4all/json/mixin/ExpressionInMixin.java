package org.smartbit4all.json.mixin;

import java.util.Collection;
import java.util.Set;
import org.smartbit4all.domain.meta.Operand;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ExpressionIn")
public abstract class ExpressionInMixin <T> extends ExpressionMixin {

  @JsonProperty
  Operand<T> operand;

  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
  @JsonProperty
  Set<T> values;
  
  @JsonCreator
  public ExpressionInMixin(@JsonProperty("operand") Operand<T> operand,
      @JsonProperty ("values") Collection<T> values) {
  }
}
