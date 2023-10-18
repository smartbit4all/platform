package org.smartbit4all.api.collection;

import java.util.function.BiFunction;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;

public final class CustomExpressionMapping {

  BiFunction<Object, Property<?>, Expression> expressionProcessor;

  BiFunction<Object, EntityDefinition, Expression> complexExpressionProcessor;

  BiFunction<Object, SearchIndexMappingObject, Expression> detailExpressionProcessor;

  public CustomExpressionMapping(
      BiFunction<Object, Property<?>, Expression> customExpressionProcessor,
      BiFunction<Object, EntityDefinition, Expression> complexExpressionProcessor,
      BiFunction<Object, SearchIndexMappingObject, Expression> detailExpressionProcessor) {
    super();
    this.expressionProcessor = customExpressionProcessor;
    this.complexExpressionProcessor = complexExpressionProcessor;
    this.detailExpressionProcessor = detailExpressionProcessor;
  }

}
