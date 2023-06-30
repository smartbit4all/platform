package org.smartbit4all.api.collection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.JoinPath;
import org.smartbit4all.domain.meta.Reference;
import static java.util.stream.Collectors.toList;

/**
 * The search entity definition contains an {@link EntityDefinition} and its detail entities with
 * the master references. The detail is named so we can construct complex queries using this name.
 *
 * @author Peter Boros
 */
public class SearchEntityDefinition {

  /**
   * The entity definition of the current node.
   */
  EntityDefinition definition;

  Reference<?, ?> masterRef;

  /**
   * The details of the current node by name.
   */
  Map<String, DetailDefinition> detailsByName = new HashMap<>();

  /**
   * The detail definition that contains a detail search definition and a join to the master.
   *
   * @author Peter Boros
   */
  public static class DetailDefinition {

    SearchEntityDefinition detail;

    JoinPath masterJoin;

    public DetailDefinition(SearchEntityDefinition detail, JoinPath masterJoin) {
      super();
      this.detail = detail;
      this.masterJoin = masterJoin;
    }

  }

  final SearchEntityDefinition definition(EntityDefinition definition) {
    this.definition = definition;
    return this;
  }

  public EntityDefinition getDefinition() {
    return definition;
  }

  public List<EntityDefinition> getDefinitions() {
    return Stream.concat(Stream.of(definition), getDetailDefinitions()).collect(toList());
  }

  public Stream<EntityDefinition> getDetailDefinitions() {
    return detailsByName.values().stream()
        .map(d -> d.detail)
        .flatMap(d -> Stream.concat(Stream.of(d.getDefinition()),
            d.getDetailDefinitions()));
  }

  public JoinPath getMasterJoin(String detailName) {
    if (detailName == null) {
      return null;
    }
    return detailsByName.entrySet().stream().filter(e -> e.getKey().equals(detailName))
        .map(e -> e.getValue().masterJoin).findFirst().orElse(null);
  }

  public EntityDefinition getEntityDef(String detailName) {
    if (detailName == null) {
      return null;
    }
    return detailsByName.entrySet().stream().filter(e -> e.getKey().equals(detailName))
        .map(e -> e.getValue().detail.definition).findFirst().orElse(null);
  }
}
