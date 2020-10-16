package org.smartbit4all.json.mixin;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.meta.BooleanOperator;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.OperandLiteral;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.meta.Expression2Operand.Operator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ExpressionClause")
public abstract class ExpressionClauseMixin extends ExpressionMixin {

  @JsonProperty
  BooleanOperator operator;

  @JsonProperty
  List<Expression> expressions = new ArrayList<>();

  @JsonCreator
  public ExpressionClauseMixin(@JsonProperty("operator") BooleanOperator operator) {}
}
