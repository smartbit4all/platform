package org.smartbit4all.json.mixin;

import java.util.Comparator;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.OperandLiteral;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.meta.Expression2Operand.Operator;
import org.smartbit4all.json.deserializer.EntityDefinitionDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeName("Expression2Operand")
public abstract class Expression2OperandMixin<T> extends ExpressionMixin {

  @JsonProperty
  OperandProperty<T> op;

  @JsonProperty
  Operator operator;

  @JsonProperty
  OperandLiteral<T> literal;

  @JsonProperty
  Comparator<T> comparator;

  @JsonCreator
  public Expression2OperandMixin(@JsonProperty("op") OperandProperty<T> op,
      @JsonProperty("operator") Operator operator,
      @JsonProperty("literal") OperandLiteral<T> literal) {
  }
}
