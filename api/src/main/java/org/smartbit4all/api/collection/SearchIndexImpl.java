package org.smartbit4all.api.collection;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.service.entity.EntityManagerImpl;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SearchIndexImpl<F, O> implements SearchIndex<F, O> {

  private String logicalSchema;

  private String name;

  /**
   * The entity definition the search working on.
   */
  private EntityDefinition definition;

  @Autowired
  private EntityManagerImpl entityManagerImpl;

  protected SearchIndexImpl(String logicalSchema, String name) {
    super();
    this.name = name;
    this.logicalSchema = logicalSchema;
  }

  @Override
  public synchronized EntityDefinition getDefinition() {
    if (definition == null) {
      definition = constructDefinition();
      entityManagerImpl.registerEntityDef(definition);
    }
    return definition;
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
