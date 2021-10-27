package org.smartbit4all.domain.data.storage.index;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.ExpressionVisitor;

public class ExpressionEntityDefinitionExtractor {

  public static Optional<EntityDefinition> getOnlyEntityDefinition(Expression expression) {
    EntityDefinitionExtractorVisitor visitor = new EntityDefinitionExtractorVisitor();
    expression.accept(visitor);

    Set<EntityDefinition> entityDefinitions = visitor.getEntityDefinitions();
    if (entityDefinitions.size() > 1) {
      throw new IllegalArgumentException(
          "Expression contains multiple EntityDefinitions, only one is allowed: "
              + entityDefinitions);
    }

    if (entityDefinitions.size() < 1) {
      return Optional.empty();
    }

    return Optional.of(entityDefinitions.iterator().next());
  }

  public static class EntityDefinitionExtractorVisitor extends ExpressionVisitor {

    public Set<EntityDefinition> entityDefinitions = new HashSet<>();

    // TODO only extract entityDef from two opernd expressions, is this enough?

    @Override
    public <T> void visit2Operand(Expression2Operand<T> expression) {
      entityDefinitions.add(expression.getOp().property().getEntityDef());
    }

    public Set<EntityDefinition> getEntityDefinitions() {
      return entityDefinitions;
    }

  }

}
