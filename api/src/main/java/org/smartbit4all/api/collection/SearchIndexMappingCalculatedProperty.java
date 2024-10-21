package org.smartbit4all.api.collection;

import java.util.List;
import java.util.function.Function;

class SearchIndexMappingCalculatedProperty extends SearchIndexMapping {

  String name;

  Function<SearchIndexDataRowWrapper, Object> complexProcessor;

  Class<?> type;

  List<String> dependsOnProperties;

  SearchIndexMappingCalculatedProperty(String name, List<String> dependsOnProperties, Class<?> type,
      Function<SearchIndexDataRowWrapper, Object> complexProcessor) {
    super();
    this.name = name;
    this.dependsOnProperties = dependsOnProperties;
    this.type = type;
    this.complexProcessor = complexProcessor;
  }

}
