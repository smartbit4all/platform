package org.smartbit4all.api.collection;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.smartbit4all.core.object.ObjectNode;

class SearchIndexMappingProperty extends SearchIndexMapping {

  String name;

  String[] path;

  boolean primaryKey = false;

  UnaryOperator<Object> processor;

  Function<ObjectNode, Object> complexProcessor;

  Class<?> type;

  /**
   * The length can be defined in case of string.
   */
  int length;

  SearchIndexMappingProperty(String name, String[] path, Class<?> type, int length,
      UnaryOperator<Object> processor,
      Function<ObjectNode, Object> complexProcessor) {
    super();
    this.name = name;
    this.path = path;
    this.type = type;
    this.length = length;
    this.processor = processor;
    this.complexProcessor = complexProcessor;
  }

}
