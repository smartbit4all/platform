package org.smartbit4all.api.collection;

public class SearchIndexReferenceMapping {

  String referenceName;
  String targetSearchIndexSchema;
  String targetSearchIndexName;
  String sourceProperty;
  String targetProperty;

  public SearchIndexReferenceMapping(String referenceName,
      String sourceProperty,
      String targetSearchIndexSchema,
      String targetSearchIndexName,
      String targetProperty) {
    super();
    this.sourceProperty = sourceProperty;
    this.targetProperty = targetProperty;
    this.referenceName = referenceName;
    this.targetSearchIndexSchema = targetSearchIndexSchema;
    this.targetSearchIndexName = targetSearchIndexName;
  }



}
