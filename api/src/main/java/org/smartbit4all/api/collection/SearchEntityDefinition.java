package org.smartbit4all.api.collection;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.JoinPath;

/**
 * The search entity definition contains an {@link EntityDefinition} and its detail entities with
 * the master references. The detail is named so we can construct complex queries using this name.
 * 
 * @author Peter Boros
 */
class SearchEntityDefinition {

  /**
   * The entity definition of the current node.
   */
  EntityDefinition definition;

  /**
   * The details of the current node by name.
   */
  Map<String, DetailDefinition> detailsByName = new HashMap<>();

  /**
   * The detail definition that contains a detail search definition and a join to the master.
   * 
   * @author Peter Boros
   */
  class DetailDefinition {

    SearchEntityDefinition detail;

    JoinPath masterJoin;

  }

  final SearchEntityDefinition definition(EntityDefinition definition) {
    this.definition = definition;
    return this;
  }

}
