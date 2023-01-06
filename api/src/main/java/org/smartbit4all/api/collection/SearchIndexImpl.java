package org.smartbit4all.api.collection;

import org.smartbit4all.domain.meta.EntityDefinition;

public abstract class SearchIndexImpl<F, O> implements SearchIndex<F, O> {

  private String logicalSchema;

  private String name;

  /**
   * The entity definition the search working on.
   */
  protected EntityDefinition definition;

  protected SearchIndexImpl(String logicalSchema, String name) {
    super();
    this.name = name;
    this.logicalSchema = logicalSchema;
  }

  public abstract EntityDefinition constructDefinition();

  @Override
  public String logicalSchema() {
    return logicalSchema;
  }

  @Override
  public String name() {
    return name;
  }

}
