package org.smartbit4all.api.collection;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.smartbit4all.core.object.ObjectNode;

public class SearchIndexMappingProperty extends SearchIndexMapping {

  String name;

  String[] path;

  UnaryOperator<Object> processor;

  Function<ObjectNode, Object> complexProcessor;

  Class<?> type;

  SearchIndexMappingProperty(String name, String[] path, Class<?> type,
      UnaryOperator<Object> processor,
      Function<ObjectNode, Object> complexProcessor) {
    super();
    this.name = name;
    this.path = path;
    this.type = type;
    this.processor = processor;
    this.complexProcessor = complexProcessor;
  }

}
