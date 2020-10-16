package org.smartbit4all.json.mixin;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class QueryInputMixin <T extends EntityDefinition> {

  @JsonProperty
  String name;

  @JsonProperty
  List<Property<?>> properties = new ArrayList<>();

  @JsonProperty
  Expression where;

// TODO How to set sort order?
//  @JsonProperty("orderBy")
//  SortOrder orderBy;

  @JsonProperty
  List<Property<?>> groupByProperties = new ArrayList<>(2);

  @JsonProperty
  EntityDefinition entityDef;
}
